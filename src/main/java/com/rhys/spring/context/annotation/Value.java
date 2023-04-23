package com.rhys.spring.context.annotation;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2023/4/19 3:12 AM
 */
@Target({FIELD, METHOD, PARAMETER, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Documented
public @interface Value {
    String value();
}
