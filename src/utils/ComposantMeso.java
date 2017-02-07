/**
 * 
 */
package utils;

import java.util.Hashtable;
import java.util.Map.Entry;

import javax.swing.JComboBox;
import javax.swing.JComponent;

import org.apache.log4j.Logger;

/**
 * @author quinton
 * 
 * Classe basee sur ComposantAlisma, qui permet de gerer
 * les saisies particulieres des donnees mesologiques :
 * JComboBox toutes identiques
 *
 */
public class ComposantMeso extends ComposantAlisma{
	
	public Hashtable <String, JComboBox<Object>> mesoList = new Hashtable<String, JComboBox<Object>>();
	boolean mesoError = false;
	static Logger logger = Logger.getLogger(ComposantMeso.class);


	/**
	 * Renseigne les comboBox a partir d'un tableau des comboBox
	 * Les JComboBox sont inseres l'un apres l'autre
	 * @param listeCombo
	 */
	public void addComboMesoList(String[] listeCombo) {
		int y = 0;
		for (String name : listeCombo) {
			addComboMeso(name, 0, y, 1);
			y ++;
		}		
	}
	
	/**
	 * Rajoute un nouveau JComboBox specifique donnees mesologiques
	 * @param name
	 * @param x
	 * @param y
	 * @param z
	 * @param editable
	 */
	@SuppressWarnings("unchecked")
	public void addComboMeso(String name, int x, int y, int z) {
		JComboBox<Object> jcb;
		Integer i;
		addCombo(name, 0, y, 1, false);
		jcb = (JComboBox<Object>) fieldList.get(name);
		jcb.setToolTipText(Langue.getString("echelleMeso"));
		jcb.setEditable(false);
		jcb.addItem(null);
		for (i = 0 ; i < 6 ; i++){
			jcb.addItem(i.toString());
		}
		 mesoList.put(name, jcb);
		//fieldRecommanded.add(name);
	}
	
	@SuppressWarnings("unchecked")
	public int validation () {
		int retour = super.validation();
		//logger.debug("ComposantMeso - retour de validation : " + String.valueOf(retour));
		JComboBox<Object> jcb;
		/*
		 * Verification qu'au moins une entree a ete saisie
		 */
		if (retour == 0) {			
			retour = 1;
			for (Entry<String, JComponent> entry : fieldList.entrySet()) {
				jcb = (JComboBox<Object>) entry.getValue();
				if (jcb.getSelectedIndex() > 0) 
					retour = 0;
			}
		}
		/*
		 * Verification des taux de recouvrement
		 */
		int min = 0, max = 0;
		boolean error = false;
		boolean valeurEntree = false ;
		/*
		 * Totalise les valeurs mini et maxi
		 */
		for (Entry<String, JComponent> entry : fieldList.entrySet()) {
			jcb = (JComboBox<Object>) entry.getValue();
			if (jcb.getSelectedIndex() > 0){
				valeurEntree = true;
				switch(jcb.getSelectedItem().toString()) {
				case "0":
					break;
				case "1":
					max += 1;
					break;
				case "2":
					min += 1;
					max += 10;
					break;
				case "3":
					min += 10;
					max += 25;
					break;
				case "4":
					min += 25;
					max += 75;
					break;
				case "5":
					min += 75;
					max += 100;
					break;
				}
			}
		}
		/*
		 * Realise le test de validation
		 */
		if (min >0 || max >0) {
			if (min > 100 || max < 100) {
				error = true;
			}
		} else {
			if (valeurEntree) 
				error = true;
		}
		
		/*
		 * Positionne le code de retour si erreur detectee
		 */
		if (error) {
			retour = 3;
		}
		
		/*
		 * Positionne la bordure
		 */
		this.bordure.setBordure(pane, retour);	
		return retour;
	}	
}
