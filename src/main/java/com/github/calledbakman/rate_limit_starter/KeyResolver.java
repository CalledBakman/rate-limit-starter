package com.github.calledbakman.rate_limit_starter;

import org.springframework.stereotype.Component;

@Component
public interface KeyResolver {
    String resolve();
}
