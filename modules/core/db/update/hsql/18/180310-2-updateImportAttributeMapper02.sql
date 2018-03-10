-- update DDCDI_IMPORT_ATTRIBUTE_MAPPER set IMPORT_SCENARIO_ID = <default_value> where IMPORT_SCENARIO_ID is null ;
alter table DDCDI_IMPORT_ATTRIBUTE_MAPPER alter column IMPORT_SCENARIO_ID set not null ;
