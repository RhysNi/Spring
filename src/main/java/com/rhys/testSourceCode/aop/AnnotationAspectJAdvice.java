package com.rhys.testSourceCode.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;

/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2023/8/15 12:32 AM
 */
@Aspect
@Component
@EnableAspectJAutoProxy
public class AnnotationAspectJAdvice {

    @Pointcut("execution(* com.rhys.testSourceCode.aop.beans.*.exec*(..))")
    public void execMethodsPoint() {}

    @Pointcut("execution(* com.rhys.testSourceCode.aop.beans.*.service*(..))")
    public void servicesPoint() {}

    @Before("execMethodPoint() && args(val)")
    public void before(String val) {
        System.out.println("增强了 AnnotationAspectJAdvice.before方法，val=" + val);
    }

    @Around(value = "servicesPoint() && args(namem,..)", argNames = "proceedingJoinPoint,name")
    public Object around(ProceedingJoinPoint proceedingJoinPoint, String name) throws Throwable {
        System.out.println("增强了 AnnotationAspectJAdvice.around方法，name=" + name);
        System.out.println("增强了 AnnotationAspectJAdvice.around方法，环绕前-" + proceedingJoinPoint);
        Object proceed = proceedingJoinPoint.proceed();
        System.out.println("增强了 AnnotationAspectJAdvice.around方法，环绕后-" + proceedingJoinPoint);
        return proceed;
    }

    @AfterReturning(value = "servicesPoint()", returning = "val")
    public void afterReturning(Object val) {
        System.out.println("增强了 AnnotationAspectJAdvice.afterReturning方法，val=" + val);
    }

    @AfterThrowing(value = "servicesPoint()", throwing = "e")
    public void afterThrowing(JoinPoint joinPoint, Exception e) {
        System.out.println("增强了 AnnotationAspectJAdvice.afterThrowing方法，joinPoint-" + joinPoint);
        System.out.println("增强了 AnnotationAspectJAdvice.afterThrowing方法，e: " + e);
    }

    @After(value = "execMethodPoint()")
    public void after(JoinPoint joinPoint) {
        System.out.println("增强了 AnnotationAspectJAdvice.after方法，joinPoint-" + joinPoint);
    }
}
