alter table DDCDI_IMPORT_LOG add column SUCCESS boolean ^
update DDCDI_IMPORT_LOG set SUCCESS = false where SUCCESS is null ;
alter table DDCDI_IMPORT_LOG alter column SUCCESS set not null ;
