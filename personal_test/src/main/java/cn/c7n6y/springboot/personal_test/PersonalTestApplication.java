package cn.c7n6y.springboot.personal_test;

import cn.c7n6y.springboot.personal_test.service.EsService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;

@SpringBootApplication
@EnableJms //启动消息队列
public class PersonalTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(PersonalTestApplication.class, args);
    }

}
