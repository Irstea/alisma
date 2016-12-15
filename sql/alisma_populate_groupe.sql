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

INSERT INTO Statut
  (id_statut, libelle_statut)
VALUES
  (0,'En saisie'),
  (1,'Validé')
ON DUPLICATE KEY UPDATE
  libelle_statut = values(libelle_statut);

insert into Cours_Eau (id_cours_eau, cours_eau)
values (9999999, 'Inconnu');