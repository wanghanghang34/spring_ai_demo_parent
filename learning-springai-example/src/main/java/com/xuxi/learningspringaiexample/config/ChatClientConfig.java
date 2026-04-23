package com.xuxi.learningspringaiexample.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientConfig {
    /*
        创建 OpenAi ChatClient 需要配置yml或者properties文件中的 spring.ai.openai 相关配置
        自动配置类需要关掉spring.ai.openai.enabled=false
     */
//    @Bean
//    public ChatClient openAiChatClient(OpenAiChatModel chatModel) {
//        return ChatClient.create(chatModel);
//    }

    @Bean
    ChatClient chatClient(ChatClient.Builder builder) {
        return builder.defaultSystem("You are a helpful assistant.")
                .build();
    }
    /*
        创建 Ollama ChatClient 需要配置yml或者properties文件中的 spring.ai.ollama 相关配置
     */
//    @Bean
//    public ChatClient ollamaChatClient(OllamaChatModel ollamaChatModel) {
//        return ChatClient.create(ollamaChatModel);
//    }
}
