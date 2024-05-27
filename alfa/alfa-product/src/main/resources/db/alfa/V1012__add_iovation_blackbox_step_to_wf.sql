CREATE OR REPLACE FUNCTION upgrade()
  RETURNS VOID AS $$
DECLARE
  wf             RECORD;
  act_resolution TEXT;
  act_status     TEXT;

BEGIN
  --   Get all current active workflows
  FOR wf IN (SELECT w.id
             FROM workflow.workflow w
             WHERE
               w.name = 'UnderwritingFirstLoan'
               AND
               w.status = 'ACTIVE'
               AND
               NOT exists(
                   SELECT *
                   FROM workflow.activity wa
                   WHERE wa.workflow_id = w.id AND wa.name = 'IovationBlackBox'
               )
  ) LOOP
    --  Check if there are pending Iovation steps
    IF exists(SELECT *
              FROM workflow.activity
              WHERE activity.workflow_id = wf.id AND name = 'Iovation' AND status = 'WAITING')
    THEN
      IF exists(SELECT *
                FROM workflow.activity
                WHERE activity.workflow_id = wf.id AND name = 'PhoneVerification' AND status = 'COMPLETED')
      THEN
        act_resolution := '';
        act_status := 'ACTIVE';
      ELSE
        act_resolution := '';
        act_status := 'WAITING';
      END IF;
      --  Insert only if Iovation is waiting
      INSERT INTO workflow.activity (id, workflow_id, name, actor, attempts, resolution, status, created_at, updated_at, entity_version,created_by,updated_by)
      VALUES
        (nextval('common.id_seq'), wf.id, 'IovationBlackBox', 'CLIENT', 0, act_resolution, act_status, now(), now(), 0,'system:backend:install','system:backend:install');
    END IF;
  END LOOP;
END;
$$
LANGUAGE plpgsql;

SELECT upgrade();

DROP FUNCTION IF EXISTS upgrade();
