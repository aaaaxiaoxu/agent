package com.adx.agent.app;

import com.adx.agent.advisor.MyLoggerAdvisor;
import com.adx.agent.advisor.ReReadingAdvisor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

@Component
@Slf4j
public class ChatApp {

    private final ChatClient chatClient;

    private static final String SYSTEM_PROMPT = "场景定义：主要围绕个人创业者的公司及产品宣传推广工作，通过智能化手段助力用户高效完成市场分析、内容创作、渠道管理等宣发相关任务，提升品牌知名度和产品影响力。\n" +
            "核心功能\n" +
            "市场分析：动画人物可通过调用市场数据接口，实时获取行业动态、竞争对手信息、目标用户群体特征等数据。之后对这些数据进行深度分析，识别市场趋势、潜在机会与风险，并以直观易懂的方式呈现给用户，如生成数据图表、趋势报告等。例如，当用户想了解某一产品的市场需求时，动画人物会快速收集相关数据，分析不同地区、不同人群的需求差异，并给出针对性的结论。\n" +
            "内容创作：能够根据用户的产品特点、目标受众和宣传目标，创作各类宣传内容。包括但不限于撰写吸引人的文案（如产品介绍文案、活动宣传文案等）、提供海报设计思路与元素建议、构思短视频脚本等。在创作过程中，动画人物会结合市场热点和用户偏好，不断优化内容质量。若用户对创作的内容不满意，可提出修改意见，动画人物会及时调整。\n" +
            "渠道管理：动画人物会根据产品类型、目标用户分布等因素，为用户推荐合适的宣传渠道，如社交媒体平台、行业论坛、线下活动等。同时，通过关联的渠道数据接口，跟踪各渠道的宣传效果，如曝光量、点击率、转化率等，并生成效果分析报告。用户可根据报告调整渠道策略，提高宣传效率。\n" +
            "动画人物交互方式：用户可通过语音或文字向动画人物下达指令，如 “帮我分析一下最近的市场情况”“生成一份产品宣传文案” 等。动画人物会及时响应，在执行任务过程中，会以生动的表情和动作反馈进度，如思考时的托腮动作、完成任务后的微笑等。在展示数据或内容时，会配合相应的肢体动作指向屏幕展示区域，增强用户的理解和体验。\n" +
            "关联 API 能力：市场数据接口（用于获取行业数据、竞争对手信息等）、内容创作工具接口（用于辅助文案生成、设计元素推荐等）、渠道数据接口（用于跟踪各宣传渠道的效果数据）、社交媒体平台接口（用于直接在平台发布宣传内容）。";
    public ChatApp(ChatModel dashscopeChatModel) {
        // 初始化基于内存的对话记忆
        ChatMemory chatMemory = new InMemoryChatMemory();
        chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(
                        new MessageChatMemoryAdvisor(chatMemory),
                        new MyLoggerAdvisor()
//                        new ReReadingAdvisor()
                )
                .build();
    }

    public String doChat(String message, String chatId) {
        ChatResponse response = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .call()
                .chatResponse();
        String content = response.getResult().getOutput().getText();
        log.info("content: {}", content);
        return content;
    }

    public Flux<String> doChatByStream(String message, String chatId) {
        return chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .stream()
                .content();
    }



}
