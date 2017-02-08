package database;

public class Typo  extends DbObject{

	public Typo() {
		init("typo", "typo_id", true);
		setNumericList(new String[] { "ibmr_ref", "groupe" });
		setStringList(new String[] { "typo_name" });

	}

}
