package com.magicfish.weroll.aspect;

import com.magicfish.weroll.annotation.Method;
import com.magicfish.weroll.utils.ParamProcessor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.util.LinkedHashMap;
import java.util.Map;

@Aspect
public class RestfulAspect {

    @Pointcut("@within(org.springframework.web.bind.annotation.RestController) && @annotation(org.springframework.web.bind.annotation.GetMapping))")
    public void exec() {

    }

    @Before("exec() && args(params)")
    public void beforeExec(JoinPoint joinPoint, Map<String,String> params) throws Throwable {
        System.out.println(joinPoint.getTarget());
    }

    @Around("exec()")
    public Object aroundExecWithServletObject(ProceedingJoinPoint joinPoint) throws Throwable {
        Object firstParam = joinPoint.getArgs()[0];
        if (!firstParam.getClass().equals(LinkedHashMap.class)) {
            return joinPoint.proceed(joinPoint.getArgs());
        }
        Map<String, Object> params = (Map) firstParam;

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        java.lang.reflect.Method method = signature.getMethod();

        Method annotation = method.getAnnotation(Method.class);
        Map<String, Object> args = ParamProcessor.checkAndReturnMap(annotation.params(), params);

        Object[] newArgs = joinPoint.getArgs();
        newArgs[0] = args;

        return joinPoint.proceed(newArgs);
    }

}