package com.rhys.spring.context.annotation;

import com.rhys.spring.IoC.BeanDefinition;

import java.lang.annotation.*;

/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2023/4/19 3:11 AM
 */
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Scope {
    String value() default BeanDefinition.SCOPE_SINGLETON;
}
