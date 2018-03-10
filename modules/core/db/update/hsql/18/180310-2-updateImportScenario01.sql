alter table DDCDI_IMPORT_SCENARIO add column ENTITY_CLASS varchar(255) ^
update DDCDI_IMPORT_SCENARIO set ENTITY_CLASS = '' where ENTITY_CLASS is null ;
alter table DDCDI_IMPORT_SCENARIO alter column ENTITY_CLASS set not null ;
