package com.github.calledbakman.rate_limit_starter;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Aspect
@Component
public class AvailableTimeAspect {

    @Autowired
    private AvailableTimeService availableTimeService;

    private final Logger logger = LoggerFactory.getLogger(AvailableTimeAspect.class);

    @Around("@annotation(AvailableTime)")
    public Object availableTimeAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        AvailableTime availableTime = method.getAnnotation(AvailableTime.class);

        logger.info("Before executing method : {}", methodSignature);

        List<LocalTime> timeList = validateTime(availableTime.from(), availableTime.to());

        availableTimeService.checkAvailability(timeList.get(0), timeList.get(1));

        Object result = joinPoint.proceed();

        logger.info("Executing done : {}", methodSignature);

        return result;
    }

    private List<LocalTime> validateTime(String from, String to) {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        List<LocalTime> timeList = new ArrayList<>();

        try {
            logger.info("Checking the Starting time : {}", from);
            timeList.add(LocalTime.parse(from, timeFormatter));
            logger.info("Checking the Ending time : {}", to);
            timeList.add(LocalTime.parse(to, timeFormatter));
            return timeList;
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Time should be in HH:mm format!");
        }
    }
}
