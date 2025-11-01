package com.example.logging;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Component
public class OpenFeignTransactionIdInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        String transactionId = MDC.get("transactionId");
        if (transactionId != null) {
            template.header("X-Transaction-Id", transactionId);
        }
    }
}
