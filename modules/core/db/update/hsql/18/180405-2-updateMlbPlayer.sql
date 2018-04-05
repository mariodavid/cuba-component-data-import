alter table DDCDI_MLB_PLAYER alter column TEAM rename to TEAM__UNUSED ;
alter table DDCDI_MLB_PLAYER add column TEAM_ID varchar(36) ;
