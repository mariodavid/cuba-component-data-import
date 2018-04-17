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
);
