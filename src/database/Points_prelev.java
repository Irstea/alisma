/**
 * 
 */
package database;

import java.util.Hashtable;

/**
 * @author quinton
 *
 * Classe permettant de mettre a jour la table Points_prelev
 */
public class Points_prelev extends DbObject {
	
	public Points_prelev () {
		init("points_prelev", "id_pt_prel", true);
		setStringList(new String[] {});
		setNumericList(new String [] {"coord_x", "coord_y", 
				"altitude", "longueur", "largeur", "id_station",
				"wgs84_x", "wgs84_y"});
	}

	/**
	 * Lancement de l'ecriture en base
	 * @param Hashtable<String, String> lData
	 * @param int cle
	 * @return int : cle
	 */
	public int ecrire (Hashtable<String, String> lData, int cle) {
		/*
		 * Recherche de id_station
		 */
		if (lData.get("cd_station") != null) {
			String sql = "select id_station from stations where cd_station = " + lData.get("cd_station");
			try {
				query = connection.createStatement();
				rs =query.executeQuery(sql);
				if (rs.next()) {
					lData.put("id_station", rs.getString(1));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		/*
		 * Lancement de l'ecriture
		 */
		setData(lData);
		if (cle > 0 ) {
			update(cle) ;
		} else 
			cle = insert();
		return cle;
	}

}
