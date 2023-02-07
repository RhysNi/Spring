package com.rhys.spring.beans;

/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2023/2/7 12:08 AM
 */
public interface BeanFactory {
    Object getBean(String beanName) throws Exception;
}
