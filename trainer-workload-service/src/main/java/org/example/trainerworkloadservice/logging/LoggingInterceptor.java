package org.example.trainerworkloadservice.logging;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoggingInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String transactionId = MDC.get("transactionId");
        System.out.printf("[TransactionId=%s] Request: %s %s%n", transactionId, request.getMethod(), request.getRequestURI());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        String transactionId = MDC.get("transactionId");
        System.out.printf("[TransactionId=%s] Response: status=%d%n", transactionId, response.getStatus());
        if (ex != null) {
            System.out.printf("[TransactionId=%s] Exception: %s%n", transactionId, ex.getMessage());
        }
    }
}
