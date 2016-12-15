/**
 * 
 */
package utils;

/**
 * @author quinton
 * Classe etendant les fonctions utilisables dans un objet observable
 */
public interface ObservableExtended {

	/**
	 * Fonction a surcharger, pour ramener un objet a partir du parametre
	 * fourni
	 * @param String argName
	 * @return Object
	 */
	public Object getValue(String argName);
}
