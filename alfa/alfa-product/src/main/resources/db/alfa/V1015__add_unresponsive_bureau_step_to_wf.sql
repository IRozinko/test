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
                   WHERE wa.workflow_id = w.id AND wa.name = 'UnresponsiveBureauRules'
               )
  ) LOOP
    --  Check if there are pending PrepareOffer steps
    IF exists(SELECT *
              FROM workflow.activity
              WHERE activity.workflow_id = wf.id AND name = 'PrepareOffer' AND status = 'WAITING')
    THEN
      IF exists(SELECT *
                FROM workflow.activity
                WHERE activity.workflow_id = wf.id AND (
                  (name in ('ExperianCaisResumenRun2','ExperianCaisOperacionesRun2','EquifaxRun2') AND status = 'COMPLETED' AND resolution='SKIP')
                  OR
                  (name in ('ExperianRulesRun2') AND status = 'COMPLETED' AND resolution='APPROVE')
                )
      )
      THEN
        act_resolution := '';
        act_status := 'ACTIVE';
      ELSE
        act_resolution := '';
        act_status := 'WAITING';
      END IF;

      INSERT INTO workflow.activity (id, workflow_id, name, actor, attempts, resolution, status, created_at, updated_at, entity_version,created_by,updated_by)
      VALUES
        (nextval('common.id_seq'), wf.id, 'UnresponsiveBureauRules', 'SYSTEM', 0, act_resolution, act_status, now(), now(), 0,'system:backend:install','system:backend:install');
    END IF;
  END LOOP;
END;
$$
LANGUAGE plpgsql;

SELECT upgrade();

DROP FUNCTION IF EXISTS upgrade();
