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
    ENTITY_ATTRIBUTE varchar(255) not null,
    FILE_COLUMN_NUMBER integer not null,
    FILE_COLUMN_ALIAS varchar(255),
    --
    primary key (ID)
);
