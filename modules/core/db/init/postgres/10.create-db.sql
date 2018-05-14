-- begin DDCDI_IMPORT_LOG
create table DDCDI_IMPORT_LOG (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    FILE_ID uuid not null,
    STARTED_AT timestamp,
    FINISHED_AT timestamp,
    ENTITIES_PROCESSED integer,
    ENTITIES_IMPORT_SUCCESS integer,
    ENTITIES_IMPORT_VAL_ERROR integer,
    ENTITIES_PRE_COMMIT_SKIPPED integer,
    ENTITIES_UNIQUE_CONSTRAINT_SKIPPED integer,
    SUCCESS boolean not null,
    CONFIGURATION_ID uuid not null,
    --
    primary key (ID)
)^
-- end DDCDI_IMPORT_LOG
-- begin DDCDI_IMPORT_LOG_RECORD
create table DDCDI_IMPORT_LOG_RECORD (
    ID uuid,
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
    STACKTRACE text,
    IMPORT_LOG_ID uuid not null,
    --
    primary key (ID)
)^
-- end DDCDI_IMPORT_LOG_RECORD
-- begin DDCDI_IMPORT_CONFIGURATION
create table DDCDI_IMPORT_CONFIGURATION (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    NAME varchar(255) not null,
    TRANSACTION_STRATEGY varchar(50) not null,
    ENTITY_CLASS varchar(255) not null,
    AD_HOC boolean,
    TEMPLATE_ID uuid,
    COMMENT_ text,
    IMPORTER_BEAN_NAME varchar(255),
    DATE_FORMAT varchar(255),
    BOOLEAN_TRUE_VALUE varchar(255),
    BOOLEAN_FALSE_VALUE varchar(255),
    PRE_COMMIT_SCRIPT text,
    --
    primary key (ID)
)^
-- end DDCDI_IMPORT_CONFIGURATION
-- begin DDCDI_IMPORT_ATTRIBUTE_MAPPER
create table DDCDI_IMPORT_ATTRIBUTE_MAPPER (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    CONFIGURATION_ID uuid not null,
    ENTITY_ATTRIBUTE varchar(255) not null,
    DYNAMIC_ATTRIBUTE boolean,
    FILE_COLUMN_NUMBER integer not null,
    FILE_COLUMN_ALIAS varchar(255),
    CUSTOM_ATTRIBUTE_BIND_SCRIPT text,
    --
    primary key (ID)
)^
-- end DDCDI_IMPORT_ATTRIBUTE_MAPPER
-- begin DDCDI_UNIQUE_CONFIGURATION_ATTRIBUTE
create table DDCDI_UNIQUE_CONFIGURATION_ATTRIBUTE (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    ENTITY_ATTRIBUTE varchar(255) not null,
    UNIQUE_CONFIGURATION_ID uuid not null,
    --
    primary key (ID)
)^
-- end DDCDI_UNIQUE_CONFIGURATION_ATTRIBUTE
-- begin DDCDI_UNIQUE_CONFIGURATION
create table DDCDI_UNIQUE_CONFIGURATION (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    POLICY varchar(50) not null,
    IMPORT_CONFIGURATION_ID uuid not null,
    --
    primary key (ID)
)^
-- end DDCDI_UNIQUE_CONFIGURATION
