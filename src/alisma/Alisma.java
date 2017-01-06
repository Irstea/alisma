package alisma;

import javax.swing.JOptionPane;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import utils.Langue;
import utils.Parametre;

public class Alisma {

	public static final String VERSION = "v1.0.3 - 6 janvier 2017";
	public static final String VERSIONNUMBER = "1.0.3";
	static String parametre = "param/param.ini";
	public static String versionOs ;
	public static boolean isWindowsOs = false;
	static Logger logger = Logger.getLogger(Alisma.class);

	public static void main(String[] args) {
		/*
		 * Configuration des logs
		 */
		DOMConfigurator.configure("param/log4j.xml");
		BasicConfigurator.configure();
		/*
		 * Verification de la version de Java installee
		 */
		Double java_version = getJavaVersion();
		logger.info("Version Java : " + java_version);
		
		if (java_version < 1.7) {
			JOptionPane.showMessageDialog(null, "Java version is too old ("+java_version+"), this program will stop now",
					"Error", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
		
		/*
		 * Lecture des parametres dans le fichier param.ini
		 */
		new Parametre(parametre);
		
		/*
		 * Lecture de la version de l'OS
		 */
		versionOs = System.getProperty("os.name");
		isWindowsOs = versionOs.toLowerCase().indexOf("win") >= 0 ? true : false;
		
		
		/*
		 * Chargement des fichiers de langue
		 */
		new Langue();

		/*
		 * Affichage de la fenetre principale
		 */
		new Controleur();

	}
	
	static double getJavaVersion () {
	    String version = System.getProperty("java.version");
	    int pos = version.indexOf('.');
	    pos = version.indexOf('.', pos+1);
	    return Double.parseDouble (version.substring (0, pos));
	}
}
