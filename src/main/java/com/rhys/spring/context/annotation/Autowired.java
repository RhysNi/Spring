package com.rhys.spring.context.annotation;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2023/4/19 3:10 AM
 */
@Target({CONSTRUCTOR, METHOD, PARAMETER, FIELD, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Documented
public @interface Autowired {
    boolean required() default true;
}
