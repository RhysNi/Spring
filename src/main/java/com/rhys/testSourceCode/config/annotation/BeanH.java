package com.rhys.testSourceCode.config.annotation;

import com.rhys.testSourceCode.config.xml.BeanE;
import org.springframework.beans.factory.annotation.Autowired;

public class BeanH {

    // @Autowired
    // private BeanE be;

    public void doH() {
        System.out.println(this + " doH");
        // be.doSomething();
    }
}
