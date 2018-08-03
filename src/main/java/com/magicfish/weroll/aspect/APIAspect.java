package com.magicfish.weroll.aspect;

import com.magicfish.weroll.net.APIAction;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class APIAspect {

    @Pointcut("execution(* com.magicfish.weroll.service.APIService.exec(..))")
    public void exec() {

    }

    @Around("exec()")
    public Object aroundExec(ProceedingJoinPoint joinPoint) throws Throwable {
        return joinPoint.proceed();
    }

    @Before("exec() && args(request)")
    public void beforeExec(JoinPoint joinPoint, APIAction request) throws Throwable {
        System.out.println("start execute api...");
    }

    @After("exec() && args(request)")
    public void afterExec(JoinPoint joinPoint, APIAction request) throws Throwable {
        System.out.println("after execute api...");
        long costTime = request.recordTime();
        System.out.println("time cost: " + costTime + " ms");
    }

}