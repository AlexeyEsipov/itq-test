--liquibase formatted sql
--changeset esipov:create_docs_table
create table docs
(
    id BIGINT PRIMARY KEY DEFAULT nextval('docs_id_seq'),
    created_at timestamp(6) with time zone,
    created_by varchar(255),
    inner_id   varchar(255),
    status     varchar(255)
        constraint docs_status_check
            check ((status)::text =
                ANY ((ARRAY
                       ['DRAFT'::character varying,
                        'SUBMITTED'::character varying,
                        'APPROVED'::character varying]
                      )::text[])),
    title      varchar(255),
    updated_at timestamp(6) with time zone,
    updated_by varchar(255)--,
);


