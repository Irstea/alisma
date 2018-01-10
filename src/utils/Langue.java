/**
 * 
 */
package utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

/**
 * @author quinton Classe permettant de gerer ResourceBundle
 */
public class Langue {
	static Hashtable<String, String> langueParam = null;
	static String langageCourant = "";
	static ResourceBundle rbDefaut, rbCourant;
	String nomResource = "ressources.Langue";
	String fileLanguageLocal = "langage.config";
	static Locale locale;
	static Logger logger = Logger.getLogger(Langue.class);
	public static String languageSelect;

	/**
	 * Initialisation de la langue par defaut
	 */
	public Langue() {
		langueParam = Parametre.getSection("language");
		String infosLang[] = langueParam.get("default").split("_");
		locale = new Locale(infosLang[0], infosLang[1]);
		rbDefaut = ResourceBundle.getBundle(nomResource, locale);

		/*
		 * Recuperation de la langue par defaut
		 */
		setLanguage(getLanguageLocal());
		logger.debug("Langue par defaut : "+getLanguageLocal());

	}

	/**
	 * Retourne le libelle recherche, dans la langue choisie ou dans la lange
	 * par defaut
	 * 
	 * @param name
	 * @return String
	 */
	public static String getString(String name) {
		String libelle = "";
		try {
			libelle = rbCourant.getString(name);
		} catch (MissingResourceException mre) {
			try {
			libelle = rbDefaut.getString(name);
			} catch (Exception e) {
				logger.error("Entrée "+name+" inconnue dans les fichiers de langue");
				libelle = "unknown tag";
			}
		}
		return libelle;
	}

	/**
	 * Charge un nouveau langage
	 * 
	 * @param langage
	 */
	public void setLanguage(String langage) {
		/*
		 * Chargement du fichier de langue
		 */
		String infoLangueCourant[] = langage.split("_");
		/*
		 * Rajout pour gerer la compatibilite avec la version precedente
		 */
		if (infoLangueCourant.length == 1)  
			infoLangueCourant = langage.split("-");
		
		locale = new Locale(infoLangueCourant[0], infoLangueCourant[1]);
		languageSelect = infoLangueCourant[0];
		try {
			rbCourant = ResourceBundle.getBundle(nomResource, locale);

		} catch (Exception e) {
			/*
			 * Le langage choisi n'existe pas : on force avec la langue par
			 * defaut
			 */
			rbCourant = rbDefaut;
		}

		/*
		 * Definition du langage courant
		 */
		if (langageCourant != langage) {
			langageCourant = langage;
			writeLanguageLocal(langage);
		}
	}

	private String getLanguageLocal() {
		File path = new File("");
		String line = langueParam.get("preferred");
		String pathConfig = path.getAbsolutePath() + File.separator
				+ fileLanguageLocal;

		try {
			BufferedReader buffer = new BufferedReader(new FileReader(
					pathConfig));

			try {
				line = buffer.readLine();
			} finally {
				buffer.close();
			}
		} catch (IOException ioe) {
			writeLanguageLocal(line);
		}
		if (line == null)
			line = langueParam.get("preferred");
		return line;
	}

	/**
	 * Ecrit la langue courante pour l'implementation consideree
	 * 
	 * @param line
	 */
	private void writeLanguageLocal(String line) {
		File langFile = new File(fileLanguageLocal);
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(langFile));
			writer.write(line);
			writer.close();
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
	}
	/**
	 * Retourne la valeur de locale
	 * @return
	 */
	public Locale getLocale() {
		return locale;
	}

}
