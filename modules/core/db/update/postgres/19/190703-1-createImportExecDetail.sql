create table DDCDI_IMPORT_EXEC_DETAIL (
    ID uuid,
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
    STACKTRACE text,
    IMPORT_EXEC_ID uuid not null,
    DATA_ROW text,
    DATA_ROW_INDEX integer,
    ENTITY_INSTANCE text,
    CATEGORY varchar(50),
    --
    primary key (ID)
);