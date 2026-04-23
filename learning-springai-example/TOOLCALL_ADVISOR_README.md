# ToolCallAdvisor 完整示例

## 📚 项目简介

这是一个基于Spring AI 1.1.3+的`ToolCallAdvisor`完整示例项目，展示了如何使用ToolCallAdvisor来管理和拦截AI工具调用过程。

## 🎯 核心特性

- ✅ **工具调用管理**：演示ToolCallAdvisor如何管理工具调用的完整生命周期
- ✅ **多种配置方式**：默认配置、对话历史控制、完全自定义配置
- ✅ **工具调用监控**：记录和追踪所有工具调用历史
- ✅ **性能对比**：测试不同配置下的性能差异
- ✅ **RESTful API**：提供7个API端点，方便测试和集成
- ✅ **可视化测试界面**：提供友好的HTML测试页面

## 📁 项目结构

```
learning-springai-example/
├── src/main/java/com/xuxi/learningspringaiexample/
│   ├── advisor/
│   │   └── CustomToolCallAdvisor.java          # 自定义ToolCallAdvisor封装类
│   ├── tool/
│   │   └── EnhancedDateTimeTools.java          # 增强版工具类（6个工具方法）
│   └── controller/
│       └── toolcalladvisor/
│           └── ToolCallAdvisorController.java  # REST控制器（7个API端点）
├── src/main/resources/
│   ├── static/
│   │   └── toolcall-advisor-test.html          # 可视化测试页面
│   └── application.yaml                        # 应用配置
└── ToolCallAdvisor使用指南.md                  # 详细使用文档
```

## 🚀 快速开始

### 1. 环境要求

- Java 17+
- Maven 3.6+
- Spring Boot 3.x
- Spring AI 1.1.3+
- OpenAI API Key（或其他兼容的LLM服务）

### 2. 配置API Key

在启动应用前，设置环境变量：

**Windows (PowerShell):**
```powershell
$env:OPENAI_API_KEY="your-api-key-here"
```

**Linux/Mac:**
```bash
export OPENAI_API_KEY="your-api-key-here"
```

### 3. 启动应用

```bash
cd learning-springai-example
mvn clean install
mvn spring-boot:run
```

应用将在 `http://localhost:8080` 启动。

### 4. 访问测试页面

打开浏览器访问：
```
http://localhost:8080/toolcall-advisor-test.html
```

## 📡 API端点

### 1. 默认ToolCallAdvisor
```bash
GET /api/toolcall-advisor/default?message=现在几点了？
```

### 2. 带对话历史
```bash
GET /api/toolcall-advisor/with-history?message=现在是什么时间？
```

### 3. 自定义配置
```bash
GET /api/toolcall-advisor/custom?message=计算2024-01-01到2024-12-31之间有多少天
```

### 4. 复杂多工具调用
```bash
GET /api/toolcall-advisor/complex?message=现在几点了？帮我设置一个30分钟后的闹钟
```

### 5. 查看工具调用历史
```bash
GET /api/toolcall-advisor/history
```

### 6. 清除工具调用历史
```bash
POST /api/toolcall-advisor/history/clear
```

### 7. 性能对比测试
```bash
GET /api/toolcall-advisor/performance-comparison
```

## 🔧 工具方法

`EnhancedDateTimeTools` 提供了6个工具方法：

| 方法名 | 描述 | 参数 |
|--------|------|------|
| `getCurrentDateTime()` | 获取当前日期时间 | 无 |
| `setAlarm(String time)` | 设置闹钟 | time (ISO-8601) |
| `daysBetweenDates(String start, String end)` | 计算天数差 | start, end |
| `formatDate(String date, String format)` | 格式化日期 | date, format |
| `getCurrentTimestamp()` | 获取当前时间戳 | 无 |
| `timestampToDateTime(long timestamp)` | 时间戳转日期 | timestamp |

## 💡 ToolCallAdvisor配置说明

### 配置参数

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `toolCallingManager` | ToolCallingManager | 必需 | 工具调用管理器 |
| `conversationHistoryEnabled` | boolean | true | 是否启用对话历史 |
| `streamToolCallResponses` | boolean | false | 是否启用流式响应 |

### 使用方式

#### 方式1：默认配置
```java
ToolCallAdvisor defaultAdvisor = ToolCallAdvisor.builder()
    .toolCallingManager(toolCallingManager)
    .build();
```

#### 方式2：控制对话历史
```java
ToolCallAdvisor historyAdvisor = ToolCallAdvisor.builder()
    .toolCallingManager(toolCallingManager)
    .conversationHistoryEnabled(true)  // 启用对话历史
    .build();
```

#### 方式3：完全自定义
```java
ToolCallAdvisor customAdvisor = ToolCallAdvisor.builder()
    .toolCallingManager(toolCallingManager)
    .conversationHistoryEnabled(true)
    .streamToolCallResponses(false)
    .build();
```

## 🎓 学习资源

- [详细使用指南](./ToolCallAdvisor使用指南.md)
- [Spring AI官方文档 - 工具调用](https://docs.spring.io/spring-ai/reference/api/tools.html)
- [Spring AI官方文档 - Advisors API](https://docs.spring.io/spring-ai/reference/api/advisors.html)
- [ToolCallAdvisor API文档](https://docs.spring.io/spring-ai/docs/current/api/org/springframework/ai/chat/client/advisor/ToolCallAdvisor.html)

## 🧪 测试示例

### 示例1：基本工具调用
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

### 示例2：复杂多工具调用
```bash
curl "http://localhost:8080/api/toolcall-advisor/complex?message=现在几点了？帮我设置一个30分钟后的闹钟"
```

这个请求会触发：
1. `getCurrentDateTime()` - 获取当前时间
2. `setAlarm()` - 设置30分钟后的闹钟

### 示例3：日期计算
```bash
curl "http://localhost:8080/api/toolcall-advisor/custom?message=计算2024-01-01到2024-12-31之间有多少天"
```

**预期响应：**
```json
{
  "message": "计算2024-01-01到2024-12-31之间有多少天",
  "response": "2024-01-01到2024-12-31之间有365天",
  "executionTime": 1567,
  "advisorType": "Custom ToolCallAdvisor"
}
```

## 📊 性能监控

使用性能对比端点测试不同配置的性能：

```bash
curl "http://localhost:8080/api/toolcall-advisor/performance-comparison"
```

**响应示例：**
```json
{
  "testMessage": "现在是什么时间？",
  "defaultAdvisor": {
    "time": "1234ms",
    "type": "Default"
  },
  "historyAdvisor": {
    "time": "1345ms",
    "type": "With History"
  },
  "customAdvisor": {
    "time": "1456ms",
    "type": "Custom"
  }
}
```

## ❓ 常见问题

### Q1: ToolCallAdvisor和直接调用工具有什么区别？

**A**: 
- **直接调用**：ChatClient内部管理工具调用，不经过Advisor链
- **ToolCallAdvisor**：工具调用作为Advisor链的一部分，可被其他Advisor拦截

### Q2: 什么时候应该使用ToolCallAdvisor？

**A**: 当你需要：
- 监控和记录工具调用
- 在工具调用前后执行自定义逻辑
- 控制工具调用的行为
- 实现复杂的工具调用工作流

### Q3: conversationHistoryEnabled应该设置为什么值？

**A**:
- `true`：多轮对话场景，需要保持上下文
- `false`：单轮对话场景，减少token消耗

### Q4: 如何调试工具调用过程？

**A**: 
1. 查看控制台日志，会显示工具调用信息
2. 使用`/history`端点查看工具调用历史
3. 启用DEBUG级别日志

## 📝 最佳实践

1. **合理配置conversationHistoryEnabled**：根据场景选择是否保留对话历史
2. **记录工具调用历史**：用于监控、调试和性能分析
3. **测试不同配置**：使用性能对比功能找到最佳配置
4. **处理工具异常**：在工具方法中做好异常处理和错误提示
5. **监控工具调用性能**：定期查看工具调用历史，优化性能

## 📄 许可证

本示例代码遵循Spring AI项目的许可证。

## 🤝 贡献

欢迎提交Issue和Pull Request！

## 📧 联系方式

如有问题，请提交Issue或联系项目维护者。

---

**注意**: 本项目仅用于学习和演示目的，生产环境请根据实际情况进行调整和优化。
