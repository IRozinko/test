-- OLD INDEXES

-- Improve performance of affiliate event reporting scheduler
CREATE INDEX IF NOT EXISTS idx_eaffiliate_event_report_status_pending on affiliate.event (report_status)
  WHERE report_status = 'PENDING';
