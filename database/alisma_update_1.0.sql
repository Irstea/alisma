

ALTER TABLE alisma.Taxons_MP DROP FOREIGN KEY fk_taxons_mp_auteurs;

ALTER TABLE alisma.Synonymes DROP FOREIGN KEY fk_synonymes_cd_contrib;

ALTER TABLE alisma.Synonymes DROP FOREIGN KEY fk_synonymes_cd_taxon;

ALTER TABLE alisma.Synonymes DROP FOREIGN KEY fk_synonymes_cd_valide;

DROP TABLE alisma.Synonymes;

ALTER TABLE alisma.Taxons_MP ADD COLUMN auteur VARCHAR(50) AFTER id_groupe;

ALTER TABLE alisma.Taxons_MP ADD COLUMN cd_valide VARCHAR(6) AFTER auteur;

ALTER TABLE alisma.Taxons_MP DROP COLUMN id_auteur;

ALTER TABLE alisma.Unite_releves MODIFY COLUMN numUR INT DEFAULT 1 NOT NULL;

ALTER TABLE alisma.Stations MODIFY COLUMN cd_station VARCHAR(10) NOT NULL;

ALTER TABLE alisma.Cours_Eau MODIFY COLUMN cours_eau VARCHAR(255) NOT NULL;

ALTER TABLE alisma.Stations MODIFY COLUMN station VARCHAR(255) NOT NULL;

ALTER TABLE alisma.Taxons_MP ADD COLUMN cd_contrib VARCHAR(6) AFTER cd_valide;