package com.magicfish.weroll.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.magicfish.weroll.exception.TypeException;
import org.springframework.beans.BeanUtils;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;

@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
public class TypeConverter {

    public TypeConverter() throws NoSuchMethodException {
        TypeConverter.init();
    }

    static HashMap<String, Class> TYPES = new HashMap<>();
    static HashMap<String, java.lang.reflect.Method> CASTS = new HashMap<>();
    static HashMap<String, java.lang.reflect.Method> TRANSFERS = new HashMap<>();

    public static void init() throws NoSuchMethodException {
        TYPES.put("string", String.class);
        TYPES.put("int", Integer.class);
        TYPES.put("long", Long.class);
        TYPES.put("float", Float.class);
        TYPES.put("double", Double.class);
        TYPES.put("boolean", Boolean.class);
        TYPES.put("object", LinkedHashMap.class);
        TYPES.put("array", ArrayList.class);

        CASTS.put("java.lang.String-int", TypeConverter.class.getDeclaredMethod("cast_string_to_int", String.class));
        CASTS.put("java.lang.String-long", TypeConverter.class.getDeclaredMethod("cast_string_to_long", String.class));
        CASTS.put("java.lang.String-float", TypeConverter.class.getDeclaredMethod("cast_string_to_float", String.class));
        CASTS.put("java.lang.String-double", TypeConverter.class.getDeclaredMethod("cast_string_to_double", String.class));
        CASTS.put("java.lang.String-boolean", TypeConverter.class.getDeclaredMethod("cast_string_to_boolean", String.class));
        CASTS.put("java.lang.String-object", TypeConverter.class.getDeclaredMethod("cast_string_to_object", String.class));
        CASTS.put("java.lang.String-array", TypeConverter.class.getDeclaredMethod("cast_string_to_array", String.class));

        // TRANSFERS.put("object", TypeConverter.class.getDeclaredMethod("transfer_object", HashMap.class));
        // TRANSFERS.put("array", TypeConverter.class.getDeclaredMethod("cast_string_to_array", ArrayList.class));
    }

    // private static JSONObject transfer_object(HashMap<String, Object> val) {
    //     return new JSONObject(val);
    // }

    // private static ArrayList transfer_array(ArrayList<Object> val) {
    //     return val;
    // }

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

    private static JSONObject cast_string_to_object(String val) {
        return JSON.parseObject(val);
    }

    private static JSONArray cast_string_to_array(String val) {
        return JSON.parseArray(val);
    }

    public static Object castValueAs(Object val, String typeName) throws TypeException {
        CheckResult result = checkType(val, typeName);
        if (result.isMatch()) return val;

        String srcClassName = result.getClassName();

        String castMethodKey = srcClassName + "-" + typeName;
        java.lang.reflect.Method method = CASTS.getOrDefault(castMethodKey, null);
        if (method != null) {
            try {
                return method.invoke(null, val);
            } catch (Exception e) {
                throw new TypeException(typeName);
            }
        }

        return result.getType().cast(val);
    }

    public static Object castValueAs(String val, Class<?> type) throws TypeException {
        return castValueAs(val, type, BeanUtils.isSimpleValueType(type), false);
    }

    public static Object castValueAs(String val, Class<?> type, boolean isStrict) throws TypeException {
        return castValueAs(val, type, BeanUtils.isSimpleValueType(type), isStrict);
    }

    public static Object castValueAs(String val, Class<?> type, boolean isSimpleType, boolean isStrict) throws TypeException {
        Object obj = null;

        if (isSimpleType) {
            if (type.isPrimitive()) {
                try {
                    obj = castValueAs(val, type.getName());
                } catch (Exception e) {
                    throw new TypeException(type.getName());
                }
            } else {
                try {
                    obj = type.cast(val);
                } catch (Exception e1) {
                    try {
                        Method valueOf = type.getMethod("valueOf", String.class);
                        obj = valueOf.invoke(null, new Object[] { val });
                    } catch (Exception e2) {
                        throw new TypeException(type.getName());
                    }
                }
            }
        } else {
            try {
                obj = JSONObject.parseObject(val, type);
            } catch (Exception e1) {
                try {
                    obj = JSONArray.parseArray(val, type);
                } catch (Exception e2) {
                    throw new TypeException(type.getName());
                }
            }
            if (obj != null && isStrict && !type.isInstance(obj)) {
                throw new TypeException(type.getName());
            }
        }

        if (val != null && obj == null) {
            throw new TypeException(type.getName());
        }

        return obj;
    }

    // public static Object transferValue(Object val, String typeName) throws ServiceException {
    //     java.lang.reflect.Method method = TRANSFERS.getOrDefault(typeName, null);
    //     if (method != null) {
    //         try {
    //             return method.invoke(null, val);
    //         } catch (Exception e) {
    //             throw new ServiceException("can't parse param type [" + typeName + "]", ErrorCodes.REQUEST_PARAMS_INVALID);
    //         }
    //     }
    //     return val;
    // }

    public static boolean isMatchType(Object val, String typeName) throws TypeException {
        CheckResult result = checkType(val, typeName);
        return result.isMatch();
    }

    private static CheckResult checkType(Object val, String typeName) throws TypeException {
        Class type = TYPES.getOrDefault(typeName, null);
        if (type == null)
            throw new TypeException(typeName);
        String srcClassName = val.getClass().getName();
        if (srcClassName.equals(type.getName())) {
            return new CheckResult(type, srcClassName, true);
        }
        return new CheckResult(type, srcClassName, false);
    }
}

class CheckResult {
    public CheckResult(Class type, String className, boolean result) {
        this.type = type;
        this.className = className;
        this.result = result;
    }

    private Class type;

    public Class getType() {
        return type;
    }

    private String className;

    public String getClassName() {
        return className;
    }

    private boolean result = false;

    public boolean isMatch() {
        return result;
    }
}
