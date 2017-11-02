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
alter table unite_releves change id_UR id_ur integer not null ,
change numUR num_ur integer not null,
change pc_UR pc_ur double,
change longueur_UR longueur_ur double,
change largeur_UR largeur_ur double
;
alter table unite_releves change autreTypeClass autretypeclass integer;
