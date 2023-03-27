package com.rhys.spring.aop.weaving;

import com.rhys.spring.IoC.BeanFactory;
import com.rhys.spring.IoC.BeanFactoryAware;
import com.rhys.spring.IoC.BeanPostProcessor;
import com.rhys.spring.aop.advisor.Advisor;
import com.rhys.spring.aop.advisor.PointCutAdvisor;
import com.rhys.spring.aop.pointcut.PointCut;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.*;

/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2023/3/28 12:36 AM
 */
public class AdvisorAutoProxyCreator implements BeanPostProcessor, BeanFactoryAware {
    private BeanFactory beanFactory;

    private List<Advisor> advisorList;

    private volatile boolean getAllAdvisors = false;

    /**
     * <p>
     * <b>初始化之后执行</b>
     * </p >
     *
     * @param bean     <span style="color:#e38b6b;">bean实例</span>
     * @param beanName <span style="color:#e38b6b;">bean名称</span>
     * @return <span style="color:#ffcb6b;"> java.lang.Object</span>
     * @throws Exception <span style="color:#ffcb6b;">异常类</span>
     * @author <span style="color:#4585ff;">RhysNi</span>
     * @date 2023/3/27
     * @CopyRight: <a href="https://blog.csdn.net/weixin_44977377?type=blog">倪倪N</a>
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws Exception {
        //先判断Bean是否需要增强
        List<Advisor> advisors = getMatchedAdvisors(bean, beanName);

        //如果有匹配的切面，则代表需要创建代理实例实现增强
        if (CollectionUtils.isNotEmpty(advisors)) {
            bean = this.createProxy(bean, beanName, advisors);
        }

        return bean;
    }

    /**
     * 创建代理实例
     *
     * @param bean 原始bean实例
     * @param beanName bean名称
     * @param advisors 切面通知者
     * @return java.lang.Object
     * @author Rhys.Ni
     * @date 2023/3/28
     */
    private Object createProxy(Object bean, String beanName, List<Advisor> advisors) {
        //doProcess...
        return bean;
    }

    /**
     * 匹配通知者
     *
     * @param bean     bean实例
     * @param beanName bean名称
     * @return java.util.List<com.rhys.spring.aop.advisor.Advisor>
     * @author Rhys.Ni
     * @date 2023/3/28
     */
    private List<Advisor> getMatchedAdvisors(Object bean, String beanName) throws Exception {
        //第一次执行时，先从BeanFactory中得到用户配置的所有通知者
        if (!getAllAdvisors) {
            synchronized (this) {
                if (!getAllAdvisors) {
                    //获取所有通知者并改getAllAdvisors状态为true代表获取过了所有通知者
                    advisorList = this.beanFactory.getBeanListOfType(Advisor.class);
                    getAllAdvisors = true;
                }
            }
        }

        //如果没有获取到切面配置直接返回
        if (CollectionUtils.isEmpty(advisorList)) {
            return null;
        }

        //存在切面配置则获取BeanClass和其对应的所有方法
        Class<?> beanClass = bean.getClass();
        List<Method> methods = this.getAllMethodForClass(beanClass);

        //存放匹配上的通知者
        List<Advisor> matchedAdvisors = new ArrayList<>();

        //匹配Advisor
        for (Advisor advisor : this.advisorList) {
            if (advisor instanceof PointCutAdvisor && isPointCutMatchBean((PointCutAdvisor) advisor, beanClass, methods)) {
                matchedAdvisors.add(advisor);
            }
        }

        return matchedAdvisors;
    }

    /**
     * 切入点Bean是否匹配
     *
     * @param advisor   切入点通知者
     * @param beanClass bean类型
     * @param methods   beanClass的本类以及所有实现接口的方法
     * @return boolean
     * @author Rhys.Ni
     * @date 2023/3/28
     */
    private boolean isPointCutMatchBean(PointCutAdvisor advisor, Class<?> beanClass, List<Method> methods) {
        PointCut pointCut = advisor.getPointCut();
        //先判断类是否匹配
        if (!pointCut.matchClass(beanClass)) {
            return false;
        }
        //再判断方法是否匹配
        for (Method method : methods) {
            if (!pointCut.matchMethod(method, beanClass)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 获取类的所有方法
     *
     * @param beanClass bean类型
     * @return java.util.List<java.lang.reflect.Method>
     * @author Rhys.Ni
     * @date 2023/3/28
     */
    private List<Method> getAllMethodForClass(Class<?> beanClass) {
        //获取本类以及所实现接口的方法
        LinkedList<Method> methods = new LinkedList<>();
        //获取类的所有接口
        Set<Class<?>> classes = new LinkedHashSet<>(ClassUtils.getAllInterfacesForClassAsSet(beanClass));
        //将本类一并添加进集合中进行方法收集操作
        classes.add(beanClass);
        for (Class<?> clazz : classes) {
            //获取所有已声明的方法
            Method[] allDeclaredMethods = ReflectionUtils.getAllDeclaredMethods(clazz);
            methods.addAll(Arrays.asList(allDeclaredMethods));
        }
        return methods;
    }

    /**
     * <p>
     * <b>设置Bean工厂</b>
     * </p >
     *
     * @param beanFactory <span style="color:#e38b6b;">字段描述</span>
     * @return <span style="color:#ffcb6b;"></span>
     * @throws Exception <span style="color:#ffcb6b;">异常类</span>
     * @author <span style="color:#4585ff;">RhysNi</span>
     * @date 2023/3/27
     * @CopyRight: <a href="https://blog.csdn.net/weixin_44977377?type=blog">倪倪N</a>
     */
    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }
}
