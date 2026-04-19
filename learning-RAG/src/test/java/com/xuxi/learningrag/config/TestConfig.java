package com.xuxi.learningrag.config;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * 测试配置类 - 提供 EmbeddingModel Bean
 * 使用本地 Ollama 模型，无需 API Key
 */
@TestConfiguration
public class TestConfig {

    @Value("${spring.ai.ollama.base-url:http://localhost:11434}")
    private String baseUrl;

    @Value("${spring.ai.ollama.embedding.model:nomic-embed-text}")
    private String embeddingModel;

    /**
     * 创建 Ollama Embedding Model
     * 使用本地运行的 Ollama 服务，无需 API Key
     */
    @Bean
    public EmbeddingModel embeddingModel() {
        OllamaApi ollamaApi = new OllamaApi(baseUrl);
        return OllamaEmbeddingModel.builder()
                .ollamaApi(ollamaApi)
                .defaultOptions(org.springframework.ai.ollama.api.OllamaOptions.builder()
                        .model(embeddingModel)
                        .build())
                .build();
    }
}
