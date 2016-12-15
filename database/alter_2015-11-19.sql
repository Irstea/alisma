

CREATE TABLE alisma.facies (
                facies_id INT NOT NULL,
                facies_libelle VARCHAR(255) NOT NULL,
                PRIMARY KEY (facies_id)
);

ALTER TABLE alisma.facies COMMENT 'Type de facies dominant';


CREATE TABLE alisma.facies_autre_type (
                facies_autre_type_id INT NOT NULL,
                facies_autre_type_libelle VARCHAR(255) NOT NULL,
                PRIMARY KEY (facies_autre_type_id)
);

ALTER TABLE alisma.facies_autre_type COMMENT 'Table des autres types de facies';


CREATE TABLE alisma.hydrologie (
                hydrologie_id INT NOT NULL,
                hydrologie_libelle VARCHAR(255) NOT NULL,
                PRIMARY KEY (hydrologie_id)
);

ALTER TABLE alisma.hydrologie COMMENT 'Table des facies hydrologiques';

ALTER TABLE alisma.ibmr MODIFY COLUMN taxon_robustesse VARCHAR(255);

CREATE TABLE alisma.meteo (
                meteo_id INT NOT NULL,
                meteo_libelle VARCHAR(255) NOT NULL,
                PRIMARY KEY (meteo_id)
);

ALTER TABLE alisma.meteo COMMENT 'Table des conditions meteo';


ALTER TABLE alisma.niveau_trophique MODIFY COLUMN niveau_trophique_libelle VARCHAR(255) NOT NULL;

ALTER TABLE alisma.Op_controle DROP COLUMN coord_rive;

ALTER TABLE alisma.Op_controle DROP COLUMN hydrologie;

ALTER TABLE alisma.Op_controle ADD COLUMN protocole_id INT AFTER id_statut;

ALTER TABLE alisma.Op_controle ADD COLUMN rive_id INT AFTER protocole_id;

ALTER TABLE alisma.Op_controle ADD COLUMN hydrologie_id INT AFTER rive_id;

ALTER TABLE alisma.Op_controle DROP COLUMN meteo;

ALTER TABLE alisma.Op_controle ADD COLUMN meteo_id INT AFTER hydrologie_id;

ALTER TABLE alisma.Op_controle DROP COLUMN protocole;





ALTER TABLE alisma.Op_controle DROP COLUMN turbidite;

ALTER TABLE alisma.Op_controle ADD COLUMN turbidite_id INT AFTER meteo_id;

CREATE TABLE alisma.periphyton (
                periphyton_id INT NOT NULL,
                periphyton_libelle VARCHAR(255) NOT NULL,
                PRIMARY KEY (periphyton_id)
);

ALTER TABLE alisma.periphyton COMMENT 'Table des abondances du périphyton';


CREATE TABLE alisma.protocole (
                protocole_id INT AUTO_INCREMENT NOT NULL,
                protocole_libelle VARCHAR(255) NOT NULL,
                PRIMARY KEY (protocole_id)
);

ALTER TABLE alisma.protocole COMMENT 'Table des protocoles d''échantillonnage';


CREATE TABLE alisma.rive (
                rive_id INT NOT NULL,
                rive_libelle VARCHAR(255) NOT NULL,
                PRIMARY KEY (rive_id)
);

ALTER TABLE alisma.rive COMMENT 'Table des rives';

ALTER TABLE alisma.rive MODIFY COLUMN rive_id INTEGER COMMENT '1 : gauche
2 : droite';



CREATE TABLE alisma.turbidite (
                turbidite_id INT NOT NULL,
                turbidite_libelle VARCHAR(255) NOT NULL,
                PRIMARY KEY (turbidite_id)
);

ALTER TABLE alisma.turbidite COMMENT 'Table des conditions de turbidite';


CREATE TABLE alisma.type_ur (
                type_ur_id INT NOT NULL,
                type_ur_libelle VARCHAR(255) NOT NULL,
                PRIMARY KEY (type_ur_id)
);

ALTER TABLE alisma.type_ur COMMENT 'Type de l''unité de relevé';


ALTER TABLE alisma.Unite_releves DROP COLUMN autreType;

ALTER TABLE alisma.Unite_releves ADD COLUMN type_ur_id INT NOT NULL AFTER id_op_controle;

ALTER TABLE alisma.Unite_releves ADD COLUMN periphyton_id INT AFTER type_ur_id;

ALTER TABLE alisma.Unite_releves ADD COLUMN facies_id INT AFTER periphyton_id;


ALTER TABLE alisma.Unite_releves ADD COLUMN facies_autre_type_id INT AFTER facies_id;

ALTER TABLE alisma.Unite_releves DROP COLUMN facies_dominant;


ALTER TABLE alisma.Unite_releves DROP COLUMN periphyton;



ALTER TABLE alisma.Unite_releves DROP COLUMN typeUR;

ALTER TABLE alisma.Unite_releves ADD CONSTRAINT facies_unite_releves_fk
FOREIGN KEY (facies_id)
REFERENCES alisma.facies (facies_id)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE alisma.Unite_releves ADD CONSTRAINT facies_autre_type_unite_releves_fk
FOREIGN KEY (facies_autre_type_id)
REFERENCES alisma.facies_autre_type (facies_autre_type_id)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE alisma.Op_controle ADD CONSTRAINT hydrologie_op_controle_fk
FOREIGN KEY (hydrologie_id)
REFERENCES alisma.hydrologie (hydrologie_id)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE alisma.Op_controle ADD CONSTRAINT meteo_op_controle_fk
FOREIGN KEY (meteo_id)
REFERENCES alisma.meteo (meteo_id)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE alisma.Unite_releves ADD CONSTRAINT periphyton_unite_releves_fk
FOREIGN KEY (periphyton_id)
REFERENCES alisma.periphyton (periphyton_id)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE alisma.Op_controle ADD CONSTRAINT protocole_op_controle_fk
FOREIGN KEY (protocole_id)
REFERENCES alisma.protocole (protocole_id)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE alisma.Op_controle ADD CONSTRAINT rive_op_controle_fk
FOREIGN KEY (rive_id)
REFERENCES alisma.rive (rive_id)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE alisma.Op_controle ADD CONSTRAINT turbidite_op_controle_fk
FOREIGN KEY (turbidite_id)
REFERENCES alisma.turbidite (turbidite_id)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE alisma.Unite_releves ADD CONSTRAINT type_ur_unite_releves_fk
FOREIGN KEY (type_ur_id)
REFERENCES alisma.type_ur (type_ur_id)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

alter table alisma.Op_controle add column releve_dce smallint not null default 1;
