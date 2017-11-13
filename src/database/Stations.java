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

/**
 * @author quinton
 * 
 */
public class Stations extends DbObject {
	String message ;

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
		/*
		 * Recuperation de la liste des classes d'etat
		 */
		ClasseEtat ce = new ClasseEtat();
		List<Hashtable<String, String>> celist = ce.getListOrderBy("1");
		/*
		 * Recuperation de la liste des cours d'eau
		 */
		Cours_Eau riviere = new Cours_Eau();
		List<Hashtable<String, String>> rivierelist = riviere.getListOrderBy("1");
		int lrl = rivierelist.size();
				
		try {
			@SuppressWarnings("deprecation")
			CSVReader reader = new CSVReader(new FileReader(filename), separator);
			String [] ligne;
			while ((ligne = reader.readNext()) != null) {
				data.clear();
				data.put("cd_station", ligne[0]);
				data.put("station", ligne[1]);
				data.put("x", ligne[3]);
				data.put("y", ligne[4]);
				/*
				 * Recherche de la riviere
				 */
				if (ligne[5].length() > 0) {
					for (int i = 0; i < lrl; i++) {
						if (rivierelist.get(i).containsValue(ligne[5])) {
							data.put("id_cours_eau", rivierelist.get(i).));
						}
					}
				}
			}
			reader.close();
		
			
		
		} catch (FileNotFoundException  e) {
			message = "Fichier non trouvé";
		} catch (IOException e) {
			message = "Impossible de lire le fichier";
		}
		
		return result;
	}

}
