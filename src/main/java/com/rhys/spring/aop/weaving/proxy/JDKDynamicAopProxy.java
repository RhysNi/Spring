package com.rhys.spring.aop.weaving.proxy;

import com.rhys.spring.IoC.BeanFactory;
import com.rhys.spring.aop.advisor.Advisor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2023/3/30 5:16 AM
 */
public class JDKDynamicAopProxy implements AopProxy, InvocationHandler {
    private static final Log logger = LogFactory.getLog(JDKDynamicAopProxy.class);

    private String beanName;

    private Object target;

    private List<Advisor> matchAdvisors;

    private BeanFactory beanFactory;

    public JDKDynamicAopProxy(String beanName, Object target, List<Advisor> matchAdvisors, BeanFactory beanFactory) {
        this.beanName = beanName;
        this.target = target;
        this.matchAdvisors = matchAdvisors;
        this.beanFactory = beanFactory;
    }

    @Override
    public Object getProxy() {
        return this.getProxy(target.getClass().getClassLoader());
    }

    @Override
    public Object getProxy(ClassLoader classLoader) {
        logger.info("JDK创建代理：" + target);
        return Proxy.newProxyInstance(classLoader, target.getClass().getInterfaces(), this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return AopProxyUtils.applyAdvices(target,method,args,matchAdvisors,proxy,beanFactory);
    }
}
