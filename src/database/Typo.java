package database;

public class Typo  extends DbObjectCombo{

	public Typo() {
		init("typo", "typo_id", true);
		setNumericList(new String[] { "ibmr_ref", "groupe" });
		setStringList(new String[] { "typo_name" });

	}

}
