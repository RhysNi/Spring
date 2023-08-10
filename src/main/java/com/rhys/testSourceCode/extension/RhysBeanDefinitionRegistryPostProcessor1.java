package com.rhys.testSourceCode.extension;

import com.rhys.testSourceCode.ioc.annotation.BeanH;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.stereotype.Component;

/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2023/7/28 2:01 AM
 */

@Component
public class RhysBeanDefinitionRegistryPostProcessor1 implements BeanDefinitionRegistryPostProcessor {

    /**
     * Modify the application context's internal bean definition registry after its
     * standard initialization. All regular bean definitions will have been loaded,
     * but no beans will have been instantiated yet. This allows for adding further
     * bean definitions before the next post-processing phase kicks in.
     *
     * @param registry the bean definition registry used by the application context
     * @throws BeansException in case of errors
     */
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        System.out.println("执行了 - RhysBeanDefinitionRegistryPostProcessor1.postProcessBeanDefinitionRegistry()");
        RootBeanDefinition beanDefinition = new RootBeanDefinition(BeanH.class);
        registry.registerBeanDefinition("postProcessBeanDefinitionRegistry.beanH", beanDefinition);
    }

    /**
     * Modify the application context's internal bean factory after its standard
     * initialization. All bean definitions will have been loaded, but no beans
     * will have been instantiated yet. This allows for overriding or adding
     * properties even to eager-initializing beans.
     *
     * @param beanFactory the bean factory used by the application context
     * @throws BeansException in case of errors
     */
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        System.out.println("执行了 - RhysBeanDefinitionRegistryPostProcessor1.postProcessBeanFactory()");

        System.out.println("---------------------------------------------------------------------------");
        System.out.println("RhysBeanDefinitionRegistryPostProcessor1.postProcessBeanFactory.beanFactory.getBean");
        BeanH beanH = (BeanH) beanFactory.getBean("postProcessBeanDefinitionRegistry.beanH");
        beanH.doH();
    }
}
