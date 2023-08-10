package com.rhys.testSourceCode.ioc.base;

import com.rhys.testSourceCode.ioc.annotation.BeanH;
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
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(BPFTestMain.class);
        // System.out.println("---------------------------------------------------------------------------");
        BeanH beanH = (BeanH) applicationContext.getBean("beanH");
        // beanH.doH();

        // 主动触发对应事件发布和监听执行
        applicationContext.start();
        applicationContext.stop();
        applicationContext.close();
    }
}
