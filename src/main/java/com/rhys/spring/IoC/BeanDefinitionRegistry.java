package com.rhys.spring.IoC;

import com.rhys.spring.IoC.exception.BeanDefinitionRegistryException;

/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2023/2/7 12:09 AM
 */
public interface BeanDefinitionRegistry extends AliasRegistry {

    /**
     * 注册BeanDefinition
     *
     * @param beanName bean名称
     * @param beanDefinition bean定义
     * @author Rhys.Ni
     * @date 2023/2/14
     */
    void registryBeanDefinition(String beanName, BeanDefinition beanDefinition) throws BeanDefinitionRegistryException;

    /**
     * 获取BeanDefinition
     *
     * @param beanName bean名称
     * @return com.rhys.spring.beans.BeanDefinition
     * @author Rhys.Ni
     * @date 2023/2/14
     */
    BeanDefinition getBeanDefinition(String beanName);

    /**
     * 判断是否已经存在
     *
     * @param beanName bean名称
     * @return boolean
     * @author Rhys.Ni
     * @date 2023/2/14
     */
    boolean containsBeanDefinition(String beanName);
}
