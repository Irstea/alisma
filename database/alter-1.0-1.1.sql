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
 
ALTER TABLE alisma.Op_controle ADD COLUMN typo_id INT AFTER turbidite_id;

CREATE TABLE alisma.typo (
                typo_id INT AUTO_INCREMENT NOT NULL,
                typo_name VARCHAR(255) NOT NULL,
                ibmr_ref FLOAT NOT NULL,
                PRIMARY KEY (typo_id)
);

ALTER TABLE alisma.typo COMMENT 'Typologie des stations';

ALTER TABLE alisma.typo MODIFY COLUMN typo_name VARCHAR(255) COMMENT 'Code typologique du cours d''eau';

ALTER TABLE alisma.typo MODIFY COLUMN ibmr_ref FLOAT COMMENT 'Valeur de référence de l''IBMR pour le type de cours d''eau considéré';
ALTER TABLE alisma.Op_controle ADD CONSTRAINT typo_op_controle_fk
FOREIGN KEY (typo_id)
REFERENCES alisma.typo (typo_id)
ON DELETE NO ACTION
ON UPDATE NO ACTION;



CREATE TABLE alisma.classe_etat (
                classe_etat_id INT NOT NULL,
                classe_etat_libelle VARCHAR(255) NOT NULL,
                PRIMARY KEY (classe_etat_id)
);

ALTER TABLE alisma.classe_etat COMMENT 'Table des classes d''état';

ALTER TABLE alisma.ibmr ADD COLUMN classe_etat_id INT AFTER robustesse_niveau_trophique_id;

ALTER TABLE alisma.ibmr ADD CONSTRAINT classe_etat_ibmr_fk
FOREIGN KEY (classe_etat_id)
REFERENCES alisma.classe_etat (classe_etat_id)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE alisma.Op_controle ADD CONSTRAINT typo_op_controle_fk
FOREIGN KEY (typo_id)
REFERENCES alisma.typo (typo_id)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

insert into alisma.classe_etat (classe_etat_id, classe_etat_libelle)
values 
(1, 'Très bon'),
(2, 'Bon'),
(3, 'Moyen'),
(4, 'Médiocre'),
(5, 'Mauvais');
