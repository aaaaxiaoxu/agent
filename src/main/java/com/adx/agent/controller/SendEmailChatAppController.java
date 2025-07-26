package com.adx.agent.controller;


import com.adx.agent.app.SendEmailChatApp;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/sendEmail_chat")
public class SendEmailChatAppController {

    @Resource
    private SendEmailChatApp sendEmailChatApp;

    @Resource
    private ToolCallback[]  toolCallbacks;

    @Resource
    private ChatModel dashscopeChatModel;

    @GetMapping("/chat/sync")
    public String doChatWithSendEmailAppSync(String message, String chatId) {
        return sendEmailChatApp.doChatWithTools(message, chatId);
    }

    @GetMapping(value = "/chat/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> doChatWithLoveAppSSE(String message, String chatId) {
        return sendEmailChatApp.doChatWithToolsStream(message, chatId);
    }
}
