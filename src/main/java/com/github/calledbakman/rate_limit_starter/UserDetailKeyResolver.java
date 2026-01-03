package com.github.calledbakman.rate_limit_starter;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class UserDetailKeyResolver implements KeyResolver{
    @Override
    public String resolve() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated())
            return null;

        return authentication.getName();
    }
}
