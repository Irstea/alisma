package database;

import java.util.Hashtable;
import java.util.List;

public class TaxonView extends DbObject {
	public TaxonView() {
		init("Taxons_details_view", "cd_taxon", true);
		setNumericList(new String[] { "cd_sandre", "id_groupe" });
		setStringList(new String[] { "nom_taxon", "station" });
	}

	/**
	 * REtourne la liste des taxons correspondant au nom fourni
	 * 
	 * @param name
	 * @return List<Hashtable<String, String>>
	 */
	public List<Hashtable<String, String>> getListFromName(String name) {
		String sql = "SELECT cd_taxon, nom_taxon FROM Taxons_view";
		try {
			if (!name.isEmpty())
				sql += " WHERE upper(nom_taxon) like upper('" + name + "%')";
		} catch (NullPointerException e) {
		}

		sql += " order by nom_taxon";
		List<Hashtable<String, String>> result = executeList(sql);
		return result;

	}

	/**
	 * Retourne le premier element correspondant au nom fourni
	 * 
	 * @param name
	 * @return
	 */
	public Hashtable<String, String> getFirstFromName(String name) {
		List<Hashtable<String, String>> data = getListFromName(name);
		if (!data.isEmpty()) {
			return data.get(0);
		} else
			return null;
	}

	/**
	 * REtourne la liste des taxons correspondant au nom fourni
	 * 
	 * @param name
	 * @return List<Hashtable<String, String>>
	 */
	public List<Hashtable<String, String>> getListFromCd(String name) {
		String sql = "SELECT cd_taxon, nom_taxon FROM Taxons_view";
		try {
			if (!name.isEmpty())
				sql += " WHERE upper(cd_taxon) like upper('" + name + "%')";
		} catch (NullPointerException e) {
		}

		sql += " order by nom_taxon";
		List<Hashtable<String, String>> result = executeList(sql);
		return result;

	}

	/**
	 * Retourne le premier element correspondant au nom fourni
	 * 
	 * @param name
	 * @return
	 */
	public Hashtable<String, String> getFirstFromCd(String name) {
		List<Hashtable<String, String>> data = getListFromCd(name);
		if (!data.isEmpty()) {
			return data.get(0);
		} else
			return null;
	}

	/**
	 * Retourne un taxon avec toutes les informations associees
	 * @param cd_taxon
	 * @return
	 */
	public Hashtable<String, String> getTaxon(String cd_taxon) {
		String sql = "";
		try {
			sql = "select v.cd_taxon, v.nom_taxon, nom_groupe, t.auteur as auteur, "
					+ "p.auteur as auteurp,	t.cd_valide, t.cd_contrib, t.cote_spe, t.coef_steno, "
					+ "t1.cote_spe as cote_spe_valide, t1.coef_steno as coef_steno_valide "
					+ "from Taxons_details_view v "
					+ " left outer join Groupes g on (g.id_groupe = v.id_groupe) "
					+ "left outer join Taxons_MP t on (v.cd_taxon = t.cd_taxon) "
					+ "left outer join Taxons_MP_persos p on (v.cd_taxon = p.cd_taxon_perso) "
					+ "left outer join Taxons_MP t1 on (t.cd_contrib = t1.cd_taxon) "
					+ "where v.cd_taxon = '" + cd_taxon + "'";
		} catch (NullPointerException e) {
		}
		List<Hashtable<String, String>> result = executeList(sql);
		if (!result.isEmpty()) {
			return result.get(0);
		} else {
			return null;
		}

	}
}
