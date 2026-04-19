package com.xuxi.learningrag.service;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocumentService {

    private final VectorStore vectorStore;

    public DocumentService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public void loadDocument(Resource pdfResource) {
        // 1. 读取PDF文档
        TikaDocumentReader reader = new TikaDocumentReader(pdfResource);
        List<Document> documents = reader.read();
        // 2. 文档切块
        TokenTextSplitter splitter = new TokenTextSplitter();
        List<Document> splitDocuments = splitter.apply(documents);
        // 3. 生成向量并存入PGVector
        vectorStore.add(splitDocuments);
    }
}
