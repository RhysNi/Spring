package com.rhys.testSourceCode.aop.beans;

import org.springframework.stereotype.Component;

/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2023/8/15 1:48 AM
 */
@Component
public class BeanN {

    public void execMethod(String val) {
        System.out.println("BeanN.execMethod: val:" + val);
    }

    public String serviceMethod(String name) {
        System.out.println("BeanN.serviceMethod name:" + name);
        return name;
    }
    public String serviceMTest(String name) {
        System.out.println("BeanN.serviceMTest name:" + name);
        if (!"serviceMTest".equals(name)) {
            throw new IllegalStateException("name is not equals serviceMTest, name:" + name);

        }
        return name;
    }
}
