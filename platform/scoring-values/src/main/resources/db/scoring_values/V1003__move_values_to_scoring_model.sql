SET search_path = scoring_values;

ALTER TABLE scoring_model
    ADD COLUMN values jsonb DEFAULT '[]'::jsonb;

UPDATE scoring_model sm
SET values =
        (
            select json_agg(json_build_object(
                    'key', key,
                    'val', value,
                    'src', source,
                    'type', type))
            from scoring_values.scoring_value
            where model_id = sm.id
        );
