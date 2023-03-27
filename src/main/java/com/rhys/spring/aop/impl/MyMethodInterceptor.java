package com.rhys.spring.aop.impl;

import com.rhys.spring.aop.advice.MethodInterceptorAdvice;

import java.lang.reflect.Method;

/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2023/3/28 3:46 AM
 */
public class MyMethodInterceptor implements MethodInterceptorAdvice {
    /**
     * <p>
     * <b>对方法进行环绕（前置、后置）增强、异常处理增强，方法实现中需调用目标方法</b>
     * </p >
     *
     * @param method <span style="color:#e38b6b;">被增强的方法</span>
     * @param args   <span style="color:#e38b6b;">方法参数</span>
     * @param target <span style="color:#e38b6b;">方法所属对象</span>
     * @return <span style="color:#ffcb6b;"> java.lang.Object</span>
     * @throws Exception <span style="color:#ffcb6b;">异常类</span>
     * @author <span style="color:#4585ff;">RhysNi</span>
     * @date 2023/3/14
     * @CopyRight: <a href="https://blog.csdn.net/weixin_44977377?type=blog">倪倪N</a>
     */
    @Override
    public Object invoke(Method method, Object[] args, Object target) throws Throwable {
        System.out.println(this + "对" + target + "进行环绕增强-方法前置增强");
        Object o = method.invoke(target, args);
        System.out.println(this + "对" + target + "进行环绕增强-方法后置增强");
        return o;
    }
}
