package com.rhys.testSourceCode.ioc.annotation;

import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

@Component
public class BeanH {

    // @Autowired
    // private BeanE be;

    public void doH() {
        System.out.println(this + " doH");
        // be.doSomething();
    }

    @PreDestroy
    public void destory() {
        System.out.println("销毁对象：" + this);
    }
}
