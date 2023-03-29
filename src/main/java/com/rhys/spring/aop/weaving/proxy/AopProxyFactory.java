package com.rhys.spring.aop.weaving.proxy;

import com.rhys.spring.IoC.BeanFactory;
import com.rhys.spring.aop.advisor.Advisor;

import java.util.List;

/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2023/3/28 4:28 AM
 */
public interface AopProxyFactory {
   /**
    * 创建Aop代理    * @author Rhys.Ni
    * @date 2023/3/30
    * @param bean bean实例
    * @param beanName bean名称
    * @param matchAdvisors 用于匹配的切面通知缓存
    * @param beanFactory bean工厂
    * @return org.springframework.aop.framework.AopProxy
    */
    AopProxy createAopProxy(Object bean, String beanName, List<Advisor> matchAdvisors, BeanFactory beanFactory);

    /**
     * 获得默认的AopProxyFactory实例
     * @author Rhys.Ni
     * @date 2023/3/30
     * @param
     * @return com.rhys.spring.aop.weaving.proxy.AopProxyFactory
     */
    static AopProxyFactory getDefaultAopProxyFactory(){
        return new DefaultAopProxyFactory();
    }
}
