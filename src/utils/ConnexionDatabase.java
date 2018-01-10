package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Hashtable;

import org.apache.log4j.Logger;

/**
 * Gestion de la connexion a la base de donnees
 * 
 * @author quinton
 * 
 */
public class ConnexionDatabase {
	public static Connection connection = null;
	static Hashtable<String, String> dbParam = null;
	public static Class<?> db;
	static Logger logger = Logger.getLogger(ConnexionDatabase.class);
	public static boolean stateConnect = false;
	public static String messageConnect ;

	public ConnexionDatabase() {
		dbParam = Parametre.getSection("database");
		if (!dbParam.isEmpty()) {
			if (connection == null) {
				connect();
				}
		} else {
			System.err.println("Parametres de connexion non disponibles");
		}
	}
	
	/**
	 * Retourne la connexion courante
	 * @return connection
	 */
	public static Connection getConnexion() {
		try {
			if (connection.isValid(0) == false) {
				connect();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return connection;
	}

	/**
	 * Lance la connexion a la base de donnees
	 */
	private static void connect() {
		/*
		 * Initialisation de la classe
		 */
		try {
			db = Class.forName(dbParam.get("jdbc_class"));
		} catch (ClassNotFoundException e1) {
			messageConnect = e1.getMessage();
			logger.error("Database error", e1);
		}
		try {
			connection = DriverManager.getConnection(
					"jdbc:" + dbParam.get("dbtype") + "://"
							+ dbParam.get("server") + "/"
							+ dbParam.get("dbname"), 
							dbParam.get("dbuser"),
					dbParam.get("dbpass"));
			stateConnect = true;

		} catch (SQLException e) {
			logger.debug("Database error", e);
			messageConnect = e.getMessage();
		}

	}
}
