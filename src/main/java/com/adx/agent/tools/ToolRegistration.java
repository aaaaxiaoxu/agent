package com.adx.agent.tools;

import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbacks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ToolRegistration {

    @Autowired
    private SendEmailTool sendEmailTool;

    @Bean
    public ToolCallback[] allTools() {
        return ToolCallbacks.from(
            sendEmailTool
        );
    }
}
