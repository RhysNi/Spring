package com.rhys.spring.aop.weaving.proxy;

import com.rhys.spring.aop.advice.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2023/3/30 5:31 AM
 */
public class AopAdviceChainInvocation {
    private static final Log logger = LogFactory.getLog(AopAdviceChainInvocation.class);

    private static Method invokeMethod;

    static {
        try {
            invokeMethod = AopAdviceChainInvocation.class.getMethod("invoke", null);
        } catch (NoSuchMethodException | SecurityException e) {
            logger.error("AopAdviceChainInvocation Exception:{}", e);
        }
    }

    /**
     * 责任链执行索引记录
     */
    private int i = 0;
    private Object proxy;
    private Object target;
    private Method method;
    private Object[] args;
    private List<Object> advices;

    public AopAdviceChainInvocation(Object proxy, Object target, Method method, Object[] args, List<Object> advices) {
        this.proxy = proxy;
        this.target = target;
        this.method = method;
        this.args = args;
        this.advices = advices;
    }

    public Object invoke() throws Throwable {
        if (i < this.advices.size()) {
            Object advice = this.advices.get(i);
            //判断通知类型属性哪种则执行对应的增强逻辑
            if (advice instanceof MethodBeforeAdvice) {
                ((MethodBeforeAdvice) advice).before(method, args, target);
            } else if (advice instanceof MethodInterceptorAdvice) {
                //这里的方法和目标对象传的是这里的链invoke方法和链对象
                return ((MethodInterceptorAdvice) advice).invoke(invokeMethod, null, this);
            } else if (advice instanceof AfterReturnAdvice) {
                //先得到结果再进行后置增强
                Object returnValue = this.invoke();
                ((AfterReturnAdvice) advice).afterReturning(returnValue, method, args, target);
                return returnValue;
            } else if (advice instanceof AfterAdvice) {
                //最终通知增强在finally中进行
                Object returnValue = null;
                try {
                    returnValue = this.invoke();
                } finally {
                    ((AfterAdvice) advice).after(returnValue, method, args, target);
                }
                return returnValue;
            } else if (advice instanceof ThrowsAdvice) {
                //异常通知在catch中进行
                try {
                    return this.invoke();
                } catch (Exception e) {
                    ((ThrowsAdvice) advice).afterThrowing(method, args, target, e);
                }
            }
            return this.invoke();
        } else {
            return this.method.invoke(target, args);
        }
    }
}
