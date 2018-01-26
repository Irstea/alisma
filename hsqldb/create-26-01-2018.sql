CREATE TABLE PUBLIC.classe_etat 
(
  classe_etat_id        identity NOT NULL PRIMARY KEY,
  classe_etat_libelle   VARCHAR(255) NOT NULL,
  classe_etat_seuil     FLOAT NOT NULL
);

/*  -- Comment for table [classe_etat]: Table des classes d'état;

-- Comment for column [classe_etat_seuil]: Seuil plancher de la classe (>= à la valeur);
*/ 
CREATE TABLE PUBLIC.cours_eau 
(
  id_cours_eau   IDENTITY NOT NULL PRIMARY KEY,
  cours_eau      VARCHAR(255) NOT NULL
);

CREATE TABLE PUBLIC.facies 
(
  facies_id        identity NOT NULL PRIMARY KEY,
  facies_libelle   VARCHAR(255) NOT NULL
);

/*
-- Comment for table [facies]: Type de facies dominant;
*/ 
CREATE TABLE PUBLIC.facies_autre_type 
(
  facies_autre_type_id        identity NOT NULL PRIMARY KEY,
  facies_autre_type_libelle   VARCHAR(255) NOT NULL
);

/*
-- Comment for table [facies_autre_type]: Table des autres types de facies;
*/ 
CREATE TABLE PUBLIC.groupes 
(
  id_groupe    IDENTITY NOT NULL PRIMARY KEY,
  nom_groupe   VARCHAR(50)
);

CREATE TABLE PUBLIC.hydrologie 
(
  hydrologie_id        identity NOT NULL PRIMARY KEY,
  hydrologie_libelle   VARCHAR(255) NOT NULL
);

/*
-- Comment for table [hydrologie]: Table des facies hydrologiques;
*/ 
CREATE TABLE PUBLIC.ibmr 
(
  id_op_controle                   identity NOT NULL PRIMARY KEY,
  ibmr_value                       FLOAT,
  eqr_value                        FLOAT,
  robustesse_value                 FLOAT,
  robustesse_eqr_value             FLOAT,
  niveau_trophique_id              INTEGER,
  robustesse_niveau_trophique_id   INTEGER,
  classe_etat_id                   INTEGER,
  taxon_robustesse                 VARCHAR(255),
  ek_nb_robustesse                 INTEGER,
  robustesse_classe_etat_id        INTEGER,
  cs_moy                           DOUBLE,
  cs_min                           DOUBLE,
  cs_max                           DOUBLE,
  coef_moy                         DOUBLE,
  coef_min                         DOUBLE,
  coef_max                         DOUBLE,
  nbtaxon_het                      INTEGER,
  nbtaxon_alg                      INTEGER,
  nbtaxon_bry                      INTEGER,
  nbtaxon_pte                      INTEGER,
  nbtaxon_pha                      INTEGER,
  nbtaxon_lic                      INTEGER,
  nbtaxon_total                    INTEGER,
  nbtaxon_contrib                  INTEGER,
  nbtaxon_steno1                   INTEGER,
  nbtaxon_steno2                   INTEGER,
  nbtaxon_steno3                   INTEGER,
  seee_date                        TIMESTAMP,
  seee_version                     VARCHAR(255),
  seee_ibmr                        FLOAT,
  seee_nbtaxon_contrib             INTEGER,
  seee_robustesse_value            FLOAT,
  seee_taxon_robustesse            VARCHAR(255)
);

/*
-- Comment for table [ibmr]: Table des informations specifiques du calcul de l'IBMR;

-- Comment for column [eqr_value]: Ecological Quality Ratio : ibmr calculée rapportée à l'ibmr de la typologie retenue;

-- Comment for column [robustesse_eqr_value]: Ecological Quality Ratio : ibmr calculée rapportée à l'ibmr de la typologie retenue pour le calcul de robustesse;

-- Comment for column [ek_nb_robustesse]: Nbre de taxons avec le même EK que le taxon supprimé du calcul de robustesse;

-- Comment for column [seee_date]: Date de calcul de l'indicateur IBMR par le SEEE;

-- Comment for column [seee_version]: Version utilisée par le SEEE pour réaliser le calcul d'IBMR;

-- Comment for column [seee_ibmr]: Valeur de l'indicateur IBMR calculé par le SEEE;

-- Comment for column [seee_nbtaxon_contrib]: Nbre de taxons contributifs pris en compte pour le calcul;

-- Comment for column [seee_robustesse_value]: Valeur de robustesse de l'indicateur;

-- Comment for column [seee_taxon_robustesse]: Taxon supprimé par le SEEE pour le calcul de la robustesse;
*/ 
CREATE TABLE PUBLIC.lignes_op_controle 
(
  id_ligne_op_controle   IDENTITY NOT NULL PRIMARY KEY,
  pc_ur1                 DOUBLE,
  pc_ur2                 DOUBLE,
  cf                     INTEGER,
  id_op_controle         INTEGER NOT NULL,
  id_taxon               VARCHAR(7) NOT NULL
);

CREATE TABLE PUBLIC.meteo 
(
  meteo_id        identity NOT NULL PRIMARY KEY,
  meteo_libelle   VARCHAR(255) NOT NULL
);

/*
-- Comment for table [meteo]: Table des conditions meteo;
*/ 
CREATE TABLE PUBLIC.niveau_trophique 
(
  niveau_trophique_id        identity NOT NULL PRIMARY KEY,
  niveau_trophique_libelle   VARCHAR(255) NOT NULL
);

/*
-- Comment for table [niveau_trophique]: Table des libelles des niveaux trophiques;
*/ 
CREATE TABLE PUBLIC.op_controle 
(
  id_op_controle       IDENTITY NOT NULL PRIMARY KEY,
  id_pt_prel           INTEGER NOT NULL,
  id_statut            INTEGER,
  protocole_id         INTEGER,
  rive_id              INTEGER,
  hydrologie_id        INTEGER,
  meteo_id             INTEGER,
  turbidite_id         INTEGER,
  typo_id              INTEGER,
  date_op              DATE,
  organisme            VARCHAR(50),
  operateur            VARCHAR(50),
  observation          VARCHAR(255),
  ref_dossier          VARCHAR(25),
  releve_dce           TINYINT DEFAULT 1 NOT NULL,
  uuid                 VARCHAR(36),
  producteur_code      VARCHAR(255),
  producteur_name      VARCHAR(255),
  preleveur_code       VARCHAR(255),
  preleveur_name       VARCHAR(255),
  determinateur_code   VARCHAR(255),
  determinateur_name   VARCHAR(255),
  coord_x              INTEGER,
  coord_y              INTEGER,
  altitude             FLOAT,
  longueur             DOUBLE,
  largeur              DOUBLE,
  id_station           INTEGER,
  wgs84_x              DOUBLE,
  wgs84_y              DOUBLE,
  lambert_x_aval       INTEGER,
  lambert_y_aval       INTEGER,
  wgs84_x_aval         DOUBLE,
  wgs84_y_aval         DOUBLE
);

CREATE TABLE PUBLIC.periphyton 
(
  periphyton_id        identity NOT NULL PRIMARY KEY,
  periphyton_libelle   VARCHAR(255) NOT NULL
);

/*
-- Comment for table [periphyton]: Table des abondances du périphyton;
*/ 
CREATE TABLE PUBLIC.protocole 
(
  protocole_id        IDENTITY NOT NULL PRIMARY KEY,
  protocole_libelle   VARCHAR(255) NOT NULL
);

/*
-- Comment for table [protocole]: Table des protocoles d'échantillonnage;
*/ 
CREATE TABLE PUBLIC.rive 
(
  rive_id        identity NOT NULL PRIMARY KEY,
  rive_libelle   VARCHAR(255) NOT NULL
);

/*
-- Comment for table [rive]: Table des rives;

-- Comment for column [rive_id]: 1 : gauche
-- 2 : droite;
*/ 
CREATE TABLE PUBLIC.stations 
(
  id_station       IDENTITY NOT NULL PRIMARY KEY,
  typo_id          INTEGER,
  cd_station       VARCHAR(10),
  station          VARCHAR(255) NOT NULL,
  x                DOUBLE,
  y                DOUBLE,
  id_cours_eau     INTEGER,
  classe_etat_id   INTEGER
);

CREATE TABLE PUBLIC.statut 
(
  id_statut        identity NOT NULL PRIMARY KEY,
  libelle_statut   VARCHAR(50) NOT NULL
);

/*
-- Comment for table [statut]: Table des statuts du dossier;

-- Comment for column [id_statut]: 0 : en saisie
-- 1 : validé;
*/ 
CREATE TABLE PUBLIC.taxons_mp 
(
  cd_taxon        VARCHAR(6) NOT NULL PRIMARY KEY,
  nom_taxon       VARCHAR(100) NOT NULL,
  cote_spe        INTEGER,
  coef_steno      INTEGER,
  cd_sandre       INTEGER,
  aquaticite      INTEGER,
  date_creation   DATE,
  id_groupe       INTEGER NOT NULL,
  auteur          VARCHAR(50),
  cd_valide       VARCHAR(6),
  cd_contrib      VARCHAR(6)
);

CREATE TABLE PUBLIC.taxons_perso 
(
  cd_taxon_perso    VARCHAR(7) NOT NULL PRIMARY KEY,
  nom_taxon_perso   VARCHAR(50) NOT NULL,
  createur          VARCHAR(50),
  cd_sandre         INTEGER,
  date_creationp    DATE NOT NULL,
  auteur            VARCHAR(50),
  id_groupe         INTEGER NOT NULL
);

CREATE TABLE PUBLIC.turbidite 
(
  turbidite_id        identity NOT NULL PRIMARY KEY,
  turbidite_libelle   VARCHAR(255) NOT NULL
);

/*
-- Comment for table [turbidite]: Table des conditions de turbidite;
*/ 
CREATE TABLE PUBLIC.type_ur 
(
  type_ur_id        identity NOT NULL PRIMARY KEY,
  type_ur_libelle   VARCHAR(255) NOT NULL
);

/*
-- Comment for table [type_ur]: Type de l'unité de relevé;
*/ 
CREATE TABLE PUBLIC.typo 
(
  typo_id     IDENTITY NOT NULL PRIMARY KEY,
  typo_name   VARCHAR(255) NOT NULL,
  ibmr_ref    FLOAT NOT NULL,
  groupe      INTEGER NOT NULL
);

/*
-- Comment for table [typo]: Typologie des stations;

-- Comment for column [typo_name]: Code typologique du cours d'eau;

-- Comment for column [ibmr_ref]: Valeur de référence de l'IBMR pour le type de cours d'eau considéré;

-- Comment for column [groupe]: Groupe de la typologie;
*/ 
CREATE TABLE PUBLIC.unite_releves 
(
  id_ur                  IDENTITY NOT NULL PRIMARY KEY,
  id_op_controle         INTEGER NOT NULL,
  type_ur_id             INTEGER NOT NULL,
  periphyton_id          INTEGER,
  facies_id              INTEGER,
  facies_autre_type_id   INTEGER,
  num_ur                 INTEGER DEFAULT 1 NOT NULL,
  pc_ur                  DOUBLE,
  longueur_ur            DOUBLE,
  largeur_ur             DOUBLE,
  pc_vegetalisation      DOUBLE,
  ch_lentique            INTEGER,
  pl_lentique            INTEGER,
  mouille                INTEGER,
  fosse_dissipation      INTEGER,
  ch_lotique             INTEGER,
  radier                 INTEGER,
  cascade_1              INTEGER,
  pl_courant             INTEGER,
  rapide                 INTEGER,
  p1                     INTEGER,
  p2                     INTEGER,
  p3                     INTEGER,
  p4                     INTEGER,
  p5                     INTEGER,
  v1                     INTEGER,
  v2                     INTEGER,
  v3                     INTEGER,
  v4                     INTEGER,
  v5                     INTEGER,
  tres_ombrage           INTEGER,
  ombrage                INTEGER,
  peu_ombrage            INTEGER,
  eclaire                INTEGER,
  tres_eclaire           INTEGER,
  vase_limons            INTEGER,
  terre_marne_tourbe     INTEGER,
  cailloux_pierres       INTEGER,
  blocs_dalles           INTEGER,
  sable_graviers         INTEGER,
  racines                INTEGER,
  debris_org             INTEGER,
  artificiel             INTEGER,
  pc_heterot             DOUBLE,
  pc_algues              DOUBLE,
  pc_bryo                DOUBLE,
  pc_lichen              DOUBLE,
  pc_phanero             DOUBLE,
  pc_flottante           DOUBLE,
  pc_immerg              DOUBLE,
  pc_helophyte           DOUBLE,
  autretypeclass         INTEGER
);

CREATE VIEW PUBLIC.taxons_details_view 
(
  cd_taxon,
  nom_taxon,
  cd_sandre,
  id_groupe
)
AS
SELECT taxons_mp.cd_taxon AS cd_taxon,
       taxons_mp.nom_taxon AS nom_taxon,
       taxons_mp.cd_sandre AS cd_sandre,
       taxons_mp.id_groupe AS id_groupe
FROM taxons_mp
UNION
SELECT taxons_perso.cd_taxon_perso AS cd_taxon,
       taxons_perso.nom_taxon_perso AS nom_taxon,
       taxons_perso.cd_sandre AS cd_sandre,
       taxons_perso.id_groupe AS id_groupe
FROM taxons_perso;

CREATE VIEW PUBLIC.taxons_view
(
  cd_taxon,
  nom_taxon
)
AS 
select taxons_mp.cd_taxon AS cd_taxon,taxons_mp.nom_taxon AS nom_taxon from taxons_mp union select taxons_perso.cd_taxon_perso AS cd_taxon,taxons_perso.nom_taxon_perso AS nom_taxon from taxons_perso;


ALTER TABLE PUBLIC.ibmr ADD CONSTRAINT classe_etat_ibmr_fk FOREIGN KEY (classe_etat_id) REFERENCES PUBLIC.classe_etat (classe_etat_id) ON DELETE NO ACTION ON UPDATE NO ACTION;

ALTER TABLE PUBLIC.ibmr ADD CONSTRAINT classe_etat_ibmr_fk1 FOREIGN KEY (robustesse_classe_etat_id) REFERENCES PUBLIC.classe_etat (classe_etat_id) ON DELETE NO ACTION ON UPDATE NO ACTION;

ALTER TABLE PUBLIC.stations ADD CONSTRAINT classe_etat_stations_fk FOREIGN KEY (classe_etat_id) REFERENCES PUBLIC.classe_etat (classe_etat_id) ON DELETE NO ACTION ON UPDATE NO ACTION;

ALTER TABLE PUBLIC.stations ADD CONSTRAINT FK_Sites_Cours_eau FOREIGN KEY (id_cours_eau) REFERENCES PUBLIC.cours_eau (id_cours_eau) ON DELETE NO ACTION ON UPDATE NO ACTION;

ALTER TABLE PUBLIC.unite_releves ADD CONSTRAINT facies_Unite_releves_fk FOREIGN KEY (facies_id) REFERENCES PUBLIC.facies (facies_id) ON DELETE NO ACTION ON UPDATE NO ACTION;

ALTER TABLE PUBLIC.unite_releves ADD CONSTRAINT facies_autre_type_Unite_releves_fk FOREIGN KEY (facies_autre_type_id) REFERENCES PUBLIC.facies_autre_type (facies_autre_type_id) ON DELETE NO ACTION ON UPDATE NO ACTION;

ALTER TABLE PUBLIC.taxons_mp ADD CONSTRAINT FK_Taxons_MP_Groupes FOREIGN KEY (id_groupe) REFERENCES PUBLIC.groupes (id_groupe) ON DELETE NO ACTION ON UPDATE NO ACTION;

ALTER TABLE PUBLIC.taxons_perso ADD CONSTRAINT FK_Taxons_MP_perso_Groupes FOREIGN KEY (id_groupe) REFERENCES PUBLIC.groupes (id_groupe) ON DELETE NO ACTION ON UPDATE NO ACTION;

ALTER TABLE PUBLIC.op_controle ADD CONSTRAINT hydrologie_Op_controle_fk FOREIGN KEY (hydrologie_id) REFERENCES PUBLIC.hydrologie (hydrologie_id) ON DELETE NO ACTION ON UPDATE NO ACTION;

ALTER TABLE PUBLIC.op_controle ADD CONSTRAINT meteo_Op_controle_fk FOREIGN KEY (meteo_id) REFERENCES PUBLIC.meteo (meteo_id) ON DELETE NO ACTION ON UPDATE NO ACTION;

ALTER TABLE PUBLIC.ibmr ADD CONSTRAINT niveau_trophique_ibmr_fk FOREIGN KEY (niveau_trophique_id) REFERENCES PUBLIC.niveau_trophique (niveau_trophique_id) ON DELETE NO ACTION ON UPDATE NO ACTION;

ALTER TABLE PUBLIC.ibmr ADD CONSTRAINT niveau_trophique_ibmr_fk1 FOREIGN KEY (robustesse_niveau_trophique_id) REFERENCES PUBLIC.niveau_trophique (niveau_trophique_id) ON DELETE NO ACTION ON UPDATE NO ACTION;

ALTER TABLE PUBLIC.ibmr ADD CONSTRAINT Op_controle_ibmr_fk FOREIGN KEY (id_op_controle) REFERENCES PUBLIC.op_controle (id_op_controle) ON DELETE NO ACTION ON UPDATE NO ACTION;

ALTER TABLE PUBLIC.lignes_op_controle ADD CONSTRAINT FK_Lignes_op_controle_Op_controle FOREIGN KEY (id_op_controle) REFERENCES PUBLIC.op_controle (id_op_controle) ON DELETE NO ACTION ON UPDATE NO ACTION;

ALTER TABLE PUBLIC.unite_releves ADD CONSTRAINT Op_controle_Unite_releves_fk FOREIGN KEY (id_op_controle) REFERENCES PUBLIC.op_controle (id_op_controle) ON DELETE NO ACTION ON UPDATE NO ACTION;

ALTER TABLE PUBLIC.unite_releves ADD CONSTRAINT periphyton_Unite_releves_fk FOREIGN KEY (periphyton_id) REFERENCES PUBLIC.periphyton (periphyton_id) ON DELETE NO ACTION ON UPDATE NO ACTION;

ALTER TABLE PUBLIC.op_controle ADD CONSTRAINT protocole_Op_controle_fk FOREIGN KEY (protocole_id) REFERENCES PUBLIC.protocole (protocole_id) ON DELETE NO ACTION ON UPDATE NO ACTION;

ALTER TABLE PUBLIC.op_controle ADD CONSTRAINT rive_Op_controle_fk FOREIGN KEY (rive_id) REFERENCES PUBLIC.rive (rive_id) ON DELETE NO ACTION ON UPDATE NO ACTION;

ALTER TABLE PUBLIC.op_controle ADD CONSTRAINT stations_op_controle_fk FOREIGN KEY (id_station) REFERENCES PUBLIC.stations (id_station) ON DELETE NO ACTION ON UPDATE NO ACTION;

ALTER TABLE PUBLIC.op_controle ADD CONSTRAINT Statut_Op_controle_fk FOREIGN KEY (id_statut) REFERENCES PUBLIC.statut (id_statut) ON DELETE NO ACTION ON UPDATE NO ACTION;

ALTER TABLE PUBLIC.op_controle ADD CONSTRAINT turbidite_Op_controle_fk FOREIGN KEY (turbidite_id) REFERENCES PUBLIC.turbidite (turbidite_id) ON DELETE NO ACTION ON UPDATE NO ACTION;

ALTER TABLE PUBLIC.unite_releves ADD CONSTRAINT type_ur_Unite_releves_fk FOREIGN KEY (type_ur_id) REFERENCES PUBLIC.type_ur (type_ur_id) ON DELETE NO ACTION ON UPDATE NO ACTION;

ALTER TABLE PUBLIC.op_controle ADD CONSTRAINT typo_Op_controle_fk FOREIGN KEY (typo_id) REFERENCES PUBLIC.typo (typo_id) ON DELETE NO ACTION ON UPDATE NO ACTION;

ALTER TABLE PUBLIC.stations ADD CONSTRAINT typo_stations_fk FOREIGN KEY (typo_id) REFERENCES PUBLIC.typo (typo_id) ON DELETE NO ACTION ON UPDATE NO ACTION;

/*
 contenu des tables de parametres
 */
 
INSERT INTO groupes
  (id_groupe, nom_groupe)
VALUES
  (1,'HET'),
  (2,'ALG'),
  (3,'BRh'),
  (4,'BRm'),
  (5,'BRl'),
  (6,'PTE'),
  (7,'PHe'),
  (8,'PHg'),
  (9,'PHx'),
  (10,'PHy'),
  (11,'LIC')
  ;
  
INSERT INTO statut
  (id_statut, libelle_statut)
VALUES
  (0,'En saisie'),
  (1,'Validé');
  
insert into niveau_trophique(niveau_trophique_id, niveau_trophique_libelle) values 
(1, 'très faible'),(2, 'faible'), (3, 'moyen'), (4,'fort'), (5, 'très élevé');

insert into protocole(protocole_id, protocole_libelle) values
(1, 'IBMR standard'),
(2, 'IBMR grands cours d''eau'),
(3, 'IBMR mixte');

insert into rive(rive_id, rive_libelle) values
(1, 'Amont rive droite'),
(2, 'Amont rive gauche'),
(3, 'Amont milieu rive'),
(4, 'Aval rive droite'),
(5, 'Aval rive gauche'),
(6, 'Aval milieu rive');

insert into hydrologie(hydrologie_id, hydrologie_libelle) values
(1, 'Etiage sévère'),
(2, 'Etiage normal'),
(3, 'Moyennes eaux'),
(4, 'Hautes eaux');

insert into meteo (meteo_id, meteo_libelle) values 
(1, 'Ensoleillé'),
(2, 'Faiblement nuageux'),
(3, 'Fortement nuageux'),
(4, 'Pluie fine'),
(5, 'Orage - pluie forte'),
(6, 'Conditions crépusculaires');

insert into turbidite (turbidite_id, turbidite_libelle) values 
(1, 'nulle'),
(2, 'faible'),
(3, 'moyenne'),
(4, 'forte');

insert into periphyton (periphyton_id, periphyton_libelle) values
(1, 'Absent'),
(2, 'Peu abondant'),
(3, 'Abondant'),
(4, 'Très abondant');

insert into facies (facies_id, facies_libelle) values
(1, 'Chenal lentique'),
(2, 'Plat lentique'),
(3, 'Mouille'),
(4, 'Fosse de dissipation'), 
(5, 'Chenal lotique'),
(6, 'Radier'),
(7, 'Cascade'),
(8, 'Plat courant'),
(9, 'Rapide'),
(10, 'Autre');

insert into facies_autre_type (facies_autre_type_id, facies_autre_type_libelle) values 
(1, 'Bordure'),
(2, 'Escalier'),
(3, 'Fosse d''affouillement'),
(4, 'Darse'),
(5, 'Flaque'),
(6, 'Trou d''eau'), 
(7, 'Seuil'),
(8, 'Zones végétalisées latérales'),
(9, 'autre');

insert into type_ur (type_ur_id, type_ur_libelle) values
(1, 'Unité la plus courante'),
(2, 'Unité la moins courante'),
(3, 'Inconnue'),
(4, 'Unique'),
(5, 'Chenal'), 
(6, 'Berges'),
(7, 'Rive droite'),
(8, 'Rive gauche');
