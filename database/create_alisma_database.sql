CREATE SCHEMA `alisma` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci ;


CREATE TABLE alisma.Auteurs (
                id_auteur INT AUTO_INCREMENT NOT NULL,
                auteur VARCHAR(100),
                PRIMARY KEY (id_auteur)
);


CREATE TABLE alisma.Cours_Eau (
                id_cours_eau INT AUTO_INCREMENT NOT NULL,
                cours_eau VARCHAR(50) NOT NULL,
                PRIMARY KEY (id_cours_eau)
);


CREATE TABLE alisma.Groupes (
                id_groupe INT AUTO_INCREMENT NOT NULL,
                nom_groupe VARCHAR(50),
                PRIMARY KEY (id_groupe)
);


CREATE TABLE alisma.Lignes_op_controle (
                id_ligne_op_controle INT AUTO_INCREMENT NOT NULL,
                pc_UR1 DOUBLE PRECISION NOT NULL,
                pc_UR2 DOUBLE PRECISION,
                cf INT,
                id_op_controle INT NOT NULL,
                id_taxon VARCHAR(7) NOT NULL,
                PRIMARY KEY (id_ligne_op_controle)
);


CREATE TABLE alisma.Op_controle (
                id_op_controle INT AUTO_INCREMENT NOT NULL,
                organisme VARCHAR(50),
                operateur VARCHAR(50),
                protocole VARCHAR(20),
                coord_rive VARCHAR(20),
                hydrologie VARCHAR(50),
                meteo VARCHAR(20),
                turbidite VARCHAR(15),
                IBMR DOUBLE PRECISION,
                robustesse DOUBLE PRECISION,
                taxonRobust VARCHAR(6),
                date_op DATE,
                observation VARCHAR(255),
                ref_dossier VARCHAR(25),
                id_pt_prel INT NOT NULL,
                id_statut INT,
                PRIMARY KEY (id_op_controle)
);

ALTER TABLE alisma.Op_controle MODIFY COLUMN id_statut INTEGER COMMENT '0 : en saisie
1 : validé';


CREATE TABLE alisma.Points_prelev (
                id_pt_prel INT AUTO_INCREMENT NOT NULL,
                coord_x INT,
                coord_y INT,
                altitude DOUBLE PRECISION,
                longueur DOUBLE PRECISION,
                largeur DOUBLE PRECISION,
                id_station INT NOT NULL,
                wgs84_x VARCHAR(20),
                wgs84_y VARCHAR(20),
                PRIMARY KEY (id_pt_prel)
);

ALTER TABLE alisma.Points_prelev MODIFY COLUMN wgs84_x VARCHAR(20) COMMENT 'Coordonnée X du point de prélèvement, en WGS84';

ALTER TABLE alisma.Points_prelev MODIFY COLUMN wgs84_y VARCHAR(20) COMMENT 'Coordonnée Y du point de prélèvement, en wgs84';


CREATE TABLE alisma.Stations (
                id_station INT AUTO_INCREMENT NOT NULL,
                cd_station INT,
                station VARCHAR(50) NOT NULL,
                x DOUBLE PRECISION NOT NULL,
                y DOUBLE PRECISION NOT NULL,
                id_cours_eau INT NOT NULL,
                PRIMARY KEY (id_station)
);


CREATE TABLE alisma.Statut (
                id_statut INT NOT NULL,
                libelle_statut VARCHAR(50) NOT NULL,
                PRIMARY KEY (id_statut)
);

ALTER TABLE alisma.Statut COMMENT 'Table des statuts du dossier';

ALTER TABLE alisma.Statut MODIFY COLUMN id_statut INTEGER COMMENT '0 : en saisie
1 : validé';


CREATE TABLE alisma.Synonymes (
                cd_taxon VARCHAR(6) DEFAULT '' NOT NULL,
                cd_valide VARCHAR(6) NOT NULL,
                cd_contrib VARCHAR(6) NOT NULL,
                PRIMARY KEY (cd_taxon)
);



CREATE TABLE alisma.Taxons_MP (
                cd_taxon VARCHAR(6) DEFAULT '' NOT NULL,
                nom_taxon VARCHAR(100) NOT NULL,
                cote_spe INT,
                coef_steno INT,
                cd_sandre INT,
                aquaticite INT,
                date_creation DATE,
                id_auteur INT NOT NULL,
                id_groupe INT NOT NULL,
                PRIMARY KEY (cd_taxon)
);


CREATE TABLE alisma.Taxons_MP_persos (
                cd_taxon_perso VARCHAR(7) DEFAULT '' NOT NULL,
                nom_taxon_perso VARCHAR(50) NOT NULL,
                createur VARCHAR(50),
                cd_sandre INT,
                date_creationP DATE NOT NULL,
                auteur VARCHAR(50),
                id_groupe INT NOT NULL,
                PRIMARY KEY (cd_taxon_perso)
);

drop view alisma.Taxons_view;
drop view alisma.Taxons_details_view;

CREATE VIEW alisma.Taxons_view
AS 
select `alisma`.`Taxons_MP`.`cd_taxon` AS `cd_taxon`,`alisma`.`Taxons_MP`.`nom_taxon` AS `nom_taxon` from `alisma`.`Taxons_MP` 
union 
select `alisma`.`Taxons_MP_persos`.`cd_taxon_perso` AS `cd_taxon`,`alisma`.`Taxons_MP_persos`.`nom_taxon_perso` AS `nom_taxon` from `alisma`.`Taxons_MP_persos`;

CREATE VIEW alisma.Taxons_details_view
AS 
select `alisma`.`Taxons_MP`.`cd_taxon` AS `cd_taxon`,`alisma`.`Taxons_MP`.`nom_taxon` AS `nom_taxon`,`alisma`.`Taxons_MP`.`cd_sandre` AS `cd_sandre`,`alisma`.`Taxons_MP`.`id_groupe` AS `id_groupe` from `alisma`.`Taxons_MP` 
union 
select `alisma`.`Taxons_MP_persos`.`cd_taxon_perso` AS `cd_taxon`,`alisma`.`Taxons_MP_persos`.`nom_taxon_perso` AS `nom_taxon`,`alisma`.`Taxons_MP_persos`.`cd_sandre` AS `cd_sandre`,`alisma`.`Taxons_MP_persos`.`id_groupe` AS `id_groupe` from `alisma`.`Taxons_MP_persos`;


CREATE TABLE alisma.Unite_releves (
                id_UR INT AUTO_INCREMENT NOT NULL,
                pc_UR DOUBLE PRECISION,
                longueur_UR DOUBLE PRECISION,
                largeur_UR DOUBLE PRECISION,
                periphyton VARCHAR(30),
                pc_vegetalisation DOUBLE PRECISION,
                facies_dominant VARCHAR(25),
                ch_lentique INT,
                pl_lentique INT,
                mouille INT,
                fosse_dissipation INT,
                ch_lotique INT,
                radier INT,
                `cascade` INT,
                pl_courant INT,
                rapide INT,
                p1 INT,
                p2 INT,
                p3 INT,
                p4 INT,
                p5 INT,
                v1 INT,
                v2 INT,
                v3 INT,
                v4 INT,
                v5 INT,
                tres_ombrage INT,
                ombrage INT,
                peu_ombrage INT,
                eclaire INT,
                tres_eclaire INT,
                vase_limons INT,
                terre_marne_tourbe INT,
                cailloux_pierres INT,
                blocs_dalles INT,
                sable_graviers INT,
                racines INT,
                debris_org INT,
                artificiel INT,
                pc_heterot DOUBLE PRECISION,
                pc_algues DOUBLE PRECISION,
                pc_bryo DOUBLE PRECISION,
                pc_lichen DOUBLE PRECISION,
                pc_phanero DOUBLE PRECISION,
                pc_flottante DOUBLE PRECISION,
                pc_immerg DOUBLE PRECISION,
                pc_helophyte DOUBLE PRECISION,
                autreType VARCHAR(50),
                autreTypeClass INT,
                typeUR VARCHAR(50) NOT NULL,
                id_op_controle INT NOT NULL,
                numUR INT DEFAULT 1 NOT NULL,
                PRIMARY KEY (id_UR)
);

ALTER TABLE alisma.Unite_releves MODIFY COLUMN numUR INTEGER COMMENT '1 ou 2';


ALTER TABLE alisma.Taxons_MP ADD CONSTRAINT fk_taxons_mp_auteurs
FOREIGN KEY (id_auteur)
REFERENCES alisma.Auteurs (id_auteur)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE alisma.Stations ADD CONSTRAINT fk_sites_cours_eau
FOREIGN KEY (id_cours_eau)
REFERENCES alisma.Cours_Eau (id_cours_eau)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE alisma.Taxons_MP ADD CONSTRAINT fk_taxons_mp_groupes
FOREIGN KEY (id_groupe)
REFERENCES alisma.Groupes (id_groupe)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE alisma.Taxons_MP_persos ADD CONSTRAINT fk_taxons_mp_perso_groupes
FOREIGN KEY (id_groupe)
REFERENCES alisma.Groupes (id_groupe)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE alisma.Lignes_op_controle ADD CONSTRAINT fk_lignes_op_controle_op_controle
FOREIGN KEY (id_op_controle)
REFERENCES alisma.Op_controle (id_op_controle)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE alisma.Unite_releves ADD CONSTRAINT op_controle_unite_releves_fk
FOREIGN KEY (id_op_controle)
REFERENCES alisma.Op_controle (id_op_controle)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE alisma.Op_controle ADD CONSTRAINT fk_op_controle_points_prelev
FOREIGN KEY (id_pt_prel)
REFERENCES alisma.Points_prelev (id_pt_prel)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE alisma.Points_prelev ADD CONSTRAINT fk_points_prelev_station
FOREIGN KEY (id_station)
REFERENCES alisma.Stations (id_station)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE alisma.Op_controle ADD CONSTRAINT statut_op_controle_fk
FOREIGN KEY (id_statut)
REFERENCES alisma.Statut (id_statut)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE alisma.Synonymes ADD CONSTRAINT fk_synonymes_cd_contrib
FOREIGN KEY (cd_contrib)
REFERENCES alisma.Taxons_MP (cd_taxon)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE alisma.Synonymes ADD CONSTRAINT fk_synonymes_cd_taxon
FOREIGN KEY (cd_taxon)
REFERENCES alisma.Taxons_MP (cd_taxon)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE alisma.Synonymes ADD CONSTRAINT fk_synonymes_cd_valide
FOREIGN KEY (cd_valide)
REFERENCES alisma.Taxons_MP (cd_taxon)
ON DELETE NO ACTION
ON UPDATE NO ACTION;