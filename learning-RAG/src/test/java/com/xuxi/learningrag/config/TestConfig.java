package com.xuxi.learningrag.config;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * 测试配置类 - 提供 EmbeddingModel Bean
 */
@TestConfiguration
public class TestConfig {

    @Value("${spring.ai.openai.api-key:sk-test-placeholder}")
    private String apiKey;

    @Value("${spring.ai.openai.base-url:https://api.openai.com/v1}")
    private String baseUrl;

    /**
     * 创建 OpenAI Embedding Model
     * 注意：需要在 application.yaml 中配置 spring.ai.openai.api-key
     * 如果没有 API Key，可以使用 mock 或者跳过测试
     */
    @Bean
    public EmbeddingModel embeddingModel() {
        // 使用配置文件中的 API Key
        OpenAiApi openAiApi = new OpenAiApi(apiKey);
        
        return new OpenAiEmbeddingModel(openAiApi);
    }
}
