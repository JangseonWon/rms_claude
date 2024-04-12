create schema rms_dev;
SET search_path TO rms_dev;
create table audit
(
    create_at timestamp(6) not null,
    create_by varchar(128) not null,
    method    varchar(8),
    url       varchar(64),
    value     jsonb,
    primary key (create_at, create_by)
);

alter table audit
    owner to postgres;

create table category
(
    id         uuid        not null
        primary key,
    name       varchar(64) not null,
    order_type varchar(64) not null
);

alter table category
    owner to postgres;
INSERT INTO category (id, name, order_type) VALUES ('38fecf42-1404-490f-ab97-37ed7eeecd78', 'Precision Oncology', 'SINGLE');
INSERT INTO category (id, name, order_type) VALUES ('9b488043-ee87-447a-bd9b-000815fb0e98', 'Pre & Neonatal', 'MULTIPLE');
INSERT INTO category (id, name, order_type) VALUES ('a57e0b55-ee39-4544-a835-74b5aa4a25ef', 'Rare Disease', 'SINGLE');
INSERT INTO category (id, name, order_type) VALUES ('e3205ea8-5f6b-4731-9871-4fcfed5382cc', 'Health Checkup', 'MULTIPLE');
INSERT INTO category (id, name, order_type) VALUES ('64f9c0cd-4302-4368-955d-007ea784093e', 'Others', 'MULTIPLE');
INSERT INTO category (id, name, order_type) VALUES ('41c4da03-07e9-4729-aeff-2cd2b7e73e32', 'test Single', 'SINGLE');
INSERT INTO category (id, name, order_type) VALUES ('548b5466-6a0b-48d1-a82e-4b36e40d8089', 'test Single1', 'SINGLE');
INSERT INTO category (id, name, order_type) VALUES ('73939b87-cb67-4cca-b501-87862cbdab94', 'test Multi1', 'MULTIPLE');
INSERT INTO category (id, name, order_type) VALUES ('4cdec48b-5b39-4c93-a00b-57b463fd1286', 'test Multi2', 'MULTIPLE');
INSERT INTO category (id, name, order_type) VALUES ('f5823b6e-ca50-4f21-9eba-c697db09bff1', 'test Multi2', 'MULTIPLE');
INSERT INTO category (id, name, order_type) VALUES ('6080bec5-9648-415a-a0b3-0b4e45c3f665', 'test Multi3', 'MULTIPLE');


create table extension
(
    id    varchar(64) not null
        primary key,
    name  varchar(64) not null,
    regex varchar(64) not null
);

alter table extension
    owner to postgres;

create table sample_type
(
    id   varchar(64) not null
        primary key,
    name varchar(64) not null
);

alter table sample_type
    owner to postgres;

create table service
(
    id          varchar(8)  not null
        primary key,
    name        varchar(64) not null,
    category_id uuid
        constraint fk34cq08yegp653556yhfv601rc
            references category
);

INSERT INTO service (id, name, category_id) VALUES ('test', '테스트 서비스', '41c4da03-07e9-4729-aeff-2cd2b7e73e32');
INSERT INTO service (id, name, category_id) VALUES ('test1', '테스트 서비스', '41c4da03-07e9-4729-aeff-2cd2b7e73e32');
INSERT INTO service (id, name, category_id) VALUES ('test2', '테스트 서비스', '41c4da03-07e9-4729-aeff-2cd2b7e73e32');


alter table service
    owner to postgres;

create table service_extension
(
    extension_id varchar(255) not null
        constraint fkj78e4jamyyfbca4dwa87ue6xt
            references extension,
    service_id   varchar(255) not null
        constraint fkgwyfd1jqng5t2d0s4bh5tsth2
            references service,
    required     boolean      not null,
    primary key (extension_id, service_id)
);

alter table service_extension
    owner to postgres;

create table service_sample_type
(
    sample_type_id varchar(255) not null
        constraint fkc5tov701yq3abioa0gqeo1p3g
            references sample_type,
    service_id     varchar(255) not null
        constraint fkd9xckulxu3ylc3plwdr5s47p0
            references service,
    primary key (sample_type_id, service_id)
);

alter table service_sample_type
    owner to postgres;

create table "user"
(
    id            varchar(64)  not null
        primary key,
    branch_name   varchar(64)  not null,
    branch_serial varchar(64)  not null,
    email         varchar(64),
    key           uuid,
    name          varchar(64)  not null,
    password      varchar(64)  not null,
    phone_number  varchar(64),
    role          varchar(64)  not null,
    state         varchar(64)  not null,
    type          varchar(64)  not null,
    create_at     timestamp(6) not null
);

alter table "user"
    owner to postgres;

create table "order"
(
    id        uuid        not null
        primary key,
    create_at timestamp(6),
    serial    varchar(255)
        constraint uk_q726vm76laai1jac1wulya02i
            unique
        constraint ukq726vm76laai1jac1wulya02i
            unique,
    user_id   varchar(64) not null
        constraint fkt7abetueht6dd1gs9jyl3o4t7
            references "user"
);

alter table "order"
    owner to postgres;

create table organization
(
    id                  varchar(255) not null,
    user_id             varchar(255) not null
        constraint fkq0435723w14233u7xu6r92xev
            references "user",
    name                varchar(64)  not null,
    nursing_number      varchar(64),
    registration_number varchar(64),
    type                varchar(64),
    primary key (id, user_id)
);

alter table organization
    owner to postgres;

create table patient
(
    organization_id varchar(255) not null,
    serial          varchar(255) not null,
    user_id         varchar(255) not null,
    birth_day       numeric(2),
    birth_month     numeric(2),
    birth_year      numeric(4),
    name            varchar(64)  not null,
    sex             varchar(1),
    primary key (organization_id, serial, user_id),
    constraint fkrv491pwpdj4hh0fj89hfifwqn
        foreign key (organization_id, user_id) references organization
);

alter table patient
    owner to postgres;

create table sample
(
    id              uuid        not null
        primary key,
    age             integer,
    barcode         varchar(64)
        constraint uk_50n3bcm9hagnwh4u6q32svscx
            unique
        constraint uk50n3bcm9hagnwh4u6q32svscx
            unique,
    create_at       timestamp(6),
    quantity        integer     not null,
    resample_reason varchar(255),
    sampling_on     date        not null,
    user_sample_id  varchar(255),
    organization_id varchar(255),
    patient_serial  varchar(255),
    user_id         varchar(255),
    sample_type_id  varchar(64) not null
        constraint fkpn1l2n3ch1y8o40rad7yymn0p
            references sample_type,
    constraint fka9lp8gi9mk1ejryk61a2r91oj
        foreign key (organization_id, patient_serial, user_id) references patient
);

alter table sample
    owner to postgres;

create table request
(
    order_id         uuid         not null
        constraint fk5y7b042gqc2phr3k5itq1lp6y
            references "order",
    sample_id        uuid         not null
        constraint fkg72qaxec9bylwxjjc93k5iwpa
            references sample,
    service_id       varchar(255) not null
        constraint fkows6i5dlelxfga5nbfq6ilyku
            references service,
    cart_at          timestamp(6),
    complete_at      timestamp(6),
    create_at        timestamp(6),
    credit           boolean,
    department       varchar(64),
    emp_id           varchar(64),
    emp_mobile       varchar(64),
    emp_name         varchar(64),
    last_modify_at   timestamp(6) not null,
    memo             varchar(255),
    outsourcing_cost integer,
    physician        varchar(64),
    price            integer,
    resample_at      timestamp(6),
    specified_at     timestamp(6),
    status           varchar(64)  not null,
    test             boolean,
    user_service_id  varchar(64)  not null,
    ward             varchar(64),
    primary key (order_id, sample_id, service_id)
);

alter table request
    owner to postgres;

create index idxspkquc42s2o6ykfe2r7qeccqy
    on sample (user_id, user_sample_id);

create table sample_extension
(
    extension_id varchar(255) not null
        constraint fk7tm1jxoeqagxvxw04ew4xhocv
            references extension,
    sample_id    uuid         not null
        constraint fkpotudlsrtmnsksavyvry5fk2v
            references sample,
    value        varchar(64)  not null,
    primary key (extension_id, sample_id)
);

alter table sample_extension
    owner to postgres;

create table user_service
(
    service_id varchar(255) not null
        constraint fkh3j8qx28ovbd1pxoyetawnns6
            references service,
    user_id    varchar(255) not null
        constraint fkptefqjlemxwhxfuqa23ht4tos
            references "user",
    create_at  timestamp(6) not null,
    primary key (service_id, user_id)
);

alter table user_service
    owner to postgres;

create table report
(
    id          uuid         not null
        primary key,
    create_at   timestamp(6) not null,
    is_latest   boolean      not null,
    reported_at timestamp(6),
    type        varchar(64)  not null,
    value       varchar(255) not null,
    order_id    uuid,
    sample_id   uuid,
    service_id  varchar(255),
    constraint fkd7ynlk103p526qkuxjhcmtnn9
        foreign key (order_id, sample_id, service_id) references request
);

alter table report
    owner to postgres;

