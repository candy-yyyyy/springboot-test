package cn.c7n6y.springboot.personal_test.controller;

import cn.c7n6y.springboot.personal_test.service.ActiveMqService;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestRest {
    @Autowired
    private ActiveMqService activeMqService;

    @RequestMapping("/send")
    public JSONObject sendMq(@RequestBody String data) {
        JSONObject rspJson = new JSONObject();
        try{
            activeMqService.send(data);
            activeMqService.sendTopic(data);
        } catch (Exception e) {
            e.printStackTrace();
            rspJson.put("respCode", "9999");
            rspJson.put("respDesc", "消息发送异常：" + e.getMessage());
            return rspJson;
        }
        rspJson.put("respCode", "0000");
        rspJson.put("respDesc", "消息发送成功");
        return rspJson;
    }
}
