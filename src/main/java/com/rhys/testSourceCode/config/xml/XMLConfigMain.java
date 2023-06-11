package com.rhys.testSourceCode.config.xml;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

public class XMLConfigMain {

	public static void main(String[] args) {
		ApplicationContext context = new GenericXmlApplicationContext("classpath:com/rhys/testSourceCode/config/xml/application.xml");
		BeanF bf = context.getBean(BeanF.class);
		bf.do1();
	}
}
