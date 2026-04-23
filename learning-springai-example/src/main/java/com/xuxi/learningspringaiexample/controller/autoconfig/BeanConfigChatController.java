package com.xuxi.learningspringaiexample.controller.autoconfig;


import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BeanConfigChatController {

//    @Resource(name = "openAiChatClient")
    @Resource(name = "chatClient")
    private ChatClient chatClient;

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
