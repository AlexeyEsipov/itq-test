--liquibase formatted sql
--changeset esipov:create_sequence_approval_id_seq
CREATE SEQUENCE approval_id_seq
    START WITH 1
    INCREMENT BY 50
    MINVALUE 1
    NO MAXVALUE
    CACHE 1;