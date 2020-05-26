package com.magicfish.weroll.aspect;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.magicfish.weroll.annotation.Param;
import com.magicfish.weroll.annotation.Rest;
import com.magicfish.weroll.annotation.RestGet;
import com.magicfish.weroll.annotation.RestPost;
import com.magicfish.weroll.consts.ErrorCodes;
import com.magicfish.weroll.exception.ServiceException;
import com.magicfish.weroll.net.HttpAction;
import com.magicfish.weroll.net.IRequestBodyFilter;
import com.magicfish.weroll.net.IResponseBodyProcessor;
import com.magicfish.weroll.utils.TypeConverter;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Aspect
public class RestfulAspect {

    private static final Charset UTF8 = Charsets.toCharset("UTF-8");

    private static ApplicationContext applicationContext;

    private static Map<String, Boolean> methodNeedLoginMap;
    private static Map<String, ArrayParam> methodArrayParamMap;
    private static Map<String, IRequestBodyFilter> requestBodyFilterMap;
    private static Map<String, IResponseBodyProcessor> responseBodyProcessorMap;

    public RestfulAspect(ApplicationContext applicationContext) throws Exception {
        if (RestfulAspect.applicationContext != null) throw new Exception("RestfulAspect can't be created more than one time");
        RestfulAspect.applicationContext = applicationContext;
        initialize();
    }

    private static void initialize() throws Exception {
        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(Rest.class);
        methodNeedLoginMap = new HashMap<>();
        methodArrayParamMap = new HashMap<>();
        requestBodyFilterMap = new HashMap<>();
        responseBodyProcessorMap = new HashMap<>();

        Map<String, IRequestBodyFilter> filterMap = new HashMap<>();
        Map<String, IResponseBodyProcessor> processorMap = new HashMap<>();

        for (Map.Entry<String, Object> beanEntry : beans.entrySet()) {
            Object instance = beanEntry.getValue();
            Method[] methods = Class.forName(instance.getClass().getName().split("\\$\\$")[0]).getDeclaredMethods();
            for (int i = 0; i < methods.length; i++) {
                Method method = methods[i];
                String methodName = method.toGenericString();
                Annotation[] annotations = method.getDeclaredAnnotations();
                for (int j = 0; j < annotations.length; j++) {
                    Annotation annotation = annotations[j];
                    boolean next = false;
                    Class<?> processorClass = null;
                    if (annotation instanceof RestGet) {
                        methodNeedLoginMap.put(methodName, ((RestGet) annotation).needLogin());
                        processorClass = ((RestGet) annotation).processor();
                        next = true;
                    } else if (annotation instanceof RestPost) {
                        methodNeedLoginMap.put(methodName, ((RestPost) annotation).needLogin());
                        processorClass = ((RestPost) annotation).processor();
                        Class<?> filterClass = ((RestPost) annotation).filter();
                        if (!filterMap.containsKey(filterClass.toGenericString())) {
                            filterMap.put(filterClass.toGenericString(), (IRequestBodyFilter) filterClass.newInstance());
                        }
                        requestBodyFilterMap.put(methodName, filterMap.get(filterClass.toGenericString()));
                        next = true;
                    }
                    String processorName = processorClass.toGenericString();
                    if (!processorMap.containsKey(processorName)) {
                        processorMap.put(processorName, (IResponseBodyProcessor) processorClass.newInstance());
                    }
                    responseBodyProcessorMap.put(methodName, processorMap.get(processorName));

                    if (next) {
                        Type[] paramTypes = method.getGenericParameterTypes();
                        for (int k = 0; k < paramTypes.length; k++) {
                            Type paramType = paramTypes[k];
                            String typeName = paramType.getTypeName();
                            if (typeName.endsWith("[]")) {
                                Class paramElementType = Class.forName(typeName.substring(0, typeName.length() - 2));
                                ArrayParam arrayParam = new ArrayParam(true, paramElementType);
                                methodArrayParamMap.put(methodName + "#" + k, arrayParam);
                            } else if (paramType instanceof ParameterizedTypeImpl) {
                                Class paramElementType = (Class) ((ParameterizedTypeImpl) paramType).getActualTypeArguments()[0];
                                ArrayParam arrayParam = new ArrayParam(false, paramElementType);
                                methodArrayParamMap.put(methodName + "#" + k, arrayParam);
                            }
                        }
                        j = annotations.length;
                    }
                }
            }
        }
    }

    private static ArrayParam isArrayParam(Method method, int index) {
        return methodArrayParamMap.get(method.toGenericString() + "#" + index);
    }

    private static Object buildResponse(Object body) {
        JSONObject result = new JSONObject();
        result.put("code", ErrorCodes.OK);
        result.put("data", body);
        return result;
    }

    private static Object buildError(String msg, int code) {
        JSONObject result = new JSONObject();
        result.put("code", code);
        result.put("msg", msg);
        return result;
    }

    private static Object buildError(Exception e, int code) {
        StringWriter stringWriter = new StringWriter();
        e.printStackTrace(new PrintWriter(stringWriter));
        String msg = stringWriter.toString();

        return buildError(msg, code);
    }

    private static Object buildError(Exception e) {
        if (e instanceof ServiceException) {
            return buildError(e, ((ServiceException) e).getCode());
        }
        return buildError(e, ErrorCodes.SERVER_ERROR);
    }

    private static Object buildError(ServiceException e) {
        return buildError(e.getMessage(), e.getCode());
    }

    @Pointcut("@within(com.magicfish.weroll.annotation.Rest) && (@annotation(com.magicfish.weroll.annotation.RestPost) || @annotation(com.magicfish.weroll.annotation.RestGet)))")
    public void exec() {

    }

    @Before("exec() && args(params)")
    public void beforeExec(JoinPoint joinPoint, Map<String,String> params) throws Throwable {
        System.out.println(joinPoint.getTarget());
    }

    @Around("exec()")
    public Object aroundExecWithServletObject(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            Object[] args = joinPoint.getArgs();

            ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = servletRequestAttributes.getRequest();
            HttpServletResponse response = servletRequestAttributes.getResponse();

            JSONObject json = null;

            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            String methodName = method.toGenericString();

            HttpAction httpAction = HttpAction.create(request, response);

            boolean needLogin = methodNeedLoginMap.get(methodName);
            if (needLogin && !httpAction.isLogined()) {
                return buildError("No Permission", ErrorCodes.NO_PERMISSION);
            }

            DefaultParameterNameDiscoverer discoverer = new DefaultParameterNameDiscoverer();
            String[] paramNames = discoverer.getParameterNames(method);
            Class[] paramTypes = method.getParameterTypes();

            if (request.getMethod() == RequestMethod.POST.name()) {
                IRequestBodyFilter requestBodyFilter = requestBodyFilterMap.get(methodName);
                if (requestBodyFilter != null) {
                    json = requestBodyFilter.doFilter(request);
                }
            }

            Annotation[][] paramsAnnotations = method.getParameterAnnotations();

            Type[] types = method.getGenericParameterTypes();

            int size = paramNames.length;
            Object[] newArgs = new Object[size];

            for (int i = 0; i < size; i++) {
                String paramName = paramNames[i];
                Class<?> paramType = paramTypes[i];
                Object paramValue = args[i];

                if (paramValue != null && paramType == String.class) {
                    if (((String) paramValue).isEmpty()) {
                        paramValue = null;
                    }
                } else if (paramType == HttpAction.class) {
                    paramValue = httpAction;
                } else if (paramValue == null || paramValue instanceof Serializable) {
                    if (json != null) {
                        paramValue = json.get(paramName);
                    }
                    try {
                        if (!BeanUtils.isSimpleValueType(paramType)) {
                            if (paramValue instanceof JSONArray) {
                                JSONArray arr = (JSONArray) paramValue;
                                ArrayParam arrayParam = isArrayParam(method, i);
                                if (arrayParam == null) {
                                    return buildError("param [" + paramName + "] should not be a array.", ErrorCodes.REQUEST_PARAMS_INVALID);
                                }
                                Class<?> elementType = arrayParam.getElementType();
                                int len = arr.size();
                                if (arrayParam.isTypeArray()) {
                                    Object[] list = (Object[]) Array.newInstance(elementType, len);
                                    for (int j = 0; j < len; j++) {
                                        if (arrayParam.isSimpleValueType()) {
                                            list[j] = arr.get(j);
                                        } else {
                                            list[j] = JSON.toJavaObject((JSONObject) arr.get(j), elementType);
                                        }
                                    }
                                    paramValue = list;
                                } else {
                                    List<Object> list = new ArrayList<>();
                                    for (int j = 0; j < len; j++) {
                                        if (arrayParam.isSimpleValueType()) {
                                            list.add(arr.get(j));
                                        } else {
                                            list.add(JSON.toJavaObject((JSONObject) arr.get(j), elementType));
                                        }
                                    }
                                    paramValue = list;
                                }

                            } else {
                                paramValue = JSON.toJavaObject((JSONObject) paramValue, paramType);
                            }
                        }
                    } catch (Exception e) {
                        return buildError("failed to parse param [" + paramName + "]. " + e.getMessage(), ErrorCodes.REQUEST_PARAMS_INVALID);
                    }
                }

                if (paramValue == null) {
                    boolean allowEmpty = false;
                    Annotation[] annotations = paramsAnnotations[i];
                    if (annotations != null && annotations.length > 0) {
                        for (int j = 0; j < annotations.length; j++) {
                            Annotation annotation = annotations[j];
                            if (annotation instanceof Param) {
                                Param param = (Param) annotation;
                                if (!param.required()) {
                                    //check default value
                                    String defaultValue = param.defaultValue();
                                    if (!defaultValue.isEmpty()) {
                                        paramValue = TypeConverter.castValueAs(defaultValue, ((Class) types[i]));
                                    }
                                    allowEmpty = true;
                                }
                                break;
                            }
                        }
                    }
                    if (!allowEmpty) {
                        return buildError("param [" + paramName + "] is required", ErrorCodes.REQUEST_PARAMS_INVALID);
                    }
                }

                newArgs[i] = paramValue;
            }

            Object responseBody = buildResponse(joinPoint.proceed(newArgs));

            IResponseBodyProcessor responseBodyProcessor = responseBodyProcessorMap.get(methodName);
            if (responseBodyProcessor != null) {
                return responseBodyProcessor.doProcess(responseBody, request, response);
            }

            return responseBody;
        } catch (Exception e) {
            return buildError(e);
        }
    }

}

class ArrayParam {
    private boolean isTypeArray = false;

    private boolean isSimpleValueType = false;

    private Class<?> elementType;

    public boolean isTypeArray() {
        return isTypeArray;
    }

    public boolean isSimpleValueType() {
        return isSimpleValueType;
    }

    public Class<?> getElementType() {
        return elementType;
    }

    public ArrayParam(boolean isTypeArray, Class<?> elementType) {
        this.isTypeArray = isTypeArray;
        this.elementType = elementType;
        this.isSimpleValueType = BeanUtils.isSimpleValueType(elementType);
    }
}