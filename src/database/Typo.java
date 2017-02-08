package database;

import java.util.Hashtable;
import java.util.List;

public class Typo  extends DbObjectCombo{

	public Typo() {
		init("typo", "typo_id", true);
		setNumericList(new String[] { "ibmr_ref", "groupe" });
		setStringList(new String[] { "typo_name" });

	}
	public String getIbmrRef(String typoName) {
		String sql = "select ibmr_ref from typo where typo_name = '"+typoName+"'";
		List<Hashtable<String, String>> resultat = executeList(sql);
		String retour = "1";
		try {
			retour = resultat.get(0).get("ibmr_ref");
		} catch (Exception e) {
			
		}
		return retour;
	}
	
}
