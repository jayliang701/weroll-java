package com.magicfish.weroll.annotation;

import com.magicfish.weroll.utils.ClassNameLoader;
import com.magicfish.weroll.utils.ClassUtil;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.bytecode.*;
import javassist.bytecode.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class AnnotationRegister {

    private static final String NS = "com.magicfish.weroll.annotation.";

    private static Annotation findMethodAnnotation(CtMethod method, String annotationName) throws Exception {
        MethodInfo info = method.getMethodInfo();
        AnnotationsAttribute attr = (AnnotationsAttribute) info.getAttribute(AnnotationsAttribute.visibleTag);
        return attr.getAnnotation(annotationName);
    }

    private static void addMethodAnnotation(CtMethod method, Annotation annotation) throws Exception {
        MethodInfo info = method.getMethodInfo();
        AnnotationsAttribute attr = (AnnotationsAttribute) info.getAttribute(AnnotationsAttribute.visibleTag);
        attr.addAnnotation(annotation);
    }

    private static boolean hasClassAnnotation(CtClass cc, String annotationName) throws Exception {
        Object[] annotations = cc.getAnnotations();

        AnnotationsAttribute attr = (AnnotationsAttribute) cc.getClassFile().getAttribute(AnnotationsAttribute.visibleTag);

        String check = null;
        if (annotationName.endsWith("*")) {
            check = annotationName.substring(0, annotationName.length() - 1);
        }
        for (int i = 0; i < annotations.length; i++) {
            String name = annotations[i].toString();
            if (check != null && name.startsWith("@" + check)) {
                return true;
            } else if (check == null && attr.getAnnotation(annotationName) != null) {
                return true;
            }
        }
        return false;
    }

    public static void initialize() throws Exception {
        ClassPool pool = ClassPool.getDefault();
        String[] apiPackages = retrievePackageScan(pool);
        for (int i = 0; i < apiPackages.length; i++) {
            String apiMapping = apiPackages[i];
            if (apiMapping != null && !apiMapping.isEmpty()) {
                if (apiMapping.contains("file:") || apiMapping.contains("/")) {
                    System.out.println("found invalid mapping package: " + apiMapping);
                    continue;
                }
                injectRest(pool, apiMapping);
                injectAPI(pool, apiMapping);
            }
        }
    }

    private static Map<String, String> parsePathValuesFromUrl(String url) {
        String pattern = "\\{[a-zA-Z0-9_]+\\}";

        // 创建 Pattern 对象
        Pattern r = Pattern.compile(pattern);

        Map<String, String> paramNames = new HashMap<>();

        // 现在创建 matcher 对象
        Matcher m = r.matcher(url);
        while (m.find()) {
            String paramName = m.group();
            paramNames.put(paramName, paramName);
        }
        return paramNames;
    }

    private static String[] retrievePackageScan(ClassPool pool) throws Exception {
        Annotation annotation = null;
        try {
            throw new Exception("ops");
        } catch (Exception e) {
            StackTraceElement[] traces = e.getStackTrace();
            for (int i = 0; i < traces.length; i++) {
                StackTraceElement trace = traces[i];
                if (trace.getMethodName().equals("main")) {
                    annotation = tryToFindAnnotation(pool, trace.getClassName());
                    if (annotation != null) break;
                }
            }
        }

        if (annotation == null) {
            throw new ClassNotFoundException("can't find the main class to retrieve @Weroll annotation.");
        }

        MemberValue[] values = ((ArrayMemberValue) annotation.getMemberValue("apiScan")).getValue();
        String[] packages = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            String name = values[i].toString();
            packages[i] = name.substring(1, name.length() - 1);
        }

        return packages;
    }

    private static Annotation tryToFindAnnotation(ClassPool pool, String className) throws Exception {
        CtClass cc = pool.getCtClass(className);
        ClassFile classFile = cc.getClassFile();
        AnnotationsAttribute classAttr = (AnnotationsAttribute) classFile.getAttribute(AnnotationsAttribute.visibleTag);
        if (classAttr == null) return null;
        Annotation annotation = classAttr.getAnnotation("com.magicfish.weroll.Weroll");
        return annotation;
    }

    private static void injectRest(ClassPool pool, String packageName) throws Exception {

        ClassNameLoader nameLoader = new ClassNameLoader();
        Set<Class<?>> classes = ClassUtil.getClasses(packageName, nameLoader);
        String[] restClassNames = nameLoader.getClassNames();

        for (int n = 0; n < restClassNames.length; n ++) {
            CtClass cc = pool.getCtClass(restClassNames[n]);

            if (!hasClassAnnotation(cc, NS + "Rest")) continue;

            ClassFile classFile = cc.getClassFile();
            AnnotationsAttribute classAttr = (AnnotationsAttribute) classFile.getAttribute(AnnotationsAttribute.visibleTag);
            if (classAttr == null) continue;
            Annotation controllerAnnotation = classAttr.getAnnotation("org.springframework.web.bind.annotation.RestController");
            if (controllerAnnotation == null) {
                controllerAnnotation = new Annotation("org.springframework.web.bind.annotation.RestController", classFile.getConstPool());
                classAttr.addAnnotation(controllerAnnotation);
            }
            classFile.addAttribute(classAttr);

            CtMethod[] methods = cc.getDeclaredMethods();

            for (int m = 0; m < methods.length; m ++) {

                CtMethod targetMethod = methods[m];
                boolean isPost = false;
                Annotation annotation = findMethodAnnotation(targetMethod, NS + "RestGet");
                Annotation annotationSpring = null;
                if (annotation == null) {
                    annotation = findMethodAnnotation(targetMethod, NS + "RestPost");
                    if (annotation == null) continue;
                    isPost = true;
                    annotationSpring = findMethodAnnotation(targetMethod, "org.springframework.web.bind.annotation.PostMapping");

                    if (annotationSpring == null) {
                        annotationSpring = new Annotation("org.springframework.web.bind.annotation.PostMapping", targetMethod.getMethodInfo().getConstPool());
                    }
                    annotationSpring.addMemberValue("value", annotation.getMemberValue("value"));
                    annotationSpring.addMemberValue("path", annotation.getMemberValue("value"));
                    addMethodAnnotation(targetMethod, annotationSpring);

                } else {
                    annotationSpring = findMethodAnnotation(targetMethod, "org.springframework.web.bind.annotation.GetMapping");
                    if (annotationSpring == null) {
                        annotationSpring = new Annotation("org.springframework.web.bind.annotation.GetMapping", targetMethod.getMethodInfo().getConstPool());
                    }
                    annotationSpring.addMemberValue("value", annotation.getMemberValue("value"));
                    annotationSpring.addMemberValue("path", annotation.getMemberValue("value"));
                    addMethodAnnotation(targetMethod, annotationSpring);
                }

                System.out.println("register rest api: " + annotation.getMemberValue("value"));
                CtClass[] paramTypes = targetMethod.getParameterTypes();

                ParameterAnnotationsAttribute parameterAttribute = getMethodParameterAttribute(targetMethod, paramTypes.length);
                Annotation[][] paramArrays = parameterAttribute.getAnnotations();

                ConstPool parameterConstPool = parameterAttribute.getConstPool();

                String url = ArrayMemberValue.class.cast(annotation.getMemberValue("value")).getValue()[0].toString();
                Map<String, String> pathValues = parsePathValuesFromUrl(url);
                int pathValueNum = pathValues.size();

                for (int i = 0; i < pathValueNum; i++) {
                    Annotation[] paramAnnotations = paramArrays[i];
                    for (int j = 0; j < paramAnnotations.length; j ++) {
                        Annotation anno = paramAnnotations[j];
                        if (anno.getTypeName().equals("org.springframework.web.bind.annotation.PathVariable")) {
                            break;
                        }
                    }

                    Annotation[] newParamAnnotations = new Annotation[paramAnnotations.length + 1];
                    for (int j = 0; j < paramAnnotations.length; j ++) {
                        newParamAnnotations[j] = paramAnnotations[j];
                    }
                    newParamAnnotations[paramAnnotations.length] = new Annotation("org.springframework.web.bind.annotation.PathVariable", parameterConstPool);
                    paramArrays[i] = newParamAnnotations;
                }

                for (int i = pathValueNum; i < paramArrays.length; i++) {
                    CtClass paramType = paramTypes[i];
                    if (paramType.getSimpleName().equals("HttpAction")) continue;;
                    Annotation rpa = null;
                    Annotation[] paramAnnotations = paramArrays[i];
                    for (int j = 0; j < paramAnnotations.length; j ++) {
                        Annotation anno = paramAnnotations[j];
                        if (anno.getTypeName().equals("org.springframework.web.bind.annotation.RequestParam")) {
                            rpa = anno;
                            break;
                        }
                    }
                    if (rpa == null) {
                        rpa = new Annotation("org.springframework.web.bind.annotation.RequestParam", parameterConstPool);

                        Annotation[] newParamAnnotations = new Annotation[paramAnnotations.length + 1];
                        for (int j = 0; j < paramAnnotations.length; j ++) {
                            newParamAnnotations[j] = paramAnnotations[j];
                        }
                        newParamAnnotations[paramAnnotations.length] = rpa;
                        paramArrays[i] = newParamAnnotations;
                    }
                    rpa.addMemberValue("required", new BooleanMemberValue(Boolean.FALSE, parameterConstPool));
                }
                parameterAttribute.setAnnotations(paramArrays);

                targetMethod.getMethodInfo().addAttribute(parameterAttribute);
            }

            cc.toClass();
            cc.defrost();
        }
    }

    private static void injectAPI(ClassPool pool, String packageName) throws Exception {
        ClassNameLoader nameLoader = new ClassNameLoader();
        ClassUtil.getClasses(packageName, nameLoader);
        String[] apiClassNames = nameLoader.getClassNames();

        for (int n = 0; n < apiClassNames.length; n ++) {
            CtClass cc = pool.getCtClass(apiClassNames[n]);

            if (!hasClassAnnotation(cc, NS + "API")) continue;

            CtMethod[] methods = cc.getDeclaredMethods();

            for (int m = 0; m < methods.length; m ++) {

                CtMethod targetMethod = methods[m];
                Annotation annotation = findMethodAnnotation(targetMethod, NS + "Method");
                if (annotation == null) continue;

                CodeAttribute codeAttr = (CodeAttribute) targetMethod.getMethodInfo().getAttribute(CodeAttribute.tag);
                LocalVariableAttribute localVariableAtt = (LocalVariableAttribute) codeAttr.getAttribute(LocalVariableAttribute.tag);
                int startPos = getMethodParamStartIndex(targetMethod, localVariableAtt);
                if (startPos < 0) {
                    continue;
                }

                CtClass[] paramTypes = targetMethod.getParameterTypes();

                ParameterAnnotationsAttribute parameterAttribute = getMethodParameterAttribute(targetMethod, paramTypes.length);
                Annotation[][] paramArrays = parameterAttribute.getAnnotations();

                ConstPool parameterConstPool = parameterAttribute.getConstPool();

                for (int i = 0; i < paramArrays.length; i++) {
                    CtClass paramType = paramTypes[i];
                    String simpleName = paramType.getSimpleName();
                    if (simpleName.equals("APIAction") || simpleName.equals("HttpAction")) continue;
                    Annotation rpa = null;
                    Annotation[] paramAnnotations = paramArrays[i];
                    for (int j = 0; j < paramAnnotations.length; j ++) {
                        Annotation anno = paramAnnotations[j];
                        if (anno.getTypeName().equals(NS + "Param")) {
                            rpa = anno;
                            break;
                        }
                    }
                    if (rpa == null) {
                        rpa = new Annotation(NS + "Param", parameterConstPool);
                        rpa.addMemberValue("required", new BooleanMemberValue(Boolean.TRUE, parameterConstPool));

                        Annotation[] newParamAnnotations = new Annotation[paramAnnotations.length + 1];
                        for (int j = 0; j < paramAnnotations.length; j ++) {
                            newParamAnnotations[j] = paramAnnotations[j];
                        }
                        newParamAnnotations[paramAnnotations.length] = rpa;
                        paramArrays[i] = newParamAnnotations;
                    }
                    localVariableAtt.descriptor(1);
                    rpa.addMemberValue("name", new StringMemberValue(localVariableAtt.variableName(i + startPos), parameterConstPool));
                }
                parameterAttribute.setAnnotations(paramArrays);

                targetMethod.getMethodInfo().addAttribute(parameterAttribute);
            }

            cc.toClass();
            cc.defrost();
        }
    }

    private static int getMethodParamStartIndex(CtMethod method, LocalVariableAttribute localVariableAtt) {
        String prefix = method.getLongName().split("\\(", 2)[0];
        prefix = prefix.substring(0, prefix.lastIndexOf("."));
        prefix = prefix.replace(".", "/");
        int len = localVariableAtt.length();
        for (int i = 0; i < len; i++) {
            String desc = localVariableAtt.descriptor(i);
            if (desc.contains(prefix)) {
                int pos = i + 1;
                if (pos >= len) return -1;
                return pos;
            }
        }
        return -1;
    }

    private static ParameterAnnotationsAttribute getMethodParameterAttribute(CtMethod method, int paramCount) {
        AttributeInfo parameterAttributeInfo = method.getMethodInfo().getAttribute(ParameterAnnotationsAttribute.visibleTag);
        ParameterAnnotationsAttribute parameterAttribute;
        if (parameterAttributeInfo == null) {
            parameterAttribute = new ParameterAnnotationsAttribute(method.getMethodInfo().getConstPool(), ParameterAnnotationsAttribute.visibleTag);
            Annotation[][] paramArrays;
            paramArrays = new Annotation[paramCount][];
            for (int i = 0; i < paramArrays.length; i++) {
                paramArrays[i] = new Annotation[] {};
            }
            parameterAttribute.setAnnotations(paramArrays);
        } else {
            parameterAttribute = ((ParameterAnnotationsAttribute) parameterAttributeInfo);
//            paramArrays = parameterAttribute.getAnnotations();
        }

        return parameterAttribute;
    }

}
