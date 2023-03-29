package com.rhys.spring.IoC;

import com.rhys.spring.DI.PropertyValue;
import org.apache.commons.lang.StringUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2023/2/7 12:09 AM
 */
public interface BeanDefinition {

    String SCOPE_SINGLETON = "singleton";
    String SCOPE_PROTOTYPE = "prototype";

    /**
     * 对外提供具体的Bean类
     *
     * @param
     * @return java.lang.Class<?>
     * @author Rhys.Ni
     * @date 2023/2/14
     */
    Class<?> getBeanClass();

    /**
     * 对外提供工厂bean名
     *
     * @param
     * @return java.lang.String
     * @author Rhys.Ni
     * @date 2023/2/14
     */
    String getFactoryBeanName();

    /**
     * 对外提供工厂方法名
     *
     * @param
     * @return java.lang.String
     * @author Rhys.Ni
     * @date 2023/2/14
     */
    String getFactoryMethodName();

    /**
     * 初始化方法
     *
     * @param
     * @return java.lang.String
     * @author Rhys.Ni
     * @date 2023/2/14
     */
    String getInitMethodName();

    /**
     * 销毁方法
     *
     * @param
     * @return java.lang.String
     * @author Rhys.Ni
     * @date 2023/2/14
     */
    String getDestroyMethodName();

    /**
     * 作用域
     *
     * @param
     * @return java.lang.String
     * @author Rhys.Ni
     * @date 2023/2/14
     */
    String getScope();

    /**
     * 是否是单例
     *
     * @param
     * @return boolean
     * @author Rhys.Ni
     * @date 2023/2/14
     */
    boolean isSingleton();

    /**
     * 是否是原型
     *
     * @param
     * @return boolean
     * @author Rhys.Ni
     * @date 2023/2/14
     */
    boolean isPrototype();

    /**
     * 是否是主要
     *
     * @param
     * @return boolean
     * @author Rhys.Ni
     * @date 2023/2/14
     */
    boolean isPrimary();

    /**
     * 获取构造参数对应值
     *
     * @param
     * @return java.util.List<?>
     * @author Rhys.Ni
     * @date 2023/3/8
     */
    List<?> getConstructorArgumentValues();

    /**
     * 缓存构造参数
     * @author Rhys.Ni
     * @date 2023/3/28
     * @param values
     * @return void
     */
    void setConstructorArgumentRealValues(Object[] values);

    /**
     * 获取构造参数
     * @author Rhys.Ni
     * @date 2023/3/30
     * @param
     * @return java.lang.Object[]
     */
    Object[] getConstructorArgumentRealValues();

    /**
     * 用于BeanFactory获取工厂方法
     *
     * @param
     * @return java.lang.reflect.Method
     * @author Rhys.Ni
     * @date 2023/3/13
     */
    Method getFactoryMethod();

    /**
     * 用于BeanFactory获取具体的构造函数
     *
     * @param
     * @return java.lang.reflect.Constructor<?>
     * @author Rhys.Ni
     * @date 2023/3/13
     */
    Constructor<?> getConstructor();

    /**
     * 用于BeanFactory设置具体的构造函数
     *
     * @param constructor 构造函数
     * @return void
     * @author Rhys.Ni
     * @date 2023/3/13
     */
    void setConstructor(Constructor<?> constructor);

    /**
     * 用于BeanFactory设置工厂方法
     *
     * @param factoryMethod 工厂方法
     * @return void
     * @author Rhys.Ni
     * @date 2023/3/13
     */
    void setFactoryMethod(Method factoryMethod);

    /**
     * 获取属性值
     * @author Rhys.Ni
     * @date 2023/3/14
     * @param
     * @return java.util.List<com.rhys.spring.DI.PropertyValue>
     */
    List<PropertyValue> getPropertyValues();

    /**
     * 校验bean定义的合法性
     *
     * @param
     * @return boolean
     * @author Rhys.Ni
     * @date 2023/2/7
     */
    default boolean validate() {
        //没定义class,工厂bean或工厂方法没指定的均不合法
        if (this.getBeanClass() == null) {
            if (StringUtils.isBlank(this.getFactoryBeanName()) || StringUtils.isBlank(this.getFactoryMethodName())) {
                return false;
            }
        }

        //定义了类，又定义工厂bean认为不合法，这里都不为空则满足以上条件，任意一个为空则校验通过
        return this.getBeanClass() == null || StringUtils.isBlank(this.getFactoryBeanName());
    }


}
