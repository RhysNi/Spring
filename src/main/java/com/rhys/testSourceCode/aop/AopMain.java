//package com.rhys.testSourceCode.aop;
//
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.annotation.AnnotationConfigApplicationContext;
//import org.springframework.context.annotation.ComponentScan;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//@ComponentScan(basePackages = "com.rhys.testSourceCode.aop")
//public class AopMain {
//    public static void main(String[] args) {
//        ApplicationContext context = new AnnotationConfigApplicationContext(
//                AopMain.class);
//
//        BeanQ bq = context.getBean(BeanQ.class);
//        bq.do1("task1", 20);
//        System.out.println();
//
//        bq.service1("service1");
//
//        System.out.println();
//        bq.service2("ssss");
//    }
//}
