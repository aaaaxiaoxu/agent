package com.adx.agent.tools;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SendEmailToolTest {

    @Autowired
    private SendEmailTool sendEmailTool;  // 这里确保了 SendEmailTool 被自动注入

    @Autowired
    private ChatModel dashscopeChatModel;  // 这里确保了 ChatModel 被自动注入

    @Test
    void createEmailDraft() {
        System.out.println(dashscopeChatModel.toString());  // 确保 dashscopeChatModel 正常注入
        String senderEmail = "m19120557714@163.com";
        String receiverEmail = "1275706080@qq.com";
        String mailContent = "请你帮我写一封邮件，内容主要是祝贺老师论文发表成功，表达诚恳";
        String authCode = "AHaWkJtvKFcFYyZk";

        String result = sendEmailTool.createEmailDraft(senderEmail, receiverEmail, mailContent, authCode);
        System.out.println(result);
        Assertions.assertNotNull(result);
    }

    @Test
    void sendEmail() {

        String senderEmail = "m19120557714@163.com";
        String receiverEmail = "1275706080@qq.com";
        String mailContent = "请你帮我写一封邮件，内容主要是祝贺老师论文发表成功，表达诚恳";
        String authCode = "AHaWkJtvKFcFYyZk";
        String subject = "祝贺论文发表";

        String result = sendEmailTool.sendEmail(senderEmail, receiverEmail, mailContent, subject, authCode);
        Assertions.assertNotNull(result);
    }

}