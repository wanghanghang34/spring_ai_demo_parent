//package com.xuxi.learningrag;
//
//import com.xuxi.learningrag.config.TestConfig;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.ai.document.Document;
//import org.springframework.ai.embedding.EmbeddingModel;
//import org.springframework.ai.vectorstore.SearchRequest;
//import org.springframework.ai.vectorstore.VectorStore;
//import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
//import org.springframework.context.annotation.Import;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.testcontainers.containers.PostgreSQLContainer;
//import org.testcontainers.junit.jupiter.Container;
//import org.testcontainers.junit.jupiter.Testcontainers;
//import org.testcontainers.utility.DockerImageName;
//
//import javax.sql.DataSource;
//import java.util.List;
//import java.util.Map;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
///**
// * PGVector 向量数据库测试类
// * 演示如何使用 Spring AI 与 PostgreSQL + pgvector 扩展进行交互
// * 使用 Testcontainers 自动管理 PostgreSQL 容器
// */
//@SpringBootTest
//@Testcontainers
//@Import(TestConfig.class)
//class PgVectorStoreTests {
//
//    @Container
//    @ServiceConnection
//    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
//            DockerImageName.parse("pgvector/pgvector:pg16"))
//            .withDatabaseName("testdb")
//            .withUsername("test")
//            .withPassword("test");
//
//    @Autowired
//    private EmbeddingModel embeddingModel;
//
//    @Autowired
//    private DataSource dataSource;
//
//    private VectorStore vectorStore;
//
//    /**
//     * 初始化 PGVector 向量存储
//     */
//    @BeforeEach
//    void setUp() {
//        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
//
//        // 创建 PGVector 向量存储实例
//        vectorStore = PgVectorStore.builder(jdbcTemplate, embeddingModel)
//                .dimensions(768)  // qwen3-embedding:0.6b 模型的维度是 768
//                .initializeSchema(true)  // 自动初始化数据库表结构
//                .build();
//    }
//
//    /**
//     * 测试添加文档到向量数据库
//     */
//    @Test
//    void testAddDocuments() {
//        Document doc1 = new Document(
//                "Spring AI is a framework for building AI applications with Spring.",
//                Map.of("source", "doc1", "category", "framework")
//        );
//
//        Document doc2 = new Document(
//                "PGVector is a PostgreSQL extension that enables vector similarity search.",
//                Map.of("source", "doc2", "category", "database")
//        );
//
//        Document doc3 = new Document(
//                "Machine learning models can be integrated with Java applications using Spring AI.",
//                Map.of("source", "doc3", "category", "ml")
//        );
//
//        vectorStore.add(List.of(doc1, doc2, doc3));
//
//        assertThat(vectorStore).isNotNull();
//        System.out.println("成功添加 3 个文档到 PGVector 向量数据库");
//    }
//
//    /**
//     * 测试相似度搜索功能
//     */
//    @Test
//    void testSimilaritySearch() {
//        Document doc1 = new Document(
//                "Spring AI provides abstractions for working with AI models in Spring applications.",
//                Map.of("source", "doc1")
//        );
//
//        Document doc2 = new Document(
//                "Vector databases store high-dimensional vectors for similarity search.",
//                Map.of("source", "doc2")
//        );
//
//        vectorStore.add(List.of(doc1, doc2));
//
//        SearchRequest searchRequest = SearchRequest.builder()
//                .query("What is Spring AI?")
//                .topK(2)
//                .similarityThreshold(0.5)
//                .build();
//
//        List<Document> results = vectorStore.similaritySearch(searchRequest);
//
//        assertThat(results).isNotEmpty();
//        assertThat(results.size()).isLessThanOrEqualTo(2);
//
//        System.out.println("搜索结果:");
//        results.forEach(doc ->
//            System.out.println("内容: " + doc.getContent() + ", 相似度分数: " + doc.getScore())
//        );
//    }
//
//    /**
//     * 测试删除文档功能
//     */
//    @Test
//    void testDeleteDocuments() {
//        Document doc1 = new Document(
//                "This document will be deleted later.",
//                Map.of("source", "to_delete")
//        );
//
//        Document doc2 = new Document(
//                "This document will remain.",
//                Map.of("source", "to_keep")
//        );
//
//        vectorStore.add(List.of(doc1, doc2));
//
//        List<String> ids = vectorStore.similaritySearch(
//            SearchRequest.builder().query("deleted").topK(1).build()
//        ).stream().map(Document::getId).toList();
//
//        if (!ids.isEmpty()) {
//            vectorStore.delete(ids);
//
//            List<Document> remainingResults = vectorStore.similaritySearch(
//                SearchRequest.builder().query("deleted").topK(1).build()
//            );
//
//            assertThat(remainingResults).isEmpty();
//            System.out.println("成功删除文档");
//        }
//    }
//
//    /**
//     * 测试基于元数据的过滤搜索
//     */
//    @Test
//    void testMetadataFiltering() {
//        Document doc1 = new Document(
//                "Python is great for data science and machine learning.",
//                Map.of("language", "python", "domain", "data-science")
//        );
//
//        Document doc2 = new Document(
//                "Java is excellent for enterprise applications and microservices.",
//                Map.of("language", "java", "domain", "enterprise")
//        );
//
//        Document doc3 = new Document(
//                "JavaScript is perfect for web development and frontend programming.",
//                Map.of("language", "javascript", "domain", "web")
//        );
//
//        vectorStore.add(List.of(doc1, doc2, doc3));
//
//        SearchRequest searchRequest = SearchRequest.builder()
//                .query("programming languages")
//                .topK(5)
//                .filterExpression("metadata->>'language' = 'java'")
//                .build();
//
//        List<Document> results = vectorStore.similaritySearch(searchRequest);
//
//        assertThat(results).isNotEmpty();
//        System.out.println("过滤后的搜索结果:");
//        results.forEach(doc ->
//            System.out.println("内容: " + doc.getContent() +
//                             ", 元数据: " + doc.getMetadata())
//        );
//    }
//
//    /**
//     * 测试批量操作
//     */
//    @Test
//    void testBatchOperations() {
//        List<Document> documents = List.of(
//            new Document("Document batch 1", Map.of("batch", "1")),
//            new Document("Document batch 2", Map.of("batch", "2")),
//            new Document("Document batch 3", Map.of("batch", "3")),
//            new Document("Document batch 4", Map.of("batch", "4")),
//            new Document("Document batch 5", Map.of("batch", "5"))
//        );
//
//        vectorStore.add(documents);
//
//        SearchRequest searchRequest = SearchRequest.builder()
//                .query("Document batch")
//                .topK(10)
//                .build();
//
//        List<Document> results = vectorStore.similaritySearch(searchRequest);
//
//        assertThat(results).hasSizeGreaterThanOrEqualTo(5);
//        System.out.println("批量操作结果数量: " + results.size());
//    }
//}
