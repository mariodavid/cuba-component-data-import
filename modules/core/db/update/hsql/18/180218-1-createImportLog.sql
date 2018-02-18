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
    STARTED_AT timestamp not null,
    FINISHED_AT timestamp,
    ENTITIES_PROCESSED integer,
    SCENARIO_ID varchar(36) not null,
    --
    primary key (ID)
);
