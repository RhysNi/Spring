package com.rhys.spring.IoC;

import org.apache.commons.lang.StringUtils;

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
     * @author Rhys.Ni
     * @date 2023/2/14
     * @param
     * @return java.lang.String
     */
    String getFactoryBeanName();

    /**
     * 对外提供工厂方法名
     * @author Rhys.Ni
     * @date 2023/2/14
     * @param
     * @return java.lang.String
     */
    String getFactoryMethodName();

    /**
     * 初始化方法
     * @author Rhys.Ni
     * @date 2023/2/14
     * @param
     * @return java.lang.String
     */
    String getInitMethodName();

    /**
     * 销毁方法
     * @author Rhys.Ni
     * @date 2023/2/14
     * @param
     * @return java.lang.String
     */
    String getDestroyMethodName();

    /**
     * 作用域
     * @author Rhys.Ni
     * @date 2023/2/14
     * @param
     * @return java.lang.String
     */
    String getScope();

    /**
     * 是否是单例
     * @author Rhys.Ni
     * @date 2023/2/14
     * @param
     * @return boolean
     */
    boolean isSingleton();

    /**
     * 是否是原型
     * @author Rhys.Ni
     * @date 2023/2/14
     * @param
     * @return boolean
     */
    boolean isPrototype();

    /**
     * 是否是主要
     * @author Rhys.Ni
     * @date 2023/2/14
     * @param
     * @return boolean
     */
    boolean isPrimary();

    /**
     * 获取构造参数对应值
     * @author Rhys.Ni
     * @date 2023/3/8
     * @param
     * @return java.util.List<?>
     */
    List<?> getConstructorArgumentValues();


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
