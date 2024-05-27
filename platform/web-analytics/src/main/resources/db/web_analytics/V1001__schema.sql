CREATE TABLE web_analytics.event (
  id             INT8      NOT NULL,
  created_at     TIMESTAMP NOT NULL,
  created_by     TEXT,
  entity_version INT8      NOT NULL,
  updated_at     TIMESTAMP NOT NULL,
  updated_by     TEXT,
  application_id INT8,
  client_id      INT8,
  event_type     TEXT      NOT NULL,
  ip_address     TEXT,
  loan_id        INT8,
  utm_campaign   TEXT,
  utm_content    TEXT,
  utm_medium     TEXT,
  utm_source     TEXT,
  utm_term       TEXT,
  PRIMARY KEY (id)
);

CREATE INDEX idx_web_analytics_event_client_id
  ON web_analytics.event (client_id);
CREATE INDEX idx_web_analytics_event_created_at
  ON web_analytics.event (created_at);
