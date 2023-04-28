package com.rhys.spring.context;


import org.springframework.beans.factory.support.BeanDefinitionReader;

/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2023/4/19 3:29 AM
 */
public class XmlApplicationContext extends AbstractApplicationContext {
    private BeanDefinitionReader reader;

    public XmlApplicationContext(String... location) throws Throwable {
        // 加载解析注配置 生成BeanDefinition 并注册BeanFactory
        super.refresh();
    }
}
