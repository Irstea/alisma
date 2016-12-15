CREATE DATABASE `alisma` /*!40100 DEFAULT CHARACTER SET utf8 */;

use alisma;

CREATE TABLE `Auteurs` (
  `id_auteur` int(11) NOT NULL AUTO_INCREMENT,
  `auteur` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id_auteur`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `Cours_Eau` (
  `id_cours_eau` int(11) NOT NULL AUTO_INCREMENT,
  `cours_eau` varchar(255) NOT NULL,
  PRIMARY KEY (`id_cours_eau`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;

CREATE TABLE `Stations` (
  `id_station` int(11) NOT NULL AUTO_INCREMENT,
  `cd_station` varchar(10) NOT NULL,
  `station` varchar(255) NOT NULL,
  `x` double NOT NULL,
  `y` double NOT NULL,
  `id_cours_eau` int(11) NOT NULL,
  PRIMARY KEY (`id_station`),
  KEY `fk_sites_cours_eau` (`id_cours_eau`),
  CONSTRAINT `fk_sites_cours_eau` FOREIGN KEY (`id_cours_eau`) REFERENCES `Cours_Eau` (`id_cours_eau`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE `Groupes` (
  `id_groupe` int(11) NOT NULL AUTO_INCREMENT,
  `nom_groupe` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id_groupe`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8;

CREATE TABLE `Statut` (
  `id_statut` int(11) NOT NULL DEFAULT '0' COMMENT '0 : en saisie\n1 : validé',
  `libelle_statut` varchar(50) NOT NULL,
  PRIMARY KEY (`id_statut`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Table des statuts du dossier';

CREATE TABLE `Points_prelev` (
  `id_pt_prel` int(11) NOT NULL AUTO_INCREMENT,
  `coord_x` int(11) DEFAULT NULL,
  `coord_y` int(11) DEFAULT NULL,
  `altitude` double DEFAULT NULL,
  `longueur` double DEFAULT NULL,
  `largeur` double DEFAULT NULL,
  `id_station` int(11) NOT NULL,
  `wgs84_x` varchar(20) DEFAULT NULL COMMENT 'Coordonnée X du point de prélèvement, en WGS84',
  `wgs84_y` varchar(20) DEFAULT NULL COMMENT 'Coordonnée Y du point de prélèvement, en wgs84',
  PRIMARY KEY (`id_pt_prel`),
  KEY `fk_points_prelev_station` (`id_station`),
  CONSTRAINT `fk_points_prelev_station` FOREIGN KEY (`id_station`) REFERENCES `Stations` (`id_station`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE `Op_controle` (
  `id_op_controle` int(11) NOT NULL AUTO_INCREMENT,
  `organisme` varchar(50) DEFAULT NULL,
  `operateur` varchar(50) DEFAULT NULL,
  `protocole` varchar(20) DEFAULT NULL,
  `coord_rive` varchar(20) DEFAULT NULL,
  `hydrologie` varchar(50) DEFAULT NULL,
  `meteo` varchar(20) DEFAULT NULL,
  `turbidite` varchar(15) DEFAULT NULL,
  `IBMR` double DEFAULT NULL,
  `robustesse` double DEFAULT NULL,
  `taxonRobust` varchar(6) DEFAULT NULL,
  `date_op` date DEFAULT NULL,
  `observation` varchar(255) DEFAULT NULL,
  `ref_dossier` varchar(25) DEFAULT NULL,
  `id_pt_prel` int(11) NOT NULL,
  `id_statut` int(11) DEFAULT NULL COMMENT '0 : en saisie\n1 : validé',
  PRIMARY KEY (`id_op_controle`),
  KEY `fk_op_controle_points_prelev` (`id_pt_prel`),
  KEY `statut_op_controle_fk` (`id_statut`),
  CONSTRAINT `fk_op_controle_points_prelev` FOREIGN KEY (`id_pt_prel`) REFERENCES `Points_prelev` (`id_pt_prel`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `statut_op_controle_fk` FOREIGN KEY (`id_statut`) REFERENCES `Statut` (`id_statut`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE `Lignes_op_controle` (
  `id_ligne_op_controle` int(11) NOT NULL AUTO_INCREMENT,
  `pc_UR1` double NOT NULL,
  `pc_UR2` double DEFAULT NULL,
  `cf` int(11) DEFAULT NULL,
  `id_op_controle` int(11) NOT NULL,
  `id_taxon` varchar(7) NOT NULL,
  PRIMARY KEY (`id_ligne_op_controle`),
  KEY `fk_lignes_op_controle_op_controle` (`id_op_controle`),
  CONSTRAINT `fk_lignes_op_controle_op_controle` FOREIGN KEY (`id_op_controle`) REFERENCES `Op_controle` (`id_op_controle`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE `Taxons_MP` (
  `cd_taxon` varchar(6) NOT NULL DEFAULT '',
  `nom_taxon` varchar(100) NOT NULL,
  `cote_spe` int(11) DEFAULT NULL,
  `coef_steno` int(11) DEFAULT NULL,
  `cd_sandre` int(11) DEFAULT NULL,
  `aquaticite` int(11) DEFAULT NULL,
  `date_creation` date DEFAULT NULL,
  `id_groupe` int(11) NOT NULL,
  `auteur` varchar(50) DEFAULT NULL,
  `cd_valide` varchar(6) DEFAULT NULL,
  `cd_contrib` varchar(6) DEFAULT NULL,
  PRIMARY KEY (`cd_taxon`),
  KEY `fk_taxons_mp_groupes` (`id_groupe`),
  CONSTRAINT `fk_taxons_mp_groupes` FOREIGN KEY (`id_groupe`) REFERENCES `Groupes` (`id_groupe`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `Taxons_MP_persos` (
  `cd_taxon_perso` varchar(7) NOT NULL DEFAULT '',
  `nom_taxon_perso` varchar(50) NOT NULL,
  `createur` varchar(50) DEFAULT NULL,
  `cd_sandre` int(11) DEFAULT NULL,
  `date_creationP` date NOT NULL,
  `auteur` varchar(50) DEFAULT NULL,
  `id_groupe` int(11) NOT NULL,
  PRIMARY KEY (`cd_taxon_perso`),
  KEY `fk_taxons_mp_perso_groupes` (`id_groupe`),
  CONSTRAINT `fk_taxons_mp_perso_groupes` FOREIGN KEY (`id_groupe`) REFERENCES `Groupes` (`id_groupe`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `Unite_releves` (
  `id_UR` int(11) NOT NULL AUTO_INCREMENT,
  `pc_UR` double DEFAULT NULL,
  `longueur_UR` double DEFAULT NULL,
  `largeur_UR` double DEFAULT NULL,
  `periphyton` varchar(30) DEFAULT NULL,
  `pc_vegetalisation` double DEFAULT NULL,
  `facies_dominant` varchar(25) DEFAULT NULL,
  `ch_lentique` int(11) DEFAULT NULL,
  `pl_lentique` int(11) DEFAULT NULL,
  `mouille` int(11) DEFAULT NULL,
  `fosse_dissipation` int(11) DEFAULT NULL,
  `ch_lotique` int(11) DEFAULT NULL,
  `radier` int(11) DEFAULT NULL,
  `cascade` int(11) DEFAULT NULL,
  `pl_courant` int(11) DEFAULT NULL,
  `rapide` int(11) DEFAULT NULL,
  `p1` int(11) DEFAULT NULL,
  `p2` int(11) DEFAULT NULL,
  `p3` int(11) DEFAULT NULL,
  `p4` int(11) DEFAULT NULL,
  `p5` int(11) DEFAULT NULL,
  `v1` int(11) DEFAULT NULL,
  `v2` int(11) DEFAULT NULL,
  `v3` int(11) DEFAULT NULL,
  `v4` int(11) DEFAULT NULL,
  `v5` int(11) DEFAULT NULL,
  `tres_ombrage` int(11) DEFAULT NULL,
  `ombrage` int(11) DEFAULT NULL,
  `peu_ombrage` int(11) DEFAULT NULL,
  `eclaire` int(11) DEFAULT NULL,
  `tres_eclaire` int(11) DEFAULT NULL,
  `vase_limons` int(11) DEFAULT NULL,
  `terre_marne_tourbe` int(11) DEFAULT NULL,
  `cailloux_pierres` int(11) DEFAULT NULL,
  `blocs_dalles` int(11) DEFAULT NULL,
  `sable_graviers` int(11) DEFAULT NULL,
  `racines` int(11) DEFAULT NULL,
  `debris_org` int(11) DEFAULT NULL,
  `artificiel` int(11) DEFAULT NULL,
  `pc_heterot` double DEFAULT NULL,
  `pc_algues` double DEFAULT NULL,
  `pc_bryo` double DEFAULT NULL,
  `pc_lichen` double DEFAULT NULL,
  `pc_phanero` double DEFAULT NULL,
  `pc_flottante` double DEFAULT NULL,
  `pc_immerg` double DEFAULT NULL,
  `pc_helophyte` double DEFAULT NULL,
  `autreType` varchar(50) DEFAULT NULL,
  `autreTypeClass` int(11) DEFAULT NULL,
  `typeUR` varchar(50) NOT NULL,
  `id_op_controle` int(11) NOT NULL,
  `numUR` int(11) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id_UR`),
  KEY `op_controle_unite_releves_fk` (`id_op_controle`),
  CONSTRAINT `op_controle_unite_releves_fk` FOREIGN KEY (`id_op_controle`) REFERENCES `Op_controle` (`id_op_controle`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;

CREATE VIEW `Taxons_details_view` AS select `Taxons_MP`.`cd_taxon` AS `cd_taxon`,`Taxons_MP`.`nom_taxon` AS `nom_taxon`,`Taxons_MP`.`cd_sandre` AS `cd_sandre`,`Taxons_MP`.`id_groupe` AS `id_groupe` from `Taxons_MP` union select `Taxons_MP_persos`.`cd_taxon_perso` AS `cd_taxon`,`Taxons_MP_persos`.`nom_taxon_perso` AS `nom_taxon`,`Taxons_MP_persos`.`cd_sandre` AS `cd_sandre`,`Taxons_MP_persos`.`id_groupe` AS `id_groupe` from `Taxons_MP_persos`;

CREATE VIEW `Taxons_view` AS select `Taxons_MP`.`cd_taxon` AS `cd_taxon`,`Taxons_MP`.`nom_taxon` AS `nom_taxon` from `Taxons_MP` union select `Taxons_MP_persos`.`cd_taxon_perso` AS `cd_taxon`,`Taxons_MP_persos`.`nom_taxon_perso` AS `nom_taxon` from `Taxons_MP_persos`;

