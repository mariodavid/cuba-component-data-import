create table DDCDI_IMPORT_EXEC_DETAIL (
    ID varchar(36) not null,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    MESSAGE varchar(4000) not null,
    LEVEL_ varchar(50) not null,
    TIME_ timestamp not null,
    STACKTRACE longvarchar,
    IMPORT_EXEC_ID varchar(36) not null,
    DATA_ROW longvarchar,
    DATA_ROW_INDEX integer,
    ENTITY_INSTANCE longvarchar,
    CATEGORY varchar(50),
    --
    primary key (ID)
);