CREATE INDEX IF NOT EXISTS idx_debt_execute_at_id ON dc.debt USING btree (execute_at, id);
DROP INDEX IF EXISTS dc.idx_debt_execute_at;

CREATE INDEX IF NOT EXISTS idx_debt_portfolio_agent ON dc.debt USING btree (portfolio, agent);
DROP INDEX IF EXISTS dc.idx_debt_portfolio;
