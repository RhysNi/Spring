package com.rhys.spring.demo;

import com.rhys.spring.context.AnnotationApplicationContext;
import com.rhys.spring.context.ApplicationContext;

/**
 * <p>
 * <b>功能描述</b>
 * </p >
 *
 * @author : RhysNi
 * @version : v1.0
 * @date : 2023/4/23 14:06
 * @CopyRight :　<a href="https://blog.csdn.net/weixin_44977377?type=blog">倪倪N</a>
 */
public class TestApplicationContext {
    public static void main(String[] args) throws Throwable {
        ApplicationContext applicationContext = new AnnotationApplicationContext("com.rhys.spring.demo");
        TestA testA = (TestA) applicationContext.getBean("testABean");
        testA.execute();
    }
}
