package com.rhys.spring.demo;

import com.rhys.spring.context.annotation.Autowired;
import com.rhys.spring.context.annotation.Component;
import com.rhys.spring.context.annotation.Value;

import java.lang.annotation.Retention;

/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2023/3/8 12:21 AM
 */
@Component("testBBean")
public class TestB {

    @Value("TestB_RhysNi")
    private String name;

    @Autowired
    public TestB(@Value("testBBean") String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
