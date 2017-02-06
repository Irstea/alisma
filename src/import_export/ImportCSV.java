package import_export;

import java.awt.Component;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import utils.Langue;

public class ImportCSV {

	/**
	 * Selectionne le fichier a importer
	 * @param Component parentComp : composant d'affichage du parent
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
	
	public boolean importSEEE(String filename) {
		boolean retour = false;
		String version, calculDate;
		List<String> lines = getContentCsv(filename);
		if (! lines.isEmpty()) {
			Hashtable <String, Hashtable <String, String>> content = new Hashtable <String, Hashtable <String, String>>();
			/*
			 * Lecture de la premiere ligne, qui contient les informations generales
			 */
			String line = lines.get(0);
			String []lineHeader = line.split(";");
			version = lineHeader[1];
			calculDate = lineHeader[2];
			/*
			 * Traitement des lignes contenant les resultats
			 * La seconde ligne contient les champs d'entete, et est ignoree
			 */
			for (int i = 2 ; i < lines.size(); i++) {
				line = lines.get(i);
				Hashtable<String, String> lc = new Hashtable<String,String>();
				String [] lineContent = line.split(";");
				if (content.containsKey(lineContent[0])) {
					lc = content.get(lineContent[0]);
				}
				switch (Integer.parseInt(lineContent[3])) {
				case 7974:
					lc.put("seee_nbtaxon_contrib", lineContent[5]);
					break;
				case 2928:
					lc.put("seee_ibmr",lineContent[5]);
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
		}
		return retour;
	}
	/**
	 * Retourne la liste des lignes dans un arrayList
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
			}		
		}
		return (ArrayList<String>) lines;
	}
}
