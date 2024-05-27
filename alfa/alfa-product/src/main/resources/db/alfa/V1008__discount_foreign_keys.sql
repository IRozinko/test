ALTER TABLE lending.discount ADD CONSTRAINT discount_client_fk FOREIGN KEY (client_id) REFERENCES crm.client (id);
ALTER TABLE lending.loan_application ADD CONSTRAINT loan_application_discount_fk FOREIGN KEY (discount_id) REFERENCES lending.discount (id);
ALTER TABLE lending.loan ADD CONSTRAINT loan_discount_fk FOREIGN KEY (discount_id) REFERENCES lending.discount (id);
