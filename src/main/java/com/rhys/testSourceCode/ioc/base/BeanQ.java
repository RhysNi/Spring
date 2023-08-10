package com.rhys.testSourceCode.ioc.base;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2023/8/8 3:39 AM
 */
@Component
public class BeanQ {

    @Autowired
    private BeanT beanT;

    public BeanQ() {
    }
}

@Component
class BeanT {

    @Autowired
    private BeanQ beanQ;

    public BeanT() {
    }
}