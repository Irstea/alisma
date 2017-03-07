package import_export;

import java.awt.Component;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.log4j.Logger;

import database.Ibmr;
import database.Op_controle;
import utils.Langue;

public class ImportCSV {
	static Logger logger = Logger.getLogger(ImportCSV.class);

	/**
	 * Selectionne le fichier a importer
	 * 
	 * @param Component
	 *            parentComp : composant d'affichage du parent
	 * @return String
	 */
	public String selectFile(Component parentComp) {
		JFileChooser jfc = new JFileChooser();
		jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		jfc.addChoosableFileFilter(new FileNameExtensionFilter(Langue.getString("csvfile"), "csv"));
		int retour = jfc.showOpenDialog(parentComp);
		if (retour == JFileChooser.APPROVE_OPTION) {
			return jfc.getSelectedFile().getAbsolutePath();
		} else
			return "";
	}

	/**
	 * Declenche la lecture et l'ecriture du resultat des calculs SEEE
	 * contenu dans le fichier fourni
	 * @param String filename
	 * @return true|false
	 */
	public boolean importSeeeFromFilename (String filename) {
		return importSEEE(getContentCsv(filename));
	}
	/**
	 * Lecture et ecriture du resultat des calculs SEEE
	 * @param List<String> lines
	 * @return true|false
	 */
	public boolean importSEEE(List<String> lines) {
		boolean retour = false;
		String version, calculDate;
		if (!lines.isEmpty()) {
			try {
				Ibmr ibmr = new Ibmr();
				Op_controle op = new Op_controle();
				Hashtable<String, Hashtable<String, String>> content = new Hashtable<String, Hashtable<String, String>>();
				/*
				 * Lecture de la premiere ligne, qui contient les informations
				 * generales
				 */
				String line = lines.get(0);
				logger.debug("Import SEEE - line 0:" + line);
				String[] lineHeader = line.split(";");
				version = lineHeader[1];
				calculDate = lineHeader[2];
				/*
				 * Traitement des lignes contenant les resultats La seconde
				 * ligne contient les champs d'entete, et est ignoree
				 */
				for (int i = 2; i < lines.size(); i++) {
					line = lines.get(i);
					logger.debug("Import SEEE - line "+i+":" + line);
					Hashtable<String, String> lc = new Hashtable<String, String>();
					String[] lineContent = line.split(";");
					if (content.containsKey(lineContent[0])) {
						lc = content.get(lineContent[0]);
					}
					switch (Integer.parseInt(lineContent[3])) {
					case 7974:
						lc.put("seee_nbtaxon_contrib", lineContent[5]);
						break;
					case 2928:
						lc.put("seee_ibmr", lineContent[5]);
						break;
					case 8063:
						lc.put("seee_robustesse_value", lineContent[5]);
						/*
						 * Recuperation du taxon robustesse
						 */
						String[] taxonrob = lineContent[6].split("'");
						lc.put("seee_taxon_robustesse", taxonrob[1]);
						break;
					}
					content.put(lineContent[0], lc);
				}
				/*
				 * Traitement de chaque releve
				 */
				Set<String> keys = content.keySet();
				for (String key : keys) {
					Hashtable<String, String> l = content.get(key);
					/*
					 * Ajout des infos generales sur le calcul seee
					 */
					l.put("seee_version", version);
					l.put("seee_date", calculDate);
					int keynum = Integer.parseInt(key);
					ibmr.ecrire(l, keynum);
					/*
					 * Mise a jour du statut
					 */
					Hashtable<String, String> lop = new Hashtable<String, String>();
					lop.put("id_statut", "2");
					op.ecrireSimple(lop, keynum);	
					retour = true;
				}
				JOptionPane.showMessageDialog(null, Langue.getString("importSEEEok"), Langue.getString("exportKO"),
						JOptionPane.INFORMATION_MESSAGE);

			} catch (Exception e) {
				logger.error(e.getMessage());
			}

		}
		return retour;
	}

	/**
	 * Retourne la liste des lignes dans un arrayList
	 * 
	 * @param filename
	 * @return
	 */
	public List<String> getContentCsv(String filename) {
		List<String> lines = new ArrayList<String>();
		if (!filename.isEmpty()) {
			/*
			 * Lecture du fichier
			 */
			try {
				File f = new File(filename);
				/*
				 * Lecture des lignes du fichier
				 */

				FileReader fr = new FileReader(f);
				BufferedReader br = new BufferedReader(fr);
				for (String line = br.readLine(); line != null; line = br.readLine()) {
					lines.add(line);
				}
				br.close();
				fr.close();

			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
		return (ArrayList<String>) lines;
	}
	/**
	 * ecriture du resultat du calcul SEEE a partir d'une chaine
	 * contenant les donnees recuperees
	 * @param content
	 * @return
	 */
	public boolean importSeeeFromFileContent(String content) {
		String[] slines =  content.split("\\r?\\n");
		logger.debug("nb de lignes dans le fichier SEEE:" + slines.length);
		return importSEEE ( Arrays.asList(slines));
	}
}
