CREATE OR REPLACE FUNCTION upgrade()
  RETURNS VOID AS $$
DECLARE
  entry RECORD;

BEGIN
  FOR entry IN (SELECT
                  wf.id             wf_id,
                  wf.application_id application_id,
                  a.updated_at      updated_at
                FROM workflow.workflow wf
                  JOIN workflow.activity as a ON wf.id = a.workflow_id
                  JOIN lending.loan_application la ON la.workflow_id = wf.id
                WHERE a.resolution = 'EXPIRE' AND (a.name = 'DocumentForm' OR a.name = 'InstantorReview')
                      AND wf.status = 'ACTIVE' AND la.status_detail = 'PENDING') LOOP

    UPDATE workflow.activity
    SET status = 'CANCELLED'
    WHERE workflow_id = entry.wf_id AND (status = 'WAITING' OR status = 'ACTIVE');

    UPDATE workflow.trigger
    SET status = 'CANCELLED'
    WHERE workflow_id = entry.wf_id AND status = 'WAITING';

    UPDATE workflow.workflow
    SET status = 'TERMINATED', completed_at = entry.updated_at, terminate_reason = 'EXPIRED'
    WHERE id = entry.wf_id;

    UPDATE lending.loan_application
    SET status = 'CLOSED', status_detail = 'CANCELLED', close_date = entry.updated_at
    WHERE id = entry.application_id;

  END LOOP;
END;
$$
LANGUAGE plpgsql;

SELECT upgrade();

DROP FUNCTION IF EXISTS upgrade();
