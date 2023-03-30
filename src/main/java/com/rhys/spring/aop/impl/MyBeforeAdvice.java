package com.rhys.spring.aop.impl;

import com.rhys.spring.aop.advice.MethodBeforeAdvice;

import java.lang.reflect.Method;

/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2023/3/28 3:42 AM
 */
public class MyBeforeAdvice implements MethodBeforeAdvice {
    /**
     * <p>
     * <b>提供前置增强</b>
     * </p >
     *
     * @param method <span style="color:#e38b6b;">被增强的方法</span>
     * @param args   <span style="color:#e38b6b;">方法参数</span>
     * @param target <span style="color:#e38b6b;">方法所属对象</span>
     * @throws Throwable <span style="color:#ffcb6b;">异常类</span>
     * @author <span style="color:#4585ff;">RhysNi</span>
     * @date 2023/3/14
     * @CopyRight: <a href="https://blog.csdn.net/weixin_44977377?type=blog">倪倪N</a>
     */
    @Override
    public void before(Method method, Object[] args, Object target) throws Throwable {
        System.out.println(this + " 对" + target + "进行前置增强");
    }
}
