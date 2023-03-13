package com.rhys.spring.DI;

/**
 * 依赖注入中描述Bean依赖
 * @author Rhys.Ni
 * @version 1.0
 * @date 2023/3/8 3:27 AM
 */
public class BeanReference {
    private String beanName;

    private Class<?> type;

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public BeanReference(String beanName) {
        super();
        this.beanName = beanName;
    }

    public BeanReference(Class<?> type) {
        this.type = type;
    }
}
