package com.adx.agent.tools;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 邮件工具类 - 用于AI工具调用
 * 此工具类完全独立于系统其他邮件功能，不会影响系统默认的邮件配置
 */
@Component
public class SendEmailTool {

    private final ChatModel dashscopeChatModel;

    @Autowired
    public SendEmailTool(ChatModel dashscopeChatModel) {
        this.dashscopeChatModel = dashscopeChatModel;
    }

    @Tool(description = "生成邮件草稿，帮助用户润色邮件内容并生成格式化的邮件")
    public String createEmailDraft(
            @ToolParam(description = "发件人邮箱地址（支持163邮箱）") String senderEmail,
            @ToolParam(description = "收件人邮箱地址") String receiverEmail,
            @ToolParam(description = "邮件内容") String mailContent,
            @ToolParam(description = "邮箱授权码") String authCode
    ){
        try {
            System.out.println("创建邮件草稿 - 开始处理...");
            // 检查邮件信息是否完整
            if (senderEmail == null || receiverEmail == null || mailContent == null || authCode == null) {
                return "错误：邮件生成缺少必要字段。";
            }

            // 检查是否为163邮箱
            if (!senderEmail.endsWith("@163.com")) {
                return "错误：目前仅支持163邮箱作为发件人。";
            }

            // 使用大模型润色邮件内容
            System.out.println("调用AI润色邮件内容...");
            String polishedContent = polishEmailContentWithAI(mailContent);
            System.out.println("润色后的内容: " + polishedContent);
            
            // 提取主题（如果AI返回了主题）
            String subject = extractSubject(polishedContent);
            System.out.println("提取的主题: " + (subject != null ? subject : "无"));
            
            String content = removeMetadata(polishedContent);
            System.out.println("处理后的内容: " + content);

            // 生成邮件草稿
            String draft;
            if (subject != null && !subject.isEmpty()) {
                draft = String.format("邮件草稿：\n发件人：%s\n收件人：%s\n主题：%s\n\n内容：\n%s\n\n此邮件已准备好，可以使用sendEmail工具发送。",
                        senderEmail, receiverEmail, subject, content);
            } else {
                draft = String.format("邮件草稿：\n发件人：%s\n收件人：%s\n\n内容：\n%s\n\n此邮件已准备好，可以使用sendEmail工具发送。",
                        senderEmail, receiverEmail, content);
            }

            // 返回草稿
            return draft;

        } catch (Exception e) {
            System.err.println("生成邮件草稿时出错：" + e.getMessage());
            e.printStackTrace();
            return "生成邮件草稿时出错：" + e.getMessage();
        }
    }
    
    /**
     * 从AI生成的内容中提取主题
     */
    private String extractSubject(String content) {
        if (content == null) return null;
        
        // 尝试匹配"邮箱主题："或"主题："后面的内容
        Pattern pattern = Pattern.compile("(?:邮箱主题|主题)[:：]\\s*(.+?)(?:\\n|$)");
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return null;
    }
    
    /**
     * 从AI生成的内容中移除元数据（如主题、发件人等信息）
     */
    private String removeMetadata(String content) {
        if (content == null) return "";
        
        // 移除可能的元数据行
        String[] metadataPatterns = {
            "邮箱主题[:：].*\\n?", 
            "主题[:：].*\\n?",
            "发件人地址[:：].*\\n?", 
            "收件人地址[:：].*\\n?",
            "邮箱授权码[:：].*\\n?"
        };
        
        String result = content;
        for (String pattern : metadataPatterns) {
            result = result.replaceAll(pattern, "");
        }
        
        // 清理多余的空行
        result = result.replaceAll("\\n{3,}", "\n\n");
        return result.trim();
    }
    
    @Tool(description = "发送邮件到指定收件人")
    public String sendEmail(
            @ToolParam(description = "发件人邮箱地址（支持163邮箱）") String senderEmail,
            @ToolParam(description = "收件人邮箱地址") String receiverEmail,
            @ToolParam(description = "邮件主题") String subject,
            @ToolParam(description = "邮件内容") String mailContent,
            @ToolParam(description = "邮箱授权码") String authCode
    ) {
        try {
            // 检查邮件信息是否完整
            if (senderEmail == null || receiverEmail == null || mailContent == null || authCode == null) {
                return "错误：发送邮件缺少必要字段。";
            }

            // 检查是否为163邮箱
            if (!senderEmail.endsWith("@163.com")) {
                return "错误：目前仅支持163邮箱作为发件人。";
            }

            // 创建独立的邮件会话和配置，不影响系统其他邮件功能
            Session session = createIsolatedMailSession(senderEmail, authCode);

            // 创建邮件消息
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(receiverEmail));
            message.setSubject(subject);
            message.setText(mailContent);

            // 发送邮件
            Transport.send(message);

            return "邮件已成功发送至 " + receiverEmail;
        } catch (MessagingException e) {
            return "发送邮件时出错：" + e.getMessage();
        }
    }
    
    /**
     * 创建独立的邮件会话，不影响系统其他邮件配置
     * @param senderEmail 发件人邮箱
     * @param authCode 授权码
     * @return 邮件会话
     */
    private Session createIsolatedMailSession(String senderEmail, String authCode) {
        // 创建新的Properties对象，不使用System.getProperties()
        Properties props = new Properties();
        
        // 配置163邮箱SMTP服务器
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.163.com");
        props.put("mail.smtp.port", "25");
        
        // 使用匿名内部类创建认证器，不影响全局配置
        return Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, authCode);
            }
        });
    }
    
    /**
     * 使用SpringAI调用大模型来润色邮件内容
     * @param originalContent 原始邮件内容
     * @return 润色后的邮件内容
     */
    private String polishEmailContentWithAI(String originalContent) {
        try {
            System.out.println("开始调用AI润色邮件内容...");

            // 构建提示词
            String promptText = String.format(
                "请帮我根据我的原始内容来生成一封邮件内容。并且帮我总结一个主题。并且你最后的输出要有说明主题是什么。生成的格式是：发件人地址：...，收件人地址：...，邮箱主题：...，邮箱内容：...，邮箱授权码：...。输出内容严格遵循上面格式" +
                "添加适当的问候语和结束语，但不要过于冗长。并且我要邮件内容是邮件格式" +
                "原始内容：\n\n%s", originalContent);
            
            System.out.println("提示词构建完成，准备调用大模型");
            
            try {
                // 调用大模型
                System.out.println("开始调用dashscopeChatModel...");


                AssistantMessage assistantMessage = dashscopeChatModel.call(new Prompt(promptText))
                        .getResult()
                        .getOutput();
                
                if (assistantMessage == null) {
                    System.out.println("警告: 大模型返回的AssistantMessage为null，使用备用方法");
                    return polishEmailContentFallback(originalContent);
                }
                
                String polishedContent = assistantMessage.getText();
                System.out.println("大模型返回内容: " + polishedContent);
                
                // 如果返回内容为空，使用备用方法
                if (polishedContent == null || polishedContent.trim().isEmpty()) {
                    System.out.println("警告: 大模型返回内容为空，使用备用方法");
                    return polishEmailContentFallback(originalContent);
                }
                
                System.out.println("AI润色邮件内容完成");
                return polishedContent;
            } catch (Exception e) {
                System.err.println("调用大模型时出现异常: " + e.getMessage());
                e.printStackTrace();
                return polishEmailContentFallback(originalContent);
            }
            
        } catch (Exception e) {
            // 发生异常时使用备用方法
            System.err.println("使用AI润色邮件内容时出错: " + e.getMessage());
            e.printStackTrace();
            return polishEmailContentFallback(originalContent);
        }
    }
    
    /**
     * 备用的邮件内容润色方法（当AI调用失败时使用）
     */
    private String polishEmailContentFallback(String originalContent) {
        // 这里是备用的简单实现
        System.out.println("使用备用方法润色邮件内容");
        if (originalContent == null || originalContent.trim().isEmpty()) {
            return "您好，\n\n感谢您的关注。\n\n此致，\n敬礼";
        }
        
        // 添加基本的邮件格式
        if (!originalContent.startsWith("您好") && !originalContent.startsWith("尊敬的")) {
            originalContent = "您好，\n\n" + originalContent;
        }
        
        // 添加结尾敬语（如果没有）
        if (!originalContent.contains("此致") && !originalContent.contains("敬礼") && 
            !originalContent.contains("祝好") && !originalContent.contains("谢谢")) {
            originalContent = originalContent + "\n\n此致，\n敬礼";
        }
        
        return originalContent;
    }
}
