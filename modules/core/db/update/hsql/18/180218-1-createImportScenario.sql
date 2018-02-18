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
);
