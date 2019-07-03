create table DDCDI_IMPORT_EXEC_DETAIL (
    ID varchar(32),
    VERSION integer not null,
    CREATE_TS datetime(3),
    CREATED_BY varchar(50),
    UPDATE_TS datetime(3),
    UPDATED_BY varchar(50),
    DELETE_TS datetime(3),
    DELETED_BY varchar(50),
    --
    MESSAGE varchar(4000) not null,
    LEVEL_ varchar(50) not null,
    TIME_ datetime(3) not null,
    STACKTRACE longtext,
    IMPORT_EXEC_ID varchar(32) not null,
    DATA_ROW longtext,
    DATA_ROW_INDEX integer,
    ENTITY_INSTANCE longtext,
    CATEGORY varchar(50),
    --
    primary key (ID)
);