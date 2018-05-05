-- begin DDCDI_IMPORT_LOG
create table DDCDI_IMPORT_LOG (
    ID varchar2(32),
    VERSION number(10) not null,
    CREATE_TS timestamp,
    CREATED_BY varchar2(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar2(50),
    DELETE_TS timestamp,
    DELETED_BY varchar2(50),
    --
    FILE_ID varchar2(32) not null,
    STARTED_AT timestamp,
    FINISHED_AT timestamp,
    ENTITIES_PROCESSED number(10),
    ENTITIES_IMPORT_SUCCESS number(10),
    ENTITIES_IMPORT_VAL_ERROR number(10),
    ENTITIES_PRE_COMMIT_SKIPPED number(10),
    ENTITIES_UNIQUE_CONSTRAINT_SKIPPED number(10),
    SUCCESS char(1) not null,
    CONFIGURATION_ID varchar2(32) not null,
    --
    primary key (ID)
)^
-- end DDCDI_IMPORT_LOG
-- begin DDCDI_IMPORT_LOG_RECORD
create table DDCDI_IMPORT_LOG_RECORD (
    ID varchar2(32),
    VERSION number(10) not null,
    CREATE_TS timestamp,
    CREATED_BY varchar2(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar2(50),
    DELETE_TS timestamp,
    DELETED_BY varchar2(50),
    --
    MESSAGE varchar2(255) not null,
    LEVEL_ varchar2(50) not null,
    TIME_ timestamp not null,
    STACKTRACE clob,
    IMPORT_LOG_ID varchar2(32) not null,
    --
    primary key (ID)
)^
-- end DDCDI_IMPORT_LOG_RECORD
-- begin DDCDI_IMPORT_CONFIGURATION
create table DDCDI_IMPORT_CONFIGURATION (
    ID varchar2(32),
    VERSION number(10) not null,
    CREATE_TS timestamp,
    CREATED_BY varchar2(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar2(50),
    DELETE_TS timestamp,
    DELETED_BY varchar2(50),
    --
    NAME varchar2(255) not null,
    TRANSACTION_STRATEGY varchar2(50) not null,
    ENTITY_CLASS varchar2(255) not null,
    AD_HOC char(1),
    TEMPLATE_ID varchar2(32),
    COMMENT_ clob,
    IMPORTER_BEAN_NAME varchar2(255),
    DATE_FORMAT varchar2(255),
    BOOLEAN_TRUE_VALUE varchar2(255),
    BOOLEAN_FALSE_VALUE varchar2(255),
    PRE_COMMIT_SCRIPT clob,
    --
    primary key (ID)
)^
-- end DDCDI_IMPORT_CONFIGURATION
-- begin DDCDI_IMPORT_ATTRIBUTE_MAPPER
create table DDCDI_IMPORT_ATTRIBUTE_MAPPER (
    ID varchar2(32),
    VERSION number(10) not null,
    CREATE_TS timestamp,
    CREATED_BY varchar2(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar2(50),
    DELETE_TS timestamp,
    DELETED_BY varchar2(50),
    --
    CONFIGURATION_ID varchar2(32) not null,
    ENTITY_ATTRIBUTE varchar2(255) not null,
    DYNAMIC_ATTRIBUTE char(1),
    FILE_COLUMN_NUMBER number(10) not null,
    FILE_COLUMN_ALIAS varchar2(255),
    CUSTOM_ATTRIBUTE_BIND_SCRIPT clob,
    --
    primary key (ID)
)^
-- end DDCDI_IMPORT_ATTRIBUTE_MAPPER
-- begin DDCDI_UNIQUE_CONFIGURATION_ATTRIBUTE
create table DDCDI_UNIQUE_CONFIGURATION_ATTRIBUTE (
    ID varchar2(32),
    VERSION number(10) not null,
    CREATE_TS timestamp,
    CREATED_BY varchar2(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar2(50),
    DELETE_TS timestamp,
    DELETED_BY varchar2(50),
    --
    ENTITY_ATTRIBUTE varchar2(255) not null,
    UNIQUE_CONFIGURATION_ID varchar2(32) not null,
    --
    primary key (ID)
)^
-- end DDCDI_UNIQUE_CONFIGURATION_ATTRIBUTE
-- begin DDCDI_UNIQUE_CONFIGURATION
create table DDCDI_UNIQUE_CONFIGURATION (
    ID varchar2(32),
    VERSION number(10) not null,
    CREATE_TS timestamp,
    CREATED_BY varchar2(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar2(50),
    DELETE_TS timestamp,
    DELETED_BY varchar2(50),
    --
    POLICY varchar2(50) not null,
    IMPORT_CONFIGURATION_ID varchar2(32) not null,
    --
    primary key (ID)
)^
-- end DDCDI_UNIQUE_CONFIGURATION
