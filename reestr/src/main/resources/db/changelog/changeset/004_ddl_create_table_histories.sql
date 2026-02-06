--liquibase formatted sql
--changeset esipov:create_histories_table
create table histories
(
    id BIGINT PRIMARY KEY DEFAULT nextval('histories_id_seq'),
    action      varchar(255)
        constraint histories_documents_action_check
            check ((action)::text = ANY ((ARRAY ['SUBMIT'::character varying, 'APPROVE'::character varying])::text[])),
    action_at   timestamp(6) with time zone,
    action_by   varchar(255),
    description varchar(255),
    doc_id      bigint
);

-- alter table histories  owner to postgres;