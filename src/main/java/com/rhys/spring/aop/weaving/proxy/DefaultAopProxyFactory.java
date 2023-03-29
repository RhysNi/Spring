package com.rhys.spring.aop.weaving.proxy;

import com.rhys.spring.IoC.BeanFactory;
import com.rhys.spring.aop.advisor.Advisor;

import java.util.List;

/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2023/3/28 4:30 AM
 */
public class DefaultAopProxyFactory implements AopProxyFactory {
    @Override
    public AopProxy createAopProxy(Object bean, String beanName, List<Advisor> matchAdvisors, BeanFactory beanFactory) {
        //判断用JDK动态代理还是用Cglib
        if (shouldUseJDKDynamicProxy(bean, beanName)) {
            return new JDKDynamicAopProxy(beanName, bean, matchAdvisors, beanFactory);
        } else {
            return new CglibDynamicAopProxy(beanName, bean, matchAdvisors, beanFactory);
        }
    }

    private boolean shouldUseJDKDynamicProxy(Object bean, String beanName) {
        //doProcess...后续实现
        return false;
    }
}
