create table DDCDI_IMPORT_EXEC (
    ID varchar(32),
    VERSION integer not null,
    CREATE_TS datetime(3),
    CREATED_BY varchar(50),
    UPDATE_TS datetime(3),
    UPDATED_BY varchar(50),
    DELETE_TS datetime(3),
    DELETED_BY varchar(50),
    --
    FILE_ID varchar(32),
    STARTED_AT datetime(3),
    FINISHED_AT datetime(3),
    ENTITIES_PROCESSED integer,
    ENTITIES_IMPORT_SUCCESS integer,
    ENTITIES_IMPORT_VAL_ERROR integer,
    ENTITIES_PRE_COMMIT_SKIPPED integer,
    ENTITIES_UNIQUE_CONSTRAINT_SKIPPED integer,
    SUCCESS boolean not null,
    CONFIGURATION_ID varchar(32),
    --
    primary key (ID)
);