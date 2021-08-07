package cn.c7n6y.springboot.personal_test.service;

import cn.c7n6y.springboot.personal_test.utils.RedisUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class RedisService {

    @Autowired
    private RedisUtils redisUtils;


    public void set() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String nowTime = sdf.format(new Date());
        System.out.println(redisUtils.set("nowTime", nowTime));
    }


    public void get(){
        System.out.println(redisUtils.get("nowTime"));
    }
}
