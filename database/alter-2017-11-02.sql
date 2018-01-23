/*
 * Renommage des tables et colonnes :
 * suppression des majuscules
 */ 
RENAME TABLE Taxons_MP TO taxons_mp;

RENAME TABLE Groupes TO groupes;

DROP TABLE Auteurs;

RENAME TABLE Taxons_MP_persos TO taxons_perso;

-- ALTER TABLE CHANGE ancien_nom new_nom;
DROP VIEW Taxons_details_view;

CREATE OR REPLACE VIEW taxons_details_view 
AS
SELECT cd_taxon AS cd_taxon,
       nom_taxon AS nom_taxon,
       cd_sandre AS cd_sandre,
       id_groupe AS id_groupe
FROM taxons_mp
UNION
SELECT cd_taxon_perso AS cd_taxon,
       nom_taxon_perso AS nom_taxon,
       cd_sandre AS cd_sandre,
       id_groupe AS id_groupe
FROM taxons_perso;

drop view Taxons_view;

CREATE OR REPLACE VIEW taxons_view 
AS
SELECT cd_taxon AS cd_taxon,
       nom_taxon AS nom_taxon
FROM  taxons_mp
UNION
SELECT cd_taxon_perso AS cd_taxon,
       nom_taxon_perso AS nom_taxon
FROM  taxons_perso;

rename table Lignes_op_controle to lignes_op_controle;
rename table Op_controle to op_controle;
rename table Points_prelev to points_prelev;
rename table Stations to stations;
rename table Unite_releves to unite_releves;
rename table Cours_Eau to cours_eau;
rename table Statut to statut;

alter table lignes_op_controle change pc_UR1 pc_ur1 double not null,
change pc_UR2 pc_ur2 double;
alter table taxons_perso change date_creationP date_creation_perso date not null;
alter table unite_releves change id_UR id_ur integer not null auto_increment ,
change numUR num_ur integer not null,
change pc_UR pc_ur double,
change longueur_UR longueur_ur double,
change largeur_UR largeur_ur double
;
alter table unite_releves change autreTypeClass autretypeclass integer;
alter table op_controle add column uuid varchar(36);
update op_controle set uuid = uuid();

ALTER TABLE points_prelev ADD COLUMN lambert_x_aval INT  COMMENT 'Coordonnée x du point aval, en Lambert 93';
ALTER TABLE points_prelev ADD COLUMN lambert_y_aval INT  COMMENT 'Coordonnée Y du point aval, en Lambert 93';
ALTER TABLE points_prelev ADD COLUMN wgs84_x_aval VARCHAR(20)  COMMENT 'Coordonnée X du point aval, en WGS84';
ALTER TABLE points_prelev ADD COLUMN wgs84_y_aval VARCHAR(20)  COMMENT 'Coordonnée Y du point aval, en WGS84';


insert into rive (rive_id, rive_libelle) values 
(3, 'Amont milieu rive'),
(4, 'Aval rive droite'),
(5, 'Aval rive gauche'),
(6, 'Aval milieu rive');
update op_controle set rive_id = 3 where rive_id = 1;
update op_controle set rive_id = 1 where rive_id = 2;
update op_controle set rive_id = 2 where rive_id = 3;
update rive set rive_libelle = 'Amont rive droite' where rive_id = 1;
update rive set rive_libelle = 'Amont rive gauche' where rive_id = 2;

insert into turbidite (turbidite_id, turbidite_libelle) values (4, 'forte');
update turbidite set turbidite_libelle = 'moyenne' where turbidite_id = 3;
update turbidite set turbidite_libelle = 'faible' where turbidite_id = 2;
update turbidite set turbidite_libelle = 'nulle' where turbidite_id = 1;

update op_controle set turbidite_id = 4 where turbidite_id = 3;
update op_controle set turbidite_id = 3 where turbidite_id = 2;
update op_controle set turbidite_id = 2 where turbidite_id = 1;

alter table points_prelev change wgs84_x wgs84_x double, change wgs84_y wgs84_y double, change wgs84_x_aval wgs84_x_aval double, change wgs84_y_aval wgs84_y_aval double;


insert into facies_autre_type (facies_autre_type_id, facies_autre_type_libelle) values (9, 'autre');

alter table stations add column typo_id integer after id_station, add constraint foreign key (typo_id) references typo(typo_id);

alter table op_controle add column producteur_code varchar(255) comment 'Code du producteur',
add column producteur_name varchar(255) comment 'Nom du producteur',
add column preleveur_code varchar(255) comment 'Code du preleveur',
add column preleveur_name varchar(255) comment 'Nom du preleveur',
add column determinateur_code varchar(255) comment 'Code du determinateur',
add column determinateur_name varchar(255) comment 'Nom du determinateur';

alter table stations change id_cours_eau id_cours_eau integer;

ALTER TABLE op_controle ADD COLUMN coord_x INT  COMMENT 'Coordonnée X du point amont, en Lambert 93';
ALTER TABLE op_controle ADD COLUMN coord_y INT  COMMENT 'Coordonnée Y du point amont, en Lambert 93';
ALTER TABLE op_controle ADD COLUMN altitude float  COMMENT 'Altitude du point amont, en mètres';
ALTER TABLE op_controle ADD COLUMN longueur double  COMMENT 'Longueur de la station, en mètres';
ALTER TABLE op_controle ADD COLUMN largeur double  COMMENT 'Largeur du cours d''eau, en mètres';
ALTER TABLE op_controle ADD COLUMN wgs84_x VARCHAR(20)  COMMENT 'Coordonnée X du point amont, en WGS84';
ALTER TABLE op_controle ADD COLUMN wgs84_y VARCHAR(20)  COMMENT 'Coordonnée Y du point amont, en WGS84';
ALTER TABLE op_controle ADD COLUMN id_station INT  COMMENT '';
ALTER TABLE op_controle ADD COLUMN lambert_x_aval INT  COMMENT 'Coordonnée x du point aval, en Lambert 93';
ALTER TABLE op_controle ADD COLUMN lambert_y_aval INT  COMMENT 'Coordonnée Y du point aval, en Lambert 93';
ALTER TABLE op_controle ADD COLUMN wgs84_x_aval VARCHAR(20)  COMMENT 'Coordonnée X du point aval, en WGS84';
ALTER TABLE op_controle ADD COLUMN wgs84_y_aval VARCHAR(20)  COMMENT 'Coordonnée Y du point aval, en WGS84';
alter table op_controle add constraint stations_op_controle_fk foreign key (id_station) references stations(id_station) on update no action on delete no action;

update op_controle o, points_prelev p 
set o.coord_x = p.coord_x, o.coord_y = p.coord_y, o.altitude = p.altitude, o.longueur = p.longueur, o.largeur = p.largeur, o.wgs84_x = p.wgs84_y, 
o.id_station = p.id_station, o.lambert_x_aval = p.lambert_x_aval, o.lambert_y_aval = p.lambert_y_aval, o.wgs84_x_aval = p.wgs84_x_aval, o.wgs84_y_aval = p.wgs84_y_aval
where o.id_pt_prel = p.id_pt_prel;

alter table op_controle drop foreign key fk_op_controle_points_prelev;
drop table points_prelev;
alter table op_controle drop column id_pt_prel;

