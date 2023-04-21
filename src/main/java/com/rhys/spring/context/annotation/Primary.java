package com.rhys.spring.context.annotation;

import java.lang.annotation.*;

/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2023/4/19 3:11 AM
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Primary {
}