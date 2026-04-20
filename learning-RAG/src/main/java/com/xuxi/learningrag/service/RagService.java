package com.xuxi.learningrag.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.rag.preretrieval.query.transformation.RewriteQueryTransformer;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
@Service
public class RagService {


    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    public RagService(ChatClient.Builder chatClientBuilder, VectorStore vectorStore) {
        this.chatClient = chatClientBuilder.build();
        this.vectorStore = vectorStore;
    }

    public String answerQuestion(String question) {
        // 1. 使用 Builder 模式创建搜索请求，从向量数据库检索最相关的文档片段
        List<Document> similarDocs = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(question)
                        .topK(3)
                        .build()
        );

        // 2. 构建提示词上下文
        if (similarDocs == null || similarDocs.isEmpty()) {
            return "抱歉，知识库中暂无相关信息";
        }
        String context = similarDocs.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n\n"));

        // 3. 调用LLM，要求其基于上下文回答问题
        return chatClient.prompt()
                .system("请只基于以下背景信息回答问题。如果背景信息中没有答案，请说明'抱歉，知识库中暂无相关信息'。\n\n背景信息：\n" + context)
                .user(question)
                .call()
                .content();
    }

    /** 流式输出回答 */
    public void answerQuestionStream(String question, Consumer<String> chunkConsumer) {
        // 1. 向量检索
        List<Document> similarDocs = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(question)
                        .topK(1)
                        .build()
        );

        if (similarDocs == null || similarDocs.isEmpty()) {
            chunkConsumer.accept("抱歉，知识库中暂无相关信息");
            return;
        }

        String context = similarDocs.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n\n"));

        // 2. 流式调用LLM（doOnNext逐块回调，blockLast阻塞直到流结束）
        chatClient.prompt()
                .system("你是问答助手，必须遵守：\n" +
                        "1. 答案只能来自背景信息，禁止编造\n" +
                        "2. 背景信息无答案，只输出：抱歉，知识库中暂无相关信息\n" +
                        "背景信息：\n" + context)
                .user(question)
                .stream()
                .content()
                .doOnNext(chunkConsumer)
                .blockLast();
    }
}
