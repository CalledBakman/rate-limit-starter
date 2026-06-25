package com.github.calledbakman.rate_limit_starter;

import lombok.Getter;
import org.springframework.stereotype.Component;

@Getter
@Component
public class CleanUpConfig {
    private final long cleanUpInterval;
    private final long windowTime;

    public CleanUpConfig(long cleanUpInterval, long windowTime) {
        this.cleanUpInterval = cleanUpInterval;
        this.windowTime = windowTime;
    }
}
