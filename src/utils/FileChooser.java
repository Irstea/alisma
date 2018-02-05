package utils;

import java.awt.Component;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Classe permettant de selectionner un fichier dans l'ordinateur
 * par defaut, le filtre accepte les fichiers csv ou txt
 * @author quinton
 *
 */
public class FileChooser {
	private JFileChooser chooser = new JFileChooser();
	private  FileNameExtensionFilter filter = new FileNameExtensionFilter(
        Langue.getString("csvfile"), "csv");
	public FileChooser() {
		
	}
	/**
	 * Retourne le fichier choisi
	 * @param parent
	 * @param filtre
	 * @return
	 */
	public String getFile(Component parent, FileNameExtensionFilter filtre, String title) {
	 String filepath = "";
	 if (title.isEmpty()) {
		 title = Langue.getString("boutonImporter");
	 }
	 chooser.setFileFilter(filtre);
	 chooser.setDialogTitle(title);
	 int returnVal = chooser.showOpenDialog (parent);
	    if(returnVal == JFileChooser.APPROVE_OPTION) {
	    	filepath = chooser.getSelectedFile().getPath();
	    }
	 return filepath;
	}
	
	public String getFile(Component parent, String title) {
		return getFile(parent, filter, title);
	}
	
   
}
