package com.adx.agent.app;

import cn.hutool.core.lang.UUID;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest()
class SendEmailChatAppTest {

    @Resource
    private SendEmailChatApp sendEmailChatApp;

    @Autowired
    private ChatModel dashscopeChatModel;  // 这里确保了 ChatModel 被自动注入

    @Test
    void doChat() {
        String chatId = UUID.randomUUID().toString();
        // 第一轮
        String message = "你好，我是程序员鱼皮";
        String answer = sendEmailChatApp.doChat(message, chatId);
        Assertions.assertNotNull(answer);
        // 第二轮
        message = "我想让另一半（编程导航）更爱我";
        answer = sendEmailChatApp.doChat(message, chatId);
        Assertions.assertNotNull(answer);
        // 第三轮
        message = "我的另一半叫什么来着？刚跟你说过，帮我回忆一下";
        answer = sendEmailChatApp.doChat(message, chatId);
        Assertions.assertNotNull(answer);
    }

    @Test
    void doChatWithTools() {
        String chatId = UUID.randomUUID().toString();
        String message = "请你帮我生成一份邮件草稿，发送方是 m19120557714@163.com ，接收方是 1275706080@qq.com， 授权码是 AHaWkJtvKFcFYyZk， 邮件大致内容是要给导师发送请假的请求，并且因为生病想请假";
        String answer = sendEmailChatApp.doChatWithTools(message, chatId);
        Assertions.assertNotNull(answer);

        message = "发送邮件";
        answer = sendEmailChatApp.doChatWithTools(message, chatId);

        Assertions.assertNotNull(answer);
    }


}