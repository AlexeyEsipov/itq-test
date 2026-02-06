--liquibase formatted sql
--changeset esipov:create_index_idx_docs_inner_id.sql
CREATE INDEX idx_docs_inner_id ON docs (inner_id);