-- OLD INDEXES
CREATE INDEX IF NOT EXISTS idx_email_log_created_at ON email.log (created_at);

-- Improve performance of email sending scheduler
CREATE INDEX IF NOT EXISTS idx_email_log_sending_status_pending ON email.log (sending_status)
  WHERE sending_status = 'PENDING';
