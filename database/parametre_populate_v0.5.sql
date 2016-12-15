INSERT INTO Statut
  (id_statut, libelle_statut)
VALUES
  (0,'En saisie'),
  (1,'Validé')
ON DUPLICATE KEY UPDATE
  libelle_statut = values(libelle_statut);

INSERT INTO Groupes
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
ON DUPLICATE KEY UPDATE
  nom_groupe = values(nom_groupe);

insert into Cours_Eau (id_cours_eau, cours_eau)
values (0, "Inconnu");

insert into alisma.niveau_trophique(niveau_trophique_id, niveau_trophique_libelle) values 
(1, 'très faible'),(2, 'faible'), (3, 'moyen'), (4,'fort'), (5, 'très élevé');

insert into protocole(protocole_id, protocole_libelle) values
(1, 'IBMR standard'),
(2, 'IBMR grands cours d''eau'),
(3, 'IBMR mixte');

insert into rive(rive_id, rive_libelle) values
(1, 'gauche'),
(2, 'droite');

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
(1, 'Nulle ou faible'),
(2, 'Moyenne'),
(3, 'Forte');

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
(8, 'Zones végétalisées latérales');

insert into type_ur (type_ur_id, type_ur_libelle) values
(1, 'Unité la plus courante'),
(2, 'Unité la moins courante'),
(3, 'Inconnue'),
(4, 'Unique'),
(5, 'Chenal'), 
(6, 'Berges'),
(7, 'Rive droite'),
(8, 'Rive gauche');