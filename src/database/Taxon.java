package database;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.List;

import com.opencsv.CSVReader;

import utils.Langue;

public class Taxon extends DbObject {

	public Taxon() {
		init("taxons_mp", "cd_taxon", false);
		setNumericList(new String[] { "cote_spe", "coef_steno", "cd_sandre", "aquaticite", "id_groupe" });
		setStringList(new String[] { "cd_taxon", "nom_taxon", "date_creation", "auteur", "cd_valide", "cd_contrib" });
		isKeyText = true;
	}

	/**
	 * REtourne la liste des taxons en fonction des criteres fournis
	 * 
	 * @param param
	 * @return List<Hashtable<String, String>>
	 */
	public List<Hashtable<String, String>> getListByParam(Hashtable<String, String> param) {
		String sql = "select * from taxons_mp" + " left outer join groupes using (id_groupe)";
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
				where += " (upper(cd_taxon) like upper('%" + param.get("taxon") + "%') "
						+ "or upper(nom_taxon) like upper('%" + param.get("taxon") + "%'))";
			}
		} catch (NullPointerException e) {
		}
		if (isWhere)
			sql += where;
		sql += " order by nom_taxon";
		return executeList(sql);
	}

	public String[][] getListByParamToTable(Hashtable<String, String> param) {
		List<Hashtable<String, String>> result = getListByParam(param);

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

	/**
	 * Import de la liste officielle des taxons
	 * @param filename
	 * @param separator
	 * @return boolean
	 */
	public boolean importFromCsv(String filename, char separator) {
		boolean result = false;
		data = new Hashtable<String, String>();
		try {
			@SuppressWarnings("deprecation")
			CSVReader reader = new CSVReader (new InputStreamReader ( new FileInputStream(filename), "UTF8"), separator);
			String[] ligne;
			result = true;
			/*
			 * Suppression de la premiere ligne
			 */
			reader.readNext();
			while ((ligne = reader.readNext()) != null) {
				data.clear();
				if (ligne.length > 4) {
					data.put("cd_taxon", ligne[0]);
					data.put("nom_taxon", ligne[1]);
					data.put("cd_sandre", ligne[2]);
					data.put("id_groupe", ligne[3]);
					data.put("auteur", ligne[4]);
					data.put("cd_valide", ligne[5]);
					data.put("cd_contrib", ligne[6]);
					try {
					write(data, ligne[0]);
				} catch (Exception e) {
					message = "System error:"+e.getMessage();
					result = false;
					break;
				}
				}
			}
			reader.close();
		} catch (FileNotFoundException e) {
			message = Langue.getString("filenotfound");
			result = false;
		} catch (IOException e) {
			message = Langue.getString("filenotreadable");
			result = false;
		}
		return result;
	}

	/**
	 * Import des parametres officiels utilises pour le calcul de l'indicateur
	 * @param filename
	 * @param separator
	 * @return boolean
	 */
	public boolean importParamFromCsv(String filename, char separator) {
		boolean result = false;
		data = new Hashtable<String, String>();
		try {
			@SuppressWarnings("deprecation")
			CSVReader reader = new CSVReader (new InputStreamReader ( new FileInputStream(filename), "UTF8"), separator);
			String[] ligne;
			result = true;
			/*
			 * Suppression de la premiere ligne
			 */
			reader.readNext();
			while ((ligne = reader.readNext()) != null) {
				data.clear();
				if (ligne.length > 2) {
					data.put("cote_spe", ligne[1]);
					data.put("coef_steno", ligne[2]);
					try {
					write(data, ligne[0]);
					} catch (Exception e) {
						message = "System error:"+e.getMessage();
						result = false;
						break;
					}
				}
			}
			reader.close();
		} catch (FileNotFoundException e) {
			message = Langue.getString("filenotfound");
			result = false;
		} catch (IOException e) {
			message = Langue.getString("filenotreadable");
			result = false;
		
		}
		return result;
	}
}
