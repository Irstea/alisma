package alisma;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import utils.Langue;
import utils.Parametre;

public class Alisma {

	public static final String VERSION = "v1.0.2 - 3 janvier 2017";
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
}
