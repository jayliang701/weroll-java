package com.magicfish.weroll.utils;

import com.magicfish.weroll.consts.ErrorCodes;
import com.magicfish.weroll.exception.ServiceException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
public class TypeConverter {

    public TypeConverter() throws NoSuchMethodException{
        TypeConverter.init();
    }

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

    public static Object castValueAs(Object val, String typeName) throws ServiceException {
        Class type = TYPES.getOrDefault(typeName, null);
        if (type == null) throw new ServiceException("unsupported param type [" + typeName + "]", ErrorCodes.REQUEST_PARAMS_INVALID);
        String srcClassName = val.getClass().getName();
        if (srcClassName.equals(type.getName())) return val;

        String castMethodKey = srcClassName + "-" + type.getName();
        java.lang.reflect.Method method = CASTS.getOrDefault(castMethodKey, null);
        if (method != null) {
            try {
                return method.invoke(null, val);
            } catch (Exception e) {
                throw new ServiceException("unsupported param type [" + typeName + "]", ErrorCodes.REQUEST_PARAMS_INVALID);
            }
        }

        return type.cast(val);
    }
}
