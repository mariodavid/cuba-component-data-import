alter table DDCDI_MLB_PLAYER add constraint FK_DDCDI_MLB_PLAYER_TEAM foreign key (TEAM_ID) references DDCDI_MLB_TEAM(ID);
create index IDX_DDCDI_MLB_PLAYER_TEAM on DDCDI_MLB_PLAYER (TEAM_ID);
