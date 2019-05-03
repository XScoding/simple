package com.xs.simple.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by xs code on 2019/3/17.
 */

@Documented
@Target(PARAMETER)
@Retention(RUNTIME)
public @interface ProgressUp {
}
