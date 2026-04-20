# Run Postgres with pgvector in Docker
docker run -d --name pgvector -e POSTGRES_DB=vectordb -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres -p 5434:5432 pgvector/pgvector:pg17