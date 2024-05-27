# Line Of Credit

## Getting started

### Setup local databases on PostgreSQL:
```
CREATE USER fintech WITH PASSWORD 'fintech';
CREATE DATABASE template_loc WITH OWNER fintech;
\c template_loc;
CREATE EXTENSION pg_trgm;
\c postgres;

CREATE DATABASE loc WITH OWNER fintech TEMPLATE template_loc;

CREATE USER itest WITH PASSWORD 'itest';
ALTER USER itest WITH SUPERUSER;
CREATE DATABASE itest WITH OWNER itest TEMPLATE template_loc;
ALTER USER itest CREATEDB;
```    

## Running

To reset DB on app start, add system property:
```
-Ddb.reset=true
```
To enable/disable demo data, add system property:
```
 -DdemoData.enabled=true
```
To run with real integrations, set spring profiles:
```
-Dspring.profiles.active=integrations
```
To speed up application or test start times, add following JVM args:
```
-Xms512m -Xmx512m -XX:+TieredCompilation -XX:TieredStopAtLevel=1 -Xverify:none 
``` 

## Production
To make sure that reset-db can not happen by accident, create this table in productin database:
```
create table prod ( prod boolean default true);
```

### For restoring prod backup locally
```
CREATE USER presto_app WITH PASSWORD 'fintech';
alter user presto_app WITH SUPERUSER;

DROP DATABASE IF EXISTS presto_app;
CREATE DATABASE presto_app with owner presto_app;
```

```
psql presto_app < /tmp/presto.sql
```

```
Or run support/restore_db.sh script
```


Backend:
```
-DdemoData.enabled=false -Ddb.reset=false -Ddb.user=presto_app -Ddb.password=fintech -Ddb.url=jdbc:postgresql://localhost:5432/presto_app
```    

Backoffice:
```
-Dspring.datasource.username=presto_app -Dspring.datasource.password=fintech -Dspring.datasource.url=jdbc:postgresql://localhost:5432/presto_app
```    

p
