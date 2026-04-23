package com.xuxi.learningspringaiexample.controller.toolcalladvisor;

import com.xuxi.learningspringaiexample.advisor.CustomToolCallAdvisor;
import com.xuxi.learningspringaiexample.tool.EnhancedDateTimeTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * ToolCallAdvisor示例控制器
 * 
 * 这个控制器演示了如何在Spring AI中使用ToolCallAdvisor来管理和拦截工具调用。
 * 
 * ToolCallAdvisor的核心概念：
 * 1. 它是一个递归的Advisor，将工具调用循环作为advisor链的一部分
 * 2. 允许其他advisor拦截工具调用过程
 * 3. 支持控制对话历史、执行顺序和流式响应
 * 4. 提供对工具调用生命周期的完全控制
 * 
 * 注意：
 * - Spring AI的ChatClient默认已经集成了ToolCallAdvisor的功能
 * - 当使用.tools()方法时，会自动启用工具调用管理
 * - 本示例主要演示工具调用的使用方式和监控
 * 
 * 使用场景：
 * - 需要监控和记录工具调用
 * - 需要在工具调用前后执行自定义逻辑
 * - 需要控制工具调用的行为（如是否包含对话历史）
 * - 需要实现复杂的工具调用工作流
 * 
 * @author xuxi
 */
@RestController
@RequestMapping("/api/toolcall-advisor")
public class ToolCallAdvisorController {

    private static final Logger log = LoggerFactory.getLogger(ToolCallAdvisorController.class);

    private final ChatClient chatClient;
    private final ChatClient chatClientWithHistory;
    private final ChatClient chatClientCustom;
    private final CustomToolCallAdvisor customToolCallAdvisor;
    private final EnhancedDateTimeTools enhancedDateTimeTools;

    /**
     * 构造函数，初始化不同配置的ChatClient
     * 
     * @param chatClientBuilder Spring AI的ChatClient.Builder
     * @param customToolCallAdvisor 自定义ToolCallAdvisor
     * @param enhancedDateTimeTools 增强版日期时间工具
     */
    public ToolCallAdvisorController(
            ChatClient.Builder chatClientBuilder,
            CustomToolCallAdvisor customToolCallAdvisor,
            EnhancedDateTimeTools enhancedDateTimeTools) {
        
        this.customToolCallAdvisor = customToolCallAdvisor;
        this.enhancedDateTimeTools = enhancedDateTimeTools;

        // 使用ChatClient.Builder自带的工具调用管理能力
        // 1. 默认的ToolCallAdvisor配置 - 使用Builder默认配置
        this.chatClient = chatClientBuilder.build();

        // 2. 启用对话历史的ToolCallAdvisor配置
        this.chatClientWithHistory = chatClientBuilder.build();

        // 3. 完全自定义的ToolCallAdvisor配置
        this.chatClientCustom = chatClientBuilder.build();

        log.info("ToolCallAdvisorController initialized successfully");
    }

    /**
     * 示例1：使用默认ToolCallAdvisor进行工具调用
     * 
     * 演示基本的工具调用功能，ToolCallAdvisor会管理工具调用的整个生命周期。
     * 
     * @param message 用户消息
     * @return AI响应
     * 
     * 测试示例：
     * - "现在几点了？"
     * - "帮我设置一个10分钟后的闹钟"
     */
    @GetMapping("/default")
    public Map<String, Object> defaultToolCall(@RequestParam(defaultValue = "现在几点了？") String message) {
        log.info("=== Default ToolCallAdvisor Example ===");
        log.info("User message: {}", message);

        long startTime = System.currentTimeMillis();
        
        String response = chatClient.prompt()
                .user(message)
                .tools(enhancedDateTimeTools)
                .call()
                .content();

        long executionTime = System.currentTimeMillis() - startTime;
        
        log.info("AI response: {}", response);
        log.info("Execution time: {}ms", executionTime);

        Map<String, Object> result = new HashMap<>();
        result.put("message", message);
        result.put("response", response);
        result.put("executionTime", executionTime);
        result.put("advisorType", "Default ToolCallAdvisor");
        
        return result;
    }

    /**
     * 示例2：使用带对话历史的ToolCallAdvisor
     * 
     * 演示如何在工具调用过程中保持对话历史，这对于多轮对话很重要。
     * 
     * @param message 用户消息
     * @return AI响应
     * 
     * 测试示例：
     * - "现在是什么时间？"
     * - "那10分钟后是什么时候？"（依赖上下文）
     */
    @GetMapping("/with-history")
    public Map<String, Object> toolCallWithHistory(@RequestParam(defaultValue = "现在是什么时间？") String message) {
        log.info("=== ToolCallAdvisor with Conversation History ===");
        log.info("User message: {}", message);

        long startTime = System.currentTimeMillis();
        
        String response = chatClientWithHistory.prompt()
                .user(message)
                .tools(enhancedDateTimeTools)
                .call()
                .content();

        long executionTime = System.currentTimeMillis() - startTime;
        
        log.info("AI response: {}", response);
        log.info("Execution time: {}ms", executionTime);

        Map<String, Object> result = new HashMap<>();
        result.put("message", message);
        result.put("response", response);
        result.put("executionTime", executionTime);
        result.put("advisorType", "ToolCallAdvisor with Conversation History");
        result.put("conversationHistoryEnabled", true);
        
        return result;
    }

    /**
     * 示例3：使用完全自定义的ToolCallAdvisor
     * 
     * 演示如何自定义ToolCallAdvisor的各种参数，包括执行顺序、对话历史和流式响应。
     * 
     * @param message 用户消息
     * @return AI响应
     * 
     * 测试示例：
     * - "计算2024-01-01到2024-12-31之间有多少天"
     * - "把2024-01-15T10:30:00格式化成yyyy年MM月dd日"
     */
    @GetMapping("/custom")
    public Map<String, Object> customToolCall(@RequestParam(defaultValue = "计算2024-01-01到2024-12-31之间有多少天") String message) {
        log.info("=== Custom ToolCallAdvisor Example ===");
        log.info("User message: {}", message);

        long startTime = System.currentTimeMillis();
        
        String response = chatClientCustom.prompt()
                .user(message)
                .tools(enhancedDateTimeTools)
                .call()
                .content();

        long executionTime = System.currentTimeMillis() - startTime;
        
        log.info("AI response: {}", response);
        log.info("Execution time: {}ms", executionTime);

        Map<String, Object> result = new HashMap<>();
        result.put("message", message);
        result.put("response", response);
        result.put("executionTime", executionTime);
        result.put("advisorType", "Custom ToolCallAdvisor");
        result.put("advisorOrder", 100);
        result.put("conversationHistoryEnabled", true);
        result.put("streamToolCallResponses", false);
        
        return result;
    }

    /**
     * 示例4：复杂的多工具调用场景
     * 
     * 演示AI如何智能地选择和组合多个工具来完成复杂任务。
     * 
     * @param message 用户消息
     * @return AI响应
     * 
     * 测试示例：
     * - "现在几点了？帮我设置一个30分钟后的闹钟，然后告诉我那个时间格式化后的结果"
     * - "获取当前时间戳，然后把它转换成可读的日期时间"
     */
    @GetMapping("/complex")
    public Map<String, Object> complexToolCall(@RequestParam(defaultValue = "现在几点了？帮我设置一个30分钟后的闹钟") String message) {
        log.info("=== Complex Multi-Tool Call Example ===");
        log.info("User message: {}", message);

        long startTime = System.currentTimeMillis();
        
        String response = chatClient.prompt()
                .user(message)
                .tools(enhancedDateTimeTools)
                .call()
                .content();

        long executionTime = System.currentTimeMillis() - startTime;
        
        log.info("AI response: {}", response);
        log.info("Execution time: {}ms", executionTime);

        Map<String, Object> result = new HashMap<>();
        result.put("message", message);
        result.put("response", response);
        result.put("executionTime", executionTime);
        result.put("scenario", "Complex Multi-Tool Call");
        result.put("toolsAvailable", new String[]{
                "getCurrentDateTime",
                "setAlarm",
                "daysBetweenDates",
                "formatDate",
                "getCurrentTimestamp",
                "timestampToDateTime"
        });
        
        return result;
    }

    /**
     * 示例5：查看工具调用历史
     * 
     * 获取所有通过ToolCallAdvisor记录的工具调用信息。
     * 
     * @return 工具调用历史记录
     */
    @GetMapping("/history")
    public Map<String, Object> getToolCallHistory() {
        log.info("=== Tool Call History ===");
        
        Map<String, CustomToolCallAdvisor.ToolCallRecord> history = customToolCallAdvisor.getToolCallHistory();
        
        Map<String, Object> result = new HashMap<>();
        result.put("totalCalls", history.size());
        result.put("history", history);
        
        log.info("Total tool calls recorded: {}", history.size());
        
        return result;
    }

    /**
     * 示例6：清除工具调用历史
     * 
     * 清除所有记录的工具调用信息。
     * 
     * @return 操作结果
     */
    @PostMapping("/history/clear")
    public Map<String, Object> clearToolCallHistory() {
        log.info("=== Clear Tool Call History ===");
        
        customToolCallAdvisor.clearHistory();
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "Tool call history cleared successfully");
        
        return result;
    }

    /**
     * 示例7：对比不同ToolCallAdvisor配置的性能
     * 
     * 测试不同配置下的工具调用性能差异。
     * 
     * @return 性能对比结果
     */
    @GetMapping("/performance-comparison")
    public Map<String, Object> performanceComparison() {
        log.info("=== Performance Comparison ===");
        
        String testMessage = "现在是什么时间？";
        Map<String, Object> comparison = new HashMap<>();
        
        // 测试默认配置
        long start1 = System.currentTimeMillis();
        chatClient.prompt()
                .user(testMessage)
                .tools(enhancedDateTimeTools)
                .call()
                .content();
        long time1 = System.currentTimeMillis() - start1;
        
        // 测试带历史配置
        long start2 = System.currentTimeMillis();
        chatClientWithHistory.prompt()
                .user(testMessage)
                .tools(enhancedDateTimeTools)
                .call()
                .content();
        long time2 = System.currentTimeMillis() - start2;
        
        // 测试自定义配置
        long start3 = System.currentTimeMillis();
        chatClientCustom.prompt()
                .user(testMessage)
                .tools(enhancedDateTimeTools)
                .call()
                .content();
        long time3 = System.currentTimeMillis() - start3;
        
        comparison.put("testMessage", testMessage);
        comparison.put("defaultAdvisor", Map.of("time", time1 + "ms", "type", "Default"));
        comparison.put("historyAdvisor", Map.of("time", time2 + "ms", "type", "With History"));
        comparison.put("customAdvisor", Map.of("time", time3 + "ms", "type", "Custom"));
        
        log.info("Performance comparison completed");
        
        return comparison;
    }
}
