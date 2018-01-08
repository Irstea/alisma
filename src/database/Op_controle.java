/**
 * 
 */
package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;

import utils.Langue;

/**
 * @author quinton
 * 
 *         Classe permettant d'ecrire dans la table Op_controle
 * 
 */
public class Op_controle extends DbObject {
	static Logger logger = Logger.getLogger(Op_controle.class);
	public Hashtable<String, ParametreDb> params = new Hashtable<String, ParametreDb>();
	public String[] paramList = { "protocole", "rive", "hydrologie", "meteo", "turbidite" };
	public Hashtable<String, Integer> yesNoForKey;

	public Op_controle() {
		init("op_controle", "id_op_controle", true);
		setStringList(new String[] { "organisme", "operateur", "date_op", "observation", "ref_dossier", "uuid", "producteur_code", "producteur_name", "preleveur_code", "preleveur_name", "determinateur_code", "determinateur_name" });
		setNumericList(new String[] { "id_pt_prel", "id_statut", "protocole_id", "rive_id", "hydrologie_id", "meteo_id",
				"turbidite_id", "releve_dce", "typo_id" });
		/*
		 * Initialisation des tables de parametre
		 */
		for (String param : paramList) {
			params.put(param, new ParametreDb(param));
			params.get(param).readData();
		}
		/*
		 * Initialisation de la table permettant de retourner la valeur de oui
		 * ou non
		 */
		yesNoForKey = new Hashtable<String, Integer>();
		yesNoForKey.put(Langue.getString("oui"), 1);
		yesNoForKey.put(Langue.getString("non"), 0);
	}

	/**
	 * Lance la mise en table
	 * 
	 * @param lData
	 *            : donnees
	 * @param keyOp
	 *            : cle avant l'enregistrement
	 * @return keyOp : cle de l'enregistrement genere ou mis a jour
	 */
	public int ecrire(Hashtable<String, String> data, int keyOp) {
		/*
		 * Recherche des cles correspondant aux valeurs des comboBox
		 */
		for (String param : paramList) {
			try {
				data.put(param + "_id", params.get(param).getKeyFromValue(data.get(param)).toString());
			} catch (NullPointerException e) {
				data.put(param + "_id", "");
			}
		}
		/*
		 * Recuperation de la valeur de releve_dce
		 */
		if (data.containsKey("releve_dce")) {
			data.put("releve_dce", yesNoForKey.get(data.get("releve_dce")).toString());
			logger.debug("releve_dce value : " + data.get("releve_dce"));
		}
		/*
		 * Traitement de l'uuid
		 */
		if (data.get("uuid").isEmpty()) {
			data.put("uuid", UUID.randomUUID().toString());
		}
		keyOp = write(data, keyOp);
		return keyOp;
	}

	/**
	 * Ecriture directe dans l'enregistrement,
	 * sans tenir compte des tables liees
	 * @param data
	 * @param keyOp
	 * @return
	 */
	public int ecrireSimple(Hashtable<String, String> data, int keyOp) {
		return write(data, keyOp);
	}

	
	/**
	 * Lecture d'une operation, avec ajout des donnees des combo
	 * 
	 * @param keyOp
	 * @return
	 */
	public Hashtable<String, String> lire(int keyOp) {
		Hashtable<String, String> data = read(keyOp);
		/*
		 * Mise a jour des informations liees aux combobox
		 */
		for (String param : paramList) {
			try {
				int key = Integer.parseInt(data.get(param + "_id"));

				data.put(param, params.get(param).getValueFromKey(key));
				logger.debug("lire - " + param + " - Clé lue : " + String.valueOf(key) + " valeur transmise : "
						+ String.valueOf(key));
			} catch (Exception e) {
				logger.debug("lire : exception levée pour " + param + ", qui est transmis en chaîne vide");
				data.put(param, "");
			}
		}
		return data;
	}

	/**
	 * Supprime une operation
	 * 
	 * @param keyOp
	 * @param keyPointPrelevement
	 */
	public void deleteOperation(int keyOp, int keyPointPrelevement) {
		if (keyOp > -1) {
			String[] queries = { "DELETE FROM unite_releves WHERE id_op_controle = " + keyOp,
					"DELETE FROM lignes_op_controle WHERE id_op_controle = " + keyOp,
					"DELETE from ibmr where id_op_controle = " + keyOp,
					"DELETE FROM op_controle WHERE id_op_controle = " + keyOp };
			try {
				query = connection.createStatement();
				for (String sql : queries) {
					logger.debug(sql);
					query.execute(sql);
				}
				if (keyPointPrelevement > -1) {
					String sql = "delete from points_prelev  where id_pt_prel = " + keyPointPrelevement;
					logger.debug(sql);
					query.execute(sql);
				}
			} catch (SQLException e) {
				logger.error(e);
			}
		}
	}

	/**
	 * Retourne la liste des releves
	 * 
	 * @return String[][]
	 */
	public String[][] getListeReleve(Hashtable<String, String> param) {
		String[][] returned = null;
		ArrayList<Object> data = new ArrayList<Object>();
		String sql = "SELECT id_op_controle,cd_station, station,cours_eau,operateur,date_op, op.id_statut, "
				+ "cd_station, releve_dce "
				+ "FROM op_controle op "
				+ "JOIN points_prelev ON op.id_pt_prel = points_prelev.id_pt_prel "
				+ "JOIN stations ON points_prelev.id_station = stations.id_station "
				+ "JOIN cours_eau ON stations.id_cours_eau = cours_eau.id_cours_eau "
				;

		String where = getWhere(param);
		logger.debug(sql + where);
		try {
			query = connection.createStatement();
			ResultSet rs = query.executeQuery(sql + where);
			while (rs.next()) {
				data.add(rs.getString("id_op_controle"));
				try {
					data.add(rs.getString("cd_station"));
				} catch (NullPointerException e) {
					data.add("");
				}
				data.add(rs.getString("station"));
				data.add(rs.getString("cours_eau"));
				data.add(rs.getString("operateur"));
				data.add(rs.getString("date_op"));

				switch (rs.getString("id_statut")) {
				case "0":
					data.add(Langue.getString("statut0"));
					break;
				case "1":
					data.add(Langue.getString("statut1"));
					break;
				case "2":
					data.add(Langue.getString("statut2"));
					break;
				default:
					data.add(rs.getString("id_statut"));
				}
				switch (rs.getString("releve_dce")) {
				case "1":
					data.add(Langue.getString("oui"));
					break;
				default:
					data.add(Langue.getString("non"));
				}
			}

			returned = new String[data.size() / 8][8];

			for (int i = 0; i < data.size(); i++)
				returned[i / 8][i % 8] = (String) data.get(i);
		} catch (SQLException e) {
			logger.error(e);
		}
		return returned;
	}

	/**
	 * Retourne la liste avec toutes les colonnes des releves pour les
	 * parametres consideres
	 * 
	 * @param param
	 * @return
	 */
	public List<Hashtable<String, String>> getListeReleveComplet(Hashtable<String, String> param) {
		String sql = " select op.id_op_controle, organisme, operateur, op.id_statut,"
				+ " protocole_libelle, rive_libelle, hydrologie_libelle,"
				+ " meteo_libelle, turbidite_libelle, date_op, observation, " 
				+ " ref_dossier, "
				+ " coord_x, coord_y, wgs84_x, wgs84_y, altitude, longueur, largeur,"
				+ " cd_station, station, x, y, cours_eau," 
				+ " libelle_statut, releve_dce, " 
				+ " ibmr.*,"
				+ " op.typo_id, typo_name, ibmr_ref, groupe,"
				+ " ibmr.classe_etat_id, c1.classe_etat_libelle, eqr_value, robustesse_eqr_value,"
				+ " ibmr.robustesse_classe_etat_id, c2.classe_etat_libelle as robustesse_classe_etat_libelle"
				+ " from op_controle op" 
				+ " join points_prelev prelev on (prelev.id_pt_prel = op.id_pt_prel)"
				+ " join stations station on (station.id_station = prelev.id_station)"
				+ " join cours_eau cours on (cours.id_cours_eau = station.id_cours_eau)"
				+ " left outer join ibmr on (op.id_op_controle = ibmr.id_op_controle)"
				+ " left outer join statut on (op.id_statut = Statut.id_statut)"
				+ " left outer join protocole on ( protocole.protocole_id = op.protocole_id)"
				+ " left outer join rive on (rive.rive_id = op.rive_id)"
				+ " left outer join hydrologie on (hydrologie.hydrologie_id = op.hydrologie_id)"
				+ " left outer join meteo on (meteo.meteo_id = op.meteo_id)"
				+ " left outer join turbidite on (turbidite.turbidite_id = op.turbidite_id)"
				+ " left outer join typo on (op.typo_id = typo.typo_id)"
				+ " left outer join classe_etat c1 on (ibmr.classe_etat_id = c1.classe_etat_id)"
				+ " left outer join classe_etat c2 on (ibmr.robustesse_classe_etat_id = c2.classe_etat_id)"
				+ "";
		String order = " order by date_op";
		return executeList(sql + getWhere(param) + order, false);
	}

	/**
	 * Genere la commande where a partir du tableau de parametres
	 * 
	 * @param param
	 * @return String
	 */
	String getWhere(Hashtable<String, String> param) {
		String where = "";
		String and = "";
		try {
			if (! param.get("annee").equals("")) {
				where += and + " extract(year from date_op) = "+param.get("annee");
				and = " and ";
			} else
			if (!param.get("debut").isEmpty() && !param.get("fin").isEmpty()) {
				where += and + " date_op >= '" + param.get("debut") + "' " + "and date_op <= '" + param.get("fin")
						+ "' ";
				and = " and ";
			}
		} catch (NullPointerException e) {
		}
		;
		try {
			if (!param.get("zoneSearch").isEmpty()) {
				where += and + " (upper(station) like '%" + param.get("zoneSearch").toUpperCase() + "%' "
						+ " or cd_station like '%" + param.get("zoneSearch").toUpperCase() + "%')";
				and = " and ";
			}
		} catch (NullPointerException e) {
		}
		;
		try {
			if (!param.get("statut").isEmpty()) {
				where += and + " op.id_statut = " + param.get("statut");
				and = " and ";
			}
		} catch (NullPointerException e) {
		}
		;
		try {
			if (!param.get("id_op_controle").isEmpty()) {
				where += and + " op.id_op_controle = " + param.get("id_op_controle");
				and = " and ";
			}
		} catch (NullPointerException e) {
		}
		;
		try {
			if (!param.get("releve_dce").isEmpty()) {
				where += and + " op.releve_dce = " + param.get("releve_dce");
				and = " and ";
			}
		} catch (NullPointerException e) {
		}
		;

		if (!where.isEmpty())
			where = " where " + where;
		return where;
	}

}
