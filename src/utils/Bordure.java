/**
 * 
 */
package utils;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JComponent;

/**
 * @author quinton
 *
 * Trace une bordure autour du composant
 */
public class Bordure {
	
	public Bordure () {
		
	}
	
	/**
	 * Definit une bordure de couleur autour du composant fourni en parametre,
	 * selon le niveau prevu
	 * @param comp
	 * @param level
	 */
	public static void setBordure (JComponent comp, int level) {
		switch (level) {
		case 1:
			comp.setBorder(BorderFactory.createLineBorder(Color.BLUE));
			break;
		case 2:
			comp.setBorder(BorderFactory.createLineBorder(Color.ORANGE));
			break;
		case 3:
			comp.setBorder(BorderFactory.createLineBorder(Color.RED));
			break;
		case 0:
		default:
			comp.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		}
	}
	
	/**
	 * Definit une couleur de fond au composant fourni en parametre,
	 * selon le niveau prevu
	 * @param comp
	 * @param level
	 */
	public static void setBackground(JComponent comp, int level) {
		switch (level) {
		case 1:
			//comp.set;
			comp.setBackground(Color.BLUE);
			break;
		case 2:
			comp.setBackground(Color.ORANGE);
			break;
		case 3:
			comp.setBackground(new Color(255,170,170));
			break;
		case 0:
		default:
			comp.setBackground(Color.WHITE);
		}
	}

}
