SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;


CREATE SCHEMA IF NOT EXISTS marketing;

SET search_path = marketing;
SET default_tablespace = '';
SET default_with_oids = false;

CREATE TABLE marketing_template
(
    id             bigint                   NOT NULL,
    created_at     timestamp with time zone NOT NULL,
    created_by     text,
    entity_version bigint                   NOT NULL,
    updated_at     timestamp with time zone NOT NULL,
    updated_by     text,
    name           text                     NOT NULL,
    email_body     text                     NOT NULL,
    html_template  text                     NOT NULL,
    image_file_id  bigint                   NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE marketing_template_audit
(
    id            bigint                   NOT NULL,
    rev           integer                  NOT NULL,
    revtype       smallint,
    created_at    timestamp with time zone NOT NULL,
    created_by    text,
    updated_at    timestamp with time zone NOT NULL,
    updated_by    text,
    name          text                     NOT NULL,
    email_body    text                     NOT NULL,
    html_template text                     NOT NULL,
    image_file_id bigint                   NOT NULL
);


CREATE TABLE marketing_campaign
(
    id                            bigint                   NOT NULL,
    created_at                    timestamp with time zone NOT NULL,
    created_by                    text,
    entity_version                bigint                   NOT NULL,
    updated_at                    timestamp with time zone NOT NULL,
    updated_by                    text,
    main_marketing_template_id    bigint                   NOT NULL,
    remind_marketing_template_id  bigint,
    name                          text                     NOT NULL,
    email_subject                 text                     NOT NULL,
    email_body                    text                     NOT NULL,
    sms                           text,
    remind_email_subject          text,
    remind_email_body             text,
    schedule_type                 text,
    remind_interval_hours         int,
    audience_settings_json_config text                     NOT NULL,
    main_promo_code_id            bigint,
    remind_promo_code_id          bigint,
    status                        text                     NOT NULL,
    main_image_file_id            bigint                   NOT NULL,
    remind_image_file_id          bigint,
    has_main_promo_code           boolean                  NOT NULL,
    has_remind_promo_code         boolean                  NOT NULL,
    enable_remind                 boolean                  NOT NULL,
    schedule_date                 timestamp with time zone NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_main_marketing_template_id
        FOREIGN KEY (main_marketing_template_id) REFERENCES marketing_template (id) ON DELETE CASCADE,
    CONSTRAINT fk_remind_marketing_template_id
        FOREIGN KEY (remind_marketing_template_id) REFERENCES marketing_template (id) ON DELETE CASCADE,
    CONSTRAINT fk_main_promo_code_id
        FOREIGN KEY (main_promo_code_id) REFERENCES lending.promo_code (id) ON DELETE RESTRICT,
    CONSTRAINT fk_remind_promo_code_id
        FOREIGN KEY (remind_promo_code_id) REFERENCES lending.promo_code (id) ON DELETE RESTRICT
);


CREATE TABLE marketing_campaign_audit
(
    id                            bigint                   NOT NULL,
    rev                           integer                  NOT NULL,
    revtype                       smallint,
    created_at                    timestamp with time zone,
    created_by                    text,
    updated_at                    timestamp with time zone,
    updated_by                    text,
    main_marketing_template_id    bigint                   NOT NULL,
    remind_marketing_template_id  bigint,
    name                          text                     NOT NULL,
    email_subject                 text                     NOT NULL,
    email_body                    text                     NOT NULL,
    sms                           text,
    remind_email_subject          text,
    remind_email_body             text,
    schedule_type                 text,
    remind_interval_hours         int,
    audience_settings_json_config text                     NOT NULL,
    main_promo_code_id            bigint,
    remind_promo_code_id          bigint,
    status                        text                     NOT NULL,
    main_image_file_id            bigint                   NOT NULL,
    remind_image_file_id          bigint,
    has_main_promo_code           boolean                  NOT NULL,
    has_remind_promo_code         boolean                  NOT NULL,
    enable_remind                 boolean                  NOT NULL,
    schedule_date                 timestamp with time zone NOT NULL
);

CREATE TABLE marketing_communication
(
    id                            bigint                   NOT NULL,
    marketing_campaign_id         bigint                   NOT NULL,
    created_at                    timestamp with time zone NOT NULL,
    created_by                    text,
    entity_version                bigint                   NOT NULL,
    updated_at                    timestamp with time zone NOT NULL,
    updated_by                    text,
    status                        text                     NOT NULL,
    email_subject                 text                     NOT NULL,
    email_body                    text                     NOT NULL,
    sms                           text,
    audience_settings_json_config text                     NOT NULL,
    promo_code_id                 bigint,
    next_action_at                timestamp with time zone,
    reminder                      boolean                  not null,
    click_rate                    NUMERIC(19, 4),
    view_rate                     NUMERIC(19, 4),
    targeted_users                int,
    image_file_id                 bigint                   NOT NULL,
    last_execution_result         text,
    views_hll_hex                 text,
    clicks_hll_hex                text,
    PRIMARY KEY (id),
    CONSTRAINT fk_marketing_campaign_id
        FOREIGN KEY (marketing_campaign_id) REFERENCES marketing_campaign (id) ON DELETE CASCADE
);

CREATE TABLE marketing_communication_audit
(
    id                            bigint                   NOT NULL,
    marketing_campaign_id         bigint                   NOT NULL,
    created_at                    timestamp with time zone NOT NULL,
    created_by                    text,
    rev                           integer                  NOT NULL,
    revtype                       smallint,
    updated_at                    timestamp with time zone NOT NULL,
    updated_by                    text,
    status                        text                     NOT NULL,
    email_subject                 text                     NOT NULL,
    email_body                    text                     NOT NULL,
    sms                           text,
    audience_settings_json_config text                     NOT NULL,
    promo_code_id                 bigint,
    next_action_at                timestamp with time zone,
    reminder                      boolean                  not null,
    click_rate                    NUMERIC(19, 4),
    view_rate                     NUMERIC(19, 4),
    targeted_users                int,
    image_file_id                 bigint                   NOT NULL,
    last_execution_result         text,
    views_hll_hex                 text,
    clicks_hll_hex                text
);
