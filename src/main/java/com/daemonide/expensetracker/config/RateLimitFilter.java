package com.daemonide.expensetracker.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitFilter implements Filter {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    private Bucket createBucket() {
        Bandwidth limit = Bandwidth.builder()
                .capacity(5)
                .refillGreedy(5, Duration.ofMinutes(1))
                .build();

        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;

        String path = req.getRequestURI();

        if (path.startsWith("/auth/login")
                || path.startsWith("/auth/register")) {

            String ip = request.getRemoteAddr();

            Bucket bucket = buckets.computeIfAbsent(ip,
                    k -> createBucket());

            if (!bucket.tryConsume(1)) {
                HttpServletResponse res =
                        (HttpServletResponse) response;

                res.setStatus(429);
                res.getWriter().write("Too many requests");
                return;
            }
        }

        chain.doFilter(request, response);
    }
}