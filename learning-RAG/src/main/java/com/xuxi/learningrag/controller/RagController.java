package com.xuxi.learningrag.controller;

import com.xuxi.learningrag.service.DocumentService;
import com.xuxi.learningrag.service.RagService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/ai")
public class RagController {
    private final DocumentService documentService;
    private final RagService ragService;

    public RagController(DocumentService documentService, RagService ragService) {
        this.documentService = documentService;
        this.ragService = ragService;
    }

    // 上传并索引知识库文档
    @PostMapping("/upload")
    public ResponseEntity<String> uploadDocument(@RequestParam("file") MultipartFile file) {
        try {
            documentService.loadDocument(new InputStreamResource(file.getInputStream()));
            return ResponseEntity.ok("文档上传并索引成功");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("上传失败: " + e.getMessage());
        }
    }

    // 基于知识库的问答
    @PostMapping("/chat")
    public ResponseEntity<String> chat(@RequestBody String question) {
        String answer = ragService.answerQuestion(question);
        return ResponseEntity.ok(answer);
    }
}