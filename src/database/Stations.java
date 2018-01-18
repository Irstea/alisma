/**
 * 
 */
package database;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.opencsv.CSVReader;

import utils.Langue;

/**
 * @author quinton
 * 
 */
public class Stations extends DbObject {

	public Stations() {
		init("stations", "id_station", true);
		setNumericList(new String[] { "x", "y", "id_cours_eau", "id_station" });
		setStringList(new String[] { "cd_station", "station" });
	}

	/**
	 * Retourne la liste des stations triees
	 * 
	 * @return
	 */
	public ArrayList<Hashtable<String, String>> getStations() {
		return (ArrayList<Hashtable<String, String>>) getListOrderBy("station");
	}

	/**
	 * Retourne le nom de la riviere correspondant a une station
	 * 
	 * @param nomStation
	 * @return String
	 */
	public String getNomRiv(String libelle) {
		String result = "";
		String sql = "SELECT cours_eau FROM stations JOIN cours_eau ON stations.id_cours_eau = cours_eau.id_cours_eau "
				+ "where station = '" + libelle + "' or cd_station = '" + libelle + "'";
		try {
			rs = query.executeQuery(sql);
			if (rs.next())
				result = rs.getString("cours_eau");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * Retourne la liste des stations triees par cd_station
	 * 
	 * @return
	 */
	public ArrayList<Hashtable<String, String>> getCdStations() {
		return (ArrayList<Hashtable<String, String>>) getListOrderBy("cd_station");
	}

	/**
	 * retourne le cd_station correspondant au nom d'une station
	 * 
	 * @param name
	 * @return String
	 */
	public String getCodeStation(String name) {
		return readByKey("station", name).get("cd_station");
	}

	public String getCdStationFromId(String id) {
		return readByKey("id_station", id).get("cd_station");
	}

	/**
	 * Retourne le nom d'une station correspondant au cd
	 * 
	 * @param cd
	 * @return
	 */
	public String getNameStation(String cd) {
		return readByKey("cd_station", cd).get("station");
	}

	public List<Hashtable<String, String>> getListStationFromSearch(String search) {
		String sql = "select id_station, cd_station, station, s.id_cours_eau, cours_eau " + "from stations s "
				+ "join cours_eau c on (c.id_cours_eau = s.id_cours_eau) ";
		if (search.matches("[0-9]+")) {
			sql += " where cd_station like '" + search + "%'";
		} else {
			sql += " where upper(station) like upper('%" + search + "%') ";
		}
		sql += " order by cd_station";
		return executeList(sql);
	}

	public String[][] searchByName(Hashtable<String, String> param) {
		String search;
		try {
			search = param.get("zoneSearch");
		} catch (NullPointerException e) {
			search = "";
		}
		String sql = "select id_station, cd_station, station, cours_eau " + "from stations s "
				+ "join cours_eau c on (c.id_cours_eau = s.id_cours_eau) ";
		try {

			if (search.length() > 0) {
				logger.debug("search : " + search);
				if (search.matches("[0-9]+")) {
					logger.debug("digit trouve");
					sql += " where cd_station like '" + search + "%'";
				} else {
					sql += " where upper(station) like upper('%" + search + "%') ";
				}
			}
		} catch (NullPointerException e) {
		}

		sql += " order by cd_station";
		List<Hashtable<String, String>> result = executeList(sql);
		String[][] returned = new String[result.size()][5];
		for (int i = 0; i < result.size(); i++) {
			returned[i][0] = result.get(i).get("id_station");
			returned[i][1] = result.get(i).get("cd_station");
			returned[i][2] = result.get(i).get("station");
			returned[i][3] = result.get(i).get("cours_eau");
		}
		return returned;

	}

	public boolean importFromCsv(String filename, char separator) {

		boolean result = false;
		data = new Hashtable <String, String>();
		/*
		 * Recuperation de la liste des classes d'etat
		 */
		ClasseEtat ce = new ClasseEtat();
		List<Hashtable<String, String>> celist = ce.getListOrderBy("classe_etat_libelle");
		/*
		 * Recuperation de la liste des cours d'eau
		 */
		Cours_Eau riviere = new Cours_Eau();
		List<Hashtable<String, String>> rivierelist = riviere.getListOrderBy("cours_eau");
		int lrl = rivierelist.size();

		try {
			@SuppressWarnings("deprecation")
			CSVReader reader = new CSVReader(new FileReader(filename), separator);
			String[] ligne;
			result = true;
			/*
			 * Suppression de la premiere ligne
			 */
			reader.readNext();
			while ((ligne = reader.readNext()) != null) {
				data.clear();
				if (ligne.length > 4) {
					data.put("cd_station", ligne[0]);
					data.put("station", ligne[1]);
					data.put("x", ligne[2]);
					data.put("y", ligne[3]);
					/*
					 * Recherche de la riviere
					 */
					if (ligne[4].length() > 0) {
						logger.debug(ligne[4]);
						for (int i = 0; i < lrl; i++) {
							if (rivierelist.get(i).containsValue(ligne[4])) {
								logger.debug("id_cours_eau:"+rivierelist.get(i).get("id_cours_eau")+" "+rivierelist.get(i).get("cours_eau"));
								data.put("id_cours_eau", rivierelist.get(i).get("id_cours_eau"));
								break;
							}
						}
					}
					/*
					 * Recherche si la station existe deja
					 */
					Integer id = 0;
					try {
						id = Integer.valueOf(readByKey("cd_station", ligne[0].replace("'", "''")).get("id_station"));
						if (id > 0) {
							data.put("id_station", String.valueOf(id));
						} else {
							id = 0;
						}
					} catch (Exception e) {
					}
					/*
					 * Ecriture dans la base de donnees
					 */
					write(data, id);
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
