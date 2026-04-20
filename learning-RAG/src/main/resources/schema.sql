-- 启用 pgvector 扩展
CREATE EXTENSION IF NOT EXISTS vector;

-- 删除已存在的表（如果需要重建）
DROP TABLE IF EXISTS public.vector_store CASCADE;

-- 创建向量存储表
CREATE TABLE public.vector_store (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    content TEXT,
    metadata JSONB,
    embedding VECTOR(1024)  -- 根据 embedding 模型维度调整，qwen3-embedding:0.6b 是 1024 维
);

-- 创建索引以提高搜索性能
CREATE INDEX ON public.vector_store USING hnsw (embedding vector_cosine_ops);

-- 添加注释
COMMENT ON TABLE public.vector_store IS 'Spring AI PGVector 向量存储表';
COMMENT ON COLUMN public.vector_store.id IS '文档唯一标识';
COMMENT ON COLUMN public.vector_store.content IS '文档内容';
COMMENT ON COLUMN public.vector_store.metadata IS '文档元数据（JSON格式）';
COMMENT ON COLUMN public.vector_store.embedding IS '向量嵌入（768维）';
