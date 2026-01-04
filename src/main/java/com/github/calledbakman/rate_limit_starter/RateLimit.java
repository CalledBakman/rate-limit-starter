package com.github.calledbakman.rate_limit_starter;

import com.github.calledbakman.rate_limit_starter.enums.KeyType;
import com.github.calledbakman.rate_limit_starter.enums.PeriodType;
import com.github.calledbakman.rate_limit_starter.enums.RateLimitScope;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RateLimit {
    //This key will be used to decide client-base or method-base approach to limit, you can ignore it to be method base
    KeyType key() default KeyType.METHOD_SIGN;

    //The limit you set that client can send to an endpoint
    int limit() default 5;

    //Period of the limitation
    PeriodType period() default PeriodType.MIN;

    //(Optional) Duration you set for period the defaults are : 1000 millis for SEC (1 Second), 60000 millis for MIN (1 Minute), 3600000 millis for HOUR (1 Hour)
    int duration() default 1;

    /**
     * Cleanup Time-To-Live in minutes
     * -1 = Cleanup immediately after rate limit window passes
     * 0 = Never cleanup (use with caution)
     * >0 = Cleanup after X minutes of inactivity
     * Default: 60 (1 hour of inactivity)
     */
    int cleanUpTtl() default 60;
}
