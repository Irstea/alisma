/**
 * 
 */
package utils;

import java.awt.Color;
import java.awt.Image;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Hashtable;

import javax.swing.ImageIcon;
import javax.swing.UIManager;

import org.apache.log4j.Logger;

/**
 * @author quinton
 * 
 *         classe contenant les parametres generaux de l'application
 */
public class Parametre {
	private static Hashtable<String, String> database = null, language = null, others = null, display = null,
			fieldsLevel = null, seee = null;
	public static Hashtable<String, Hashtable<String, String>> param = new Hashtable<String, Hashtable<String, String>>();
	public static ImageIcon icone;
	public static Image iconeImage;
	public static ImageIcon logo;
	public static Color cFooter, cBanniere, cCentral, cTab;
	// public static boolean modeDebug;
	public static Logger logger = Logger.getLogger(Parametre.class);

	/**
	 * Instanciation post-lecture des parametres
	 */
	public Parametre() {

	}

	/**
	 * Premiere instanciation pour lecture des parametres
	 * 
	 * @param String
	 *            nomFichierParam
	 */
	public Parametre(String nomFichierParam) {

		/*
		 * Lecture des parametres
		 */
		ProfileReader pr = new ProfileReader();

		try {
			InputStream inputStream = new FileInputStream(nomFichierParam);
			try {
				pr.load(inputStream);
			} catch (Exception e) {
				logger.error("Impossible de lire le fichier " + nomFichierParam + " " + e.getMessage());
			}

			/*
			 * Lecture des parametres
			 */
			try {
				database = pr.getSection("database");
				language = pr.getSection("language");
				others = pr.getSection("others");
				display = pr.getSection("display");
				fieldsLevel = pr.getSection("fieldsLevel");
				seee = pr.getSection("seee");
				param.put("database", database);
				param.put("language", language);
				param.put("others", others);
				param.put("display", display);
				param.put("fieldsLevel", fieldsLevel);
				param.put("seee", seee);

			} catch (Exception e) {
				logger.error("Impossible d'initialiser correctement la classe ProfileReader");
			}

		} catch (Exception e) {
			logger.error("Impossible d'ouvrir ou de trouver le fichier de parametres " + nomFichierParam);
		}
		/*
		 * Lecture de l'icone de l'application
		 */
		try {
			icone = (new ImageIcon(getClass().getClassLoader().getResource(display.get("icone"))));
			iconeImage = icone.getImage();
			logo = new ImageIcon(getClass().getClassLoader().getResource(display.get("logo")));
			/*
			 * Preparation des couleurs
			 */
			cFooter = getCouleur("colorFooter");
			cBanniere = getCouleur("colorBanniere");
			cCentral = getCouleur("colorCentral");
			cTab = getCouleur("colorTab");

			UIManager.put("ComboBox.disabledForeground", Color.DARK_GRAY);
		} catch (NullPointerException e) {
			logger.error(
					"Color and icons initialisation error : probably missing entries in param.ini " + e.getMessage());
		}
	}

	/**
	 * Retourne une valeur à partir de la section et de l'attribut recherché vide si
	 * inexistant
	 * 
	 * @param section
	 * @param attribut
	 * @return
	 */
	public static String getValue(String section, String attribut) {
		String val = "";
		try {
			val = param.get(section).get(attribut);
		} catch (Exception e) {
			logger.debug("Section " + section + ", attribut " + attribut + " non trouvé dans le fichier de paramètres");
		}
		if (val == null) {
			val = "";
		}
		return val;
	}

	/**
	 * Retourne une section entiere
	 * @param section
	 * @return
	 */
	public static Hashtable <String, String> getSection (String section) {
		return param.get(section);
	}
	/**
	 * Prepare les couleurs
	 * 
	 * @param pCouleur
	 * @param pNom
	 */
	private Color getCouleur(String pNom) {
		try {
			String[] couleur = null;
			couleur = display.get(pNom).split(",");
			return new Color(Integer.parseInt(couleur[0]), Integer.parseInt(couleur[1]), Integer.parseInt(couleur[2]));
		} catch (NullPointerException e) {
			logger.error("Couleur " + pNom + " not define in param.ini");
			return null;
		}
	}
}
