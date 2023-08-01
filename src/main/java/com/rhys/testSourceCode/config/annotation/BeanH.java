package com.rhys.testSourceCode.config.annotation;

import com.rhys.testSourceCode.config.xml.BeanE;
import org.springframework.beans.factory.annotation.Autowired;
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
