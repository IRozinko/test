SET search_path = lending;

ALTER TABLE loan
  ADD COLUMN interest_strategy_id BIGINT,
  ADD COLUMN penalty_strategy_id BIGINT,
  ADD COLUMN extension_strategy_id BIGINT,
  ADD COLUMN fee_strategy_id BIGINT;

ALTER TABLE loan_audit
  ADD COLUMN interest_strategy_id BIGINT,
  ADD COLUMN penalty_strategy_id BIGINT,
  ADD COLUMN extension_strategy_id BIGINT,
  ADD COLUMN fee_strategy_id BIGINT;

ALTER TABLE loan_application
  ADD COLUMN interest_strategy_id BIGINT,
  ADD COLUMN penalty_strategy_id BIGINT,
  ADD COLUMN extension_strategy_id BIGINT,
  ADD COLUMN fee_strategy_id BIGINT;

ALTER TABLE loan_application_audit
  ADD COLUMN interest_strategy_id BIGINT,
  ADD COLUMN penalty_strategy_id BIGINT,
  ADD COLUMN extension_strategy_id BIGINT,
  ADD COLUMN fee_strategy_id BIGINT;
