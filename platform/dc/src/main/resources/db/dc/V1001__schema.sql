CREATE TABLE dc.action (
  id                    INT8                     NOT NULL,
  created_at            TIMESTAMP WITH TIME ZONE NOT NULL,
  created_by            TEXT,
  entity_version        INT8                     NOT NULL,
  updated_at            TIMESTAMP WITH TIME ZONE NOT NULL,
  updated_by            TEXT,
  action_name           TEXT                     NOT NULL,
  action_status         TEXT                     NOT NULL,
  agent                 TEXT,
  aging_bucket          TEXT                     NOT NULL,
  assigned_to_agent     TEXT,
  client_id             INT8                     NOT NULL,
  comments              TEXT,
  debt_status           TEXT                     NOT NULL,
  debt_status_before    TEXT                     NOT NULL,
  dpd                   INT4                     NOT NULL,
  fee_due               NUMERIC(19, 2)           NOT NULL,
  fee_outstanding       NUMERIC(19, 2)           NOT NULL,
  fee_paid              NUMERIC(19, 2)           NOT NULL,
  interest_due          NUMERIC(19, 2)           NOT NULL,
  interest_outstanding  NUMERIC(19, 2)           NOT NULL,
  interest_paid         NUMERIC(19, 2)           NOT NULL,
  loan_id               INT8                     NOT NULL,
  max_dpd               INT4                     NOT NULL,
  next_action           TEXT,
  next_action_at        TIMESTAMP WITH TIME ZONE,
  penalty_due           NUMERIC(19, 2)           NOT NULL,
  penalty_outstanding   NUMERIC(19, 2)           NOT NULL,
  penalty_paid          NUMERIC(19, 2)           NOT NULL,
  portfolio             TEXT                     NOT NULL,
  portfolio_before      TEXT                     NOT NULL,
  principal_due         NUMERIC(19, 2)           NOT NULL,
  principal_outstanding NUMERIC(19, 2)           NOT NULL,
  principal_paid        NUMERIC(19, 2)           NOT NULL,
  priority              INT4                     NOT NULL,
  promise_amount        NUMERIC(19, 2),
  promise_due_date      DATE,
  resolution            TEXT,
  total_due             NUMERIC(19, 2)           NOT NULL,
  total_outstanding     NUMERIC(19, 2)           NOT NULL,
  total_paid            NUMERIC(19, 2)           NOT NULL,
  debt_id               INT8                     NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE dc.agent (
  id             INT8                     NOT NULL,
  created_at     TIMESTAMP WITH TIME ZONE NOT NULL,
  created_by     TEXT,
  entity_version INT8                     NOT NULL,
  updated_at     TIMESTAMP WITH TIME ZONE NOT NULL,
  updated_by     TEXT,
  agent          TEXT                     NOT NULL,
  disabled       BOOLEAN                  NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE dc.agent_absence (
  id             INT8                     NOT NULL,
  created_at     TIMESTAMP WITH TIME ZONE NOT NULL,
  created_by     TEXT,
  entity_version INT8                     NOT NULL,
  updated_at     TIMESTAMP WITH TIME ZONE NOT NULL,
  updated_by     TEXT,
  date_from      DATE                     NOT NULL,
  date_to        DATE                     NOT NULL,
  reason         TEXT,
  agent_id       INT8                     NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE dc.agent_absence_audit (
  id         INT8 NOT NULL,
  rev        INT4 NOT NULL,
  revtype    INT2,
  created_at TIMESTAMP WITH TIME ZONE,
  created_by TEXT,
  updated_at TIMESTAMP WITH TIME ZONE,
  updated_by TEXT,
  date_from  DATE,
  date_to    DATE,
  reason     TEXT,
  agent_id   INT8,
  PRIMARY KEY (id, rev)
);

CREATE TABLE dc.agent_audit (
  id         INT8 NOT NULL,
  rev        INT4 NOT NULL,
  revtype    INT2,
  created_at TIMESTAMP WITH TIME ZONE,
  created_by TEXT,
  updated_at TIMESTAMP WITH TIME ZONE,
  updated_by TEXT,
  agent      TEXT,
  disabled   BOOLEAN,
  PRIMARY KEY (id, rev)
);

CREATE TABLE dc.agent_portfolio (
  agent_id   INT8 NOT NULL,
  portfolios TEXT
);

CREATE TABLE dc.agent_portfolio_audit (
  rev        INT4 NOT NULL,
  agent_id   INT8 NOT NULL,
  portfolios TEXT NOT NULL,
  revtype    INT2,
  PRIMARY KEY (rev, agent_id, portfolios)
);

CREATE TABLE dc.debt (
  id                       INT8                     NOT NULL,
  created_at               TIMESTAMP WITH TIME ZONE NOT NULL,
  created_by               TEXT,
  entity_version           INT8                     NOT NULL,
  updated_at               TIMESTAMP WITH TIME ZONE NOT NULL,
  updated_by               TEXT,
  agent                    TEXT,
  aging_bucket             TEXT                     NOT NULL,
  auto_assignment_required BOOLEAN                  NOT NULL,
  client_id                INT8                     NOT NULL,
  dpd                      INT4                     NOT NULL,
  execute_at               TIMESTAMP WITH TIME ZONE,
  fee_due                  NUMERIC(19, 2)           NOT NULL,
  fee_outstanding          NUMERIC(19, 2)           NOT NULL,
  fee_paid                 NUMERIC(19, 2)           NOT NULL,
  interest_due             NUMERIC(19, 2)           NOT NULL,
  interest_outstanding     NUMERIC(19, 2)           NOT NULL,
  interest_paid            NUMERIC(19, 2)           NOT NULL,
  last_action              TEXT,
  last_action_at           TIMESTAMP WITH TIME ZONE,
  last_executed_at         TIMESTAMP WITH TIME ZONE,
  last_execution_result    TEXT,
  last_paid                NUMERIC(19, 2),
  last_payment_date        DATE,
  loan_id                  INT8                     NOT NULL,
  loan_number              TEXT                     NOT NULL,
  loan_status              TEXT                     NOT NULL,
  loan_status_detail       TEXT                     NOT NULL,
  maturity_date            DATE                     NOT NULL,
  max_dpd                  INT4                     NOT NULL,
  next_action              TEXT,
  next_action_at           TIMESTAMP WITH TIME ZONE,
  payment_due_date         DATE                     NOT NULL,
  penalty_due              NUMERIC(19, 2)           NOT NULL,
  penalty_outstanding      NUMERIC(19, 2)           NOT NULL,
  penalty_paid             NUMERIC(19, 2)           NOT NULL,
  portfolio                TEXT                     NOT NULL,
  principal_due            NUMERIC(19, 2)           NOT NULL,
  principal_outstanding    NUMERIC(19, 2)           NOT NULL,
  principal_paid           NUMERIC(19, 2)           NOT NULL,
  priority                 INT4                     NOT NULL,
  promise_amount           NUMERIC(19, 2),
  promise_due_date         DATE,
  status                   TEXT                     NOT NULL,
  total_due                NUMERIC(19, 2)           NOT NULL,
  total_outstanding        NUMERIC(19, 2)           NOT NULL,
  total_paid               NUMERIC(19, 2)           NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE dc.settings (
  id             INT8                     NOT NULL,
  created_at     TIMESTAMP WITH TIME ZONE NOT NULL,
  created_by     TEXT,
  entity_version INT8                     NOT NULL,
  updated_at     TIMESTAMP WITH TIME ZONE NOT NULL,
  updated_by     TEXT,
  settings_json  TEXT,
  PRIMARY KEY (id)
);

CREATE TABLE dc.settings_audit (
  id            INT8 NOT NULL,
  rev           INT4 NOT NULL,
  revtype       INT2,
  created_at    TIMESTAMP WITH TIME ZONE,
  created_by    TEXT,
  updated_at    TIMESTAMP WITH TIME ZONE,
  updated_by    TEXT,
  settings_json TEXT,
  PRIMARY KEY (id, rev)
);
CREATE INDEX idx_debt_action_client_id
  ON dc.action (client_id);
CREATE INDEX idx_debt_action_loan_id
  ON dc.action (loan_id);
CREATE INDEX idx_debt_action_debt_id
  ON dc.action (debt_id);

ALTER TABLE dc.agent
  ADD CONSTRAINT UK_5ifyr83t40xpp54dy2dcdn6ju UNIQUE (agent);
CREATE INDEX idx_debt_client_id
  ON dc.debt (client_id);
CREATE INDEX idx_debt_loan_id
  ON dc.debt (loan_id);
CREATE INDEX idx_debt_execute_at
  ON dc.debt (execute_at);

ALTER TABLE dc.debt
  ADD CONSTRAINT UK_c053gl0skxqvj93bdm77mgdf UNIQUE (loan_number);

ALTER TABLE dc.action
  ADD CONSTRAINT FKfk0tox5yv4prl8q7egm6lqlsx
FOREIGN KEY (debt_id)
REFERENCES dc.debt;

ALTER TABLE dc.agent_absence
  ADD CONSTRAINT FKm0mfqm1bet67y00uksfrnta6e
FOREIGN KEY (agent_id)
REFERENCES dc.agent;

ALTER TABLE dc.agent_absence_audit
  ADD CONSTRAINT FKjsw0tj5r3eem6aiek2jmq2cv8
FOREIGN KEY (rev)
REFERENCES common.revision;

ALTER TABLE dc.agent_audit
  ADD CONSTRAINT FK2ikr09ebywpggoq8v8wylj62n
FOREIGN KEY (rev)
REFERENCES common.revision;

ALTER TABLE dc.agent_portfolio
  ADD CONSTRAINT FK8c04ta12jls514t7pffye9sde
FOREIGN KEY (agent_id)
REFERENCES dc.agent;

ALTER TABLE dc.agent_portfolio_audit
  ADD CONSTRAINT FKmwh5kjc8xwjhglwusjlmxofs
FOREIGN KEY (rev)
REFERENCES common.revision;

ALTER TABLE dc.settings_audit
  ADD CONSTRAINT FKi9i4ntjuqlhnxq7tjvxqd6t9s
FOREIGN KEY (rev)
REFERENCES common.revision;

