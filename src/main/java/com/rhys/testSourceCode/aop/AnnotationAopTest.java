package com.rhys.testSourceCode.aop;

import com.rhys.testSourceCode.aop.beans.BeanN;
import com.rhys.testSourceCode.ioc.base.BeanQ;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2023/8/15 1:55 AM
 */
public class AnnotationAopTest {
    public static void main(String[] args) {
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext("com.rhys.testSourceCode.aop");
        BeanN beanN = applicationContext.getBean(BeanN.class);
        beanN.execMethod("testVal");
        beanN.serviceMethod("serviceMethod");
        beanN.serviceMTest("serviceMTest");
    }
}
