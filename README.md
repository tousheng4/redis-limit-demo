# 🎯 Redis 接口限流项目

基于 Spring Boot + Redis 实现的接口限流功能。

## 功能特点

- ✅ 简单的限流注解 `@RateLimit`
- ✅ 基于 IP + 接口的分布式限流
- ✅ 可配置限流时间窗口和请求次数
- ✅ 卡通风格的前端测试页面

## 快速开始

### 1. 启动 Redis

```bash
# WSL 中启动 Redis
sudo systemctl start redis-server

# 或 docker 启动
docker run -d -p 6379:6379 redis
```

### 2. 启动项目

```bash
# IDEA 中运行 RedisDemo1Application
# 或命令行
mvn spring-boot:run
```

### 3. 访问测试页面

浏览器打开：http://localhost:8080

## 使用方法

### 1. 添加限流注解

```java
@GetMapping("/api/test")
@RateLimit(time = 60, count = 10, msg = "请求过于频繁，请稍后再试")
public Map<String, Object> test() {
    return Map.of("code", 200, "msg", "success");
}
```

### 2. 注解参数说明

| 参数 | 说明 | 默认值 |
|------|------|--------|
| `time` | 限流时间窗口（秒） | 60 |
| `count` | 允许的最大请求次数 | 100 |
| `msg` | 超限提示消息 | "请求过于频繁，请稍后再试" |

## 限流原理

```
1. 每次请求进来，拦截器检查是否有 @RateLimit 注解
2. 生成 Redis Key: rate_limit:{IP}:{URI}
3. 使用 INCR 命令递增计数
4. 如果是第一次访问，设置过期时间
5. 如果计数超过限制，返回 429 错误
```

## 项目结构

```
src/main/java/com/example/redis/
├── annotation/
│   └── RateLimit.java          # 限流注解
├── config/
│   ├── RedisConfig.java        # Redis 配置
│   └── WebConfig.java           # 拦截器注册
├── controller/
│   └── TestController.java     # 测试接口
└── interceptor/
    └── RateLimitInterceptor.java # 限流拦截器
```

## 测试接口

| 接口 | 说明 | 限流规则 |
|------|------|----------|
| `/test1` | 测试接口1 | 每分钟10次 |
| `/test2` | 测试接口2 | 每分钟5次 |
| `/normal` | 普通接口 | 不限流 |

## 依赖

- Spring Boot 4.0.2
- Spring Data Redis
- Lettuce (Redis 客户端)
- Jackson (JSON 序列化)

## 注意事项

- 确保 Redis 已启动并能访问
- 限流基于客户端 IP，请测试时注意
- 60秒后 Redis key 自动过期，计数清零
