package com.rhys.spring.aop.weaving.proxy;

import com.rhys.spring.IoC.BeanDefinition;
import com.rhys.spring.IoC.BeanFactory;
import com.rhys.spring.IoC.DefaultBeanFactory;
import com.rhys.spring.aop.advisor.Advisor;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2023/3/28 4:34 AM
 */
public class CglibDynamicAopProxy implements AopProxy, MethodInterceptor {
    private static final Log logger = LogFactory.getLog(CglibDynamicAopProxy.class);

    private static Enhancer enhancer = new Enhancer();

    private String beanName;

    private Object target;

    private List<Advisor> matchAdvisors;

    private BeanFactory beanFactory;

    public CglibDynamicAopProxy(String beanName, Object target, List<Advisor> matchAdvisors, BeanFactory beanFactory) {
        this.beanName = beanName;
        this.target = target;
        this.matchAdvisors = matchAdvisors;
        this.beanFactory = beanFactory;
    }

    @Override
    public Object getProxy() {
        return this.getProxy(target.getClass().getClassLoader());
    }

    @Override
    public Object getProxy(ClassLoader classLoader) {
        logger.info("Cglib创建代理：" + target);

        //获取目标对象类型
        Class<?> targetClass = this.target.getClass();
        enhancer.setSuperclass(targetClass);
        enhancer.setInterfaces(this.getClass().getInterfaces());
        enhancer.setCallback(this);

        Constructor<?> constructor = null;
        try {
            constructor = targetClass.getConstructor(new Class<?>[]{});
        } catch (NoSuchMethodException | SecurityException e) {
            //不处理继续往下走
        }

        //此时如果构造器不为空，直接创建代理对象，否则根据Bean定义获取对应参数类型和参数列表
        if (constructor!=null){
            return enhancer.create();
        }else {
            BeanDefinition beanDefinition = ((DefaultBeanFactory) beanFactory).getBeanDefinition(beanName);
            return enhancer.create(beanDefinition.getConstructor().getParameterTypes(),beanDefinition.getRealConstructorArgumentValues());
        }
    }

    @Override
    public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        return AopProxyUtils.applyAdvices(target,method,args,matchAdvisors,proxy,beanFactory);
    }
}
