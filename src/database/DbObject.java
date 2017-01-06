/**
 * 
 */
package database;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import org.apache.log4j.Logger;

import alisma.Alisma;
import utils.ConnexionDatabase;
import utils.Parametre;

/**
 * @author quinton
 * 
 *         Classe destinee a preparer les scripts d'insertion et de mise a jour
 */
public class DbObject {

	String[] stringList, numericList;
	Hashtable<String, String> data;
	String tableName = "", keyName = "";
	protected Connection connection = null;
	protected Statement query = null;
	protected ResultSet rs = null;
	boolean autoGenerateKey = false;
	boolean isKeyText = false;
	char identProtect = '`';
	static Logger logger = Logger.getLogger(DbObject.class);
	static boolean encode_iso8859 = false;

	/**
	 * Initialise la classe avec les valeurs de base
	 * 
	 * @param String
	 *            p_tableName : nom de la table
	 * @param String
	 *            p_keyName : nom du champ cle
	 * @param boolean
	 *            p_autoGenerateKey : indique si la table est de type cle
	 *            automatique ou non
	 */
	public void init(String p_tableName, String p_keyName, boolean p_autoGenerateKey) {
		tableName = p_tableName;
		keyName = p_keyName;
		autoGenerateKey = p_autoGenerateKey;
		try {
			connection = ConnexionDatabase.getConnexion();
		} catch (Exception e) {
			logger.error(e);
		}
		if (Parametre.database.containsKey("dbencode_iso8859") == true) {
			logger.debug("dbencode_iso8859 : "+ Parametre.database.get("dbencode_iso8859"));
			try {
				encode_iso8859 = Boolean.getBoolean(Parametre.database.get("dbencode_iso8859"));
			} catch (Exception e) {
				logger.error("impossible de lire la valeur de database/encode_iso8859 ("+Parametre.database.get("dbencode_iso8859")+")");
			}
		}

	}

	/**
	 * initialise la liste des champs de type texte
	 * 
	 * @param strings
	 */
	public void setStringList(String[] strings) {
		stringList = strings;
	}

	/**
	 * Initialise la liste des champs de type numerique
	 * 
	 * @param pListe
	 */
	public void setNumericList(String[] pListe) {
		numericList = pListe;
	}

	/**
	 * Stocke les donnees a enregistrer
	 * 
	 * @param p_data
	 */
	public void setData(Hashtable<String, String> p_data) {
		data = p_data;
	}

	/**
	 * Script permettant de mettre a jour un enregistrement dans la base de
	 * donnees
	 * 
	 * @param key
	 *            <String|int>
	 * @return int : 1 si la mise a jour a fonctionne, 0 sinon
	 */
	public int update(Object key) {
		String sql = "update " + tableName + " set ";
		String where = " where " + keyName + " = ";
		int nbRow = 0;
		boolean isComma = false;
		/*
		 * Traitement de la cle
		 */
		if (key.getClass().getSimpleName() == "String" || isKeyText) {
			where += "'" + key + "'";
		} else
			where += key;
		/*
		 * Traitement des champs de type texte
		 */
		for (String fieldName : stringList) {
			if (data.containsKey(fieldName)) {
				sql += (isComma ? "," : "");
				isComma = true;
				if (data.get(fieldName).isEmpty()) {
					/*
					 * Traitement de la valeur nulle
					 */
					sql += identProtect + fieldName + identProtect + " = NULL ";
				} else {
					sql += identProtect + fieldName + identProtect + " = '"
							+ data.get(fieldName).replaceAll("'", "\\'\\'") + "'";
				}
			}
		}
		/*
		 * Traitement des champs de type numerique
		 */
		for (String fieldName : numericList) {
			if (data.containsKey(fieldName)) {
				sql += (isComma ? ", " : "");
				isComma = true;
				if (data.get(fieldName).isEmpty()) {
					/*
					 * Traitement de la valeur nulle
					 */
					sql += identProtect + fieldName + identProtect + " = NULL ";
				} else {
					sql += identProtect + fieldName + identProtect + " = " + data.get(fieldName) + " ";
				}
			}
		}

		if (isComma) {
			/*
			 * Traitement de l'update
			 */
			logger.debug(sql + where);
			try {
				query = connection.createStatement();
				nbRow = query.executeUpdate(sql + where);
			} catch (Exception e) {
				logger.error(e);
			}
		}
		return nbRow;
	}

	/**
	 * Declenchement de l'insertion d'un nouvel enregistrement
	 * 
	 * @return
	 */
	public int insert() {
		int key = 0;
		String sql = "insert into " + tableName;
		String colonne = " (";
		String value = " values (";
		boolean isComma = false;
		/*
		 * Traitement des champs de type texte
		 */
		for (String fieldName : stringList) {
			if (data.containsKey(fieldName)) {
				colonne += (isComma ? ", " : "");
				value += (isComma ? ", " : "");
				isComma = true;
				colonne += identProtect + fieldName + identProtect;
				if (data.get(fieldName).isEmpty()) {
					/*
					 * Traitement de la valeur nulle
					 */
					value += " NULL";
				} else {
					value += "'" + data.get(fieldName).replaceAll("'", "\\'\\'") + "'";
				}
			}
		}
		/*
		 * Traitement des champs de type numerique
		 */
		for (String fieldName : numericList) {
			if (data.containsKey(fieldName)) {
				colonne += (isComma ? ", " : "");
				value += (isComma ? ", " : "");
				isComma = true;
				colonne += identProtect + fieldName + identProtect;
				if (data.get(fieldName).isEmpty()) {
					/*
					 * Traitement de la valeur nulle
					 */
					value += " NULL";
				} else {
					value += data.get(fieldName);
				}
			}
		}

		if (isComma) {
			/*
			 * Execution de l'insert
			 */
			colonne += ")";
			value += ")";
			logger.debug(sql + colonne + value);
			try {
				query = connection.createStatement();

				query.executeUpdate(sql + colonne + value,
						(autoGenerateKey ? Statement.RETURN_GENERATED_KEYS : Statement.NO_GENERATED_KEYS));
				if (autoGenerateKey) {
					/*
					 * Recuperation de la cle
					 */
					rs = query.getGeneratedKeys();
					if (rs.first())
						key = rs.getInt(1);
				}
			} catch (Exception e) {
				logger.error(e);
			}
		}
		return key;
	}

	/**
	 * Lit un enregistrement a partir de sa cle
	 * 
	 * @param key
	 * @return Hashtable<String, String>
	 */
	public Hashtable<String, String> read(Object key) {
		return readByKey(keyName, key);
	}

	/**
	 * Fonction permettant de supprimer un (ou plusieurs) enregistrement(s) a
	 * partir de la colonne et de la valeur indiques
	 * 
	 * @param field
	 *            : nom de la colonne
	 * @param value
	 *            : valeur a rechercher
	 * @param isNumeric
	 *            : booleen pour indiquer si la valeur est numerique ou non
	 */
	public boolean delete(String field, String value, boolean isNumeric) {
		boolean retour = true;
		String sql = "delete from " + identProtect + tableName + identProtect + " where " + identProtect + keyName
				+ identProtect + " = ";
		if (!isNumeric) {
			sql = sql + '"' + value + '"';
		} else
			sql = sql + value;
		logger.debug(sql);
		try {
			query = connection.createStatement();
			query.executeUpdate(sql);
		} catch (Exception e) {
			logger.error(e);
			retour = false;
		}
		return retour;
	}

	/**
	 * Retourne un enregistrement ou la colonne nomKey vaut key
	 * 
	 * @param nomKey
	 * @param key
	 * @return
	 */
	public Hashtable<String, String> readByKey(String nomKey, Object key) {
		Hashtable<String, String> data = new Hashtable<String, String>();
		String value, columnName;
		String sql = "select * from " + tableName + " where " + identProtect + nomKey + identProtect + " = ";
		if (key.getClass().getSimpleName().equals("String")) {
			sql += "'" + key + "'";
		} else
			sql += key;
		logger.debug(sql);
		try {
			query = connection.createStatement();
			rs = query.executeQuery(sql);
			ResultSetMetaData rsmd = rs.getMetaData();
			if (rs.next()) {
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					value = rs.getString(i) == null ? "" : rs.getString(i);
					columnName = rsmd.getColumnName(i);
					// if (Arrays.asList(stringList).contains(columnName) &&
					// Alisma.isWindowsOs == true)
					// value = encodeIso8859(value);
					data.put(columnName, value);
				}
			}
		} catch (Exception e) {
			logger.error(e);
		}
		return data;
	}

	/**
	 * Retourne la liste des enregistrements correspondant a une cle Utilise
	 * pour retrouver les enregistrements fils d'une table
	 * 
	 * @param String
	 *            pKeyName : nom de la colonne utilisee pour la selection
	 * @param Object
	 *            key : valeur a rechercher
	 * @return List<Hashtable<String, String>> : donnees trouvees, en lignes
	 */
	public List<Hashtable<String, String>> readListFromKey(String pKeyName, Object key) {
		String sql = "select * from " + tableName;
		return readListFromKey(pKeyName, key, sql);
	}

	/**
	 * Retourne la liste des enregistrements correspondant a une cle
	 * 
	 * @param pKeyName
	 *            : nom de la cle
	 * @param key
	 *            : cle de recherche
	 * @param sqlClause
	 *            : clause sql (select et from)
	 * @return List<Hashtable<String, String>>
	 */
	public List<Hashtable<String, String>> readListFromKey(String pKeyName, Object key, String sqlClause) {
		return readListFromKey(pKeyName, key, sqlClause, "") ;
	}
	/**
	 * retourne la liste des enregistrements correspondants à une clé, avec
	 * intégration d'une clause order
	 * @param pKeyName
	 * @param key
	 * @param sqlClause
	 * @param orderClause
	 * @return
	 */
	public List<Hashtable<String, String>> readListFromKey(String pKeyName, Object key, String sqlClause, String orderClause) {
		String where = " where " + identProtect + pKeyName + identProtect + " = ";
		if (key.getClass().getSimpleName() == "String") {
			where += "'" + key + "'";
		} else
			where += key;
		return executeList(sqlClause + where +" " + orderClause);	

	}
	

	public List<Hashtable<String, String>> getListOrderBy(String colOrder) {
		String sql = "select * from " + tableName + " order by " + identProtect + colOrder + identProtect;
		return executeList(sql);
	}

	/**
	 * Execute une commande ramenant une liste de lignes
	 * 
	 * @param String
	 *            sql : commande a executer
	 * @return List<Hashtable<String, String>> : donnees trouvees
	 */
	public List<Hashtable<String, String>> executeList(String sql, boolean encode) {
		List<Hashtable<String, String>> data = new ArrayList<Hashtable<String, String>>();
		Hashtable<String, String> ligne;
		String value, columnName;
		logger.debug(sql);
		try {
			query = connection.createStatement();
			rs = query.executeQuery(sql);
			ResultSetMetaData rsmd = rs.getMetaData();
			while (rs.next()) {
				/*
				 * Traitement de chaque ligne de resultat
				 */
				ligne = new Hashtable<String, String>();
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					value = rs.getString(i) == null ? "" : rs.getString(i);
					columnName = rsmd.getColumnLabel(i);
					if (encode)
						if (Arrays.asList(stringList).contains(columnName) && Alisma.isWindowsOs == true && encode)
							value = encodeIso8859(value);
					ligne.put(columnName, value);
				}
				data.add(ligne);
			}
		} catch (Exception e) {
			logger.error(e);
		}

		return data;
	}

	public List<Hashtable<String, String>> executeList(String sql) {
		return executeList(sql, true);
	}

	/**
	 * Encode les valeurs fournies au format ISO-8859-15
	 * 
	 * @param value
	 *            : chaine a encoder
	 * @return
	 */
	public static String encodeIso8859(String value) {
		if (!value.isEmpty() && encode_iso8859) {
			String valueEncode;
			try {
				valueEncode = new String(value.getBytes("UTF-8"));
			} catch (UnsupportedEncodingException e) {
				valueEncode = value;
			}
			return valueEncode;
		} else
			return value;
	}

	/**
	 * Encode une liste complete
	 * 
	 * @param data
	 * @return
	 */
	public List<Hashtable<String, String>> encodeAll(List<Hashtable<String, String>> data) {
		if (encode_iso8859) {
		List<Hashtable<String, String>> retour = new ArrayList<Hashtable<String, String>>();
		for (Hashtable<String, String> ligne : data) {
			Hashtable<String, String> newLine = new Hashtable<String, String>();
			Enumeration<?> cles = ligne.keys();
			while (cles.hasMoreElements()) {
				String cle = (String) cles.nextElement();
				newLine.put(cle, encodeIso8859(ligne.get(cle)));
			}
			retour.add(newLine);
		}
		return retour;
		} else {
			return data;
		}
	}

	/**
	 * Retourne un enregistrement formate en xml, sans les valeurs nulles
	 * 
	 * @param data
	 *            : hashtable <String, String>
	 * @return
	 */
	public String getXml(Hashtable<String, String> data) {
		return getXml(data, false);
	}

	/**
	 * Retourne un enregistrement formate en xml
	 * 
	 * @param data
	 *            : hashtable <String, String>
	 * @param withNull
	 *            : indique si les valeurs nulles doivent figurer ou non dans la
	 *            balise xml
	 * @return
	 */
	public String getXml(Hashtable<String, String> data, boolean withNull) {
		String newLine = System.getProperty("line.separator");
		// String xml = "<"+tableName+">";
		String xml = "";
		String key;
		Enumeration<String> keys = data.keys();
		while (keys.hasMoreElements()) {
			key = keys.nextElement();
			if (!data.get(key).isEmpty() || withNull)
				xml += "<" + key + ">" + data.get(key) + "</" + key + ">" + newLine;
		}
		// xml += "</"+tableName+">"+newLine;
		return xml;
	}

	/**
	 * Teste si un enregistrement existe ou non dans la base
	 * 
	 * @param key
	 *            cle de l'enregistrement
	 * @return true|false
	 */
	public boolean isExist(Object key) {
		boolean retour = false;
		String sql = "select count(*) as nb from " + tableName + " where " + identProtect + keyName + identProtect
				+ " = ";
		if (key.getClass().getSimpleName().equals("String")) {
			sql += "'" + key + "'";
		} else
			sql += key;
		logger.debug(sql);
		try {
			query = connection.createStatement();
			rs = query.executeQuery(sql);
			if (rs.next()) {
				if (rs.getInt(1) > 0)
					retour = true;
			}
		} catch (Exception e) {
			logger.error(e);
		}
		return retour;
	}

	/**
	 * Ecrit le contenu de l'enregistrement
	 * 
	 * @param data
	 * @param key
	 * @return 0|1
	 */
	public int write(Hashtable<String, String> data, int key) {
		setData(data);
		int retour;
		if (isExist(key)) {
			if (update(key) == 1)
				retour = key;
			else
				retour = -1;
		} else
			retour = insert();
		try {
			connection.commit();
		} catch (SQLException e) {
			if (e.getErrorCode() != 0)
				logger.error(e.getErrorCode() + " " + e.getSQLState());
		}
		return retour;
	}

	/**
	 * Ecrit le contenu de l'enregistrement, dans le cas ou la cle est de type
	 * texte
	 * 
	 * @param data
	 * @param key
	 * @return String
	 */
	public String write(Hashtable<String, String> data, String key) {
		setData(data);
		String retour;
		if (isExist(key)) {
			if (update(key) == 1)
				retour = key;
			else
				retour = "-1";
		} else
			retour = insertWithKeyString();
		try {
			connection.commit();
		} catch (SQLException e) {
			if (e.getErrorCode() != 0)
				logger.error(e.getErrorCode() + " " + e.getSQLState());
		}
		return retour;
	}

	/**
	 * Realise l'insertion d'un nouvel enregistrement si la cle est de type
	 * texte
	 * 
	 * @return
	 */
	private String insertWithKeyString() {
		String sql = "insert into " + tableName;
		String colonne = " (";
		String value = " values (";
		boolean isComma = false;
		/*
		 * Traitement des champs de type texte
		 */
		for (String fieldName : stringList) {
			if (data.containsKey(fieldName)) {
				colonne += (isComma ? ", " : "");
				value += (isComma ? ", " : "");
				isComma = true;
				colonne += identProtect + fieldName + identProtect;
				if (data.get(fieldName).isEmpty()) {
					/*
					 * Traitement de la valeur nulle
					 */
					value += " NULL";
				} else {
					value += "'" + data.get(fieldName).replaceAll("'", "\\'\\'") + "'";
				}
			}
		}
		/*
		 * Traitement des champs de type numerique
		 */
		for (String fieldName : numericList) {
			if (data.containsKey(fieldName)) {
				colonne += (isComma ? ", " : "");
				value += (isComma ? ", " : "");
				isComma = true;
				colonne += identProtect + fieldName + identProtect;
				if (data.get(fieldName).isEmpty()) {
					/*
					 * Traitement de la valeur nulle
					 */
					value += " NULL";
				} else {
					value += data.get(fieldName);
				}
			}
		}

		if (isComma) {
			/*
			 * Execution de l'insert
			 */
			colonne += ")";
			value += ")";
			logger.debug(sql + colonne + value);
			try {
				query = connection.createStatement();

				query.executeUpdate(sql + colonne + value, Statement.NO_GENERATED_KEYS);
			} catch (Exception e) {
				logger.error(e);
			}
		}
		return data.get(keyName);

	}
}
