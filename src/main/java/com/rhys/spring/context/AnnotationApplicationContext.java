package com.rhys.spring.context;


/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2023/4/19 3:29 AM
 */
public class AnnotationApplicationContext extends AbstractApplicationContext {
    public AnnotationApplicationContext(String... basePackages) throws Throwable {
        //找到所有被@Component修饰的Java类的BeanDefinition
        new ClassPathBeanDefinitionScanner(this.beanFactory).scan(basePackages);
        super.refresh();
    }
}
