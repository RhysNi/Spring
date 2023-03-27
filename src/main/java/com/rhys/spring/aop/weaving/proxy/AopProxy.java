package com.rhys.spring.aop.weaving.proxy;

/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2023/3/28 4:34 AM
 */
public interface AopProxy {
    Object getProxy();

    Object getProxy(ClassLoader classLoader);
}
