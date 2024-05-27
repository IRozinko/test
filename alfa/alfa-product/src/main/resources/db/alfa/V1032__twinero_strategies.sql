SET search_path = alfa;

CREATE TABLE alfa_extension_strategy (
  id                      BIGINT                   NOT NULL,
  created_at              TIMESTAMP WITH TIME ZONE NOT NULL,
  created_by              TEXT,
  entity_version          BIGINT                   NOT NULL,
  updated_at              TIMESTAMP WITH TIME ZONE NOT NULL,
  updated_by              TEXT,

  calculation_strategy_id BIGINT                   NOT NULL,
  rate                    NUMERIC(19, 2)           NOT NULL,
  term                    BIGINT                   NOT NULL,

  PRIMARY KEY (id)
);

CREATE INDEX idx_alfa_ext_str_str_id
  ON alfa_extension_strategy (calculation_strategy_id);

ALTER TABLE alfa_extension_strategy
  ADD CONSTRAINT fk_extension_calc_strat FOREIGN KEY (calculation_strategy_id) REFERENCES strategy.calculation_strategy (id);

CREATE TABLE alfa_monthly_interest_strategy (
  id                      BIGINT                   NOT NULL,
  created_at              TIMESTAMP WITH TIME ZONE NOT NULL,
  created_by              TEXT,
  entity_version          BIGINT                   NOT NULL,
  updated_at              TIMESTAMP WITH TIME ZONE NOT NULL,
  updated_by              TEXT,

  calculation_strategy_id BIGINT                   NOT NULL,
  interest_rate           NUMERIC(19, 2)           NOT NULL,

  PRIMARY KEY (id)
);

CREATE INDEX idx_alfa_mon_int_str_id
  ON alfa_monthly_interest_strategy (calculation_strategy_id);

ALTER TABLE alfa_monthly_interest_strategy
  ADD CONSTRAINT fk_mon_inter_calc_strat FOREIGN KEY (calculation_strategy_id) REFERENCES strategy.calculation_strategy (id);

CREATE TABLE alfa_daily_penalty_strategy (
  id                      BIGINT                   NOT NULL,
  created_at              TIMESTAMP WITH TIME ZONE NOT NULL,
  created_by              TEXT,
  entity_version          BIGINT                   NOT NULL,
  updated_at              TIMESTAMP WITH TIME ZONE NOT NULL,
  updated_by              TEXT,

  calculation_strategy_id BIGINT                   NOT NULL,
  penalty_rate            NUMERIC(19, 2)           NOT NULL,

  PRIMARY KEY (id)
);

CREATE INDEX idx_alfa_day_pen_str_id
  ON alfa_daily_penalty_strategy (calculation_strategy_id);

ALTER TABLE alfa_daily_penalty_strategy
  ADD CONSTRAINT fk_daily_pen_calc_strat FOREIGN KEY (calculation_strategy_id) REFERENCES strategy.calculation_strategy (id);

-- audit tables

CREATE TABLE alfa_extension_strategy_audit (
  id                      BIGINT         NOT NULL,
  rev                     INTEGER        NOT NULL,
  revtype                 SMALLINT,
  created_at              TIMESTAMP WITH TIME ZONE,
  created_by              TEXT,
  updated_at              TIMESTAMP WITH TIME ZONE,
  updated_by              TEXT,

  calculation_strategy_id BIGINT         NOT NULL,
  rate                    NUMERIC(19, 2) NOT NULL,
  term                    BIGINT         NOT NULL,

  PRIMARY KEY (id, rev)
);

CREATE TABLE alfa_monthly_interest_strategy_audit (
  id                      BIGINT         NOT NULL,
  rev                     INTEGER        NOT NULL,
  revtype                 SMALLINT,
  created_at              TIMESTAMP WITH TIME ZONE,
  created_by              TEXT,
  updated_at              TIMESTAMP WITH TIME ZONE,
  updated_by              TEXT,

  calculation_strategy_id BIGINT         NOT NULL,
  interest_rate           NUMERIC(19, 2) NOT NULL,

  PRIMARY KEY (id, rev)
);

CREATE TABLE alfa_daily_penalty_strategy_audit (
  id                      BIGINT         NOT NULL,
  rev                     INTEGER        NOT NULL,
  revtype                 SMALLINT,
  created_at              TIMESTAMP WITH TIME ZONE,
  created_by              TEXT,
  updated_at              TIMESTAMP WITH TIME ZONE,
  updated_by              TEXT,

  calculation_strategy_id BIGINT         NOT NULL,
  penalty_rate            NUMERIC(19, 2) NOT NULL,

  PRIMARY KEY (id, rev)
);
