/**
 * 
 */
package database;

import java.util.Hashtable;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * @author quinton
 * 
 *         Classe permettant de mettre a jour la table Unites_releve
 *
 */
public class Unite_releves extends DbObject {
	public Hashtable<String, ParametreDb> params = new Hashtable<String, ParametreDb>();
	public String[] paramList = { "type_ur", "periphyton", "facies", "facies_autre_type", "protocole" };
	static Logger logger = Logger.getLogger(Unite_releves.class);

	public Unite_releves() {
		init("Unite_releves", "id_UR", true);
		setStringList(new String[] {});
		setNumericList(new String[] { "pc_UR", "longueur_UR", "largeur_UR", "pc_vegetalisation", "ch_lentique",
				"pl_lentique", "mouille", "fosse_dissipation", "ch_lotique", "radier", "cascade", "pl_courant",
				"rapide", "p1", "p2", "p3", "p4", "p5", "v1", "v2", "v3", "v4", "v5", "tres_ombrage", "ombrage",
				"peu_ombrage", "eclaire", "tres_eclaire", "vase_limons", "terre_marne_tourbe", "cailloux_pierres",
				"blocs_dalles", "sable_graviers", "racines", "debris_org", "artificiel", "pc_heterot", "pc_algues",
				"pc_bryo", "pc_lichen", "pc_phanero", "pc_flottante", "pc_immerg", "pc_helophyte", "autreTypeClass",
				"id_op_controle", "numUR", "type_ur_id", "periphyton_id", "facies_id", "facies_autre_type_id" });
		/*
		 * Initialisation des tables de parametre
		 */
		for (String param : paramList) {
			params.put(param, new ParametreDb(param));
			params.get(param).readData();
		}

	}

	/**
	 * Ecriture des donnees en base
	 * 
	 * @param Hashtable<String,
	 *            String> lData
	 * @param int
	 *            cle
	 * @return int : cle generee
	 */
	public int ecrire(Hashtable<String, String> lData, int cle) {
		/*
		 * Recherche des cles correspondant aux valeurs des comboBox
		 */
		for (String param : paramList) {
			try {
				lData.put(param + "_id", params.get(param).getKeyFromValue(lData.get(param)).toString());
			} catch (Exception e) {
				lData.put(param + "_id", "");
			}
		}
		cle = write(lData, cle);
		return cle;
	}

	/**
	 * Lecture d'un releve, avec ajout des donnees des combo
	 * 
	 * @param keyOp
	 * @return
	 */
	public Hashtable<String, String> lire(int cle) {
		logger.debug("Unite_releves : lire(" + String.valueOf(cle));
		Hashtable<String, String> data = read(cle);
		/*
		 * Mise a jour des informations liees aux combobox
		 */
		data = setValueCombo(data);
		return data;
	}

	/**
	 * Ajoute les donnees pour les combo aux informations de l'enregistrement
	 * 
	 * @param data
	 * @return
	 */
	public Hashtable<String, String> setValueCombo(Hashtable<String, String> data) {
		/*
		 * Mise a jour des informations liees aux combobox
		 */
		for (String param : paramList) {
			try {
				int key = Integer.parseInt(data.get(param + "_id"));

				logger.debug(param + "_id :" + String.valueOf(key));
				logger.debug(param + "_id value : " + params.get(param).getValueFromKey(key));
				data.put(param, params.get(param).getValueFromKey(key));
				// data.put(param, String.valueOf(key));
			} catch (Exception e) {
				data.put(param, "");
			}
		}
		return data;
	}

	/**
	 * Retourne le nombre d'unites de releve pour une operation de controle
	 * 
	 * @param int
	 *            keyOp
	 * @return
	 */
	public int getNbReleve(int keyOp) {
		int nbReleve = 2;
		String sql = "select count(*) from Unite_releves where id_op_controle = " + keyOp;
		try {
			query = connection.createStatement();
			rs = query.executeQuery(sql);
			if (rs.next()) {
				nbReleve = rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return nbReleve;
	}

	/**
	 * Retourne la liste des ur correspondant a une operation, avec les tables
	 * de parametre associees
	 * 
	 * @param key
	 * @return
	 */
	public List<Hashtable<String, String>> getListeFromOp(int key) {
		String sql = "select * from " + tableName + " ur"
				+ " left outer join type_ur on (ur.type_ur_id = type_ur.type_ur_id)"
				+ " left outer join periphyton on (ur.periphyton_id = periphyton.periphyton_id)"
				+ " left outer join facies on (ur.facies_id = facies.facies_id)"
				+ " left outer join facies_autre_type on (ur.facies_autre_type_id = facies_autre_type.facies_autre_type_id)";
		String order = " order by id_UR limit 2";
		return readListFromKey("id_op_controle", key, sql, order);
	}

}
