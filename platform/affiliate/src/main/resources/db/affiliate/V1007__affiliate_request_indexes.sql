CREATE INDEX affiliate_request_json_iban ON affiliate.affiliate_request ((request ->> 'IBAN'));
CREATE INDEX affiliate_request_json_phone ON affiliate.affiliate_request ((request ->> 'phone'));
CREATE INDEX affiliate_request_json_email ON affiliate.affiliate_request ((request ->> 'email'));
CREATE INDEX affiliate_request_json_dni ON affiliate.affiliate_request ((request ->> 'id_doc_number'));
