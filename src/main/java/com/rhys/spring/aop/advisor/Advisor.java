package com.rhys.spring.aop.advisor;

/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2023/3/20 11:45 PM
 */
public interface Advisor {
    /**
     * 获取通知类的Bean名
     * @author Rhys.Ni
     * @date 2023/3/20
     * @param
     * @return java.lang.String
     */
    String getAdviceBeanName();

    /**
     * 获取表达式
     * @author Rhys.Ni
     * @date 2023/3/20
     * @param
     * @return java.lang.String
     */
    String getExpression();
}
