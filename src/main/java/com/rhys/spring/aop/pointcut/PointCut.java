package com.rhys.spring.aop.pointcut;

import java.lang.reflect.Method;

/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2023/3/20 4:03 AM
 */
public interface PointCut {
    /**
     * 匹配类
     *
     * @param targetClass 需要进行匹配的类型
     * @return boolean
     * @author Rhys.Ni
     * @date 2023/3/20
     */
    boolean matchClass(Class<?> targetClass);

    /**
     * 匹配方法
     *
     * @param method      需要进行匹配的方法
     * @param targetClass 需要进行匹配的类型
     * @return boolean
     * @author Rhys.Ni
     * @date 2023/3/20
     */
    boolean matchMethod(Method method, Class<?> targetClass);
}
