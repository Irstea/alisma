package database;

import java.util.Hashtable;
import java.util.List;

/**
 * Classe permettant de gerer les tables utilisees dans les combobox
 * 
 * @author quinton
 *
 */
public class DbObjectCombo extends DbObject {
	String libelleName = "";
	public Hashtable<Integer, String> comboData = new Hashtable<Integer, String>();
	public Hashtable<String, Integer> comboDataInv = new Hashtable<String, Integer>();

	/**
	 * 
	 * @param withEmpty : indique si une valeur vide doit etre proposee
	 * @return String[]
	 */
	public String[] getArray(boolean withEmpty) {
		int nb = comboData.size();
		String ds[];
		if (withEmpty) {
			ds = new String[nb + 1];
			ds [0] = "";
			for (int i = 1; i <= nb; i++)

				ds[i] = comboData.get(i);
		} else {
			if (nb > 0) {
				ds = new String[nb];
				int j = 0;
				for (int i = 1; i <= nb; i++) {
					try {
					if (comboData.get(i).length() > 0) {
						ds[j] = comboData.get(i);
						j++;
					}
					}catch (NullPointerException e) {};
				}
			} else
				ds = new String[0];
		}
		return ds;
	}

	/**
	 * Retourne la liste des valeurs, avec une valeur vide
	 * @return
	 */
	public String[] getArray() {
		return getArray(true);
	}

	/**
	 * Lecture des donnees de la table
	 * 
	 * @param where
	 *            criteres de recherche le cas echeant
	 */
	public String[] readData(String where) {
		String sql = "select * from " + tableName;
		if (where != null && where.length() > 0)
			sql += " where " + where;
		sql += " order by " + keyName;
		List<Hashtable<String, String>> result = executeList(sql, false);
		comboData.clear();
		comboDataInv.clear();
		if (libelleName.length() == 0)
			libelleName = stringList[0];
		//comboData.put(0, "");
		for (Hashtable<String, String> ligne : result) {
			String value = ligne.get(libelleName);
			String key = ligne.get(keyName);
			//logger.debug("contenu de "+tableName+" : "+ key +"/"+value);
			comboData.put(Integer.valueOf(key), value);
			comboDataInv.put(value, Integer.valueOf(key));
		}
		return getArray();
	}

	public String[] readData() {
		return readData("");
	}

	/**
	 * Retourne le texte correspondant a la cle
	 * 
	 * @param key
	 * @return value
	 */
	public String getValueFromKey(Integer key) {
		return comboData.get(key);
	}

	/**
	 * Retourne la cle correspondant au libelle fourni
	 * 
	 * @param value
	 * @return key
	 */
	public Integer getKeyFromValue(String value) {
		return comboDataInv.get(value);
	}

}
