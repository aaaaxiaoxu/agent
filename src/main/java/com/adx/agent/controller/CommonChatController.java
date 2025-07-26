package com.adx.agent.controller;


import com.adx.agent.app.ChatApp;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/common_chat")
public class CommonChatController {

    @Resource
    private ChatApp chatApp;

    @Resource
    private ToolCallback[]  toolCallbacks;

    @Resource
    private ChatModel dashscopeChatModel;



    @GetMapping("/chat/sync")
    public String doChatWithCommonAppSync(String message, String chatId) {
        return chatApp.doChat(message, chatId);
    }

    @GetMapping(value = "/chat/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> doChatWithLoveAppSSE(String message, String chatId) {
        return chatApp.doChatByStream(message, chatId);
    }


}
