

CREATE TABLE alisma.ibmr (
                id_op_controle INT NOT NULL,
                ibmr_value DOUBLE ,
                robustesse_value DOUBLE ,
                niveau_trophique_id INT ,
                robustesse_niveau_trophique_id INT,
                taxon_robustesse VARCHAR(50),
                cs_moy DOUBLE ,
                cs_min DOUBLE ,
                cs_max DOUBLE ,
                coef_moy DOUBLE ,
                coef_min DOUBLE ,
                coef_max DOUBLE ,
                nbtaxon_het INT,
                nbtaxon_alg INT,
                nbtaxon_bry INT,
                nbtaxon_pte INT,
                nbtaxon_pha INT,
                nbtaxon_lic INT,
                nbtaxon_total INT,
                nbtaxon_contrib INT,
                nbtaxon_steno1 INT,
                nbtaxon_steno2 INT,
                nbtaxon_steno3 INT,
                PRIMARY KEY (id_op_controle)
);

ALTER TABLE alisma.ibmr COMMENT 'Table des informations specifiques du calcul de l''IBMR';


CREATE TABLE alisma.niveau_trophique (
                niveau_trophique_id INT NOT NULL,
                niveau_trophique_libelle VARCHAR(50) NOT NULL,
                PRIMARY KEY (niveau_trophique_id)
);

ALTER TABLE alisma.niveau_trophique COMMENT 'Table des libelles des niveaux trophiques
';



ALTER TABLE alisma.ibmr ADD CONSTRAINT niveau_trophique_ibmr_fk
FOREIGN KEY (niveau_trophique_id)
REFERENCES alisma.niveau_trophique (niveau_trophique_id)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE alisma.ibmr ADD CONSTRAINT niveau_trophique_ibmr_fk1
FOREIGN KEY (robustesse_niveau_trophique_id)
REFERENCES alisma.niveau_trophique (niveau_trophique_id)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE alisma.ibmr ADD CONSTRAINT op_controle_ibmr_fk
FOREIGN KEY (id_op_controle)
REFERENCES alisma.Op_controle (id_op_controle)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

insert into alisma.niveau_trophique(niveau_trophique_id, niveau_trophique_libelle) values 
(1, 'très faible'),(2, 'faible'), (3, 'moyen'), (4,'fort'), (5, 'très élevé');
