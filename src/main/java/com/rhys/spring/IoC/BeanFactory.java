package com.rhys.spring.IoC;

/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2023/2/7 12:08 AM
 */
public interface BeanFactory {
    /**
     * 获取Bean实例
     * @author Rhys.Ni
     * @date 2023/2/16
     * @param beanName bean名称
     * @return java.lang.Object
     */
    Object getBean(String beanName) throws Exception;
}
