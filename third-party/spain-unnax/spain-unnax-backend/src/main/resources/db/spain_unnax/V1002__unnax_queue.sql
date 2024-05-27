SET search_path = spain_unnax, pg_catalog;

CREATE TABLE disbursement_queue
(
  id              bigint                      NOT NULL PRIMARY KEY,
  created_at      timestamp without time zone NOT NULL,
  created_by      text,
  entity_version  bigint                      NOT NULL,
  updated_at      timestamp without time zone NOT NULL,
  updated_by      text,

  disbursement_id bigint                      not null,
  status          text,
  attempts        integer
);

CREATE TABLE disbursement_queue_audit
(
  id              bigint                      NOT NULL PRIMARY KEY,
  rev             INT4                        NOT NULL,
  revtype         INT2,
  created_at      timestamp without time zone NOT NULL,
  created_by      text,
  entity_version  bigint                      NOT NULL,
  updated_at      timestamp without time zone NOT NULL,
  updated_by      text,

  disbursement_id bigint                      not null,
  status          text,
  attempts        integer
);
