package com.rhys.spring.context.annotation;

import java.lang.annotation.*;

/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2023/4/19 3:12 AM
 */
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Value {
    String value();
}
