package com.xuxi.learningspringaiexample.controller.autoconfig;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

//@RestController
public class AutoConfigChatController {

    /**
     * 使用自动配置的 ChatClient.Builder
     * 在最简单的使用场景中，Spring AI 提供了 Spring Boot 自动配置，
     * 为您创建一个原型 ChatClient.Builder Bean，以便您将其注入到类中。
     * 以下是一个简单的示例，用于检索对简单用户请求的 String 响应。
     * 需要在YML中配置好OpenAI的API KEY
     *
     */
    private final ChatClient chatClient;

    public AutoConfigChatController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    @GetMapping("/ai")
    String generation(@RequestParam("message") String message) {
        return this.chatClient.prompt()
                .user(message)
                .call()
                .content();
        /*
        在这个简单示例中，用户输入设置了用户消息的内容。
         call() 方法向 AI 模型发送请求，
         而 content() 方法将 AI 模型的响应作为 String 返回。
         */
    }
}
