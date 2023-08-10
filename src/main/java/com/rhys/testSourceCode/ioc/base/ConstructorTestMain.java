package com.rhys.testSourceCode.ioc.base;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * <p>
 * <b>功能描述</b>
 * </p >
 *
 * @author : RhysNi
 * @version : v1.0
 * @date : 2023/8/7 10:36
 * @CopyRight :　<a href="https://blog.csdn.net/weixin_44977377?type=blog">倪倪N</a>
 */
public class ConstructorTestMain {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext("com.rhys.testSourceCode.config.base");
        applicationContext.getBean(BeanQ.class);
    }
}
