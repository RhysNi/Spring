package com.rhys.spring.context.annotation;

import com.rhys.spring.IoC.BeanDefinition;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2023/4/19 3:11 AM
 */
@Target({TYPE, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Documented
public @interface Scope {
    String value() default BeanDefinition.SCOPE_SINGLETON;
}
