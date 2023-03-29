package com.rhys.spring.aop.weaving.proxy;

import com.rhys.spring.IoC.BeanFactory;
import com.rhys.spring.aop.advisor.Advisor;
import com.rhys.spring.aop.advisor.PointCutAdvisor;
import org.apache.commons.collections4.CollectionUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2023/3/28 4:38 AM
 */
public class AopProxyUtils {

    /**
     * 对方法应用advices增强
     *
     * @param target        目标对象
     * @param method        方法
     * @param args          参数列表
     * @param matchAdvisors 用于匹配的切面缓存
     * @param proxy         代理对象
     * @param beanFactory   bean工厂
     * @return java.lang.Object
     * @author Rhys.Ni
     * @date 2023/3/30
     */
    public static Object applyAdvices(Object target, Method method, Object[] args, List<Advisor> matchAdvisors, Object proxy, BeanFactory beanFactory) throws Throwable {
        //获取对当前方法进行增强的Advice通知
        List<Object> advices = AopProxyUtils.getShouldApplyAdvices(target.getClass(), method, matchAdvisors, beanFactory);

        //如果匹配到存在增强的advice,责任链式执行增强
        if (CollectionUtils.isNotEmpty(advices)) {
            AopAdviceChainInvocation chainInvocation = new AopAdviceChainInvocation(proxy, target, method, args, advices);
            return chainInvocation.invoke();
        } else {
            return method.invoke(target, args);
        }
    }

    /**
     * 获取对当前方法进行增强的Advice通知
     *
     * @param clazz         目标对象
     * @param method        方法
     * @param matchAdvisors 用于匹配的切面缓存
     * @param beanFactory   bean工厂
     * @return java.util.List<java.lang.Object>
     * @author Rhys.Ni
     * @date 2023/3/30
     */
    private static List<Object> getShouldApplyAdvices(Class<?> clazz, Method method, List<Advisor> matchAdvisors, BeanFactory beanFactory) throws Exception {
        if (matchAdvisors.isEmpty()) {
            return null;
        }
        //缓存匹配的切面并收集返回
        List<Object> advisors = new ArrayList<>();
        for (Advisor advisor : matchAdvisors) {
            if (advisor instanceof PointCutAdvisor && ((PointCutAdvisor)advisor).getPointCut().matchMethod(method,clazz)){
                advisors.add(beanFactory.getBean(advisor.getAdviceBeanName()));
            }
        }
        return advisors;
    }
}
