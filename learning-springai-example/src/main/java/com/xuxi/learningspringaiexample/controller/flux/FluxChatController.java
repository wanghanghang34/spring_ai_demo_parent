package com.xuxi.learningspringaiexample.controller.flux;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class FluxChatController {


    @Resource(name = "chatClient")
    private ChatClient chatClient;

    @Resource
    private ChatModel chatModel;

    @Resource
    private ImageModel openaiImageModel;

    @GetMapping(value = "/ai/flux", produces = "text/html;charset=UTF-8")
    public Flux<String> generation(@RequestParam("message") String message) {
        return chatClient.prompt()
                .user(message)
                .stream()
                .content();
    }

    @GetMapping(value = "/chatmodel/ai", produces = "text/html;charset=UTF-8")
    public String chatModelGeneration(@RequestParam("message") String message) {

        ChatResponse response = chatModel.call(
                new Prompt(
                        message,
                        OpenAiChatOptions.builder()
                                .model("gpt-4o")
//                                .maxTokens(150)  // Use maxTokens for non-reasoning models
                                .build()
                ));
        return response.getResult().getOutput().getText();
    }

    /**
     * 测试文生图功能
     */
    @GetMapping(value = "/ai/image", produces = "text/html;charset=UTF-8")
    public String imageGeneration(@RequestParam("message") String message) {
        System.out.println("开始生成图片");
        ImageResponse response = openaiImageModel.call(
                new ImagePrompt(message,
                        OpenAiImageOptions.builder()
                                .quality("hd")
                                .N(4)
                                .height(1024)
                                .width(1024).build())

        );
        return response.getResult().getOutput().getUrl();
    }
}
