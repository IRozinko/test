CREATE TABLE instantor.response_attribute (
    response_id bigint NOT NULL,
    value text,
    key text NOT NULL
);

ALTER TABLE ONLY instantor.response_attribute
    ADD CONSTRAINT response_attribute_pkey PRIMARY KEY (response_id, key);


ALTER TABLE instantor.response_attribute ADD CONSTRAINT fk_response_attribute_response_id FOREIGN KEY (response_id) REFERENCES instantor.response (id);
CREATE INDEX IF NOT EXISTS idx_response_attribute_response_id ON instantor.response_attribute USING btree (response_id);
