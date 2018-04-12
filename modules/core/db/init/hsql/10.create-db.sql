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
    SUCCESS boolean not null,
    CONFIGURATION_ID varchar(36) not null,
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
-- begin DDCDI_IMPORT_CONFIGURATION
create table DDCDI_IMPORT_CONFIGURATION (
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
    ENTITY_CLASS varchar(255) not null,
    AD_HOC boolean,
    TEMPLATE_ID varchar(36),
    COMMENT_ longvarchar,
    IMPORTER_BEAN_NAME varchar(255),
    DATE_FORMAT varchar(255),
    BOOLEAN_TRUE_VALUE varchar(255),
    BOOLEAN_FALSE_VALUE varchar(255),
    --
    primary key (ID)
)^
-- end DDCDI_IMPORT_CONFIGURATION
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
-- begin DDCDI_IMPORT_ATTRIBUTE_MAPPER
create table DDCDI_IMPORT_ATTRIBUTE_MAPPER (
    ID varchar(36) not null,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    CONFIGURATION_ID varchar(36) not null,
    ENTITY_ATTRIBUTE varchar(255) not null,
    FILE_COLUMN_NUMBER integer not null,
    FILE_COLUMN_ALIAS varchar(255),
    CUSTOM_ATTRIBUTE_BIND_SCRIPT longvarchar,
    --
    primary key (ID)
)^
-- end DDCDI_IMPORT_ATTRIBUTE_MAPPER
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
-- begin DDCDI_UNIQUE_CONFIGURATION_ATTRIBUTE
create table DDCDI_UNIQUE_CONFIGURATION_ATTRIBUTE (
    ID varchar(36) not null,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    ENTITY_ATTRIBUTE varchar(255) not null,
    UNIQUE_CONFIGURATION_ID varchar(36) not null,
    --
    primary key (ID)
)^
-- end DDCDI_UNIQUE_CONFIGURATION_ATTRIBUTE
-- begin DDCDI_UNIQUE_CONFIGURATION
create table DDCDI_UNIQUE_CONFIGURATION (
    ID varchar(36) not null,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    POLICY varchar(50) not null,
    IMPORT_CONFIGURATION_ID varchar(36) not null,
    --
    primary key (ID)
)^
-- end DDCDI_UNIQUE_CONFIGURATION
