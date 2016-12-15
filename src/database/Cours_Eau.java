package database;

/**
 * Gestion des requetes vers la table Cours_Eau
 * @author quinton
 *
 */

import java.util.Hashtable;
import java.util.List;

import org.apache.log4j.Logger;

public class Cours_Eau extends DbObject {
	static Logger logger = Logger.getLogger(Cours_Eau.class);
	public String[] paramList = { "coursEau" };

	public Cours_Eau() {
		init("Cours_Eau", "id_cours_eau", true);
		setStringList(new String[] { "cours_eau" });
		setNumericList(new String[] {});

	}

	/**
	 * Retourne la liste des cours d'eau en fonction du nom fourni
	 * 
	 * @param value
	 * @return List<Hashtable<String, String>>
	 */
	public String[][] searchByName(Hashtable<String, String> param) {
		List<Hashtable<String, String>> result = getListByParam(param);
		String[][] returned = new String[result.size()][2];
		for (int i = 0; i < result.size(); i++) {
			returned[i][0] = result.get(i).get("id_cours_eau");
			returned[i][1] = result.get(i).get("cours_eau");
		}
		return returned;

	}

	/**
	 * Retourne la liste correspondante aux criteres de recherche
	 * @param param
	 * @return List<Hashtable<String,String>>
	 */
	public List<Hashtable<String,String>> getListByParam(Hashtable<String,String>param) {
		String sql = "select * from Cours_Eau ";
		try {
			if (param.containsKey("cours_eau")) {		
			sql += " where upper(cours_eau) like upper('%" + param.get("cours_eau") + "%')";
			} else {
				if (param.containsKey("id_cours_eau")) {
					sql += " where id_cours_eau = "+param.get("id_cours_eau");
				}
			}
		} catch (NullPointerException e) {
		}
		
		sql += " order by cours_eau";
		List<Hashtable<String, String>> result = executeList(sql);
		return result;
	}

}
