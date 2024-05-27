--
-- Name: loan_application_attribute; Type: TABLE; Schema: lending; Owner: -
--

CREATE TABLE lending.loan_application_attribute (
  loan_application_id bigint NOT NULL,
  value text,
  key text NOT NULL
);


--
-- Name: loan_application_attribute_audit; Type: TABLE; Schema: lending; Owner: -
--

CREATE TABLE lending.loan_application_attribute_audit (
  rev integer NOT NULL,
  loan_application_id bigint NOT NULL,
  value text NOT NULL,
  key text NOT NULL,
  revtype smallint
);
