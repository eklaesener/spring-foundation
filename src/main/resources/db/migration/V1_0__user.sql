drop table if exists user;

create table user (
    id bigint not null primary key,
    username varchar(255) not null,
    mail_address varchar(255) not null,
    password varchar(255) not null,
    locked bool not null,
    force_change_password bool not null,
    first_name varchar(255) null,
    last_name varchar(255) null
);

create unique index user_idx_username ON user (username);
alter table user add constraint user_uc_username unique (username);

create unique index user_idx_mail_address ON user (mail_address);
alter table user add constraint user_uc_mail_address unique (mail_address);

create sequence user_sequence start with 1 increment by 3;

drop table if exists user_authorities;

create table user_authorities (
    user_id bigint not null,
    authority integer not null,
    constraint user_authorities_fk_user foreign key (user_id) references user (id) on delete cascade
);

create table password_dictionary (
    password varchar(255) not null primary key
)
