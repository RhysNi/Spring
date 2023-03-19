package com.rhys.spring.aop.pointcut;

import org.aspectj.weaver.tools.PointcutExpression;
import org.aspectj.weaver.tools.PointcutParser;
import org.aspectj.weaver.tools.ShadowMatch;

import java.lang.reflect.Method;

/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2023/3/20 4:07 AM
 */
public class AspectJExpressionPointCut implements PointCut {
    /**
     * 切点解析器
     */
    public static PointcutParser pointcutParser = PointcutParser.getPointcutParserSupportingAllPrimitivesAndUsingContextClassloaderForResolution();

    private PointcutExpression pointcutExpression;

    private String expression;

    public AspectJExpressionPointCut(String expression) {
        this.pointcutExpression = pointcutParser.parsePointcutExpression(expression);
        this.expression = expression;
    }

    public String getExpression() {
        return expression;
    }

    /**
     * 匹配类
     *
     * @param targetClass 需要进行匹配的类型
     * @return boolean
     * @author Rhys.Ni
     * @date 2023/3/20
     */
    @Override
    public boolean matchClass(Class<?> targetClass) {
        return pointcutExpression.couldMatchJoinPointsInType(targetClass);
    }

    /**
     * 匹配方法
     *
     * @param method      需要进行匹配的方法
     * @param targetClass 需要进行匹配的类型
     * @return boolean
     * @author Rhys.Ni
     * @date 2023/3/20
     */
    @Override
    public boolean matchMethod(Method method, Class<?> targetClass) {
        ShadowMatch shadowMatch = pointcutExpression.matchesMethodExecution(method);
        return shadowMatch.alwaysMatches();
    }
}
