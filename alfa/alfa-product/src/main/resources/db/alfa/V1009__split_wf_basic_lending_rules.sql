create or replace function upgrade()
  returns void as $$
DECLARE
  wf             RECORD;
  act_resolution text;
  act_status     text;

BEGIN
  FOR wf IN (select w.id
             from workflow.workflow w
             where
               w.name = 'UnderwritingFirstLoan' and
               w.status = 'ACTIVE' and
               not exists(
                   select * from workflow.activity wa where wa.workflow_id = w.id and wa.name = 'CollectBasicInformation'
               )) LOOP
    IF exists(select *
              from workflow.activity
              where activity.workflow_id = wf.id and name = 'BasicLendingRules' and status = 'COMPLETED')
    then
      act_resolution := 'APPROVE';
      act_status := 'COMPLETED';
    else
      act_resolution := '';
      act_status := 'WAITING';
    end if;

    INSERT INTO workflow.activity (id, workflow_id, name, actor, attempts, resolution, status, created_at, updated_at, entity_version) VALUES
      (nextval('common.id_seq'), wf.id, 'CollectBasicInformation', 'SYSTEM', 0, act_resolution, act_status, now(), now(), 0),
      (nextval('common.id_seq'), wf.id, 'MandatoryLendingRules', 'SYSTEM', 0, act_resolution, act_status, now(), now(), 0);

  END LOOP;
END;
$$
language plpgsql;

SELECT upgrade();

DROP FUNCTION IF EXISTS upgrade();
