CREATE TABLE lending.discount (id BIGINT NOT NULL, entity_version BIGINT NOT NULL, created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL, created_by TEXT, updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL, updated_by TEXT, client_id BIGINT NOT NULL, rate_in_percent NUMERIC(19, 2) NOT NULL, effective_from DATE, effective_to DATE, max_times_to_apply BIGINT NOT NULL);

CREATE TABLE lending.discount_audit (id BIGINT, rev INTEGER, revtype SMALLINT, created_at TIMESTAMP WITHOUT TIME ZONE, created_by TEXT, updated_at TIMESTAMP WITHOUT TIME ZONE, updated_by TEXT, client_id BIGINT, rate_in_percent NUMERIC(19, 2), effective_from DATE, effective_to DATE, max_times_to_apply BIGINT);

ALTER TABLE ONLY lending.discount
  ADD CONSTRAINT discount_pkey PRIMARY KEY (id);

ALTER TABLE ONLY lending.discount_audit
  ADD CONSTRAINT discount_audit_pkey PRIMARY KEY (id, rev);
