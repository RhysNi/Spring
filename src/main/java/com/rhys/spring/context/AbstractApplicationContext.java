package com.rhys.spring.context;

import com.rhys.spring.IoC.BeanPostProcessor;
import com.rhys.spring.IoC.PreBuildBeanFactory;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2023/4/19 3:16 AM
 */
public abstract class AbstractApplicationContext implements ApplicationContext {
    protected PreBuildBeanFactory beanFactory;

    public AbstractApplicationContext() {
        this.beanFactory = new PreBuildBeanFactory();
    }

    /**
     * 注册Bean执行器
     *
     * @param
     * @return void
     * @author Rhys.Ni
     * @date 2023/4/19
     */
    private void doRegistryBeanPostProcessor() throws Throwable {
        List<BeanPostProcessor> beanPostProcessors = this.beanFactory.getBeanListOfType(BeanPostProcessor.class);
        if (CollectionUtils.isNotEmpty(beanPostProcessors)) {
            for (BeanPostProcessor beanPostProcessor : beanPostProcessors) {
                this.beanFactory.registerBeanPostProcessor(beanPostProcessor);
            }
        }
    }

    protected void refresh() throws Throwable {
        this.beanFactory.registerTypeMap();
        this.doRegistryBeanPostProcessor();
        this.beanFactory.instantiateSingleton();
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
        return this.beanFactory.getBean(beanName);
    }

    /**
     * 根据beanName获取Type
     *
     * @param beanName
     * @return java.lang.Class<?>
     * @author Rhys.Ni
     * @date 2023/3/1
     */
    @Override
    public Class<?> getType(String beanName) throws Exception {
        return this.beanFactory.getType(beanName);
    }

    /**
     * 根据Type获取bean实例
     *
     * @param c
     * @return T
     * @author Rhys.Ni
     * @date 2023/3/1
     */
    @Override
    public <T> T getBean(Class<T> c) throws Exception {
        return this.beanFactory.getBean(c);
    }

    /**
     * 根据Type获取bean实例
     *
     * @param c
     * @return java.util.Map<java.lang.String, T>
     * @author Rhys.Ni
     * @date 2023/3/1
     */
    @Override
    public <T> Map<String, T> getBeanOfType(Class<T> c) throws Exception {
        return this.beanFactory.getBeanOfType(c);
    }

    /**
     * 根据Type获取bean集合
     *
     * @param c
     * @return java.util.List<T>
     * @author Rhys.Ni
     * @date 2023/3/1
     */
    @Override
    public <T> List<T> getBeanListOfType(Class<T> c) throws Exception {
        return this.beanFactory.getBeanListOfType(c);
    }

    /**
     * <p>
     * <b>beanPostProcessor注册器</b>
     * </p >
     *
     * @param beanPostProcessor <span style="color:#e38b6b;">bean执行器</span>
     * @return <span style="color:#ffcb6b;"></span>
     * @throws Exception <span style="color:#ffcb6b;">异常类</span>
     * @author <span style="color:#4585ff;">RhysNi</span>
     * @date 2023/3/27
     * @CopyRight: <a href="https://blog.csdn.net/weixin_44977377?type=blog">倪倪N</a>
     */
    @Override
    public void registerBeanPostProcessor(BeanPostProcessor beanPostProcessor) {
        this.beanFactory.registerBeanPostProcessor(beanPostProcessor);
    }
}
