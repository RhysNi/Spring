package com.rhys.spring.aop.advisor;

import com.rhys.spring.aop.pointcut.PointCut;

/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2023/3/20 11:46 PM
 */
public interface PointCutAdvisor extends Advisor {
    /**
     * 获取切点
     * @author Rhys.Ni
     * @date 2023/3/20
     * @param
     * @return com.rhys.spring.aop.pointcut.PointCut
     */
    PointCut getPointCut();
}
