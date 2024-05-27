create table payment_with_card
(
    id               bigint                   NOT NULL PRIMARY KEY,
    created_at       timestamp with time zone NOT NULL,
    created_by       text,
    entity_version   bigint                   NOT NULL,
    updated_at       timestamp with time zone NOT NULL,
    updated_by       text,

    pan              text,
    bin              text,
    currency         text,
    transaction_type text,
    expiration_date  text,
    expire_month     int,
    expire_year      int,
    card_holder      text,
    card_brand       text,
    card_type        text,
    card_country     text,
    card_bank        text,
    order_code       text unique,
    token            text,
    date             timestamp with time zone,
    amount           decimal(9, 2),
    concept          text,
    state            int,
    processed_at     timestamp without time zone
);

create table payment_with_card_audit
(
    id               bigint                   NOT NULL PRIMARY KEY,
    rev              INT4                     NOT NULL,
    revtype          INT2,
    created_at       timestamp with time zone NOT NULL,
    created_by       text,
    entity_version   bigint                   NOT NULL,
    updated_at       timestamp with time zone NOT NULL,
    updated_by       text,

    pan              text,
    bin              text,
    currency         text,
    transaction_type text,
    expiration_date  text,
    expire_month     int,
    expire_year      int,
    card_holder      text,
    card_brand       text,
    card_type        text,
    card_country     text,
    card_bank        text,
    order_code       text,
    token            text,
    date             timestamp,
    amount           decimal(9, 2),
    concept          text,
    state            int,
    processed_at     timestamp without time zone
);

create table payment_with_transfer_authorized
(
    id              bigint                   NOT NULL PRIMARY KEY,
    created_at      timestamp with time zone NOT NULL,
    created_by      text,
    entity_version  bigint                   NOT NULL,
    updated_at      timestamp with time zone NOT NULL,
    updated_by      text,

    order_code      text unique,
    bank_order_code text,
    amount          decimal(9, 2),
    currency        text,
    customer_code   text,
    customer_names  text,
    service         text,
    status          text,
    success         boolean,
    error_messages  text,
    date            timestamp with time zone,
    processed_at    timestamp without time zone
);

create table payment_with_transfer_authorized_audit
(
    id              bigint                   NOT NULL PRIMARY KEY,
    rev             INT4                     NOT NULL,
    revtype         INT2,
    created_at      timestamp with time zone NOT NULL,
    created_by      text,
    entity_version  bigint                   NOT NULL,
    updated_at      timestamp with time zone NOT NULL,
    updated_by      text,

    order_code      text,
    bank_order_code text,
    amount          decimal(9, 2),
    currency        text,
    customer_code   text,
    customer_names  text,
    service         text,
    status          text,
    success         boolean,
    error_messages  text,
    date            timestamp with time zone,
    processed_at    timestamp without time zone
);

create table payment_with_transfer_completed
(
    id              bigint                   NOT NULL PRIMARY KEY,
    created_at      timestamp with time zone NOT NULL,
    created_by      text,
    entity_version  bigint                   NOT NULL,
    updated_at      timestamp with time zone NOT NULL,
    updated_by      text,

    customer_code text,
    order_code text unique,
    bank_order_code text,
    amount decimal(9, 2),
    date timestamp with time zone,
    success boolean,
    signature text,
    result boolean,
    account_number text,
    status text,
    service text,
    processed_at timestamp with time zone
);

create table payment_with_transfer_completed_audit
(
    id              bigint                   NOT NULL PRIMARY KEY,
    rev             INT4                     NOT NULL,
    revtype         INT2,
    created_at      timestamp with time zone NOT NULL,
    created_by      text,
    entity_version  bigint                   NOT NULL,
    updated_at      timestamp with time zone NOT NULL,
    updated_by      text,

    customer_code text,
    order_code text,
    bank_order_code text,
    amount decimal(9, 2),
    date timestamp with time zone,
    success boolean,
    signature text,
    result boolean,
    account_number text,
    status text,
    service text,
    processed_at timestamp with time zone
);
