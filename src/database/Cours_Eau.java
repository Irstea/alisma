package database;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Gestion des requetes vers la table Cours_Eau
 * @author quinton
 *
 */

import java.util.Hashtable;
import java.util.List;

import org.apache.log4j.Logger;

import com.opencsv.CSVReader;

import utils.Langue;

public class Cours_Eau extends DbObject {
	static Logger logger = Logger.getLogger(Cours_Eau.class);
	public String[] paramList = { "coursEau" };

	public Cours_Eau() {
		init("cours_eau", "id_cours_eau", true);
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
	 * 
	 * @param param
	 * @return List<Hashtable<String,String>>
	 */
	public List<Hashtable<String, String>> getListByParam(Hashtable<String, String> param) {
		String sql = "select * from cours_eau ";
		try {
			if (param.containsKey("cours_eau")) {
				sql += " where upper(cours_eau) like upper('%" + param.get("cours_eau") + "%')";
			} else {
				if (param.containsKey("id_cours_eau")) {
					sql += " where id_cours_eau = " + param.get("id_cours_eau");
				}
			}
		} catch (NullPointerException e) {
		}

		sql += " order by cours_eau";
		List<Hashtable<String, String>> result = executeList(sql);
		return result;
	}

	public boolean importFromCsv(String filename, char separator) {
		boolean result = false;
		data = new Hashtable<String, String>();
		try {
			@SuppressWarnings({ "deprecation", "resource" })
			CSVReader reader = new CSVReader(new FileReader(filename), separator);
			String[] ligne;
			result = true;
			/*
			 * Suppression de la premiere ligne
			 */
			reader.readNext();
			while ((ligne = reader.readNext()) != null) {
				data.clear();
				if (ligne.length == 2) {

					if (!ligne[1].isEmpty()) {
						/*
						 * Recherche si l'enregistrement existe deja
						 */
						Integer id = 0;
						try {
							id = Integer
									.valueOf(readByKey("cours_eau", ligne[1].replace("'", "''")).get("id_cours_eau"));

						} catch (Exception e) {
						}

						if (id == 0) {
							data.put("cours_eau", ligne[1]);
							write(data, 0);
						}
					}
				}
			}
			reader.close();
		} catch (FileNotFoundException e) {
			message = Langue.getString("filenotfound");
			result = false;
		} catch (IOException e) {
			message = Langue.getString("filenotreadable");
			result = false;
		}
		return result;
	}

}
