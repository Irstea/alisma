/**
 * 
 */
package database;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

/**
 * @author quinton
 *
 */
public class TaxonPerso extends DbObject {

	public TaxonPerso() {
		init("taxons_perso", "cd_taxon_perso", false);
		isKeyText = true;
		setNumericList(new String[] { "cd_sandre", "id_groupe" });
		setStringList(new String[] { "cd_taxon_perso", "nom_taxon_perso",
				"createur", "date_creationP", "auteur", });

	}

	/**
	 * REtourne la liste des taxons en fonction des criteres fournis
	 * 
	 * @param param
	 * @return List<Hashtable<String, String>>
	 */
	public List<Hashtable<String, String>> getListByParam(
			Hashtable<String, String> param) {
		String sql = "select * from taxons_perso"
				+ " left outer join groupes using (id_groupe)";
		String where = " where ";
		boolean isWhere = false;
		try {
			if (!param.get("taxon").isEmpty()) {
				if (isWhere == true) {
					where += " and ";
				} else
					isWhere = true;
				where += " (upper(cd_taxon_perso) like upper('%"
						+ param.get("taxon") + "%') "
						+ "or upper(nom_taxon_perso) like upper('%"
						+ param.get("taxon") + "%'))";
			}
		} catch (NullPointerException e) {
		}
		if (isWhere)
			sql += where;
		sql += " order by nom_taxon_perso";
		return executeList(sql);
	}

	public String[][] getListByParamToTable(Hashtable<String, String> param) {
		List<Hashtable<String, String>> result = getListByParam(param);
		String[][] returned = new String[result.size()][7];
		for (int i = 0; i < result.size(); i++) {
			returned[i][0] = result.get(i).get("cd_taxon_perso");
			returned[i][1] = result.get(i).get("nom_taxon_perso");
			returned[i][2] = result.get(i).get("auteur");
			returned[i][3] = result.get(i).get("nom_groupe");
			returned[i][4] = result.get(i).get("createur");
			returned[i][5] = result.get(i).get("cd_sandre");
			returned[i][6] = result.get(i).get("date_creation_perso");
		}
		return returned;
	}

	public String getNewCode() {
		String newCode = "";
		String sql = "select cd_taxon_perso from taxons_perso "
				+ " order by cd_taxon_perso desc limit 1";

		List<Hashtable<String, String>> result = executeList(sql);
		if (!result.isEmpty()) {
			String code = result.get(0).get("cd_taxon_perso");
			int compteur = Integer
					.valueOf(code.substring(code.indexOf("W") + 1)) + 1;
			newCode = "NEW" + String.format("%04d", compteur);
		} else
			newCode = "NEW0001";
		return newCode;
	}

	/**
	 * Forcage de la date de creation du taxon
	 */
	public String write(Hashtable<String, String> data, String key) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
		if (data.get("date_creation_perso").isEmpty() && key.isEmpty()) {
			data.put("date_creation_perso", sdf.format(new Date()));
		}
		} catch (NullPointerException e) {
			data.put("date_creation_perso",sdf.format(new Date()) );
		}
		return super.write(data, key);
	}

}
