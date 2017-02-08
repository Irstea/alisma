package database;

public class ClasseEtat extends DbObject {
	public ClasseEtat() {
	init("classe_etat", "classe_etat_id", false);
	setStringList(new String[] {"classe_etat_libelle"});
	setNumericList(new String[] {"classe_etat_id", "classe_etat_seuil"});
	}

}
