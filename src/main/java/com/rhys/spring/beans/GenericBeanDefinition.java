package com.rhys.spring.beans;

/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2023/2/14 2:32 AM
 */
public class GenericBeanDefinition implements BeanDefinition{
    /**
     * 对外提供具体的Bean类
     *
     * @return java.lang.Class<?>
     * @author Rhys.Ni
     * @date 2023/2/14
     */
    @Override
    public Class<?> getBeanClass() {
        return null;
    }

    /**
     * 对外提供工厂bean名
     *
     * @return java.lang.String
     * @author Rhys.Ni
     * @date 2023/2/14
     */
    @Override
    public String getFactoryBeanName() {
        return null;
    }

    /**
     * 对外提供工厂方法名
     *
     * @return java.lang.String
     * @author Rhys.Ni
     * @date 2023/2/14
     */
    @Override
    public String getFactoryMethodName() {
        return null;
    }

    /**
     * 初始化方法
     *
     * @return java.lang.String
     * @author Rhys.Ni
     * @date 2023/2/14
     */
    @Override
    public String getInitMethodName() {
        return null;
    }

    /**
     * 销毁方法
     *
     * @return java.lang.String
     * @author Rhys.Ni
     * @date 2023/2/14
     */
    @Override
    public String getDestroyMethodName() {
        return null;
    }

    /**
     * 作用域
     *
     * @return java.lang.String
     * @author Rhys.Ni
     * @date 2023/2/14
     */
    @Override
    public String getScope() {
        return null;
    }

    /**
     * 是否是单例
     *
     * @return boolean
     * @author Rhys.Ni
     * @date 2023/2/14
     */
    @Override
    public boolean isSingleton() {
        return false;
    }

    /**
     * 是否是原型
     *
     * @return boolean
     * @author Rhys.Ni
     * @date 2023/2/14
     */
    @Override
    public boolean isPrototype() {
        return false;
    }

    /**
     * 是否是主要
     *
     * @return boolean
     * @author Rhys.Ni
     * @date 2023/2/14
     */
    @Override
    public boolean isPrimary() {
        return false;
    }
}
