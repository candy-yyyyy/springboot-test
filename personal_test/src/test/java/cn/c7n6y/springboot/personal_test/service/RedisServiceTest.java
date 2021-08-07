package cn.c7n6y.springboot.personal_test.service;

import cn.c7n6y.springboot.personal_test.utils.RedisUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.SimpleDateFormat;
import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisServiceTest {

    @Autowired
    private RedisUtils redisUtils;

    @Test
    public void set() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String nowTime = sdf.format(new Date());
        System.out.println(redisUtils.set("nowTime", nowTime));
    }

    @Test
    public void get(){
        System.out.println(redisUtils.get("nowTime"));
    }
}