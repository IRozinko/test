-- OLD INDEXES
CREATE INDEX IF NOT EXISTS idx_sms_log_created_at ON sms.log (created_at);

-- Improve performance of sms sending scheduler
CREATE INDEX IF NOT EXISTS idx_sms_log_sending_status_pending ON sms.log (sending_status)
  WHERE sending_status = 'PENDING';
