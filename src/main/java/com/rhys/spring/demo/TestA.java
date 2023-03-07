package com.rhys.spring.demo;

/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2023/3/8 12:21 AM
 */
public class TestA {
    private String a;
    private int b;
    private char c;
    private TestB testB;


    public TestA(String a, int b, char c, TestB testB) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.testB = testB;
    }
}
