package com.example.redis.controller;

import com.example.redis.annotation.RateLimit;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class TestController {

    /**
     * 测试接口1：限制每分钟最多10次请求
     */
    @GetMapping("/test1")
    @RateLimit(time = 60, count = 10, msg = "接口1访问过于频繁，请60秒后再试")
    public Map<String, Object> test1() {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("msg", "接口1调用成功");
        result.put("data", "这是测试接口1");
        return result;
    }

    /**
     * 测试接口2：限制每分钟最多5次请求
     */
    @GetMapping("/test2")
    @RateLimit(time = 60, count = 5, msg = "接口2访问过于频繁，请60秒后再试")
    public Map<String, Object> test2() {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("msg", "接口2调用成功");
        result.put("data", "这是测试接口2");
        return result;
    }

    /**
     * 普通接口：没有限流
     */
    @GetMapping("/normal")
    public Map<String, Object> normal() {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("msg", "这是普通接口，没有限流");
        return result;
    }
}
