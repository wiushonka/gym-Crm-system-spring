package com.example.logging;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

@Component
public class LoggingInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String transactionId = MDC.get("transactionId");
        if (transactionId == null) {
            transactionId = UUID.randomUUID().toString();
            MDC.put("transactionId", transactionId);
            response.setHeader("X-Transaction-Id", transactionId);
        }

        String method = request.getMethod();
        String path = request.getRequestURI();
        String query = request.getQueryString();
        System.out.printf("TransactionId=%s Request: %s %s?%s%n", transactionId, method, path, query);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, @NotNull HttpServletResponse response, Object handler, Exception ex) throws Exception {
        String transactionId = MDC.get("transactionId");
        int status = response.getStatus();
        System.out.printf("TransactionId=%s Response: status=%d%n", transactionId, status);

        if (ex != null) {
            System.out.printf("TransactionId=%s Exception: %s%n", transactionId, ex.getMessage());
        }

        MDC.remove("transactionId");
    }
}
