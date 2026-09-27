package com.fluxbank.commons_web.annotations;

import com.fluxbank.commons_web.enums.RouteFilter;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Route {

    RouteFilter[] filters() default {};

    boolean enabled() default true;

}
