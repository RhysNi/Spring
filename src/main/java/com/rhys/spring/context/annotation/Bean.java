package com.rhys.spring.context.annotation;

import org.springframework.core.annotation.AliasFor;

/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2023/4/19 3:10 AM
 */
public @interface Bean {
    @AliasFor("value")
    String value() default "";
    @AliasFor("name")
    String name() default "";
    String initMethod() default "";
    String destroyMethod() default "";
}
