package com.github.calledbakman.rate_limit_starter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AvailableTime {
    // if you don't fill this it will start the available time at midnight
    String from() default "00:00";

    // if you don't fill this it will end the available time one minute before the midnight
    String to() default "23:59";
}
