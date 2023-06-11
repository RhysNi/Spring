package com.rhys.testSourceCode.config.annotation;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class AnnotationMain {

	public static void main(String[] args) {
		ApplicationContext context = new AnnotationConfigApplicationContext("com.rhys.testSourceCode.config");
		BeanG bg = context.getBean(BeanG.class);
		bg.dog();
	}
}