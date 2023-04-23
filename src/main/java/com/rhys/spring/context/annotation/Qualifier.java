package com.rhys.spring.context.annotation;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2023/4/19 3:11 AM
 */

@Target({FIELD, METHOD, PARAMETER, TYPE, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Inherited
@Documented
public @interface Qualifier {
    String value() default "";
}
