SET search_path = dowjones;

alter table request drop column reason;
alter table request_audit drop column reason;
drop INDEX IF EXISTS idx_dowjones_client_id;

alter table match alter column score type numeric(19,4) USING score::numeric(19,4);
alter table match_audit alter column score type numeric(19,4) USING score::numeric(19,4);

alter table search_result add column request_id INT8;
alter table search_result_audit add column request_id INT8;

alter table search_result add constraint fk_dowjones_request_id FOREIGN KEY (request_id) references request(id);

update search_result s set request_id = (select max(r.id) from request r where r.client_id = s.client_id);
alter table search_result drop column client_id;
alter table search_result_audit drop column client_id;

CREATE INDEX IF NOT EXISTS idx_dowjones_search_result_request_id
    ON search_result USING btree (request_id);

alter table search_result alter column request_id set not null;
