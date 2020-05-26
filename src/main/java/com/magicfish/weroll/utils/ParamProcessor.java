package com.magicfish.weroll.utils;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.magicfish.weroll.exception.ServiceIllegalParamException;
import com.magicfish.weroll.exception.ServiceParamRequiredException;
import com.magicfish.weroll.exception.TypeException;
import com.magicfish.weroll.model.APIParamObj;
import org.springframework.beans.BeanUtils;

/**
 * Created by Jay on 2020-03-07.
 */
public class ParamProcessor {

//    @SuppressWarnings("unchecked")
//    private static Object checkSingleParam(Param param, Map<String, Object> params) throws ServiceIllegalParamException {
//        Object val = null;
//        String name = param.name();
//        if (params.containsKey(name)) {
//            val = params.get(name);
//            try {
//                if (!TypeConverter.isMatchType(val, param.type())) {
//                    throw new ServiceIllegalParamException(param, val);
//                }
//            } catch (TypeException e) {
//                throw new ServiceIllegalParamException(param, val);
//            }
//        } else {
//            if (param.required()) {
//                throw new ServiceParamRequiredException(param);
//            }
//            // set default value
//            try {
//                val = TypeConverter.castValueAs(param.defaultValue(), param.type());
//            } catch (TypeException e) {
//                throw new ServiceIllegalParamException(param, val);
//            }
//        }
//        Class<?> cls = param.classRef();
//        if (val != null && !Object.class.equals(cls)) {
//            if (param.type().equals("object")) {
//                val = JSONObject.parseObject(JSON.toJSONString(val), cls);
//            } else if (param.type().equals("array")) {
//                List<Object> list = (List<Object>) val;
//                Object arr = Array.newInstance(cls, list.size());
//                for (int i = 0; i < list.size(); i ++) {
//                    Object item = JSONObject.parseObject(JSON.toJSONString(list.get(i)), cls);
//                    // list.set(i, item);
//                    ((Object[]) arr)[i] = item;
//                }
//                val = arr;
//            }
//        }
//        return val;
//    }

    @SuppressWarnings("unchecked")
    private static Object checkSingleParam(APIParamObj param, Map<String, Object> params) throws ServiceIllegalParamException {
        Object val = null;
        String name = param.getName();
        Class<?> cls = param.getType();

        Boolean isSimpleType = BeanUtils.isSimpleValueType(cls);

        if (params.containsKey(name)) {
            try {
                val = cls.cast(params.get(name));
            } catch (Exception e1) {
                Object tmp = params.get(name);
                if (isSimpleType) {
                    try {
                        val = TypeConverter.castValueAs(tmp.toString(), cls, isSimpleType);
                    } catch (TypeException e2) {
                        throw new ServiceIllegalParamException(param.getAnnotation(), tmp);
                    }
                } else {
                    val = tmp;
                }
            }
        } else {
            if (param.getRequired()) {
                throw new ServiceParamRequiredException(param.getAnnotation());
            }
            //处理默认值
            String defaultValue = param.getDefaultValue();
            try {
                val = TypeConverter.castValueAs(defaultValue, cls, isSimpleType, true);
            } catch (TypeException e) {
                e.printStackTrace();
                return null;
            }
        }
        if (val != null && !isSimpleType && !Object.class.equals(cls)) {
            try {
                //try array mapping
                List<Object> list = (List<Object>) val;
                Object arr = Array.newInstance(cls, list.size());
                for (int i = 0; i < list.size(); i ++) {
                    Object item = JSONObject.parseObject(JSON.toJSONString(list.get(i)), cls);
                    // list.set(i, item);
                    ((Object[]) arr)[i] = item;
                }
                val = arr;
            } catch (Exception e) {
                //try object mapping
                val = JSONObject.parseObject(JSON.toJSONString(val), cls);
            }
        }
        return val;
    }

    public static Map<String, Object> checkAndReturnMap(APIParamObj[] paramDef, Map<String, Object> params) throws ServiceIllegalParamException {
        Map<String, Object> args = new LinkedHashMap<>();

        for (int i = 0; i < paramDef.length; i++) {
            APIParamObj param = paramDef[i];
            if (param == null) continue;
            Object val = checkSingleParam(param, params);
            args.put(param.getName(), val);
        }
        return args;
    }

    public static Object[] checkAndReturnArray(APIParamObj[] paramsDef, Map<String, Object> params, int argsLength) throws ServiceIllegalParamException {
        Object[] args = new Object[argsLength];
        for (int i = 0; i < paramsDef.length; i++) {
            APIParamObj param = paramsDef[i];
            if (param == null || param.getName() == null || param.getName().isEmpty()) continue;
            Object val = checkSingleParam(param, params);
            args[i] = val;
        }
        return args;
    }

}