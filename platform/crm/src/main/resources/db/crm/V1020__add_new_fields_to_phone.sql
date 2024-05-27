ALTER TABLE crm.phone_contact
    ADD COLUMN active BOOLEAN NOT NULL DEFAULT false,
    ADD COLUMN active_till date,
    ADD COLUMN source text,
    ADD COLUMN legal_consent BOOLEAN NOT NULL DEFAULT true,
    ALTER COLUMN phone_type TYPE text;

UPDATE crm.phone_contact
SET source     = 'REGISTRATION',
    phone_type = case WHEN phone_type = '0' THEN 'MOBILE' else 'OTHER' end;

ALTER TABLE crm.phone_contact
    ALTER COLUMN source SET NOT NULL;

UPDATE crm.phone_contact
SET active = true
WHERE is_primary = true
   OR id in (
    SELECT phone.id
    FROM crm.phone_contact phone
    WHERE phone.is_primary = false
      and phone.client_id = client_id
    ORDER BY created_at desc
    limit 1);


ALTER TABLE crm.phone_contact_audit
    ADD COLUMN active BOOLEAN NOT NULL DEFAULT false,
    ADD COLUMN active_till date,
    ADD COLUMN source text,
    ADD COLUMN legal_consent BOOLEAN NOT NULL DEFAULT true,
    ALTER COLUMN phone_type TYPE text;
