package com.github.calledbakman.rate_limit_starter;

import com.github.calledbakman.rate_limit_starter.enums.KeyType;
import com.github.calledbakman.rate_limit_starter.enums.PeriodType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RateLimit {
    /**
     * This key will be used to decide client-base or method-base approach to limit. You can ignore it to be method base
     */
    KeyType key() default KeyType.METHOD_SIGN;

    /**
     * The limit you set that client can send to an endpoint
     */
    int limit() default 5;

    /**
     * Period of the limitation
     */
    PeriodType period() default PeriodType.MIN;

    /**
     * (Optional) Duration you set for the period
     * The defaults are:
     * 1000 millis for SEC (1 Second),
     * 60,000 millis for MIN (1 Minute),
     * 3,600,000 millis for HOUR (1 Hour)
     */
    int duration() default 1;

    /**
     * Cleanup Time-To-Live in minutes
     * -1 = Cleanup immediately after rate limit window passes
     * 0 = Never cleanup (use with caution)
     * >0 = Cleanup after X milliseconds of inactivity
     * Default: 60 (1 hour of inactivity)
     */
    int cleanUpInterval() default 60000;
}
