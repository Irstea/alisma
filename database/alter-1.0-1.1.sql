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

insert into `Statut` (id_statut, libelle_statut) values (2, 'Calculé SEEE');
