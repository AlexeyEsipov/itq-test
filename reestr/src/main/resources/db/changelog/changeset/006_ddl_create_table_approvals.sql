--liquibase formatted sql
--changeset esipov:create_approvals_table
create table approvals
(
    id          bigint primary key default nextval('approval_id_seq'),
    approved_at timestamp(6) with time zone,
    approver    varchar(255),
    comment     varchar(1000),
    decision    varchar(255)
        constraint doc_approval_decision_check
            check ((decision)::text = ANY
        ((ARRAY ['APPROVED'::character varying, 'REJECTED'::character varying])::text[])),
    doc_id      bigint
);

-- alter table approvals owner to postgres;