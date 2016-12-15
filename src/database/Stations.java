/**
 * 
 */
package database;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * @author quinton
 * 
 */
public class Stations extends DbObject {

	public Stations() {
		init("Stations", "id_station", true);
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
		String sql = "SELECT cours_Eau FROM Stations JOIN Cours_Eau ON Stations.id_cours_eau = Cours_Eau.id_cours_eau "
				+ "where station = '"
				+ libelle
				+ "' or cd_station = '"
				+ libelle + "'";
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

	public List<Hashtable<String,String>> getListStationFromSearch(String search) {
			String sql = "select id_station, cd_station, station, s.id_cours_eau, cours_eau "
					+ "from Stations s "
					+ "join Cours_Eau c on (c.id_cours_eau = s.id_cours_eau) "
					+ "where upper(cd_station) like upper('%"
					+ search
					+ "%') "
					+ "or upper(station) like upper('%"
					+ search
					+ "%') "
					+ " order by cd_station";
		return executeList(sql);
	}
	
	public String[][] searchByName(Hashtable<String, String> param) {
		String search;
		try {
			search = param.get("zoneSearch");
		} catch (NullPointerException e) {
			search = "";
		}
		String sql = "select id_station, cd_station, station, cours_eau "
				+ "from Stations s "
				+ "join Cours_Eau c on (c.id_cours_eau = s.id_cours_eau) ";
		try {
			if (search.length() > 0) {

				sql += " where upper(cd_station) like upper('%"
				+ search
				+ "%') "
				+ "or upper(station) like upper('%"
				+ search
				+ "%') ";
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

}
