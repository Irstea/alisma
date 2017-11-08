/**
 * 
 */
package utils;

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JTextField;

import org.apache.log4j.Logger;

/**
 * @author quinton
 * 
 *         Classe permettant de s'assurer qu'un champ est numerique ou, s'il est
 *         de type pourcentage, compris entre 0 et 100
 */
public class NumericVerifier extends InputVerifier {
	public boolean isPourcentage = false;
	public boolean isDecimal = false;
	public static Logger logger = Logger.getLogger(NumericVerifier.class);

	@Override
	public boolean verify(JComponent comp) {
		JTextField jt = (JTextField) comp;
		String texte = jt.getText();
		boolean isValid = true;

		if (texte.length() > 0) {
			/*
			 * Traitement de la valeur decimale
			 */
			if (isDecimal) {
				try {
					/*
					 * Rercherche si le separateur decimal a ete saisi en virgule
					 */
					boolean is_comma = false;
					if (texte.contains(",")) {
						logger.debug("initial value : "+texte);
						texte = texte.replace(",", ".");
						logger.debug("new value : "+texte);
						is_comma = true;
					}
					double iValue = Double.parseDouble(texte);
					if (isPourcentage && (iValue < 0 || iValue > 100)) {
						isValid = false;
					}
					if (is_comma) {
						jt.setText(texte);
					}
				} catch (NumberFormatException e) {
					isValid = false;
				}
			} else {
				/*
				 * Traitement d'un entier
				 */
				try {
					int iValue = Integer.parseInt(texte);
					if (isPourcentage && (iValue < 0 || iValue > 100))
						isValid = false;
				} catch (NumberFormatException e) {
					isValid = false;
				}
			}
		}
		/*
		 * Positionnement de la couleur de fond
		 */
		if (isValid) {
			Bordure.setBackground(jt, 0);
			return true;
		} else {
			Bordure.setBackground(jt, 3);
			return false;
		}
	}
}