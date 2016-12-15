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
 * Classe permettant de mettre a jour la table Lignes_op_controle
 *
 */
public class Lignes_op_controle extends DbObject {

	public Lignes_op_controle() {
		init("Lignes_op_controle", "id_ligne_op_controle", true);
		setStringList(new String[] {"id_taxon"});
		setNumericList(new String [] {"pc_UR1", "pc_UR2", "cf", "id_op_controle"});
	}

	/**
	 * Ecriture des donnees
	 * @param Hashtable<String, String> item
	 * @return
	 */
	public int ecrire(Hashtable<String, String> item) {
		int key = Integer.parseInt(item.get("id_ligne_op_controle"));
		/*
		 * Forcage de la valeur a 0 pour pc_UR1 si vide
		 */
		if (item.get("pc_UR1").isEmpty())
			item.put("pc_UR1", "0");
		/*
		 * Ecriture des donnees
		 */
		setData(item);
		if (key == -1) {
			key = insert();
		} else
			update(key);
		return key;
	}

	/**
	 * Ecrit l'ensemble de la liste des taxons, et supprime ceux qui n'existent pas
	 * @param taxons : liste des taxons
	 * @param keyOp : cle de l'operation
	 * @return List<Hashtable<String, String >>
	 */
	public List<Hashtable<String, String >> ecrireList(List<Hashtable<String, String >> taxons, int keyOp) {
		List<Hashtable<String, String>> oldValue = this.readListFromKey("id_op_controle", keyOp);
		List<Hashtable<String, String>> retour = new ArrayList<Hashtable<String, String>>();
		List<Integer> ancien = new ArrayList<Integer>();
		List<Integer> actuel = new ArrayList<Integer>();
		int key;
		for (Hashtable<String, String> item : taxons) {
			item.put("id_op_controle", String.valueOf(keyOp));
			key = ecrire(item);
			actuel.add(key);
			item.put("id_ligne_op_controle", String.valueOf(key));
			
			retour.add(item);		
		}
		/*
		 * Traitement de la suppression
		 * Recherche des enregistrements supprimes
		 */
		List<Integer> different = new ArrayList<Integer>();
		different.addAll(actuel);
		for (Hashtable<String, String> item : oldValue) {
			ancien.add(Integer.parseInt(item.get("id_ligne_op_controle")));
		}
		different.addAll(ancien);
        different.removeAll( actuel );
        
        /*
         * Suppression des enregistrements supprimes
         */
        for (Integer item : different) {
        	this.delete("id_ligne_op_controle", item.toString(), true);
        }
		return retour;
		
	}

	/**
	 * Retuourne la liste complete des taxons pour une operation
	 * @param id_op_controle
	 * @return
	 */
	public List<Hashtable<String, String>> getListFromOp(String id_op_controle) {
		String sql = "select tv.cd_taxon, tv.nom_taxon, tx.cd_sandre, tx.cd_valide, tx.cote_spe, tx.coef_steno, "
				+ " tx.cd_contrib, "
				+ "pc_UR1, pc_UR2, cf, id_taxon, id_ligne_op_controle "
				+ "from Lignes_op_controle ligne"
				+ " join Taxons_details_view tv on (tv.cd_taxon = ligne.id_taxon)"
				+ " left outer join Taxons_MP tx on (tv.cd_taxon = tx.cd_taxon)"
				+ " where id_op_controle = " + id_op_controle 
				+ " order by tv.cd_taxon";
		return executeList(sql);
		
	}
}
