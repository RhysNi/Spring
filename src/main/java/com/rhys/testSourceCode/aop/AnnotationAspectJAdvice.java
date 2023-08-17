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

    /**
     * 定义exec前缀方法切点
     *
     * @return void
     * @author Rhys.Ni
     * @date 2023/8/18
     */
    @Pointcut("execution(* com.rhys.testSourceCode.aop.beans .*.exec*(..))")
    public void execMethodsPoint() {
    }

    /**
     * 定义service前缀方法切点
     *
     * @return void
     * @author Rhys.Ni
     * @date 2023/8/18
     */
    @Pointcut("execution(* com.rhys.testSourceCode.aop.beans.*.service*(..))")
    public void servicesPoint() {
    }

    /**
     * 定义一个方法前置增强
     *
     * @return void
     * @author Rhys.Ni
     * @date 2023/8/18
     */
    @Before("execMethodsPoint() && args(val,..)")
    public void before(String val) {
        System.out.println("AnnotationAspectJAdvice.before对以下方法做了前置增强，传入值val=" + val);
    }

    /**
     * 定义一个环绕增强
     *
     * @return void
     * @author Rhys.Ni
     * @date 2023/8/18
     */
    @Around("servicesPoint() && args(name,..)")
    public Object around(ProceedingJoinPoint pjp, String name) throws Throwable {
        System.out.println("AnnotationAspectJAdvice.around，传入值name=" + name);
        System.out.println("AnnotationAspectJAdvice.around，对 " + pjp + "做了环绕前增强");
        Object ret = pjp.proceed();
        System.out.println("AnnotationAspectJAdvice.around，对 " + pjp + "做了环绕后增强");
        return ret;
    }

    /**
     * 定义一个方法后置增强
     *
     * @return void
     * @author Rhys.Ni
     * @date 2023/8/18
     */
    @AfterReturning(pointcut = "servicesPoint()", returning = "val")
    public void afterReturning(Object val) {
        System.out.println("AnnotationAspectJAdvice.afterReturning， 对以上方法做了增强 传入值val=" + val);
    }

    /**
     * 定义一个异常通知增强
     *
     * @return void
     * @author Rhys.Ni
     * @date 2023/8/18
     */
    @AfterThrowing(pointcut = "servicesPoint()", throwing = "e")
    public void afterThrowing(JoinPoint jp, Exception e) {
        System.out.println("AnnotationAspectJAdvice.afterThrowing，对 joinPoint-" + jp + "做了增强");
        System.out.println("AnnotationAspectJAdvice.afterThrowing，对 异常做了增强 e: " + e);
    }

    /**
     * 定义一个最终通知增强
     *
     * @return void
     * @author Rhys.Ni
     * @date 2023/8/18
     */
    @After("execMethodsPoint()")
    public void after(JoinPoint jp) {
        System.out.println("AnnotationAspectJAdvice.after，对 joinPoint-" + jp + "做了增强");
    }
}
