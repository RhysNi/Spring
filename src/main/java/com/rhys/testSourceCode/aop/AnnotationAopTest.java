package com.rhys.testSourceCode.aop;

import com.rhys.testSourceCode.aop.beans.BeanN;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;


/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2023/8/15 1:55 AM
 */
@Configuration
@ComponentScan(basePackages = "com.rhys.testSourceCode.aop")
public class AnnotationAopTest {
    public static void main(String[] args) {
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(AnnotationAopTest.class);
        BeanN beanN = applicationContext.getBean(BeanN.class);
        beanN.execMethod("testVal");
        beanN.serviceMethod("serviceMethod");
        beanN.serviceMTest("serviceMTest");
    }
}
