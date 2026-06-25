package com.github.calledbakman.rate_limit_starter;

import com.github.calledbakman.rate_limit_starter.enums.RateLimitScope;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

@Aspect
@Component
public class RateLimitingAspect {

    @Autowired
    private RateLimitingService rateLimitingService;
    @Autowired
    private CleanUpConfig config;

    private final Logger logger = LoggerFactory.getLogger(RateLimitingAspect.class);
    private ConcurrentHashMap<String, CleanUpConfig> configMap = new ConcurrentHashMap<>();

    @Around("@annotation(RateLimit)")
    public Object rateLimitAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        String methodName = method.getDeclaringClass().getName() + "." + method.getName();
        RateLimit rateLimit = method.getAnnotation(RateLimit.class);

        logger.info("Before executing method : {}", methodSignature);

        RateLimitScope scope = RateLimitScope.CLIENT;
        String key;
        assert rateLimit != null;
        int limit = rateLimit.limit();
        long timeWindowMillis = rateLimit.period().getMillis() * rateLimit.duration();

        if (!configMap.containsKey(methodName)){
            synchronized (this) {
                if (!configMap.containsKey(methodName)) {
                    CleanUpConfig cleanUpConfig = new CleanUpConfig(rateLimit.cleanUpInterval(), timeWindowMillis);
                    rateLimitingService.schedule(cleanUpConfig);
                    configMap.put(methodName, cleanUpConfig);
                }
            }
        }

        key = switch (rateLimit.key()) {
            case USER_DETAIL -> {
                KeyResolver userKeyResolver = new UserDetailKeyResolver();
                yield userKeyResolver.resolve()+ methodSignature;
            }
            case CUSTOM_CLIENT_ID -> {
                KeyResolver clientKeyResolver = new ClientCustomKeyResolver();
                yield clientKeyResolver.resolve()+ methodSignature;
            }
            case API_KEY -> {
                KeyResolver apiKeyResolver = new ApiKeyResolver();
                yield apiKeyResolver.resolve() + methodSignature;
            }
            default -> {
                scope = RateLimitScope.METHOD;
                yield  methodSignature.toString();
            }
        };

        rateLimitingService.checkRateLimit(scope, key, limit, timeWindowMillis);

        Object result = joinPoint.proceed();

        logger.info("Executing done : {}", methodSignature);

        return result;
    }
}
