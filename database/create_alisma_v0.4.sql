SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

DROP SCHEMA IF EXISTS `alisma` ;
CREATE SCHEMA IF NOT EXISTS `alisma` DEFAULT CHARACTER SET utf8 ;
USE `alisma` ;

-- -----------------------------------------------------
-- Table `alisma`.`Auteurs`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `alisma`.`Auteurs` (
  `id_auteur` INT(11) NOT NULL AUTO_INCREMENT,
  `auteur` VARCHAR(100) NULL DEFAULT NULL,
  PRIMARY KEY (`id_auteur`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `alisma`.`Cours_Eau`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `alisma`.`Cours_Eau` (
  `id_cours_eau` INT(11) NOT NULL AUTO_INCREMENT,
  `cours_eau` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id_cours_eau`))
ENGINE = InnoDB
AUTO_INCREMENT = 5393
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `alisma`.`Groupes`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `alisma`.`Groupes` (
  `id_groupe` INT(11) NOT NULL AUTO_INCREMENT,
  `nom_groupe` VARCHAR(50) NULL DEFAULT NULL,
  PRIMARY KEY (`id_groupe`))
ENGINE = InnoDB
AUTO_INCREMENT = 12
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `alisma`.`Stations`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `alisma`.`Stations` (
  `id_station` INT(11) NOT NULL AUTO_INCREMENT,
  `cd_station` VARCHAR(10) NULL DEFAULT NULL,
  `station` VARCHAR(255) NOT NULL,
  `x` DOUBLE NOT NULL,
  `y` DOUBLE NOT NULL,
  `id_cours_eau` INT(11) NOT NULL,
  PRIMARY KEY (`id_station`),
  INDEX `fk_sites_cours_eau` (`id_cours_eau` ASC),
  CONSTRAINT `fk_sites_cours_eau`
    FOREIGN KEY (`id_cours_eau`)
    REFERENCES `alisma`.`Cours_Eau` (`id_cours_eau`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
AUTO_INCREMENT = 22798
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `alisma`.`Points_prelev`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `alisma`.`Points_prelev` (
  `id_pt_prel` INT(11) NOT NULL AUTO_INCREMENT,
  `coord_x` INT(11) NULL DEFAULT NULL,
  `coord_y` INT(11) NULL DEFAULT NULL,
  `altitude` DOUBLE NULL DEFAULT NULL,
  `longueur` DOUBLE NULL DEFAULT NULL,
  `largeur` DOUBLE NULL DEFAULT NULL,
  `id_station` INT(11) NOT NULL,
  `wgs84_x` VARCHAR(20) NULL DEFAULT NULL COMMENT 'Coordonnée X du point de prélèvement, en WGS84',
  `wgs84_y` VARCHAR(20) NULL DEFAULT NULL COMMENT 'Coordonnée Y du point de prélèvement, en wgs84',
  PRIMARY KEY (`id_pt_prel`),
  INDEX `fk_points_prelev_station` (`id_station` ASC),
  CONSTRAINT `fk_points_prelev_station`
    FOREIGN KEY (`id_station`)
    REFERENCES `alisma`.`Stations` (`id_station`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
AUTO_INCREMENT = 72
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `alisma`.`Statut`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `alisma`.`Statut` (
  `id_statut` INT(11) NOT NULL DEFAULT '0' COMMENT '0 : en saisie\n1 : validé',
  `libelle_statut` VARCHAR(50) NOT NULL,
  PRIMARY KEY (`id_statut`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COMMENT = 'Table des statuts du dossier';


-- -----------------------------------------------------
-- Table `alisma`.`Op_controle`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `alisma`.`Op_controle` (
  `id_op_controle` INT(11) NOT NULL AUTO_INCREMENT,
  `organisme` VARCHAR(50) NULL DEFAULT NULL,
  `operateur` VARCHAR(50) NULL DEFAULT NULL,
  `protocole` VARCHAR(50) NULL DEFAULT NULL,
  `coord_rive` VARCHAR(20) NULL DEFAULT NULL,
  `hydrologie` VARCHAR(50) NULL DEFAULT NULL,
  `meteo` VARCHAR(20) NULL DEFAULT NULL,
  `turbidite` VARCHAR(15) NULL DEFAULT NULL,
  `date_op` DATE NULL DEFAULT NULL,
  `observation` VARCHAR(255) NULL DEFAULT NULL,
  `ref_dossier` VARCHAR(25) NULL DEFAULT NULL,
  `id_pt_prel` INT(11) NOT NULL,
  `id_statut` INT(11) NULL DEFAULT NULL COMMENT '0 : en saisie\n1 : validé',
  PRIMARY KEY (`id_op_controle`),
  INDEX `fk_op_controle_points_prelev` (`id_pt_prel` ASC),
  INDEX `statut_op_controle_fk` (`id_statut` ASC),
  CONSTRAINT `fk_op_controle_points_prelev`
    FOREIGN KEY (`id_pt_prel`)
    REFERENCES `alisma`.`Points_prelev` (`id_pt_prel`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `statut_op_controle_fk`
    FOREIGN KEY (`id_statut`)
    REFERENCES `alisma`.`Statut` (`id_statut`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
AUTO_INCREMENT = 5
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `alisma`.`Lignes_op_controle`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `alisma`.`Lignes_op_controle` (
  `id_ligne_op_controle` INT(11) NOT NULL AUTO_INCREMENT,
  `pc_UR1` DOUBLE NOT NULL,
  `pc_UR2` DOUBLE NULL DEFAULT NULL,
  `cf` INT(11) NULL DEFAULT NULL,
  `id_op_controle` INT(11) NOT NULL,
  `id_taxon` VARCHAR(7) NOT NULL,
  PRIMARY KEY (`id_ligne_op_controle`),
  INDEX `fk_lignes_op_controle_op_controle` (`id_op_controle` ASC),
  CONSTRAINT `fk_lignes_op_controle_op_controle`
    FOREIGN KEY (`id_op_controle`)
    REFERENCES `alisma`.`Op_controle` (`id_op_controle`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
AUTO_INCREMENT = 19
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `alisma`.`Taxons_MP`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `alisma`.`Taxons_MP` (
  `cd_taxon` VARCHAR(6) NOT NULL DEFAULT '',
  `nom_taxon` VARCHAR(100) NOT NULL,
  `cote_spe` INT(11) NULL DEFAULT NULL,
  `coef_steno` INT(11) NULL DEFAULT NULL,
  `cd_sandre` INT(11) NULL DEFAULT NULL,
  `aquaticite` INT(11) NULL DEFAULT NULL,
  `date_creation` DATE NULL DEFAULT NULL,
  `id_groupe` INT(11) NOT NULL,
  `auteur` VARCHAR(50) NULL DEFAULT NULL,
  `cd_valide` VARCHAR(6) NULL DEFAULT NULL,
  `cd_contrib` VARCHAR(6) NULL DEFAULT NULL,
  PRIMARY KEY (`cd_taxon`),
  INDEX `fk_taxons_mp_groupes` (`id_groupe` ASC),
  CONSTRAINT `fk_taxons_mp_groupes`
    FOREIGN KEY (`id_groupe`)
    REFERENCES `alisma`.`Groupes` (`id_groupe`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `alisma`.`Taxons_MP_persos`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `alisma`.`Taxons_MP_persos` (
  `cd_taxon_perso` VARCHAR(7) NOT NULL DEFAULT '',
  `nom_taxon_perso` VARCHAR(50) NOT NULL,
  `createur` VARCHAR(50) NULL DEFAULT NULL,
  `cd_sandre` INT(11) NULL DEFAULT NULL,
  `date_creationP` DATE NOT NULL,
  `auteur` VARCHAR(50) NULL DEFAULT NULL,
  `id_groupe` INT(11) NOT NULL,
  PRIMARY KEY (`cd_taxon_perso`),
  INDEX `fk_taxons_mp_perso_groupes` (`id_groupe` ASC),
  CONSTRAINT `fk_taxons_mp_perso_groupes`
    FOREIGN KEY (`id_groupe`)
    REFERENCES `alisma`.`Groupes` (`id_groupe`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `alisma`.`Unite_releves`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `alisma`.`Unite_releves` (
  `id_UR` INT(11) NOT NULL AUTO_INCREMENT,
  `pc_UR` DOUBLE NULL DEFAULT NULL,
  `longueur_UR` DOUBLE NULL DEFAULT NULL,
  `largeur_UR` DOUBLE NULL DEFAULT NULL,
  `periphyton` VARCHAR(30) NULL DEFAULT NULL,
  `pc_vegetalisation` DOUBLE NULL DEFAULT NULL,
  `facies_dominant` VARCHAR(25) NULL DEFAULT NULL,
  `ch_lentique` INT(11) NULL DEFAULT NULL,
  `pl_lentique` INT(11) NULL DEFAULT NULL,
  `mouille` INT(11) NULL DEFAULT NULL,
  `fosse_dissipation` INT(11) NULL DEFAULT NULL,
  `ch_lotique` INT(11) NULL DEFAULT NULL,
  `radier` INT(11) NULL DEFAULT NULL,
  `cascade` INT(11) NULL DEFAULT NULL,
  `pl_courant` INT(11) NULL DEFAULT NULL,
  `rapide` INT(11) NULL DEFAULT NULL,
  `p1` INT(11) NULL DEFAULT NULL,
  `p2` INT(11) NULL DEFAULT NULL,
  `p3` INT(11) NULL DEFAULT NULL,
  `p4` INT(11) NULL DEFAULT NULL,
  `p5` INT(11) NULL DEFAULT NULL,
  `v1` INT(11) NULL DEFAULT NULL,
  `v2` INT(11) NULL DEFAULT NULL,
  `v3` INT(11) NULL DEFAULT NULL,
  `v4` INT(11) NULL DEFAULT NULL,
  `v5` INT(11) NULL DEFAULT NULL,
  `tres_ombrage` INT(11) NULL DEFAULT NULL,
  `ombrage` INT(11) NULL DEFAULT NULL,
  `peu_ombrage` INT(11) NULL DEFAULT NULL,
  `eclaire` INT(11) NULL DEFAULT NULL,
  `tres_eclaire` INT(11) NULL DEFAULT NULL,
  `vase_limons` INT(11) NULL DEFAULT NULL,
  `terre_marne_tourbe` INT(11) NULL DEFAULT NULL,
  `cailloux_pierres` INT(11) NULL DEFAULT NULL,
  `blocs_dalles` INT(11) NULL DEFAULT NULL,
  `sable_graviers` INT(11) NULL DEFAULT NULL,
  `racines` INT(11) NULL DEFAULT NULL,
  `debris_org` INT(11) NULL DEFAULT NULL,
  `artificiel` INT(11) NULL DEFAULT NULL,
  `pc_heterot` DOUBLE NULL DEFAULT NULL,
  `pc_algues` DOUBLE NULL DEFAULT NULL,
  `pc_bryo` DOUBLE NULL DEFAULT NULL,
  `pc_lichen` DOUBLE NULL DEFAULT NULL,
  `pc_phanero` DOUBLE NULL DEFAULT NULL,
  `pc_flottante` DOUBLE NULL DEFAULT NULL,
  `pc_immerg` DOUBLE NULL DEFAULT NULL,
  `pc_helophyte` DOUBLE NULL DEFAULT NULL,
  `autreType` VARCHAR(50) NULL DEFAULT NULL,
  `autreTypeClass` INT(11) NULL DEFAULT NULL,
  `typeUR` VARCHAR(50) NOT NULL,
  `id_op_controle` INT(11) NOT NULL,
  `numUR` INT(11) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id_UR`),
  INDEX `op_controle_unite_releves_fk` (`id_op_controle` ASC),
  CONSTRAINT `op_controle_unite_releves_fk`
    FOREIGN KEY (`id_op_controle`)
    REFERENCES `alisma`.`Op_controle` (`id_op_controle`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
AUTO_INCREMENT = 18
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `alisma`.`niveau_trophique`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `alisma`.`niveau_trophique` (
  `niveau_trophique_id` INT(11) NOT NULL,
  `niveau_trophique_libelle` VARCHAR(50) NOT NULL,
  PRIMARY KEY (`niveau_trophique_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COMMENT = 'Table des libelles des niveaux trophiques\n';


-- -----------------------------------------------------
-- Table `alisma`.`ibmr`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `alisma`.`ibmr` (
  `id_op_controle` INT(11) NOT NULL,
  `ibmr_value` DOUBLE NULL DEFAULT NULL,
  `robustesse_value` DOUBLE NULL DEFAULT NULL,
  `niveau_trophique_id` INT(11) NULL DEFAULT NULL,
  `robustesse_niveau_trophique_id` INT(11) NULL DEFAULT NULL,
  `taxon_robustesse` VARCHAR(50) NULL DEFAULT NULL,
  `cs_moy` DOUBLE NULL DEFAULT NULL,
  `cs_min` DOUBLE NULL DEFAULT NULL,
  `cs_max` DOUBLE NULL DEFAULT NULL,
  `coef_moy` DOUBLE NULL DEFAULT NULL,
  `coef_min` DOUBLE NULL DEFAULT NULL,
  `coef_max` DOUBLE NULL DEFAULT NULL,
  `nbtaxon_het` INT(11) NULL DEFAULT NULL,
  `nbtaxon_alg` INT(11) NULL DEFAULT NULL,
  `nbtaxon_bry` INT(11) NULL DEFAULT NULL,
  `nbtaxon_pte` INT(11) NULL DEFAULT NULL,
  `nbtaxon_pha` INT(11) NULL DEFAULT NULL,
  `nbtaxon_lic` INT(11) NULL DEFAULT NULL,
  `nbtaxon_total` INT(11) NULL DEFAULT NULL,
  `nbtaxon_contrib` INT(11) NULL DEFAULT NULL,
  `nbtaxon_steno1` INT(11) NULL DEFAULT NULL,
  `nbtaxon_steno2` INT(11) NULL DEFAULT NULL,
  `nbtaxon_steno3` INT(11) NULL DEFAULT NULL,
  PRIMARY KEY (`id_op_controle`),
  INDEX `niveau_trophique_ibmr_fk` (`niveau_trophique_id` ASC),
  INDEX `niveau_trophique_ibmr_fk1` (`robustesse_niveau_trophique_id` ASC),
  CONSTRAINT `niveau_trophique_ibmr_fk`
    FOREIGN KEY (`niveau_trophique_id`)
    REFERENCES `alisma`.`niveau_trophique` (`niveau_trophique_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `niveau_trophique_ibmr_fk1`
    FOREIGN KEY (`robustesse_niveau_trophique_id`)
    REFERENCES `alisma`.`niveau_trophique` (`niveau_trophique_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `op_controle_ibmr_fk`
    FOREIGN KEY (`id_op_controle`)
    REFERENCES `alisma`.`Op_controle` (`id_op_controle`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COMMENT = 'Table des informations specifiques du calcul de l\'IBMR';

USE `alisma` ;

-- -----------------------------------------------------
-- Placeholder table for view `alisma`.`Taxons_details_view`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `alisma`.`Taxons_details_view` (`cd_taxon` INT, `nom_taxon` INT, `cd_sandre` INT, `id_groupe` INT);

-- -----------------------------------------------------
-- Placeholder table for view `alisma`.`Taxons_view`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `alisma`.`Taxons_view` (`cd_taxon` INT, `nom_taxon` INT);

-- -----------------------------------------------------
-- View `alisma`.`Taxons_details_view`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `alisma`.`Taxons_details_view`;
USE `alisma`;
CREATE  OR REPLACE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `alisma`.`Taxons_details_view` AS select `alisma`.`Taxons_MP`.`cd_taxon` AS `cd_taxon`,`alisma`.`Taxons_MP`.`nom_taxon` AS `nom_taxon`,`alisma`.`Taxons_MP`.`cd_sandre` AS `cd_sandre`,`alisma`.`Taxons_MP`.`id_groupe` AS `id_groupe` from `alisma`.`Taxons_MP` union select `alisma`.`Taxons_MP_persos`.`cd_taxon_perso` AS `cd_taxon`,`alisma`.`Taxons_MP_persos`.`nom_taxon_perso` AS `nom_taxon`,`alisma`.`Taxons_MP_persos`.`cd_sandre` AS `cd_sandre`,`alisma`.`Taxons_MP_persos`.`id_groupe` AS `id_groupe` from `alisma`.`Taxons_MP_persos`;

-- -----------------------------------------------------
-- View `alisma`.`Taxons_view`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `alisma`.`Taxons_view`;
USE `alisma`;
CREATE  OR REPLACE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `alisma`.`Taxons_view` AS select `alisma`.`Taxons_MP`.`cd_taxon` AS `cd_taxon`,`alisma`.`Taxons_MP`.`nom_taxon` AS `nom_taxon` from `alisma`.`Taxons_MP` union select `alisma`.`Taxons_MP_persos`.`cd_taxon_perso` AS `cd_taxon`,`alisma`.`Taxons_MP_persos`.`nom_taxon_perso` AS `nom_taxon` from `alisma`.`Taxons_MP_persos`;

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
