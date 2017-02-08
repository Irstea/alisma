package database;

import java.util.Hashtable;

public class Ibmr extends DbObject {

	public Ibmr() {
		init("ibmr", "id_op_controle", false);
		setStringList(new String[] { "taxon_robustesse", "seee_date","seee_version", "seee_taxon_robustesse" });
		setNumericList(new String[] { "id_op_controle", "ibmr_value", "robustesse_value", "niveau_trophique_id",
				"robustesse_niveau_trophique_id", "cs_moy", "cs_min", "cs_max", "coef_moy", "coef_min", "coef_max",
				"nbtaxon_het", "nbtaxon_alg", "nbtaxon_bry", "nbtaxon_pte", "nbtaxon_pha", "nbtaxon_lic",
				"nbtaxon_total", "nbtaxon_contrib", "nbtaxon_steno1", "nbtaxon_steno2", "nbtaxon_steno3",
				"seee_ibmr", "seee_nbtaxon_contrib", "seee_robustesse_value", "ek_nb_robustesse",
				"robustesse_classe_etat_id", "classe_etat_id", "eqr_value", "robustesse_eqr_value"});
	}

	/**
	 * Mise en table de l'enregistrement
	 * 
	 * @param lData
	 * @param keyOp
	 * @return keyOp
	 */
	public int ecrire(Hashtable<String, String> lData, int keyOp) {
		boolean insert = false;
		if (keyOp > 0) {
			if (!isExist(keyOp))
				insert = true;
		} else
			insert = true;
		if (insert) {
			lData.put("id_op_controle", String.valueOf(keyOp));
			setData(lData);
			keyOp = insert();
		} else {
			setData(lData);
			update(keyOp);
		}
		return keyOp;
	}
	
	public Hashtable <String, String> lireComplet(String id) {
		String sql = "select ibmr.*, nt.niveau_trophique_libelle, rnt.niveau_trophique_libelle as rnt_libelle, "
				+ " c1.classe_etat_libelle, c2.classe_etat_libelle as robustesse_classe_etat_libelle"
				+ " from "+tableName
				+ " left outer join niveau_trophique nt on (ibmr.niveau_trophique_id = nt.niveau_trophique_id)"
				+ " left outer join niveau_trophique rnt on (ibmr.robustesse_niveau_trophique_id = rnt.niveau_trophique_id)"
				+ " left outer join classe_etat c1 on (ibmr.classe_etat_id = c1.classe_etat_id)"
				+ " left outer join classe_etat c2 on (ibmr.robustesse_classe_etat_id = c2.classe_etat_id)"
				+ " where id_op_controle = "+id;
		
		
		Hashtable <String, String> data = new Hashtable<String, String>();
		try {	
			data = executeList(sql).get(0);
		} catch (Exception e) {
			
		}
		return data;	 
	}

}
