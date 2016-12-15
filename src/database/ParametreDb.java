package database;

import org.apache.log4j.Logger;

/**
 * Classe generique pour gerer l'ensemble des tables de parametres
 * @author quinton
 *
 */
public class ParametreDb extends DbObjectCombo {
	static Logger logger = Logger.getLogger(DbObject.class);
	
	public ParametreDb (String table) {
		init(table, table + "_id", false);
		setStringList(new String[] { table + "_libelle" });
		libelleName = table + "_libelle";
	}
}
