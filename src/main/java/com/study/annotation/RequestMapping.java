package com.study.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 用于获得注解中的内容
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestMapping {
    String value();
}
