package com.xuxi.learningrag;

import com.xuxi.learningrag.config.TestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * PGVector 向量数据库测试类
 * 演示如何使用 Spring AI 与 PostgreSQL + pgvector 扩展进行交互
 */
@SpringBootTest
@Import(TestConfig.class)
class PgVectorStoreTests {

    @Autowired
    private EmbeddingModel embeddingModel;

    @Autowired
    private DataSource dataSource;

    private VectorStore vectorStore;

    /**
     * 初始化 PGVector 向量存储
     * 注意：运行测试前需要启动 PostgreSQL 并安装 pgvector 扩展
     * Docker 命令: docker run -d --name postgres-pgvector -e POSTGRES_PASSWORD=admin -p 5432:5432 pgvector/pgvector:pg16
     */
    @BeforeEach
    void setUp() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        
        // 创建 PGVector 向量存储实例
        vectorStore = PgVectorStore.builder(jdbcTemplate, embeddingModel)
                .dimensions(1536)  // 根据使用的 embedding 模型设置维度
                .initializeSchema(true)  // 自动初始化数据库表结构
                .build();
    }

    /**
     * 测试添加文档到向量数据库
     */
    @Test
    void testAddDocuments() {
        // 创建测试文档
        Document doc1 = new Document(
                "Spring AI is a framework for building AI applications with Spring.",
                Map.of("source", "doc1", "category", "framework")
        );
        
        Document doc2 = new Document(
                "PGVector is a PostgreSQL extension that enables vector similarity search.",
                Map.of("source", "doc2", "category", "database")
        );
        
        Document doc3 = new Document(
                "Machine learning models can be integrated with Java applications using Spring AI.",
                Map.of("source", "doc3", "category", "ml")
        );

        // 添加文档到向量数据库
        vectorStore.add(List.of(doc1, doc2, doc3));
        
        assertThat(vectorStore).isNotNull();
        System.out.println("成功添加 3 个文档到 PGVector 向量数据库");
    }

    /**
     * 测试相似度搜索功能
     */
    @Test
    void testSimilaritySearch() {
        // 先添加测试文档
        Document doc1 = new Document(
                "Spring AI provides abstractions for working with AI models in Spring applications.",
                Map.of("source", "doc1")
        );
        
        Document doc2 = new Document(
                "Vector databases store high-dimensional vectors for similarity search.",
                Map.of("source", "doc2")
        );
        
        vectorStore.add(List.of(doc1, doc2));

        // 执行相似度搜索
        SearchRequest searchRequest = SearchRequest.builder()
                .query("What is Spring AI?")
                .topK(2)
                .similarityThreshold(0.5)
                .build();

        List<Document> results = vectorStore.similaritySearch(searchRequest);
        
        assertThat(results).isNotEmpty();
        assertThat(results.size()).isLessThanOrEqualTo(2);
        
        System.out.println("搜索结果:");
        results.forEach(doc -> 
            System.out.println("内容: " + doc.getText() + ", 相似度分数: " + doc.getScore())
        );
    }

    /**
     * 测试删除文档功能
     */
    @Test
    void testDeleteDocuments() {
        // 添加测试文档
        Document doc1 = new Document(
                "This document will be deleted later.",
                Map.of("source", "to_delete")
        );
        
        Document doc2 = new Document(
                "This document will remain.",
                Map.of("source", "to_keep")
        );

        vectorStore.add(List.of(doc1, doc2));

        // 查找要删除的文档
        List<String> ids = vectorStore.similaritySearch(
            SearchRequest.builder().query("deleted").topK(1).build()
        ).stream().map(Document::getId).toList();

        if (!ids.isEmpty()) {
            // 删除文档
            vectorStore.delete(ids);
            
            // 验证删除成功
            List<Document> remainingResults = vectorStore.similaritySearch(
                SearchRequest.builder().query("deleted").topK(1).build()
            );
            
            assertThat(remainingResults).isEmpty();
            System.out.println("成功删除文档");
        }
    }

    /**
     * 测试基于元数据的过滤搜索
     */
    @Test
    void testMetadataFiltering() {
        // 添加带有不同元数据的文档
        Document doc1 = new Document(
                "Python is great for data science and machine learning.",
                Map.of("language", "python", "domain", "data-science")
        );
        
        Document doc2 = new Document(
                "Java is excellent for enterprise applications and microservices.",
                Map.of("language", "java", "domain", "enterprise")
        );
        
        Document doc3 = new Document(
                "JavaScript is perfect for web development and frontend programming.",
                Map.of("language", "javascript", "domain", "web")
        );

        vectorStore.add(List.of(doc1, doc2, doc3));

        // 使用元数据过滤器进行搜索
        SearchRequest searchRequest = SearchRequest.builder()
                .query("programming languages")
                .topK(5)
                .filterExpression("metadata->>'language' = 'java'")
                .build();

        List<Document> results = vectorStore.similaritySearch(searchRequest);
        
        assertThat(results).isNotEmpty();
        System.out.println("过滤后的搜索结果:");
        results.forEach(doc -> 
            System.out.println("内容: " + doc.getText() +
                             ", 元数据: " + doc.getMetadata())
        );
    }

    /**
     * 测试批量操作
     */
    @Test
    void testBatchOperations() {
        // 批量添加文档
        List<Document> documents = List.of(
            new Document("Document batch 1", Map.of("batch", "1")),
            new Document("Document batch 2", Map.of("batch", "2")),
            new Document("Document batch 3", Map.of("batch", "3")),
            new Document("Document batch 4", Map.of("batch", "4")),
            new Document("Document batch 5", Map.of("batch", "5"))
        );

        vectorStore.add(documents);

        // 批量搜索
        SearchRequest searchRequest = SearchRequest.builder()
                .query("Document batch")
                .topK(10)
                .build();

        List<Document> results = vectorStore.similaritySearch(searchRequest);
        
        assertThat(results).hasSizeGreaterThanOrEqualTo(5);
        System.out.println("批量操作结果数量: " + results.size());
    }

    /**
     * 测试不同的相似度阈值
     */
    @Test
    void testDifferentSimilarityThresholds() {
        // 添加测试文档
        Document doc1 = new Document(
                "Artificial intelligence is transforming the technology industry.",
                Map.of("topic", "ai")
        );
        
        Document doc2 = new Document(
                "Deep learning is a subset of machine learning.",
                Map.of("topic", "deep-learning")
        );

        vectorStore.add(List.of(doc1, doc2));

        // 使用不同的相似度阈值进行搜索
        double[] thresholds = {0.1, 0.3, 0.5, 0.7};
        
        for (double threshold : thresholds) {
            SearchRequest searchRequest = SearchRequest.builder()
                    .query("AI and machine learning")
                    .topK(5)
                    .similarityThreshold(threshold)
                    .build();

            List<Document> results = vectorStore.similaritySearch(searchRequest);
            
            System.out.println(String.format("阈值 %.1f: 找到 %d 个结果", threshold, results.size()));
        }
    }

    /**
     * 测试更新文档
     */
    @Test
    void testUpdateDocuments() {
        // 添加初始文档
        Document doc = new Document(
                "Original content about Spring Framework.",
                Map.of("version", "1.0")
        );
        
        vectorStore.add(List.of(doc));

        // 获取文档 ID
        List<Document> searchResults = vectorStore.similaritySearch(
            SearchRequest.builder().query("Spring Framework").topK(1).build()
        );
        
        if (!searchResults.isEmpty()) {
            String docId = searchResults.get(0).getId();
            
            // 创建更新后的文档（使用相同的 ID）
            Document updatedDoc = new Document(
                    docId,
                    "Updated content about Spring Framework with new features.",
                    Map.of("version", "2.0")
            );
            
            // 删除旧文档并添加新文档
            vectorStore.delete(List.of(docId));
            vectorStore.add(List.of(updatedDoc));
            
            // 验证更新
            List<Document> updatedResults = vectorStore.similaritySearch(
                SearchRequest.builder().query("Spring Framework").topK(1).build()
            );
            
            assertThat(updatedResults).isNotEmpty();
            System.out.println("更新后的内容: " + updatedResults.get(0).getText());
        }
    }

    /**
     * 测试复杂查询 - 结合多个条件
     */
    @Test
    void testComplexQuery() {
        // 添加多个相关文档
        List<Document> documents = List.of(
            new Document("Spring Boot simplifies Spring application development.", 
                        Map.of("framework", "spring-boot", "type", "backend")),
            new Document("React is a popular JavaScript library for building user interfaces.", 
                        Map.of("framework", "react", "type", "frontend")),
            new Document("Spring Cloud provides tools for building distributed systems.", 
                        Map.of("framework", "spring-cloud", "type", "backend")),
            new Document("Vue.js is a progressive JavaScript framework.", 
                        Map.of("framework", "vue", "type", "frontend"))
        );

        vectorStore.add(documents);

        // 搜索后端框架
        SearchRequest backendSearch = SearchRequest.builder()
                .query("backend frameworks")
                .topK(10)
                .similarityThreshold(0.3)
                .build();

        List<Document> backendResults = vectorStore.similaritySearch(backendSearch);
        
        System.out.println("后端框架搜索结果:");
        backendResults.forEach(doc -> 
            System.out.println("内容: " + doc.getText() +
                             ", 类型: " + doc.getMetadata().get("type"))
        );
    }

    /**
     * 测试空搜索结果
     */
    @Test
    void testEmptySearchResults() {
        // 添加不相关的文档
        Document doc = new Document(
                "Cooking recipes for Italian cuisine.",
                Map.of("category", "cooking")
        );
        
        vectorStore.add(List.of(doc));

        // 搜索完全不相关的内容，设置高阈值
        SearchRequest searchRequest = SearchRequest.builder()
                .query("quantum physics and particle accelerators")
                .topK(5)
                .similarityThreshold(0.9)
                .build();

        List<Document> results = vectorStore.similaritySearch(searchRequest);
        
        // 由于阈值很高，应该没有结果或结果很少
        System.out.println("高阈值搜索结果数量: " + results.size());
    }
}
