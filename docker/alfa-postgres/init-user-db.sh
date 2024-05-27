#!/bin/bash
set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" <<-EOSQL
    CREATE USER fintech WITH PASSWORD 'fintech';
    CREATE DATABASE template_loc WITH OWNER fintech;
    \c template_loc;
    CREATE EXTENSION pg_trgm;
    \c postgres;

    CREATE DATABASE loc WITH OWNER fintech TEMPLATE template_loc;
    GRANT ALL PRIVILEGES ON DATABASE loc TO fintech;

    CREATE USER itest WITH PASSWORD 'itest';
    ALTER USER itest WITH SUPERUSER;
    CREATE DATABASE itest WITH OWNER itest TEMPLATE template_loc;
    ALTER USER itest CREATEDB;

    CREATE USER presto_app WITH PASSWORD 'fintech';
    ALTER USER presto_app WITH SUPERUSER;

    DROP DATABASE IF EXISTS presto_app;
    CREATE DATABASE presto_app with OWNER presto_app TEMPLATE template_loc;

    CREATE ROLE role_ro;
EOSQL
