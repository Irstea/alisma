/*
 contenu des tables de parametres
 */ 
INSERT INTO PUBLIC.groupes
(
  id_groupe,
  nom_groupe
)
VALUES
(
  1,
  'HET'
),
(
  2,
  'ALG'
),
(
  3,
  'BRh'
),
(
  4,
  'BRm'
),
(
  5,
  'BRl'
),
(
  6,
  'PTE'
),
(
  7,
  'PHe'
),
(
  8,
  'PHg'
),
(
  9,
  'PHx'
),
(
  10,
  'PHy'
),
(
  11,
  'LIC'
);

INSERT INTO PUBLIC.statut
(
  id_statut,
  libelle_statut
)
VALUES
(
  0,
  'En saisie'
),
(
  1,
  'Validé'
),
(2,'Calculé SEEE');

INSERT INTO PUBLIC.niveau_trophique
(
  niveau_trophique_id,
  niveau_trophique_libelle
)
VALUES
(
  1,
  'très faible'
),
(
  2,
  'faible'
),
(
  3,
  'moyen'
),
(
  4,
  'fort'
),
(
  5,
  'très élevé'
);

INSERT INTO PUBLIC.protocole
(
  protocole_id,
  protocole_libelle
)
VALUES
(
  1,
  'IBMR standard'
),
(
  2,
  'IBMR grands cours d''eau'
),
(
  3,
  'IBMR mixte'
);

INSERT INTO PUBLIC.rive
(
  rive_id,
  rive_libelle
)
VALUES
(
  1,
  'Amont rive droite'
),
(
  2,
  'Amont rive gauche'
),
(
  3,
  'Amont milieu rive'
),
(
  4,
  'Aval rive droite'
),
(
  5,
  'Aval rive gauche'
),
(
  6,
  'Aval milieu rive'
);

INSERT INTO PUBLIC.hydrologie
(
  hydrologie_id,
  hydrologie_libelle
)
VALUES
(
  1,
  'Etiage sévère'
),
(
  2,
  'Etiage normal'
),
(
  3,
  'Moyennes eaux'
),
(
  4,
  'Hautes eaux'
);

INSERT INTO PUBLIC.meteo
(
  meteo_id,
  meteo_libelle
)
VALUES
(
  1,
  'Ensoleillé'
),
(
  2,
  'Faiblement nuageux'
),
(
  3,
  'Fortement nuageux'
),
(
  4,
  'Pluie fine'
),
(
  5,
  'Orage - pluie forte'
),
(
  6,
  'Conditions crépusculaires'
);

INSERT INTO PUBLIC.turbidite
(
  turbidite_id,
  turbidite_libelle
)
VALUES
(
  1,
  'nulle'
),
(
  2,
  'faible'
),
(
  3,
  'moyenne'
),
(
  4,
  'forte'
);

INSERT INTO PUBLIC.periphyton
(
  periphyton_id,
  periphyton_libelle
)
VALUES
(
  1,
  'Absent'
),
(
  2,
  'Peu abondant'
),
(
  3,
  'Abondant'
),
(
  4,
  'Très abondant'
);

INSERT INTO PUBLIC.facies
(
  facies_id,
  facies_libelle
)
VALUES
(
  1,
  'Chenal lentique'
),
(
  2,
  'Plat lentique'
),
(
  3,
  'Mouille'
),
(
  4,
  'Fosse de dissipation'
),
(
  5,
  'Chenal lotique'
),
(
  6,
  'Radier'
),
(
  7,
  'Cascade'
),
(
  8,
  'Plat courant'
),
(
  9,
  'Rapide'
),
(
  10,
  'Autre'
);

INSERT INTO PUBLIC.facies_autre_type
(
  facies_autre_type_id,
  facies_autre_type_libelle
)
VALUES
(
  1,
  'Bordure'
),
(
  2,
  'Escalier'
),
(
  3,
  'Fosse d''affouillement'
),
(
  4,
  'Darse'
),
(
  5,
  'Flaque'
),
(
  6,
  'Trou d''eau'
),
(
  7,
  'Seuil'
),
(
  8,
  'Zones végétalisées latérales'
),
(
  9,
  'autre'
);

INSERT INTO PUBLIC.type_ur
(
  type_ur_id,
  type_ur_libelle
)
VALUES
(
  1,
  'Unité la plus courante'
),
(
  2,
  'Unité la moins courante'
),
(
  3,
  'Inconnue'
),
(
  4,
  'Unique'
),
(
  5,
  'Chenal'
),
(
  6,
  'Berges'
),
(
  7,
  'Rive droite'
),
(
  8,
  'Rive gauche'
);

INSERT INTO PUBLIC.typo (typo_id,typo_name,ibmr_ref,groupe) VALUES 
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
 (78,'P3',14,6),
 (79,'P4',14.61,3),
 (80,'P5',12.94,4),
 (81,'P9',11.17,1),
 (82,'P9-A',11.17,1),
 (83,'PTP16-A',13.17,7),
 (84,'PTP16-B',13.17,7),
 (85,'PTP8',14,6),
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
 (108,'TP17/3-21',14,6),
 (109,'TP18',11.17,1),
 (110,'TP20',13.09,2),
 (111,'TP21',14.61,3),
 (112,'TP22',14.61,3),
 (113,'TP3',14,6),
 (114,'TP4',14.61,3),
 (115,'TP5',12.94,4),
 (116,'TP6',11.17,1),
 (117,'TP7',11.17,1),
 (118,'TP9',11.17,1),
 (119,'TTGA',9,8),
 (120,'TTGL',9,8);
 
INSERT INTO PUBLIC.classe_etat (classe_etat_id,classe_etat_libelle,classe_etat_seuil) 
VALUES 
 (1,'Très bon',0.92),
 (2,'Bon',0.77),
 (3,'Moyen',0.64),
 (4,'Médiocre',0.51),
 (5,'Mauvais',-1);
 
