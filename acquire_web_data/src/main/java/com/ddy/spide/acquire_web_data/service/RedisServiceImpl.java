package com.ddy.spide.acquire_web_data.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
public class RedisServiceImpl implements RedisService {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    private final String ProjectName = "AcquireWebData_";

//    @PostConstruct
    public void init() {
        LettuceConnectionFactory jedisConnectionFactory = (LettuceConnectionFactory) stringRedisTemplate.getConnectionFactory();
        jedisConnectionFactory.setDatabase(1);
        jedisConnectionFactory.resetConnection();
    }


    private RedisTemplate getRedisTemplate(){
        LettuceConnectionFactory jedisConnectionFactory = (LettuceConnectionFactory) stringRedisTemplate.getConnectionFactory();
//        System.out.println("当前所在的db："+jedisConnectionFactory.getDatabase());
        return stringRedisTemplate;
    }


    @Override
    public int setKey(String name, String value) {
        name = ProjectName + name;
        getRedisTemplate().opsForValue().set(name, value);
        return 0;
    }

    @Override
    public int setDefaultTimeKey(String name, String value) {
        return setKey(name,value,new Long(3600 * 18));
    }

    @Override
    public int setKey(String name, String value, Long timeOut) {
        name = ProjectName + name;
        if (timeOut == null) {
            timeOut = new Long(3600 * 18);
        }
        Duration duration = Duration.ofSeconds(timeOut);
        getRedisTemplate().opsForValue().set(name, value, duration);
        return 0;
    }

    @Override
    public String getKey(String name) {
        name = ProjectName + name;
        String value = (String) getRedisTemplate().opsForValue().get(name);
        value = "null".equals(value + "") ? "" : value;
        return value;
    }

    @Override
    public Object getObjKey(String name) {
        return getRedisTemplate().opsForValue().get(name);
    }

    @Override
    public Map getAll() {
        Map<String,Object> resultMap=new HashMap<String,Object>();
        Set<String> keySets =getRedisTemplate().keys("*");
        for (String keySet : keySets) {
            Object value= getObjKey(keySet);
//            System.out.println("keySet:"+keySet+"   _value:"+ value);
            resultMap.put(keySet,value);
        }
        return resultMap;
    }

    @Override
    public boolean deleteKey(String name) {
        name = ProjectName + name;
        return getRedisTemplate().delete(name);
    }

    @Override
    public void deleteAll() {
        Set<String> keySets =getRedisTemplate().keys("*");
        for (String keySet : keySets) {
            deleteKey(keySet);
        }
    }


}
