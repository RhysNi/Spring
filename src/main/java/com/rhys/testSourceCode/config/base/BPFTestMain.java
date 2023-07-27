package com.rhys.testSourceCode.config.base;

import com.rhys.spring.context.annotation.Bean;
import com.rhys.testSourceCode.config.annotation.BeanH;
import com.rhys.testSourceCode.extension.RhysBeanDefinitionRegistryPostProcessor1;
import com.rhys.testSourceCode.extension.RhysBeanDefinitionRegistryPostProcessor2;
import com.rhys.testSourceCode.extension.RhysBeanDefinitionRegistryPostProcessor3;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2023/7/28 3:34 AM
 */
@Configuration
@ComponentScan("com.rhys")
public class BPFTestMain {
    public static void main(String[] args) {
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(BPFTestMain.class);
        // System.out.println("---------------------------------------------------------------------------");
        // BeanH beanH = (BeanH) applicationContext.getBean("postProcessBeanDefinitionRegistry.beanH");
        // beanH.doH();
    }
}
