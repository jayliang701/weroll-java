package com.magicfish.weroll.utils;

import java.lang.reflect.Array;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.magicfish.weroll.annotation.Param;
import com.magicfish.weroll.exception.ServiceIllegalParamException;
import com.magicfish.weroll.exception.ServiceParamRequiredException;
import com.magicfish.weroll.exception.TypeException;

/**
 * Created by Jay on 2020-03-07.
 */
public class ParamProcessor {

    @SuppressWarnings("unchecked")
    private static Object checkSingleParam(Param param, Map<String, Object> params) throws ServiceIllegalParamException {
        Object val = null;
        String name = param.name();
        if (params.containsKey(name)) {
            val = params.get(name);
            try {
                if (!TypeConverter.isMatchType(val, param.type())) {
                    throw new ServiceIllegalParamException(param, val);
                }
            } catch (TypeException e) {
                throw new ServiceIllegalParamException(param, val);
            }
        } else {
            if (param.required()) {
                throw new ServiceParamRequiredException(param);
            }
            // set default value
            try {
                val = TypeConverter.castValueAs(param.defaultValue(), param.type());
            } catch (TypeException e) {
                throw new ServiceIllegalParamException(param, val);
            }
        }
        Class<?> cls = param.classRef();
        if (val != null && !Object.class.equals(cls)) {
            if (param.type().equals("object")) {
                val = JSONObject.parseObject(JSON.toJSONString(val), cls);
            } else if (param.type().equals("array")) {
                List<Object> list = (List<Object>) val;
                Object arr = Array.newInstance(cls, list.size());
                for (int i = 0; i < list.size(); i ++) {
                    Object item = JSONObject.parseObject(JSON.toJSONString(list.get(i)), cls);
                    // list.set(i, item);
                    ((Object[]) arr)[i] = item;
                }
                val = arr;
            }
        }
        return val;
    }

    public static Map<String, Object> checkAndReturnMap(Param[] paramDef, Map<String, Object> params) throws ServiceIllegalParamException {
        Map<String, Object> args = new LinkedHashMap<>();

        for (int i = 0; i < paramDef.length; i++) {
            Param param = paramDef[i];
            Object val = checkSingleParam(param, params);
            args.put(param.name(), val);
        }
        return args;
    }

    public static Object[] checkAndReturnArray(Param[] paramDef, Map<String, Object> params, int argsLength) throws ServiceIllegalParamException {
        Object[] args = new Object[argsLength];
        for (int i = 0; i < paramDef.length; i++) {
            Param param = paramDef[i];
            Object val = checkSingleParam(param, params);
            args[i] = val;
        }
        return args;
    }

}