package com.example.redis.interceptor;

import com.example.redis.annotation.RateLimit;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * 限流拦截器
 * 拦截请求，检查Redis中的访问次数是否超限
 */
@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 只拦截有 @RateLimit 注解的方法
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        RateLimit rateLimit = handlerMethod.getMethodAnnotation(RateLimit.class);
        if (rateLimit == null) {
            return true;
        }

        // 获取限流配置
        int limitCount = rateLimit.count();
        int timeWindow = rateLimit.time();

        // 获取客户端IP作为限流key
        String ip = getClientIp(request);
        String uri = request.getRequestURI();
        String redisKey = "rate_limit:" + ip + ":" + uri;

        // 获取当前请求次数
        Long count = redisTemplate.opsForValue().increment(redisKey);

        // 如果是第一次访问，设置过期时间
        if (count != null && count == 1) {
            redisTemplate.expire(redisKey, Duration.ofSeconds(timeWindow));
        }

        if (count != null && count > limitCount) {
            // 超限，减少一次计数（因为刚才已经+1了）
            redisTemplate.opsForValue().decrement(redisKey);

            // 超过限流，返回错误提示
            Map<String, Object> result = new HashMap<>();
            result.put("code", 429);
            result.put("msg", rateLimit.msg());
            result.put("data", null);

            writeJson(response, result);
            return false;
        }

        return true;
    }

    /**
     * 获取客户端IP地址
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多个代理时取第一个
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    /**
     * 响应JSON数据
     */
    private void writeJson(HttpServletResponse response, Map<String, Object> result) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        PrintWriter writer = response.getWriter();
        writer.write("{\"code\":429,\"msg\":\"" + result.get("msg") + "\",\"data\":null}");
        writer.flush();
    }
}
