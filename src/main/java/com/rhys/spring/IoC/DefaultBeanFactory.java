package com.rhys.spring.IoC;

import com.rhys.spring.IoC.exception.BeanDefinitionRegistryException;
import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;
import org.apache.commons.lang.StringUtils;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2023/2/16 11:19 PM
 */
public class DefaultBeanFactory implements BeanFactory, BeanDefinitionRegistry, Closeable {
    private static final Logger logger = LoggerFactory.getLogger(DefaultBeanFactory.class);

    protected Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>(256);

    protected Map<String, Object> singletonBeanMap = new ConcurrentHashMap<>(256);

    /**
     * 注册BeanDefinition
     *
     * @param beanName       bean名称
     * @param beanDefinition bean定义
     * @author Rhys.Ni
     * @date 2023/2/14
     */
    @Override
    public void registryBeanDefinition(String beanName, BeanDefinition beanDefinition) throws BeanDefinitionRegistryException {
        //入参判空
        Objects.requireNonNull(beanName, "beanName不能为空");
        Objects.requireNonNull(beanDefinition, "beanDefinition不能为空");

        //校验bean合法性  beanDefinition named a is invalid
        if (!beanDefinition.validate()) {
            throw new BeanDefinitionRegistryException("beanDefinition named " + beanName + " is invalid !");
        }

        //校验beanName是否已存在，重复则抛异常
        if (this.containsBeanDefinition(beanName)) {
            throw new BeanDefinitionRegistryException(beanName + " Already exists ! " + this.getBeanDefinition(beanName));
        }

        //存储成功注册的bean以及beanDefinition
        beanDefinitionMap.put(beanName, beanDefinition);
    }

    /**
     * 获取BeanDefinition
     *
     * @param beanName bean名称
     * @return com.rhys.spring.beans.BeanDefinition
     * @author Rhys.Ni
     * @date 2023/2/14
     */
    @Override
    public BeanDefinition getBeanDefinition(String beanName) {
        //从beanDefinitionMap中获取注册成功的bean定义
        return this.beanDefinitionMap.get(beanName);
    }

    /**
     * 判断是否已经存在
     *
     * @param beanName bean名称
     * @return boolean
     * @author Rhys.Ni
     * @date 2023/2/14
     */
    @Override
    public boolean containsBeanDefinition(String beanName) {
        //对比beanDefinitionMap中是否存在相同key
        return this.beanDefinitionMap.containsKey(beanName);
    }


    /**
     * 获取Bean实例
     *
     * @param beanName bean名称
     * @return java.lang.Object
     * @author Rhys.Ni
     * @date 2023/2/16
     */
    @Override
    public Object getBean(String beanName) throws Exception {
        return this.doGetBean(beanName);
    }

    private Object doGetBean(String beanName) throws Exception {
        //校验beanName
        Objects.requireNonNull(beanName, "beanName can not be empty !");

        //从单例Bean所存储的Map中根据beanName获取一下这个Bean实例，获取到直接返回
        Object beanInstance = singletonBeanMap.get(beanName);
        if (beanInstance != null) {
            return beanInstance;
        }

        //没获取到则根据BeanDefinition创建Bean实例并且存放到Map中
        BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
        Objects.requireNonNull(beanDefinition, "beanDefinition named " + beanName + " is invalid !");

        if (beanDefinition.isSingleton()) {
            synchronized (this.singletonBeanMap) {
                //双重检查，再次根据beanName从单例Map中尝试获取Bean实例，如果还是没有获取到再开始创建
                beanInstance = this.singletonBeanMap.get(beanName);
                if (beanInstance == null) {
                    //创建实例
                    beanInstance = createInstance(beanDefinition);
                    //存到singletonBeanMap中
                    singletonBeanMap.put(beanName, beanInstance);
                }
            }
        } else {
            //如果不要求为单例则直接创建,不用往单例BeanMap中存，所以不关心是否存在
            beanInstance = createInstance(beanDefinition);
        }
        return beanInstance;
    }


    /**
     * <p>
     * <b>创建实例</b>
     * </p >
     *
     * @param beanDefinition <span style="color:#e38b6b;">bean定义</span>
     * @return <span style="color:#ffcb6b;"> java.lang.Object</span>
     * @throws Exception <span style="color:#ffcb6b;">异常类</span>
     * @author <span style="color:#4585ff;">RhysNi</span>
     * @date 2023/2/22
     * @CopyRight: <a href="https://blog.csdn.net/weixin_44977377?type=blog">N倪倪</a>
     */
    private Object createInstance(BeanDefinition beanDefinition) throws Exception {
        //获取bean定义对应的类(类即类型)
        Class<?> beanClass = beanDefinition.getBeanClass();
        Object beanInstance = null;

        //根据beanClass中能获取到的信息来创建实例,beanClass为空则直接通过工厂bean方式创建实例
        if (beanClass != null) {
            //获取并判断工厂成员方法名是否为空，为空则根据构造方法创建实例，否则直接根据工厂静态方法创建实例
            if (StringUtils.isBlank(beanDefinition.getFactoryMethodName())) {
                beanInstance = this.createInstanceByConstructor(beanDefinition);
            } else {
                beanInstance = this.createInstanceByStaticFactoryMethod(beanDefinition);
            }
        } else {
            beanInstance = this.createInstanceByFactoryBean(beanDefinition);
        }

        //创建完实例执行初始化方法
        this.init(beanDefinition, beanInstance);

        return beanInstance;
    }

    /**
     * 工厂bean方式创建实例
     *
     * @param beanDefinition
     * @return java.lang.Object
     * @author Rhys.Ni
     * @date 2023/2/20
     */
    private Object createInstanceByFactoryBean(BeanDefinition beanDefinition) throws Exception {
        //根据工厂bean名称创建实例
        Object bean = this.doGetBean(beanDefinition.getFactoryBeanName());
        //再通过工厂bean的类获取工厂Bean成员方法名称创建最终的Bean
        Method method = bean.getClass().getMethod(beanDefinition.getFactoryBeanName(), null);
        return method.invoke(bean, null);
    }

    /**
     * 静态工厂方法
     *
     * @param beanDefinition
     * @return java.lang.Object
     * @author Rhys.Ni
     * @date 2023/2/20
     */
    private Object createInstanceByStaticFactoryMethod(BeanDefinition beanDefinition) throws Exception {
        Class<?> beanClass = beanDefinition.getBeanClass();
        Method method = beanClass.getMethod(beanDefinition.getFactoryMethodName(), null);
        return method.invoke(beanClass, null);
    }

    /**
     * 构造函数创建实例
     *
     * @param beanDefinition
     * @return java.lang.Object
     * @author Rhys.Ni
     * @date 2023/2/20
     */
    private Object createInstanceByConstructor(BeanDefinition beanDefinition) throws Exception {
        //除了InstantiationException和IllegalAccessException异常外，为了避免相关的程序使用不当可能会存在某些危险的操作从而引发安全问题，这里需要捕获一下SecurityException
        try {
            return beanDefinition.getBeanClass().newInstance();
        } catch (SecurityException exception) {
            logger.error("create instance error beanDefinition:{}, exception:{}", beanDefinition, exception);
            throw exception;
        }
    }


    /**
     * 初始化方法
     *
     * @param beanDefinition
     * @param instance
     * @return void
     * @author Rhys.Ni
     * @date 2023/2/17
     */
    private void init(BeanDefinition beanDefinition, Object instance) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if (StringUtils.isNotBlank(beanDefinition.getInitMethodName())) {
            //获取所传入实例的初始化方法名称进行调用
            Method method = instance.getClass().getMethod(beanDefinition.getInitMethodName(), null);
            method.invoke(instance, null);
        }
    }

    /**
     * Closes this stream and releases any system resources associated
     * with it. If the stream is already closed then invoking this
     * method has no effect.
     *
     * <p> As noted in {@link AutoCloseable#close()}, cases where the
     * close may fail require careful attention. It is strongly advised
     * to relinquish the underlying resources and to internally
     * <em>mark</em> the {@code Closeable} as closed, prior to throwing
     * the {@code IOException}.
     *
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void close() {
        //执行单例的销毁方法
        for (Map.Entry<String, BeanDefinition> entry : this.beanDefinitionMap.entrySet()) {
            String beanName = entry.getKey();
            BeanDefinition beanDefinition = entry.getValue();

            if (beanDefinition.isSingleton() && StringUtils.isNotBlank(beanDefinition.getDestroyMethodName())) {
                Object instance = this.singletonBeanMap.get(beanName);
                try {
                    //获取bean的销毁方法名通过invoke执行
                    Method method = instance.getClass().getMethod(beanDefinition.getDestroyMethodName(), null);
                    method.invoke(instance, null);
                } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
                    logger.error("bean destruction method named [" + beanName + "] failed to execute ! exception:{}", e);
                }
            }
        }
    }
}
