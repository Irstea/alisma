/**
 * 
 */
package database;

/**
 * @author quinton
 *
 */
public class Groupe extends DbObjectCombo{
	public Groupe() {
		init("groupes", "id_groupe", true);
		setNumericList(new String[] {  "id_groupe" });
		setStringList(new String[] { "nom_groupe" });
		libelleName = "nom_groupe";
	}

}
