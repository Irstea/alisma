CREATE DATABASE `alisma`  DEFAULT CHARACTER SET utf8 ;
CREATE TABLE `classe_etat` (
  `classe_etat_id` int(11) NOT NULL,
  `classe_etat_libelle` varchar(255) NOT NULL,
  `classe_etat_seuil` float DEFAULT NULL COMMENT 'Seuil plancher de la classe (>= à la valeur)',
  PRIMARY KEY (`classe_etat_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Table des classes d''état';

CREATE TABLE `cours_eau` (
  `id_cours_eau` int(11) NOT NULL AUTO_INCREMENT,
  `cours_eau` varchar(255) NOT NULL,
  PRIMARY KEY (`id_cours_eau`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE `facies` (
  `facies_id` int(11) NOT NULL,
  `facies_libelle` varchar(255) NOT NULL,
  PRIMARY KEY (`facies_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Type de facies dominant';

CREATE TABLE `facies_autre_type` (
  `facies_autre_type_id` int(11) NOT NULL,
  `facies_autre_type_libelle` varchar(255) NOT NULL,
  PRIMARY KEY (`facies_autre_type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Table des autres types de facies';

CREATE TABLE `groupes` (
  `id_groupe` int(11) NOT NULL AUTO_INCREMENT,
  `nom_groupe` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id_groupe`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8;

CREATE TABLE `hydrologie` (
  `hydrologie_id` int(11) NOT NULL,
  `hydrologie_libelle` varchar(255) NOT NULL,
  PRIMARY KEY (`hydrologie_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Table des facies hydrologiques';

CREATE TABLE `meteo` (
  `meteo_id` int(11) NOT NULL,
  `meteo_libelle` varchar(255) NOT NULL,
  PRIMARY KEY (`meteo_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Table des conditions meteo';

CREATE TABLE `niveau_trophique` (
  `niveau_trophique_id` int(11) NOT NULL,
  `niveau_trophique_libelle` varchar(255) NOT NULL,
  PRIMARY KEY (`niveau_trophique_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Table des libelles des niveaux trophiques\n';

CREATE TABLE `periphyton` (
  `periphyton_id` int(11) NOT NULL,
  `periphyton_libelle` varchar(255) NOT NULL,
  PRIMARY KEY (`periphyton_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Table des abondances du périphyton';

CREATE TABLE `protocole` (
  `protocole_id` int(11) NOT NULL AUTO_INCREMENT,
  `protocole_libelle` varchar(255) NOT NULL,
  PRIMARY KEY (`protocole_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='Table des protocoles d''échantillonnage';

CREATE TABLE `rive` (
  `rive_id` int(11) NOT NULL DEFAULT '0' COMMENT '1 : gauche\n2 : droite',
  `rive_libelle` varchar(255) NOT NULL,
  PRIMARY KEY (`rive_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Table des rives';

CREATE TABLE `stations` (
  `id_station` int(11) NOT NULL AUTO_INCREMENT,
  `typo_id` int(11) DEFAULT NULL,
  `cd_station` varchar(10) DEFAULT NULL,
  `station` varchar(255) NOT NULL,
  `x` double DEFAULT NULL,
  `y` double DEFAULT NULL,
  `id_cours_eau` int(11) DEFAULT NULL,
  PRIMARY KEY (`id_station`),
  KEY `fk_sites_cours_eau` (`id_cours_eau`),
  KEY `typo_id` (`typo_id`),
  CONSTRAINT `fk_sites_cours_eau` FOREIGN KEY (`id_cours_eau`) REFERENCES `cours_eau` (`id_cours_eau`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `stations_ibfk_1` FOREIGN KEY (`typo_id`) REFERENCES `typo` (`typo_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE `statut` (
  `id_statut` int(11) NOT NULL DEFAULT '0' COMMENT '0 : en saisie\n1 : validé',
  `libelle_statut` varchar(50) NOT NULL,
  PRIMARY KEY (`id_statut`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Table des statuts du dossier';

CREATE TABLE `taxons_mp` (
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
  CONSTRAINT `fk_taxons_mp_groupes` FOREIGN KEY (`id_groupe`) REFERENCES `groupes` (`id_groupe`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `taxons_perso` (
  `cd_taxon_perso` varchar(7) NOT NULL DEFAULT '',
  `nom_taxon_perso` varchar(50) NOT NULL,
  `createur` varchar(50) DEFAULT NULL,
  `cd_sandre` int(11) DEFAULT NULL,
  `date_creation_perso` date NOT NULL,
  `auteur` varchar(50) DEFAULT NULL,
  `id_groupe` int(11) NOT NULL,
  PRIMARY KEY (`cd_taxon_perso`),
  KEY `fk_taxons_mp_perso_groupes` (`id_groupe`),
  CONSTRAINT `fk_taxons_mp_perso_groupes` FOREIGN KEY (`id_groupe`) REFERENCES `groupes` (`id_groupe`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `turbidite` (
  `turbidite_id` int(11) NOT NULL,
  `turbidite_libelle` varchar(255) NOT NULL,
  PRIMARY KEY (`turbidite_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Table des conditions de turbidite';

CREATE TABLE `type_ur` (
  `type_ur_id` int(11) NOT NULL,
  `type_ur_libelle` varchar(255) NOT NULL,
  PRIMARY KEY (`type_ur_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Type de l''unité de relevé';

CREATE TABLE `typo` (
  `typo_id` int(11) NOT NULL AUTO_INCREMENT,
  `typo_name` varchar(255) DEFAULT NULL COMMENT 'Code typologique du cours d''eau',
  `ibmr_ref` float DEFAULT NULL COMMENT 'Valeur de référence de l''IBMR pour le type de cours d''eau considéré',
  `groupe` int(11) DEFAULT NULL COMMENT 'Groupe de la typologie',
  PRIMARY KEY (`typo_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='Typologie des stations';

CREATE TABLE `op_controle` (
  `id_op_controle` int(11) NOT NULL AUTO_INCREMENT,
  `organisme` varchar(50) DEFAULT NULL,
  `operateur` varchar(50) DEFAULT NULL,
  `date_op` date DEFAULT NULL,
  `observation` varchar(255) DEFAULT NULL,
  `ref_dossier` varchar(25) DEFAULT NULL,
  `id_statut` int(11) DEFAULT NULL COMMENT '0 : en saisie\n1 : validé',
  `protocole_id` int(11) DEFAULT NULL,
  `rive_id` int(11) DEFAULT NULL,
  `hydrologie_id` int(11) DEFAULT NULL,
  `meteo_id` int(11) DEFAULT NULL,
  `turbidite_id` int(11) DEFAULT NULL,
  `typo_id` int(11) DEFAULT NULL,
  `releve_dce` smallint(6) NOT NULL DEFAULT '1',
  `uuid` varchar(36) DEFAULT NULL,
  `producteur_code` varchar(255) DEFAULT NULL COMMENT 'Code du producteur',
  `producteur_name` varchar(255) DEFAULT NULL COMMENT 'Nom du producteur',
  `preleveur_code` varchar(255) DEFAULT NULL COMMENT 'Code du preleveur',
  `preleveur_name` varchar(255) DEFAULT NULL COMMENT 'Nom du preleveur',
  `determinateur_code` varchar(255) DEFAULT NULL COMMENT 'Code du determinateur',
  `determinateur_name` varchar(255) DEFAULT NULL COMMENT 'Nom du determinateur',
  `coord_x` int(11) DEFAULT NULL COMMENT 'Coordonnée X du point amont, en Lambert 93',
  `coord_y` int(11) DEFAULT NULL COMMENT 'Coordonnée Y du point amont, en Lambert 93',
  `altitude` float DEFAULT NULL COMMENT 'Altitude du point amont, en mètres',
  `longueur` double DEFAULT NULL COMMENT 'Longueur de la station, en mètres',
  `largeur` double DEFAULT NULL COMMENT 'Largeur du cours d''eau, en mètres',
  `wgs84_x` double DEFAULT NULL,
  `wgs84_y` double DEFAULT NULL,
  `id_station` int(11) DEFAULT NULL,
  `lambert_x_aval` int(11) DEFAULT NULL COMMENT 'Coordonnée x du point aval, en Lambert 93',
  `lambert_y_aval` int(11) DEFAULT NULL COMMENT 'Coordonnée Y du point aval, en Lambert 93',
  `wgs84_x_aval` double DEFAULT NULL,
  `wgs84_y_aval` double DEFAULT NULL,
  PRIMARY KEY (`id_op_controle`),
  KEY `statut_op_controle_fk` (`id_statut`),
  KEY `hydrologie_op_controle_fk` (`hydrologie_id`),
  KEY `meteo_op_controle_fk` (`meteo_id`),
  KEY `protocole_op_controle_fk` (`protocole_id`),
  KEY `rive_op_controle_fk` (`rive_id`),
  KEY `turbidite_op_controle_fk` (`turbidite_id`),
  KEY `typo_op_controle_fk` (`typo_id`),
  KEY `stations_op_controle_fk` (`id_station`),
  CONSTRAINT `hydrologie_op_controle_fk` FOREIGN KEY (`hydrologie_id`) REFERENCES `hydrologie` (`hydrologie_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `meteo_op_controle_fk` FOREIGN KEY (`meteo_id`) REFERENCES `meteo` (`meteo_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `protocole_op_controle_fk` FOREIGN KEY (`protocole_id`) REFERENCES `protocole` (`protocole_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `rive_op_controle_fk` FOREIGN KEY (`rive_id`) REFERENCES `rive` (`rive_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `stations_op_controle_fk` FOREIGN KEY (`id_station`) REFERENCES `stations` (`id_station`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `statut_op_controle_fk` FOREIGN KEY (`id_statut`) REFERENCES `statut` (`id_statut`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `turbidite_op_controle_fk` FOREIGN KEY (`turbidite_id`) REFERENCES `turbidite` (`turbidite_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `typo_op_controle_fk` FOREIGN KEY (`typo_id`) REFERENCES `typo` (`typo_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE `unite_releves` (
  `id_ur` int(11) NOT NULL AUTO_INCREMENT,
  `pc_ur` double DEFAULT NULL,
  `longueur_ur` double DEFAULT NULL,
  `largeur_ur` double DEFAULT NULL,
  `pc_vegetalisation` double DEFAULT NULL,
  `ch_lentique` int(11) DEFAULT NULL,
  `pl_lentique` int(11) DEFAULT NULL,
  `mouille` int(11) DEFAULT NULL,
  `fosse_dissipation` int(11) DEFAULT NULL,
  `ch_lotique` int(11) DEFAULT NULL,
  `radier` int(11) DEFAULT NULL,
  `waterfall` int(11) DEFAULT NULL,
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
  `autretypeclass` int(11) DEFAULT NULL,
  `id_op_controle` int(11) NOT NULL,
  `type_ur_id` int(11) NOT NULL,
  `periphyton_id` int(11) DEFAULT NULL,
  `facies_id` int(11) DEFAULT NULL,
  `facies_autre_type_id` int(11) DEFAULT NULL,
  `num_ur` int(11) NOT NULL,
  PRIMARY KEY (`id_ur`),
  KEY `op_controle_unite_releves_fk` (`id_op_controle`),
  KEY `facies_unite_releves_fk` (`facies_id`),
  KEY `facies_autre_type_unite_releves_fk` (`facies_autre_type_id`),
  KEY `periphyton_unite_releves_fk` (`periphyton_id`),
  KEY `type_ur_unite_releves_fk` (`type_ur_id`),
  CONSTRAINT `facies_autre_type_unite_releves_fk` FOREIGN KEY (`facies_autre_type_id`) REFERENCES `facies_autre_type` (`facies_autre_type_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `facies_unite_releves_fk` FOREIGN KEY (`facies_id`) REFERENCES `facies` (`facies_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `op_controle_unite_releves_fk` FOREIGN KEY (`id_op_controle`) REFERENCES `op_controle` (`id_op_controle`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `periphyton_unite_releves_fk` FOREIGN KEY (`periphyton_id`) REFERENCES `periphyton` (`periphyton_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `type_ur_unite_releves_fk` FOREIGN KEY (`type_ur_id`) REFERENCES `type_ur` (`type_ur_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE `ibmr` (
  `id_op_controle` int(11) NOT NULL,
  `ibmr_value` double DEFAULT NULL,
  `eqr_value` float DEFAULT NULL COMMENT 'Ecological Quality Ratio : ibmr calculée rapportée à l''ibmr de la typologie retenue',
  `robustesse_value` double DEFAULT NULL,
  `robustesse_eqr_value` float DEFAULT NULL COMMENT 'Ecological Quality Ratio : ibmr calculée rapportée à l''ibmr de la typologie retenue pour le calcul de robustesse',
  `niveau_trophique_id` int(11) DEFAULT NULL,
  `robustesse_niveau_trophique_id` int(11) DEFAULT NULL,
  `classe_etat_id` int(11) DEFAULT NULL,
  `taxon_robustesse` varchar(255) DEFAULT NULL,
  `ek_nb_robustesse` int(11) DEFAULT NULL,
  `robustesse_classe_etat_id` int(11) DEFAULT NULL,
  `cs_moy` double DEFAULT NULL,
  `cs_min` double DEFAULT NULL,
  `cs_max` double DEFAULT NULL,
  `coef_moy` double DEFAULT NULL,
  `coef_min` double DEFAULT NULL,
  `coef_max` double DEFAULT NULL,
  `nbtaxon_het` int(11) DEFAULT NULL,
  `nbtaxon_alg` int(11) DEFAULT NULL,
  `nbtaxon_bry` int(11) DEFAULT NULL,
  `nbtaxon_pte` int(11) DEFAULT NULL,
  `nbtaxon_pha` int(11) DEFAULT NULL,
  `nbtaxon_lic` int(11) DEFAULT NULL,
  `nbtaxon_total` int(11) DEFAULT NULL,
  `nbtaxon_contrib` int(11) DEFAULT NULL,
  `nbtaxon_steno1` int(11) DEFAULT NULL,
  `nbtaxon_steno2` int(11) DEFAULT NULL,
  `nbtaxon_steno3` int(11) DEFAULT NULL,
  `seee_date` date DEFAULT NULL,
  `seee_version` varchar(255) DEFAULT NULL,
  `seee_ibmr` float DEFAULT NULL,
  `seee_nbtaxon_contrib` int(11) DEFAULT NULL,
  `seee_robustesse_value` float DEFAULT NULL,
  `seee_taxon_robustesse` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id_op_controle`),
  KEY `niveau_trophique_ibmr_fk` (`niveau_trophique_id`),
  KEY `niveau_trophique_ibmr_fk1` (`robustesse_niveau_trophique_id`),
  KEY `classe_etat_ibmr_fk` (`classe_etat_id`),
  CONSTRAINT `classe_etat_ibmr_fk` FOREIGN KEY (`classe_etat_id`) REFERENCES `classe_etat` (`classe_etat_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `niveau_trophique_ibmr_fk` FOREIGN KEY (`niveau_trophique_id`) REFERENCES `niveau_trophique` (`niveau_trophique_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `niveau_trophique_ibmr_fk1` FOREIGN KEY (`robustesse_niveau_trophique_id`) REFERENCES `niveau_trophique` (`niveau_trophique_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `op_controle_ibmr_fk` FOREIGN KEY (`id_op_controle`) REFERENCES `op_controle` (`id_op_controle`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Table des informations specifiques du calcul de l''IBMR';

CREATE TABLE `lignes_op_controle` (
  `id_ligne_op_controle` int(11) NOT NULL AUTO_INCREMENT,
  `pc_ur1` double DEFAULT NULL,
  `pc_ur2` double DEFAULT NULL,
  `cf` int(11) DEFAULT NULL,
  `id_op_controle` int(11) NOT NULL,
  `id_taxon` varchar(7) NOT NULL,
  PRIMARY KEY (`id_ligne_op_controle`),
  KEY `fk_lignes_op_controle_op_controle` (`id_op_controle`),
  CONSTRAINT `fk_lignes_op_controle_op_controle` FOREIGN KEY (`id_op_controle`) REFERENCES `op_controle` (`id_op_controle`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


CREATE VIEW `taxons_details_view` AS select `taxons_mp`.`cd_taxon` AS `cd_taxon`,`taxons_mp`.`nom_taxon` AS `nom_taxon`,`taxons_mp`.`cd_sandre` AS `cd_sandre`,`taxons_mp`.`id_groupe` AS `id_groupe` from `taxons_mp` union select `taxons_perso`.`cd_taxon_perso` AS `cd_taxon`,`taxons_perso`.`nom_taxon_perso` AS `nom_taxon`,`taxons_perso`.`cd_sandre` AS `cd_sandre`,`taxons_perso`.`id_groupe` AS `id_groupe` from `taxons_perso`;

CREATE VIEW `taxons_view` AS select `taxons_mp`.`cd_taxon` AS `cd_taxon`,`taxons_mp`.`nom_taxon` AS `nom_taxon` from `taxons_mp` union select `taxons_perso`.`cd_taxon_perso` AS `cd_taxon`,`taxons_perso`.`nom_taxon_perso` AS `nom_taxon` from `taxons_perso`;
