package com.magicfish.weroll.service;

import com.alibaba.fastjson.JSONObject;
import com.magicfish.weroll.aspect.API;
import com.magicfish.weroll.aspect.Method;
import com.magicfish.weroll.aspect.Param;
import com.magicfish.weroll.model.APIDef;
import com.magicfish.weroll.model.APIPostBody;
import com.magicfish.weroll.net.APIRequest;
import com.magicfish.weroll.utils.ClassUtil;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.*;

@Component
public class APIService {

    protected HashMap<String, APIDef> apis;

    public APIService() throws Exception {
        apis = new HashMap<>();

        TypeConverter.init();

        findAllMethodAnnotation(ClassUtil.getClasses("com.magicfish.weroll.service.api"));
    }

    public Object exec(APIRequest request) {
        System.out.println("exec api...");
        APIPostBody postBody = request.getPostBody();
        HashMap<String, Object> postData = postBody.getData();
        String apiName = postBody.getMethod();
        APIObj api = getAPI(apiName);
        Object ins = api.apiDef.getInstance();
        JSONObject result = new JSONObject();
        try {
            java.lang.reflect.Method method = api.apiDef.getMethod(api.method);

            Method methodDef = api.getMethodDef(api.method);

            Param[] paramDef = methodDef.params();
            Object[] objs = new Object[paramDef.length + 1];
            for (int i = 0; i < paramDef.length; i++) {
                Param param = paramDef[i];
                objs[i] = null;
                if (postData.containsKey(param.name())) {
                    Object val = postData.get(param.name());
                    objs[i] = TypeConverter.castValueAs(val, param.type());
                } else {
                    // set default value
                    objs[i] = TypeConverter.castValueAs(param.defaultValue(), param.type());
                }
            }
            objs[objs.length - 1] = request;

            result.put("code", 1);
            result.put("data", method.invoke(ins, objs));
        } catch (Exception e) {
            if (IllegalArgumentException.class.isInstance(e)) {
                result.put("code", 0);
                result.put("msg", "invalid request params");
            } else {
                result.put("code", 0);
                result.put("msg", e.toString());
            }
        }
        return result;
    }

    public APIObj getAPI(String name) {
        APIObj obj = null;
        String[] sp = name.split("\\.", 2);
        if (sp == null || sp.length < 2) {
            return null;
        }
        String tmp = sp[0];
        if (tmp != null && !tmp.isEmpty()) {
            APIDef def = apis.containsKey(tmp) ? apis.get(tmp) : null;
            if (def != null) {
                obj = new APIObj();
                obj.apiDef = def;
                obj.api = sp[0];
                obj.method = sp[1];
            }
        }
        return obj;
    }

    private void findAllMethodAnnotation(Set<Class<?>> clsSet) throws Exception {
        findAllMethodAnnotation(new ArrayList<>(clsSet));
    }

    private void findAllMethodAnnotation(List<Class> clsList) throws Exception {
        if (clsList != null && clsList.size() > 0) {
            for (Class cls : clsList) {

                API apiAnnotation = (API) cls.getAnnotation(API.class);
                if (apiAnnotation != null) {
                    Object ins = cls.newInstance();
                    HashMap<String, Method> methodDefMap = new HashMap<>();
                    HashMap<String, java.lang.reflect.Method> methodMap = new HashMap<>();
                    java.lang.reflect.Method[] methods = cls.getDeclaredMethods();
                    if (methods != null && methods.length > 0) {
                        for (java.lang.reflect.Method method : methods) {
                            Method annotation = (Method) method.getAnnotation(Method.class);
                            if (annotation != null) {
                                methodDefMap.put(annotation.name(), annotation);
                                methodMap.put(annotation.name(), method);
                                System.out.println("register api: " + apiAnnotation.name() + "." + annotation.name());
                            }
                        }
                    }

                    APIDef def = new APIDef(apiAnnotation.name(), ins, methodDefMap, methodMap);
                    apis.put(def.getName(), def);
                }
            }
        }
    }


}

class APIObj {

    public APIDef apiDef;

    public String api;

    public String method;

    public Method getMethodDef(String name) {
        return apiDef.getMethodDef(name);
    }

    public java.lang.reflect.Method getMethod(String name) {
        return apiDef.getMethod(name);
    }

}

class TypeConverter {

    static HashMap<String, Class> TYPES = new HashMap<>();
    static HashMap<String, java.lang.reflect.Method> CASTS = new HashMap<>();

    public static void init() throws NoSuchMethodException {
        TYPES.put("string", String.class);
        TYPES.put("int", int.class);
        TYPES.put("long", long.class);
        TYPES.put("float", float.class);
        TYPES.put("double", double.class);
        TYPES.put("boolean", boolean.class);

        CASTS.put("java.lang.String-int", TypeConverter.class.getDeclaredMethod("cast_string_to_int", String.class));
        CASTS.put("java.lang.String-long", TypeConverter.class.getDeclaredMethod("cast_string_to_long", String.class));
        CASTS.put("java.lang.String-float", TypeConverter.class.getDeclaredMethod("cast_string_to_float", String.class));
        CASTS.put("java.lang.String-double", TypeConverter.class.getDeclaredMethod("cast_string_to_double", String.class));
        CASTS.put("java.lang.String-boolean", TypeConverter.class.getDeclaredMethod("cast_string_to_boolean", String.class));
    }

    private static int cast_string_to_int(String val) {
        return Integer.valueOf(val);
    }

    private static long cast_string_to_long(String val) {
        return Long.valueOf(val);
    }

    private static float cast_string_to_float(String val) {
        return Float.valueOf(val);
    }

    private static double cast_string_to_double(String val) {
        return Double.valueOf(val);
    }

    private static boolean cast_string_to_boolean(String val) {
        if (val.equals("1")) {
            return true;
        } else if (val.equals("0")) {
            return false;
        }
        return Boolean.valueOf(val);
    }

    public static Object castValueAs(Object val, String typeName) throws Exception {
        Class type = TYPES.getOrDefault(typeName, null);
        if (type == null) throw new Exception("unsupported param type [" + typeName + "]");
        String srcClassName = val.getClass().getName();
        if (srcClassName.equals(type.getName())) return val;

        String castMethodKey = srcClassName + "-" + type.getName();
        java.lang.reflect.Method method = CASTS.getOrDefault(castMethodKey, null);
        if (method != null) {
            return method.invoke(null, val);
        }

        return type.cast(val);
    }
}