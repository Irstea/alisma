package database;

import java.util.Hashtable;
import java.util.List;

public class Taxon extends DbObject {

	public Taxon() {
		init("Taxons_MP", "cd_taxon", true);
		setNumericList(new String[] { "cote_spe", "coef_steno", "cd_sandre",
				"aquaticite", "id_groupe" });
		setStringList(new String[] { "nom_taxon", "date_creation", "auteur",
				"cd_valide", "cd_contrib" });

	}

	/**
	 * REtourne la liste des taxons en fonction des criteres fournis
	 * @param param
	 * @return List<Hashtable<String, String>>
	 */
	public List<Hashtable<String, String>> getListByParam(
			Hashtable<String, String> param) {
		String sql = "select * from Taxons_MP"
				+ " left outer join Groupes using (id_groupe)";
		String where = " where ";
		boolean isWhere = false;
		try {
			if (param.get("is_contrib") == "1") {
				if (isWhere == true) {
					where += " and ";
				} else
					isWhere = true;
				where += " cote_spe is not null";
			}
		} catch (NullPointerException e) {
		}
		try {
			if (!param.get("taxon").isEmpty()) {
				if (isWhere == true) {
					where += " and ";
				} else
					isWhere = true;
				where += " (upper(cd_taxon) like upper('%"+param.get("taxon")+"%') "
						+ "or upper(nom_taxon) like upper('%" + param.get("taxon") + "%'))";
			}
		} catch (NullPointerException e) {}
		if (isWhere)
			sql += where;
		sql += " order by nom_taxon";
		return executeList(sql);
	}
	
	public String[][] getListByParamToTable(Hashtable<String, String> param) {
		List<Hashtable<String,String>> result = getListByParam(param);
//		private String[] columnName = { Langue.getString("cd"),
//				Langue.getString("nom"), Langue.getString("auteur"),
//				Langue.getString("groupe"), Langue.getString("cs"),
//				Langue.getString("e"), Langue.getString("cdSandreTab"),
//				Langue.getString("cdValTab"), Langue.getString("cdContrib") };;

		String[][] returned = new String[result.size()][9];
		for (int i = 0; i < result.size(); i++) {
			returned[i][0] = result.get(i).get("cd_taxon");
			returned[i][1] = result.get(i).get("nom_taxon");
			returned[i][2] = result.get(i).get("auteur");
			returned[i][3] = result.get(i).get("nom_groupe");
			returned[i][4] = result.get(i).get("cote_spe");
			returned[i][5] = result.get(i).get("coef_steno");
			returned[i][6] = result.get(i).get("cd_sandre");
			returned[i][7] = result.get(i).get("cd_valide");
			returned[i][8] = result.get(i).get("cd_contrib");			
		}
		return returned;
	}
}
