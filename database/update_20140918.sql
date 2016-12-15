CREATE TABLE releve_ibmr.Statut (
                id_statut INT NOT NULL,
                libelle_statut VARCHAR(50) NOT NULL,
                CONSTRAINT PK_Statut PRIMARY KEY (id_statut)
)
ENGINE=InnoDB DEFAULT CHARSET=latin1
;

ALTER TABLE releve_ibmr.Statut COMMENT 'Table des statuts du dossier';

ALTER TABLE releve_ibmr.Statut MODIFY COLUMN id_statut INTEGER COMMENT '0 : en saisie
1 : validé';

ALTER TABLE releve_ibmr.Op_controle ADD COLUMN id_statut INT AFTER id_pt_prel;

ALTER TABLE releve_ibmr.Op_controle ADD CONSTRAINT statut_op_controle_fk
FOREIGN KEY (id_statut)
REFERENCES releve_ibmr.Statut (id_statut)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

insert into Statut (id_statut, libelle_statut) 
values (0, "En saisie"), (1, "Validé");

ALTER TABLE releve_ibmr.Unite_releves ADD COLUMN numUR INT DEFAULT 1 NOT NULL AFTER id_op_controle;
ALTER TABLE releve_ibmr.Unite_releves MODIFY COLUMN typeUR VARCHAR(255) ;

