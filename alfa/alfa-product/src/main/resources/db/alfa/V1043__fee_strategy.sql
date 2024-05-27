CREATE TABLE alfa_fee_strategy (
                                                id                      BIGINT                   NOT NULL,
                                                created_at              TIMESTAMP WITH TIME ZONE NOT NULL,
                                                created_by              TEXT,
                                                entity_version          BIGINT                   NOT NULL,
                                                updated_at              TIMESTAMP WITH TIME ZONE NOT NULL,
                                                updated_by              TEXT,

                                                calculation_strategy_id BIGINT                   NOT NULL,
                                                fee_rate                NUMERIC(19, 2)           NOT NULL,
                                                company                 TEXT                     NOT NULL,

                                                PRIMARY KEY (id)
);

CREATE INDEX idx_alfa_fee_str_id
    ON alfa_fee_strategy (calculation_strategy_id);

ALTER TABLE alfa_fee_strategy
    ADD CONSTRAINT fk_fee_calc_strat FOREIGN KEY (calculation_strategy_id) REFERENCES strategy.calculation_strategy (id);
