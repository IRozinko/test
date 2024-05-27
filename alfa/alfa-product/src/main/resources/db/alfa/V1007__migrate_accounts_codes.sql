UPDATE payment.institution_account
SET accounting_account_code = (CASE
                               WHEN accounting_account_code = '57202.1' THEN '57202.0' -- CAIXA
                               WHEN accounting_account_code = '57209.1' THEN '57209.0' -- SABADELL
                               WHEN accounting_account_code = '572012.1' THEN '572012.0' -- BBVA
                               WHEN accounting_account_code = '572015.1' THEN '572015.0' -- ING
                               WHEN accounting_account_code = '57204.1' THEN '57204.0' -- BANKIA
                               WHEN accounting_account_code = '5729.2' THEN '5728.2' -- PAY_TPV
                               ELSE accounting_account_code
                               END
);
