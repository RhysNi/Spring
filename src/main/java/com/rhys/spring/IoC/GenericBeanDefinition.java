package com.rhys.spring.IoC;

import com.rhys.spring.DI.PropertyValue;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2023/2/14 2:32 AM
 */
public class GenericBeanDefinition implements BeanDefinition {

    private Class<?> beanClass;

    private String scope = SCOPE_SINGLETON;

    private String factoryBeanName;

    private String factoryMethodName;

    private String initMethodName;

    private String destoryMethodName;

    private boolean primary;

    private List<?> constructorArgumentValues;

    private Constructor<?> constructor;

    private Method factoryMethod;

    private List<PropertyValue> propertyValues;

    public Object[] getRealConstructorArgumentValues() {
        return this.realConstructorArgumentValues.get();
    }

    private ThreadLocal<Object[]> realConstructorArgumentValues = new ThreadLocal<>();

    /**
     * 对外提供具体的Bean类
     *
     * @return java.lang.Class<?>
     * @author Rhys.Ni
     * @date 2023/2/14
     */
    @Override
    public Class<?> getBeanClass() {
        return this.beanClass;
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
        return this.factoryBeanName;
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
        return this.factoryMethodName;
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
        return this.initMethodName;
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
        return this.destoryMethodName;
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
        return this.scope;
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
        return SCOPE_SINGLETON.equals(this.scope);
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
        return SCOPE_PROTOTYPE.equals(this.scope);
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
        return this.primary;
    }

    /**
     * 获取构造参数对应值
     *
     * @return java.util.List<?>
     * @author Rhys.Ni
     * @date 2023/3/8
     */
    @Override
    public List<?> getConstructorArgumentValues() {
        return constructorArgumentValues;
    }

    /**
     * 缓存构造参数
     *
     * @param values 构造参数
     * @return void
     * @author Rhys.Ni
     * @date 2023/3/28
     */
    @Override
    public void setConstructorArgumentRealValues(Object[] values) {
        realConstructorArgumentValues.set(values);
    }

    /**
     * 获取参数值
     * @author Rhys.Ni
     * @date 2023/3/30
     * @param
     * @return java.lang.Object[]
     */
    @Override
    public Object[] getConstructorArgumentRealValues() {
        return realConstructorArgumentValues.get();
    }

    /**
     * 用于BeanFactory获取工厂方法
     *
     * @return java.lang.reflect.Method
     * @author Rhys.Ni
     * @date 2023/3/13
     */
    @Override
    public Method getFactoryMethod() {
        return factoryMethod;
    }

    /**
     * 用于BeanFactory获取具体的构造函数
     *
     * @return java.lang.reflect.Constructor<?>
     * @author Rhys.Ni
     * @date 2023/3/13
     */
    @Override
    public Constructor<?> getConstructor() {
        return constructor;
    }

    /**
     * 用于BeanFactory设置具体的构造函数
     *
     * @param constructor 构造函数
     * @return void
     * @author Rhys.Ni
     * @date 2023/3/13
     */
    @Override
    public void setConstructor(Constructor<?> constructor) {
        this.constructor = constructor;
    }

    /**
     * 用于BeanFactory设置工厂方法
     *
     * @param factoryMethod 工厂方法
     * @return void
     * @author Rhys.Ni
     * @date 2023/3/13
     */
    @Override
    public void setFactoryMethod(Method factoryMethod) {
        this.factoryMethod = factoryMethod;
    }

    /**
     * 获取属性值
     *
     * @return java.util.List<com.rhys.spring.DI.PropertyValue>
     * @author Rhys.Ni
     * @date 2023/3/14
     */
    @Override
    public List<PropertyValue> getPropertyValues() {
        return this.propertyValues;
    }


    public void setPropertyValues(List<PropertyValue> propertyValues) {
        this.propertyValues = propertyValues;
    }


    public void setConstructorArgumentValues(List<?> constructorArgumentValues) {
        this.constructorArgumentValues = constructorArgumentValues;
    }


    public void setBeanClass(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    public void setScope(String scope) {
        if (StringUtils.isNotBlank(scope)) {
            this.scope = scope;
        }
    }

    public void setFactoryBeanName(String factoryBeanName) {
        this.factoryBeanName = factoryBeanName;
    }

    public void setFactoryMethodName(String factoryMethodName) {
        this.factoryMethodName = factoryMethodName;
    }

    public void setInitMethodName(String initMethodName) {
        this.initMethodName = initMethodName;
    }

    public void setDestoryMethodName(String destoryMethodName) {
        this.destoryMethodName = destoryMethodName;
    }

    public void setPrimary(boolean primary) {
        this.primary = primary;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        GenericBeanDefinition that = (GenericBeanDefinition) o;

        return new EqualsBuilder()
                .append(primary, that.primary)
                .append(beanClass, that.beanClass)
                .append(scope, that.scope)
                .append(factoryBeanName, that.factoryBeanName)
                .append(factoryMethodName, that.factoryMethodName)
                .append(initMethodName, that.initMethodName)
                .append(destoryMethodName, that.destoryMethodName)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(beanClass)
                .append(scope)
                .append(factoryBeanName)
                .append(factoryMethodName)
                .append(initMethodName)
                .append(destoryMethodName)
                .append(primary)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "GenericBeanDefinition{" +
                "beanClass=" + beanClass +
                ", scope='" + scope + '\'' +
                ", factoryBeanName='" + factoryBeanName + '\'' +
                ", factoryMethodName='" + factoryMethodName + '\'' +
                ", initMethodName='" + initMethodName + '\'' +
                ", destoryMethodName='" + destoryMethodName + '\'' +
                ", primary=" + primary +
                '}';
    }


}
