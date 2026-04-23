package com.xuxi.learningspringaiexample.advisor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.advisor.ToolCallAdvisor;
import org.springframework.ai.chat.client.advisor.api.*;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 自定义ToolCallAdvisor示例
 * 
 * ToolCallAdvisor是Spring AI提供的一个递归Advisor，它禁用了内部的工具执行流程，
 * 并将工具调用循环作为advisor链的一部分来实现。这使得可以通过链中的其他advisor
 * 来拦截工具调用循环。
 * 
 * 主要功能：
 * 1. 拦截和监控工具调用过程
 * 2. 记录工具调用的历史和性能
 * 3. 在工具调用前后执行自定义逻辑
 * 4. 控制对话历史是否包含在工具调用中
 * 5. 支持流式和非流式两种模式
 * 
 * 工作原理：
 * - ToolCallAdvisor实现了CallAdvisor和StreamAdvisor接口
 * - 它使用CallAdvisorChainUtil来实现循环的advisor链调用
 * - 可以在工具调用循环的每个阶段插入自定义逻辑
 * 
 * @author xuxi
 */
@Component
public class CustomToolCallAdvisor {

    private static final Logger log = LoggerFactory.getLogger(CustomToolCallAdvisor.class);

    /**
     * 工具调用历史记录
     * 用于跟踪所有工具调用的信息
     */
    private final Map<String, ToolCallRecord> toolCallHistory = new ConcurrentHashMap<>();

    /**
     * 创建默认的ToolCallAdvisor实例
     * 
     * @param toolCallingManager 工具调用管理器
     * @return 配置好的ToolCallAdvisor
     */
    public ToolCallAdvisor createDefaultAdvisor(ToolCallingManager toolCallingManager) {
        return ToolCallAdvisor.builder()
                .toolCallingManager(toolCallingManager)
                .build();
    }

    /**
     * 创建带对话历史控制的ToolCallAdvisor实例
     * 
     * @param toolCallingManager 工具调用管理器
     * @param conversationHistoryEnabled 是否启用对话历史
     * @return 配置好的ToolCallAdvisor
     */
    public ToolCallAdvisor createAdvisorWithHistoryControl(
            ToolCallingManager toolCallingManager,
            boolean conversationHistoryEnabled) {
        
        return ToolCallAdvisor.builder()
                .toolCallingManager(toolCallingManager)
                .conversationHistoryEnabled(conversationHistoryEnabled)
                .build();
    }

    /**
     * 创建完全自定义的ToolCallAdvisor实例
     * 
     * @param toolCallingManager 工具调用管理器
     * @param advisorOrder Advisor的执行顺序（注：当前版本可能不支持自定义order）
     * @param conversationHistoryEnabled 是否启用对话历史
     * @param streamToolCallResponses 是否启用流式工具调用响应
     * @return 配置好的ToolCallAdvisor
     */
    public ToolCallAdvisor createCustomAdvisor(
            ToolCallingManager toolCallingManager,
            int advisorOrder,
            boolean conversationHistoryEnabled,
            boolean streamToolCallResponses) {
        
        log.info("Creating custom ToolCallAdvisor with order: {}, historyEnabled: {}, streamEnabled: {}", 
                advisorOrder, conversationHistoryEnabled, streamToolCallResponses);

        // 注：ToolCallAdvisor.Builder可能不支持order方法，使用默认顺序
        return ToolCallAdvisor.builder()
                .toolCallingManager(toolCallingManager)
                .conversationHistoryEnabled(conversationHistoryEnabled)
                .streamToolCallResponses(streamToolCallResponses)
                .build();
    }

    /**
     * 获取工具调用历史记录
     * 
     * @return 工具调用历史记录
     */
    public Map<String, ToolCallRecord> getToolCallHistory() {
        return toolCallHistory;
    }

    /**
     * 清除工具调用历史记录
     */
    public void clearHistory() {
        toolCallHistory.clear();
        log.info("Tool call history cleared");
    }

    /**
     * 工具调用记录
     * 用于存储每次工具调用的详细信息
     */
    public static class ToolCallRecord {
        private final String toolName;
        private final String arguments;
        private final String result;
        private final long executionTime;
        private final long timestamp;

        public ToolCallRecord(String toolName, String arguments, String result, long executionTime) {
            this.toolName = toolName;
            this.arguments = arguments;
            this.result = result;
            this.executionTime = executionTime;
            this.timestamp = System.currentTimeMillis();
        }

        public String getToolName() {
            return toolName;
        }

        public String getArguments() {
            return arguments;
        }

        public String getResult() {
            return result;
        }

        public long getExecutionTime() {
            return executionTime;
        }

        public long getTimestamp() {
            return timestamp;
        }

        @Override
        public String toString() {
            return "ToolCallRecord{" +
                    "toolName='" + toolName + '\'' +
                    ", arguments='" + arguments + '\'' +
                    ", result='" + result + '\'' +
                    ", executionTime=" + executionTime + "ms" +
                    ", timestamp=" + timestamp +
                    '}';
        }
    }
}
