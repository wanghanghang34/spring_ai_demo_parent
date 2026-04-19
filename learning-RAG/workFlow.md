flowchart TD
A[原始文档<br>PDF/Word/TXT] --> B[文档读取<br>TikaDocumentReader等]
B --> C[文档切块<br>TokenTextSplitter等]
C --> D[向量化<br>Ollama Embedding Model]
D --> E[(向量数据库<br>PostgreSQL + PGVector)]

    F[用户提问] --> G[问题向量化<br>Ollama Embedding Model]
    G --> H[语义相似度检索]
    E -- 检索相关文档块 --> H
    H --> I[组装Prompt<br>问题 + 上下文]
    I --> J[调用LLM<br>Ollama Chat Model]
    J --> K[生成答案]
    
    subgraph 离线部分 (知识库构建)
        A --> B --> C --> D --> E
    end

    subgraph 在线部分 (RAG问答)
        F --> G --> H --> I --> J --> K
    end