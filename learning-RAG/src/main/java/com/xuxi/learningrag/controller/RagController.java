package com.xuxi.learningrag.controller;

import com.xuxi.learningrag.service.DocumentService;
import com.xuxi.learningrag.service.RagService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

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
            documentService.loadDocument(new InputStreamResource(file.getInputStream()), file.getOriginalFilename());
            return ResponseEntity.ok("文档上传并索引成功");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("上传失败: " + e.getMessage());
        }
    }

    // 基于知识库的问答（流式输出）
    @PostMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter chat(@RequestBody String question) {
        SseEmitter emitter = new SseEmitter(300_000L); // 5分钟超时
        new Thread(() -> {
            try {
                ragService.answerQuestionStream(question, chunk -> {
                    try {
                        emitter.send(chunk);
                    } catch (Exception e) {
                        emitter.completeWithError(e);
                    }
                });
                emitter.send(SseEmitter.event().name("done").data("[DONE]"));
                emitter.complete();
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        }).start();
        return emitter;
    }
}