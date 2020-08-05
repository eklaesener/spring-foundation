drop table if exists building;

create table building (
    id bigint not null primary key,
    name varchar(255) not null,
    street varchar(255) not null,
    street_number int not null,
    city varchar(255) not null,
    zip_code int not null,
    country varchar(255) not null,
    constraint building_uc_name unique (name),
    constraint building_chk_positive_numbers check (street_number > 0 AND zip_code > 0)
);

create sequence building_sequence start with 1 increment by 3;
