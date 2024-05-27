create table spain_experian.cais_debt (
    id int8 not null,
    created_at TIMESTAMP WITH TIME ZONE not null,
    created_by text,
    entity_version int8 not null,
    updated_at TIMESTAMP WITH TIME ZONE not null,
    updated_by text,
    application_id int8,
    client_id int8,
    fecha_fin date,
    fecha_inicio date,
    frecuencia_pago_codigo text,
    frecuencia_pago_description text,
    id_operacion text,
    importe_cuota numeric(19, 2),
    informante text,
    numero_cuotas_impagadas int4,
    saldo_impagado numeric(19, 2) not null,
    situacion_pago_codigo text,
    situacion_pago_description text,
    tipo_interviniente_codigo text,
    tipo_interviniente_description text,
    tipo_producto_financiado_codigo text,
    tipo_producto_financiado_description text,
    operaciones_response_id int8 not null,
    primary key (id)
);

create index idx_cais_debt_client_id on spain_experian.cais_debt (client_id);

alter table spain_experian.cais_debt
    add constraint FK5h3l0pcesqkd938lvv57h0ase
foreign key (operaciones_response_id)
references spain_experian.cais_operaciones;
