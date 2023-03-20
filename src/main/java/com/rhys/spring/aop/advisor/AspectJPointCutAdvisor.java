package com.rhys.spring.aop.advisor;

import com.rhys.spring.aop.pointcut.AspectJExpressionPointCut;
import com.rhys.spring.aop.pointcut.PointCut;

/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2023/3/20 11:49 PM
 */
public class AspectJPointCutAdvisor implements PointCutAdvisor {

    private String adviceBeanName;

    private String expression;

    private AspectJExpressionPointCut aspectJExpressionPointCut;


    public AspectJPointCutAdvisor(String adviceBeanName, String expression) {
        this.adviceBeanName = adviceBeanName;
        this.expression = expression;
        this.aspectJExpressionPointCut = new AspectJExpressionPointCut(this.expression);
    }

    /**
     * 获取通知类的Bean名
     *
     * @return java.lang.String
     * @author Rhys.Ni
     * @date 2023/3/20
     */
    @Override
    public String getAdviceBeanName() {
        return this.adviceBeanName;
    }

    /**
     * 获取表达式
     *
     * @return java.lang.String
     * @author Rhys.Ni
     * @date 2023/3/20
     */
    @Override
    public String getExpression() {
        return this.expression;
    }

    /**
     * 获取切点
     *
     * @return com.rhys.spring.aop.pointcut.PointCut
     * @author Rhys.Ni
     * @date 2023/3/20
     */
    @Override
    public PointCut getPointCut() {
        return this.aspectJExpressionPointCut;
    }
}
