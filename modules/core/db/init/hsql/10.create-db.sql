-- begin DDCDI_IMPORT_LOG
create table DDCDI_IMPORT_LOG (
    ID varchar(36) not null,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    FILE_ID varchar(36) not null,
    STARTED_AT timestamp,
    FINISHED_AT timestamp,
    ENTITIES_PROCESSED integer,
    SCENARIO_ID varchar(36) not null,
    --
    primary key (ID)
)^
-- end DDCDI_IMPORT_LOG
-- begin DDCDI_IMPORT_LOG_RECORD
create table DDCDI_IMPORT_LOG_RECORD (
    ID varchar(36) not null,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    MESSAGE varchar(255) not null,
    LEVEL_ varchar(50) not null,
    TIME_ timestamp not null,
    STACKTRACE longvarchar,
    IMPORT_LOG_ID varchar(36) not null,
    --
    primary key (ID)
)^
-- end DDCDI_IMPORT_LOG_RECORD
-- begin DDCDI_IMPORT_SCENARIO
create table DDCDI_IMPORT_SCENARIO (
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
    TEMPLATE_ID varchar(36),
    COMMENT_ longvarchar,
    IMPORTER_BEAN_NAME varchar(255) not null,
    --
    primary key (ID)
)^
-- end DDCDI_IMPORT_SCENARIO
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
