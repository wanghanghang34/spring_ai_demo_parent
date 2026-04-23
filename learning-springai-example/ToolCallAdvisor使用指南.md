# ToolCallAdvisor 完整示例指南

## 概述

本示例演示了如何在Spring AI 1.1.3+中使用`ToolCallAdvisor`来管理和拦截工具调用过程。

## 什么是ToolCallAdvisor？

`ToolCallAdvisor`是Spring AI提供的一个递归Advisor，它的核心特性是：

1. **禁用内部工具执行流程**：将工具调用循环作为advisor链的一部分来实现
2. **拦截能力**：允许链中的其他advisor拦截工具调用循环
3. **灵活配置**：支持控制对话历史、执行顺序和流式响应
4. **生命周期管理**：提供对工具调用完整生命周期的控制

### 工作原理

```
用户请求 → ChatClient → Advisor链 → ToolCallAdvisor → 工具调用循环 → AI模型 → 最终响应
                                       ↓
                              其他Advisor可以拦截
```

## 项目结构

```
learning-springai-example/
├── src/main/java/com/xuxi/learningspringaiexample/
│   ├── advisor/
│   │   └── CustomToolCallAdvisor.java          # 自定义ToolCallAdvisor封装
│   ├── tool/
│   │   └── EnhancedDateTimeTools.java          # 增强版工具类（6个工具方法）
│   └── controller/
│       └── toolcalladvisor/
│           └── ToolCallAdvisorController.java  # 示例控制器（7个API端点）
└── src/main/resources/
    └── application.yaml                        # 配置文件
```

## 核心组件说明

### 1. EnhancedDateTimeTools（工具类）

提供了6个工具方法用于演示：

| 方法名 | 描述 | 参数 | 返回类型 |
|--------|------|------|----------|
| `getCurrentDateTime()` | 获取当前日期时间 | 无 | String |
| `setAlarm(String time)` | 设置闹钟 | time (ISO-8601) | void |
| `daysBetweenDates(String start, String end)` | 计算天数差 | start, end (ISO-8601) | long |
| `formatDate(String date, String format)` | 格式化日期 | date, format | String |
| `getCurrentTimestamp()` | 获取当前时间戳 | 无 | long |
| `timestampToDateTime(long timestamp)` | 时间戳转日期 | timestamp | String |

### 2. CustomToolCallAdvisor（Advisor封装）

提供了3种创建ToolCallAdvisor的方法：

#### 方法1：默认配置
```java
ToolCallAdvisor defaultAdvisor = customToolCallAdvisor.createDefaultAdvisor(toolCallingManager);
```

#### 方法2：控制对话历史
```java
ToolCallAdvisor historyAdvisor = customToolCallAdvisor.createAdvisorWithHistoryControl(
    toolCallingManager, 
    true  // 启用对话历史
);
```

#### 方法3：完全自定义
```java
ToolCallAdvisor customAdvisor = customToolCallAdvisor.createCustomAdvisor(
    toolCallingManager,
    100,  // advisor执行顺序
    true, // 启用对话历史
    false // 不启用流式工具调用响应
);
```

### 3. ToolCallAdvisorController（API端点）

提供了7个REST API端点：

| 端点 | 方法 | 描述 |
|------|------|------|
| `/api/toolcall-advisor/default` | GET | 默认ToolCallAdvisor示例 |
| `/api/toolcall-advisor/with-history` | GET | 带对话历史的示例 |
| `/api/toolcall-advisor/custom` | GET | 完全自定义配置示例 |
| `/api/toolcall-advisor/complex` | GET | 复杂多工具调用示例 |
| `/api/toolcall-advisor/history` | GET | 查看工具调用历史 |
| `/api/toolcall-advisor/history/clear` | POST | 清除工具调用历史 |
| `/api/toolcall-advisor/performance-comparison` | GET | 性能对比测试 |

## 快速开始

### 1. 环境要求

- Java 17+
- Spring Boot 3.x
- Spring AI 1.1.3+
- OpenAI API Key（或其他兼容的LLM服务）

### 2. 配置API Key

在`application.yaml`中配置：

```yaml
spring:
  ai:
    openai:
      api-key: ${OPENAI_API_KEY}
      base-url: https://api.chatanywhere.tech  # 或使用其他API端点
```

设置环境变量：
```bash
# Windows PowerShell
$env:OPENAI_API_KEY="your-api-key-here"

# Linux/Mac
export OPENAI_API_KEY="your-api-key-here"
```

### 3. 启动应用

```bash
cd learning-springai-example
mvn spring-boot:run
```

### 4. 测试API

#### 示例1：基本工具调用
```bash
curl "http://localhost:8080/api/toolcall-advisor/default?message=现在几点了？"
```

**预期响应：**
```json
{
  "message": "现在几点了？",
  "response": "现在是2024-01-15T14:30:00+08:00[Asia/Shanghai]",
  "executionTime": 1234,
  "advisorType": "Default ToolCallAdvisor"
}
```

#### 示例2：带对话历史的调用
```bash
curl "http://localhost:8080/api/toolcall-advisor/with-history?message=现在是什么时间？"
```

#### 示例3：自定义配置
```bash
curl "http://localhost:8080/api/toolcall-advisor/custom?message=计算2024-01-01到2024-12-31之间有多少天"
```

**预期响应：**
```json
{
  "message": "计算2024-01-01到2024-12-31之间有多少天",
  "response": "2024-01-01到2024-12-31之间有365天",
  "executionTime": 1567,
  "advisorType": "Custom ToolCallAdvisor",
  "advisorOrder": 100,
  "conversationHistoryEnabled": true,
  "streamToolCallResponses": false
}
```

#### 示例4：复杂多工具调用
```bash
curl "http://localhost:8080/api/toolcall-advisor/complex?message=现在几点了？帮我设置一个30分钟后的闹钟"
```

这个示例会触发多个工具调用：
1. `getCurrentDateTime()` - 获取当前时间
2. `setAlarm()` - 设置30分钟后的闹钟

#### 示例5：查看工具调用历史
```bash
curl "http://localhost:8080/api/toolcall-advisor/history"
```

#### 示例6：性能对比
```bash
curl "http://localhost:8080/api/toolcall-advisor/performance-comparison"
```

## ToolCallAdvisor配置参数详解

### 1. toolCallingManager（必需）
- **类型**：`ToolCallingManager`
- **说明**：工具调用管理器，负责管理工具执行的生命周期
- **获取方式**：通过Spring依赖注入

### 2. order（可选）
- **类型**：`int`
- **默认值**：`Advisor.DEFAULT_CHAT_MEMORY_PRECEDENCE_ORDER`
- **说明**：Advisor的执行顺序，数值越小优先级越高
- **使用场景**：当有多个Advisor时控制执行顺序

### 3. conversationHistoryEnabled（可选）
- **类型**：`boolean`
- **默认值**：`true`
- **说明**：是否在工具调用中包含对话历史
- **使用场景**：
  - `true`：多轮对话，保持上下文
  - `false`：单轮对话，减少token消耗

### 4. streamToolCallResponses（可选）
- **类型**：`boolean`
- **默认值**：`false`
- **说明**：是否启用流式工具调用响应
- **使用场景**：
  - `true`：实时流式响应，适合长时间运行的工具
  - `false`：等待所有工具执行完成后再返回

## 高级用法

### 1. 自定义ToolCallAdvisor子类

你可以继承`ToolCallAdvisor`来创建完全自定义的行为：

```java
public class MyCustomToolCallAdvisor extends ToolCallAdvisor {
    
    public MyCustomToolCallAdvisor(ToolCallingManager toolCallingManager) {
        super(toolCallingManager, 100, true, false);
    }
    
    @Override
    protected ChatClientRequest doBeforeCall(
            ChatClientRequest chatClientRequest,
            CallAdvisorChain callAdvisorChain) {
        // 在每次工具调用前执行自定义逻辑
        log.info("About to call tool: {}", chatClientRequest);
        return super.doBeforeCall(chatClientRequest, callAdvisorChain);
    }
    
    @Override
    protected ChatClientResponse doAfterCall(
            ChatClientResponse chatClientResponse,
            CallAdvisorChain callAdvisorChain) {
        // 在每次工具调用后执行自定义逻辑
        log.info("Tool call completed: {}", chatClientResponse);
        return super.doAfterCall(chatClientResponse, callAdvisorChain);
    }
}
```

### 2. 结合其他Advisor使用

ToolCallAdvisor可以与其他Advisor组合使用：

```java
ChatClient chatClient = ChatClient.builder(chatModel)
    .defaultAdvisors(
        new ToolCallAdvisor(toolCallingManager),  // 工具调用管理
        new MyCustomAdvisor(),                     // 自定义Advisor
        new LoggingAdvisor()                       // 日志Advisor
    )
    .build();
```

### 3. 流式工具调用

对于需要流式响应的场景：

```java
ToolCallAdvisor streamingAdvisor = ToolCallAdvisor.builder()
    .toolCallingManager(toolCallingManager)
    .streamToolCallResponses(true)
    .build();

Flux<ChatClientResponse> response = chatClient.prompt()
    .user(message)
    .tools(enhancedDateTimeTools)
    .stream()
    .chatResponse();
```

## 常见问题

### Q1: ToolCallAdvisor和直接调用工具有什么区别？

**A**: 
- **直接调用**：ChatClient内部管理工具调用循环，不经过Advisor链
- **ToolCallAdvisor**：工具调用循环作为Advisor链的一部分，可以被其他Advisor拦截和修改

### Q2: 什么时候应该使用ToolCallAdvisor？

**A**: 当你需要：
- 监控和记录工具调用
- 在工具调用前后执行自定义逻辑
- 控制工具调用的行为（如对话历史）
- 实现复杂的工具调用工作流
- 与其他Advisor协作

### Q3: conversationHistoryEnabled应该设置为什么值？

**A**:
- 设置为`true`：多轮对话场景，需要保持上下文
- 设置为`false`：单轮对话场景，减少token消耗和提高性能

### Q4: 如何调试工具调用过程？

**A**: 
1. 启用日志：在`application.yaml`中添加
```yaml
logging:
  level:
    com.xuxi.learningspringaiexample: DEBUG
    org.springframework.ai: DEBUG
```

2. 查看控制台输出，会显示：
   - 工具调用信息
   - 执行时间
   - 工具调用结果

### Q5: ToolCallAdvisor会影响性能吗？

**A**: 
- 会有轻微的性能开销（通常在几毫秒内）
- 但这个开销通常可以忽略不计
- 带来的可观测性和控制能力远大于性能损耗
- 可以通过`performance-comparison`端点测试具体差异

## 最佳实践

1. **合理配置conversationHistoryEnabled**：根据场景选择是否保留对话历史
2. **使用order控制执行顺序**：当有多个Advisor时，合理设置优先级
3. **记录工具调用历史**：用于监控、调试和性能分析
4. **测试不同配置**：使用性能对比功能找到最佳配置
5. **处理工具异常**：在工具方法中做好异常处理和错误提示

## 扩展阅读

- [Spring AI官方文档 - 工具调用](https://docs.spring.io/spring-ai/reference/api/tools.html)
- [Spring AI官方文档 - Advisors API](https://docs.spring.io/spring-ai/reference/api/advisors.html)
- [ToolCallAdvisor API文档](https://docs.spring.io/spring-ai/docs/current/api/org/springframework/ai/chat/client/advisor/ToolCallAdvisor.html)

## 许可证

本示例代码遵循Spring AI项目的许可证。
