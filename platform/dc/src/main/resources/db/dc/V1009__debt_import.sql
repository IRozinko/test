CREATE TABLE dc.debt_import (
                             id bigint NOT NULL,
                             created_at timestamp without time zone NOT NULL,
                             created_by text,
                             entity_version bigint NOT NULL,
                             updated_at timestamp without time zone NOT NULL,
                             updated_by text,
                             name text NOT NULL,
                             debt_import_format text,
                             disabled boolean NOT NULL DEFAULT FALSE,
                             code text

);

ALTER TABLE ONLY dc.debt_import
    ADD CONSTRAINT debt_import_pkey PRIMARY KEY (id);

INSERT INTO dc.debt_import (id, created_at, created_by, entity_version, updated_at, updated_by, name,
                                 debt_import_format, disabled, code)
VALUES (1::bigint, now(), null::text, 1::bigint,
        now(), null::text, 'Moneyman'::text, 'moneyman_xls'::text,
        false::boolean, '1'::text),
       (2::bigint, now(), null::text, 1::bigint,
        now(), null::text, 'Vivus'::text, 'vivus_xls'::text,
        false::boolean, '1'::text),
       (3::bigint, now(), null::text, 1::bigint,
        now(), null::text, 'Universal'::text, 'universal_xls'::text,
        false::boolean, '1'::text);
