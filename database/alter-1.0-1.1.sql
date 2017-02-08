/*
 * Alisma - script de modification de la structure de la base de donnees
 * passage de la version 1.0 a 1.1
 */

ALTER TABLE ibmr ADD COLUMN seee_date DATE AFTER nbtaxon_steno3;
ALTER TABLE ibmr ADD COLUMN seee_version VARCHAR(255) AFTER seee_date;
ALTER TABLE ibmr ADD COLUMN seee_ibmr FLOAT AFTER seee_version;
ALTER TABLE ibmr ADD COLUMN seee_nbtaxon_contrib INT AFTER seee_ibmr;
ALTER TABLE ibmr ADD COLUMN seee_robustesse_value FLOAT AFTER seee_nbtaxon_contrib;
ALTER TABLE ibmr ADD COLUMN seee_taxon_robustesse VARCHAR(255) AFTER seee_robustesse_value;
ALTER TABLE ibmr ADD COLUMN ek_nb_robustesse INT AFTER taxon_robustesse;

insert into `Statut` (id_statut, libelle_statut) values (2, 'Calculé SEEE');

/*
 * Typologie des stations
 */
 
ALTER TABLE Op_controle ADD COLUMN typo_id INT AFTER turbidite_id;

CREATE TABLE typo (
                typo_id INT AUTO_INCREMENT NOT NULL,
                typo_name VARCHAR(255) NOT NULL,
                ibmr_ref FLOAT NOT NULL,
                groupe INT not null,
                PRIMARY KEY (typo_id)
);

ALTER TABLE typo COMMENT 'Typologie des stations';

ALTER TABLE typo MODIFY COLUMN typo_name VARCHAR(255) COMMENT 'Code typologique du cours d''eau';
alter table typo modify column groupe integer comment 'Groupe de la typologie';

ALTER TABLE typo MODIFY COLUMN ibmr_ref FLOAT COMMENT 'Valeur de référence de l''IBMR pour le type de cours d''eau considéré';
ALTER TABLE Op_controle ADD CONSTRAINT typo_op_controle_fk
FOREIGN KEY (typo_id)
REFERENCES typo (typo_id)
ON DELETE NO ACTION
ON UPDATE NO ACTION;



CREATE TABLE classe_etat (
                classe_etat_id INT NOT NULL,
                classe_etat_libelle VARCHAR(255) NOT NULL,
                PRIMARY KEY (classe_etat_id)
);

ALTER TABLE classe_etat COMMENT 'Table des classes d''état';

ALTER TABLE ibmr ADD COLUMN classe_etat_id INT AFTER robustesse_niveau_trophique_id;

ALTER TABLE ibmr ADD CONSTRAINT classe_etat_ibmr_fk
FOREIGN KEY (classe_etat_id)
REFERENCES classe_etat (classe_etat_id)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE Op_controle ADD CONSTRAINT typo_op_controle_fk
FOREIGN KEY (typo_id)
REFERENCES typo (typo_id)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

insert into classe_etat (classe_etat_id, classe_etat_libelle)
values 
(1, 'Très bon'),
(2, 'Bon'),
(3, 'Moyen'),
(4, 'Médiocre'),
(5, 'Mauvais');

INSERT INTO typo
  (typo_id, typo_name, ibmr_ref, groupe)
VALUES
  (1,'G1',12.94,4),
  (2,'G10',9.38,5),
  (3,'G10/4',11.17,1),
  (4,'G11/3-21',9.38,5),
  (5,'G12',13.09,2),
  (6,'G14/1',9.38,5),
  (7,'G14/3',9.38,5),
  (8,'G15/5',11.17,1),
  (9,'G16',13.09,2),
  (10,'G17/3-21',9.38,5),
  (11,'G18/4',11.17,1),
  (12,'G21',13.09,2),
  (13,'G3',9.38,5),
  (14,'G3/19-8',9.38,5),
  (15,'G5',11.17,1),
  (16,'G6',11.17,1),
  (17,'G9',9.38,5),
  (18,'G9/10',9.38,5),
  (19,'G9-10/21',9.38,5),
  (20,'GM14',11.17,1),
  (21,'GM19/8',12.94,4),
  (22,'GM20',11.17,1),
  (23,'GM20/9',11.17,1),
  (24,'GM22',11.17,1),
  (25,'GM5/2',11.17,1),
  (26,'GM6/1',11.17,1),
  (27,'GM6/2-7',11.17,1),
  (28,'GM6/8',11.17,1),
  (29,'GM7/2',11.17,1),
  (30,'GM8',13.09,2),
  (31,'GMP7',11.17,1),
  (32,'M1',12.94,4),
  (33,'M10',9.38,5),
  (34,'M10/4',11.17,1),
  (35,'M11/3-21',11.17,1),
  (36,'M12-A',13.09,2),
  (37,'M12-B',13.09,2),
  (38,'M13',13.09,2),
  (39,'M14/1',12.94,4),
  (40,'M14/3-11',11.17,1),
  (41,'M14/3-8',12.94,4),
  (42,'M15-17/3-21',11.17,1),
  (43,'M16-A',13.17,7),
  (44,'M16-B',13.17,7),
  (45,'M17',11.17,1),
  (46,'M18/4',11.17,1),
  (47,'M21',13.17,7),
  (48,'M3',13.17,7),
  (49,'M3/19',12.94,4),
  (50,'M3/8',13.17,7),
  (51,'M4',14.61,3),
  (52,'M5',12.94,4),
  (53,'M8-A',13.17,7),
  (54,'M9',9.38,5),
  (55,'M9/10',9.38,5),
  (56,'M9-10/21',9.38,5),
  (57,'M9-A',11.17,1),
  (58,'MP15',11.17,1),
  (59,'MP15/5',11.17,1),
  (60,'MP18',11.17,1),
  (61,'MP6',11.17,1),
  (62,'P1',12.94,4),
  (63,'P10',11.17,1),
  (64,'P11',12.94,4),
  (65,'P11/3-21',11.17,1),
  (66,'P12-A',13.09,2),
  (67,'P12-B',13.09,2),
  (68,'P13',13.09,2),
  (69,'P14',11.17,1),
  (70,'P14/1',11.17,1),
  (71,'P17',11.17,1),
  (72,'P17/3-21',11.17,1),
  (73,'P18/4',14.61,3),
  (74,'P19',12.94,4),
  (75,'P20',13.09,2),
  (76,'P21',13.17,7),
  (77,'P22',13.09,2),
  (78,'P3',14.0,6),
  (79,'P4',14.61,3),
  (80,'P5',12.94,4),
  (81,'P9',11.17,1),
  (82,'P9-A',11.17,1),
  (83,'PTP16-A',13.17,7),
  (84,'PTP16-B',13.17,7),
  (85,'PTP8',14.0,6),
  (86,'PTP8-A',14.61,3),
  (87,'TG10-15/4',9.38,5),
  (88,'TG11/3-21',9.38,5),
  (89,'TG14/1',9.38,5),
  (90,'TG14/3-11',9.38,5),
  (91,'TG15',9.38,5),
  (92,'TG17/3-21',9.38,5),
  (93,'TG22/10',9.38,5),
  (94,'TG5/2',9.38,5),
  (95,'TG6/1-8',9.38,5),
  (96,'TG6-7/2',9.38,5),
  (97,'TG9',9.38,5),
  (98,'TG9/21',9.38,5),
  (99,'TP1',12.94,4),
  (100,'TP10',11.17,1),
  (101,'TP11',11.17,1),
  (102,'TP12-A',13.09,2),
  (103,'TP12-B',13.09,2),
  (104,'TP13',13.09,2),
  (105,'TP14',11.17,1),
  (106,'TP15',12.94,4),
  (107,'TP17',11.17,1),
  (108,'TP17/3-21',14.0,6),
  (109,'TP18',11.17,1),
  (110,'TP20',13.09,2),
  (111,'TP21',14.61,3),
  (112,'TP22',14.61,3),
  (113,'TP3',14.0,6),
  (114,'TP4',14.61,3),
  (115,'TP5',12.94,4),
  (116,'TP6',11.17,1),
  (117,'TP7',11.17,1),
  (118,'TP9',11.17,1),
  (119,'TTGA',9.0,8),
  (120,'TTGL',9.0,8)
ON DUPLICATE KEY UPDATE
  typo_name = values(typo_name),
  ibmr_ref = values(ibmr_ref),
  groupe = values(groupe);
