/**
 * 
 */
package import_export;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import utils.Langue;
import utils.Parametre;

/**
 * @author quinton
 * 
 *         Classe permettant de tester la date de la sauvegarde puis de realiser
 *         la sauvegarde le cas echeant
 */
public class Backup {
	static Logger logger = Logger.getLogger(Backup.class);
	SimpleDateFormat dateFormat;

	public Backup() {
		String df = Parametre.getValue("database","backupDateFormat");
		if (df.isEmpty())
			df = "dd/MM/yyyy";
		dateFormat = new SimpleDateFormat(df);
	}

	public boolean isBackupNecessary() {
		boolean retour = false;
		try {
			String chemin = Parametre.getValue("database","pathFileDateSave");
			if (!Parametre.getValue("database","backupDelay").equals("-1")) {
				File f = new File(chemin);
				if (!f.exists()) {
					retour = true;
				} else {
					Date date_Limite = limiteDate();
					Date date_Sauve = getSauveDate(chemin);
					if (date_Sauve.compareTo(date_Limite) == -1) {
						retour = true;
					}
				}
			}
		} catch (Exception e) {
			logger.error("Backup", e);
		}
		return retour;
	}

	/**
	 * Ecriture de la date de sauvegarde dans le fichier "chemin"
	 * 
	 * @param chemin
	 */
	public void writeDate(String chemin) {
		String texteDate = "";
		try {
			texteDate = dateFormat.format(new Date());
			FileWriter fw = new FileWriter(chemin);
			BufferedWriter bw = new BufferedWriter(fw);
			PrintWriter fichierSortie = new PrintWriter(bw);
			fichierSortie.println(texteDate);
			fichierSortie.close();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, Langue.getString("errorWriteBackupDate"),
					Langue.getString("backup"), JOptionPane.ERROR_MESSAGE);
			logger.error( e.getMessage());
		}
	}

	// recuperation de la date d'aujourd'hui - le delai indique dans le fichier
	// de parametres
	private Date limiteDate() {
		GregorianCalendar aujourdhui = new GregorianCalendar();
		try {
			Integer delay = new Integer(Parametre.getValue("database","backupDelay"));
			aujourdhui.add(Calendar.DATE, -delay);
		} catch (Exception e) {
			logger.error("Backup", e);
		}
		;
		Date dateLimite = aujourdhui.getTime();
		return dateLimite;
	}

	/**
	 * Retourne la date sauvegardee dans le fichier "chemin"
	 * 
	 * @param chemin
	 * @return Date
	 */
	private Date getSauveDate(String chemin) {

		String chaine = "";
		// lecture du fichier texte
		try {
			InputStream ips = new FileInputStream(chemin);
			InputStreamReader ipsr = new InputStreamReader(ips);
			BufferedReader br = new BufferedReader(ipsr);
			String ligne = "";
			while ((ligne = br.readLine()) != null) {
				chaine += ligne;
			}
			br.close();
			return dateFormat.parse(chaine);
		} catch (Exception e) {
			return new Date();
		}
	}

	public void sauveBase() {
		JFileChooser fc = new JFileChooser();
		fc.setMultiSelectionEnabled(false);
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		File f = null;
		String nl = System.getProperty("line.separator");
		// JOptionPane jop = new JOptionPane();

		if (JOptionPane.showConfirmDialog(null, Langue.getString("confirmBackup")) == JOptionPane.YES_OPTION) {
			/*
			 * Test de l'existence du chemin de sauvegarde
			 */
			String folder = Parametre.getValue("database","pathFolderDataSave");
			File folderFile = new File(folder);
			if (!folderFile.isDirectory()) {
				JOptionPane.showMessageDialog(null, Langue.getString("folderBackupNotFound") + nl + folder,
						Langue.getString("backup"), JOptionPane.ERROR_MESSAGE);
			} else {
				/*
				 * Preparation du fichier de sauvegarde
				 */
				String path = folder + File.separator + Parametre.getValue("database","backupFileNamePrefix")
						+ new SimpleDateFormat("_yyyyMMdd" + "_HHmmss").format(new java.util.Date()) + ".sql";
				f = new File(path);

				int rep = JOptionPane.YES_OPTION;
				if (f.exists()) {
					rep = JOptionPane.showOptionDialog(null, Langue.getString("confirmFileEcrasement"), null,
							JOptionPane.WARNING_MESSAGE, JOptionPane.YES_NO_OPTION, null, null, JOptionPane.NO_OPTION);
				}
				if (rep == JOptionPane.YES_OPTION) {
					/*
					 * Test de l'existence du programme de sauvegarde
					 */
					String backupPg = Parametre.getValue("database","backupProgram");
					File backupPgFile = new File(backupPg);
					if (!backupPgFile.exists()) {
						JOptionPane.showMessageDialog(null, Langue.getString("backupSoftwareNotFound") + nl + backupPg,
								Langue.getString("backup"), JOptionPane.ERROR_MESSAGE);
					} else {
						String mysql_cmd = backupPg + " -h " + Parametre.getValue("database","server") + " -u "
								+ Parametre.getValue("database","dbuser");
						if (!Parametre.getValue("database","dbpass").isEmpty())
							mysql_cmd += " -p" + Parametre.getValue("database","dbpass");
						mysql_cmd += " --opt " + Parametre.getValue("database","dbname");
						logger.info("mysql_cmd");
						try {
							Process p = Runtime.getRuntime().exec(mysql_cmd);
							BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
							BufferedWriter w = new BufferedWriter(new FileWriter(f));
							String s;
							while ((s = r.readLine()) != null) {
								w.write(s);
								w.newLine();
							}
							p.destroy();
							r.close();
							w.close();
							writeDate(Parametre.getValue("database","pathFileDateSave"));
							String mess = "<html>" + Langue.getString("export") + "<ul>";
							mess += "<li>" + f.getAbsolutePath() + "</li>";
							mess += "</ul></html>";
							JOptionPane.showMessageDialog(null, mess, Langue.getString("exportOK"),
									JOptionPane.INFORMATION_MESSAGE);
						} catch (IOException e) {
							logger.error(e);
							JOptionPane.showMessageDialog(null, e.getMessage(), Langue.getString("backup"),
									JOptionPane.ERROR_MESSAGE);
						}
					}
				}
			}
		}
	}
}
