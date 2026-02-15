package com.example;

import com.example.redis.pojo.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

@SpringBootTest
class RedisDemo1ApplicationTests {
    @Autowired
    private StringRedisTemplate stringredisTemplate;

    @Test
    void contextLoads() {
        //redisTemplate.opsForValue().set("sex","male");
        stringredisTemplate.opsForValue().set("day","Wednesday");
        Object day = stringredisTemplate.opsForValue().get("day");
        System.out.println("day = "+day);
    }

    private static final ObjectMapper mapper = new ObjectMapper();

    @Test
    void testSaveUser(){
        //创建对象
        User user=new User("ljc",20);
        //手动序列化
        String json=mapper.writeValueAsString(user);
        //写入数据
        stringredisTemplate.opsForValue().set("user:200",json);

        //获取数据
        String jsonUser=stringredisTemplate.opsForValue().get("user:200");
        //手动反序列化
        User user1=mapper.readValue(jsonUser,User.class);
        System.out.println("user1 = "+user1);
    }

    @Test
    void testHash(){
        stringredisTemplate.opsForHash().put("user:400","name","ljc");
        stringredisTemplate.opsForHash().put("user:400","age","20");

        Map<Object,Object> entries=stringredisTemplate.opsForHash().entries("user:400");
        System.out.println("entries = "+entries);
    }
}
