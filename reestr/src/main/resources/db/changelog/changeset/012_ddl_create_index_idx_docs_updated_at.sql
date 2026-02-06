--liquibase formatted sql
--changeset esipov:create_index_idx_docs_updated_at.sql
CREATE INDEX idx_docs_updated_at ON docs (updated_at);