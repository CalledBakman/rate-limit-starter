package com.github.calledbakman.rate_limit_starter;

import com.github.calledbakman.rate_limit_starter.exception.NoHttpRequestException;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class ApiKeyResolver implements KeyResolver{
    @Override
    public String resolve() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes == null)
            throw new NoHttpRequestException("API Key Resolver", "X-API-KEY");

        return attributes.getRequest().getHeader("X-API-KEY");
    }
}
