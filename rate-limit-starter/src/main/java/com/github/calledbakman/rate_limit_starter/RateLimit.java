package com.github.calledbakman.rate_limit_starter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RateLimit {
    //This key will be used to create a list of limit for each client, you can keep it empty to create the list for all clients
    String key() default "";

    //The limit you set that client can send to an endpoint
    int limit() default 5;

    //Period of the limitation
    PeriodType period() default PeriodType.MIN;

    //(Optional) Duration you set for period the defaults are : 1000 millis for SEC (1 Second), 60000 millis for MIN (1 Minute), 3600000 millis for HOUR (1 Hour)
    int duration();

    //(Optional) You can use this when you want to give access to an endpoint in a specific period
    String[] availableTime() default {};
}
