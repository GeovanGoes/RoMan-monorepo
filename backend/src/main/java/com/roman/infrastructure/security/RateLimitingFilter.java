package com.roman.infrastructure.security;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RateLimitingFilter implements Filter {

    private static final int CAPACITY = 10;
    private static final long REFILL_INTERVAL_MS = 60_000L;

    private final Map<String, TokenBucket> buckets = new ConcurrentHashMap<>();

    private boolean tryConsume(String key) {
        TokenBucket bucket = buckets.computeIfAbsent(key, k -> new TokenBucket(CAPACITY, REFILL_INTERVAL_MS));
        return bucket.tryConsume();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String uri = httpRequest.getRequestURI();

        if (uri.startsWith("/api/v1/auth/")) {
            String ip = getClientIp(httpRequest);
            if (!tryConsume(ip)) {
                HttpServletResponse httpResponse = (HttpServletResponse) response;
                httpResponse.setStatus(429);
                httpResponse.setContentType("application/json");
                httpResponse.getWriter().write("{\"detail\":\"Muitas tentativas. Tente novamente em instantes.\"}");
                return;
            }
        }

        chain.doFilter(request, response);
    }

    private String getClientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isEmpty()) return xff.split(",")[0].trim();
        return request.getRemoteAddr();
    }

    private static final class TokenBucket {
        private final int capacity;
        private final long refillIntervalMs;
        private final AtomicInteger tokens;
        private volatile long lastRefillTime;

        TokenBucket(int capacity, long refillIntervalMs) {
            this.capacity = capacity;
            this.refillIntervalMs = refillIntervalMs;
            this.tokens = new AtomicInteger(capacity);
            this.lastRefillTime = System.currentTimeMillis();
        }

        boolean tryConsume() {
            refillIfNeeded();
            int current;
            do {
                current = tokens.get();
                if (current <= 0) return false;
            } while (!tokens.compareAndSet(current, current - 1));
            return true;
        }

        private void refillIfNeeded() {
            long now = System.currentTimeMillis();
            if (now - lastRefillTime >= refillIntervalMs) {
                synchronized (this) {
                    if (now - lastRefillTime >= refillIntervalMs) {
                        tokens.set(capacity);
                        lastRefillTime = now;
                    }
                }
            }
        }
    }
}
