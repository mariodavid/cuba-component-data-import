-- begin DDCDI_MLB_PLAYER
create table DDCDI_MLB_PLAYER (
    ID varchar(36) not null,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    NAME varchar(255),
    TEAM_ID varchar(36),
    HEIGHT integer,
    WEIGHT integer,
    AGE double precision,
    BIRTHDAY date,
    LEFT_HANDED boolean,
    ANNUAL_SALARY decimal(19, 2),
    --
    primary key (ID)
)^
-- end DDCDI_MLB_PLAYER
-- begin DDCDI_MLB_TEAM
create table DDCDI_MLB_TEAM (
    ID varchar(36) not null,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    NAME varchar(255) not null,
    CODE varchar(255) not null,
    STATE varchar(50),
    --
    primary key (ID)
)^
-- end DDCDI_MLB_TEAM

-- begin DDCDI_CUSTOMER
create table DDCDI_CUSTOMER (
    ID varchar(36) not null,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    NAME varchar(255) not null,
    FIRST_NAME varchar(255),
    DOCUMENT_NUMBER varchar(50) not null,
    DESCRIPTION longvarchar,
    PRIORITY integer,
    --
    primary key (ID)
)^
-- end DDCDI_CUSTOMER


-- begin DDCDI_MLB_PLAYER
alter table DDCDI_MLB_PLAYER add constraint FK_DDCDI_MLB_PLAYER_TEAM foreign key (TEAM_ID) references DDCDI_MLB_TEAM(ID)^
create index IDX_DDCDI_MLB_PLAYER_TEAM on DDCDI_MLB_PLAYER (TEAM_ID)^
-- end DDCDI_MLB_PLAYER
-- begin DDCDI_MLB_TEAM
create unique index IDX_DDCDI_MLB_TEAM_CODE_UNQ on DDCDI_MLB_TEAM (CODE, DELETE_TS) ^
-- end DDCDI_MLB_TEAM
