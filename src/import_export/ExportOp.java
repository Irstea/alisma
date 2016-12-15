package import_export;

import java.awt.Desktop;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Hashtable;
import java.util.List;

import javax.swing.JOptionPane;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;

import alisma.Alisma;
import database.Ibmr;
import database.Lignes_op_controle;
import database.Op_controle;
import database.Unite_releves;
import utils.Langue;
import utils.Parametre;

/**
 * Exporte les operations
 * 
 * @author quinton
 *
 */
public class ExportOp {
	Op_controle op = new Op_controle();
	Unite_releves ur = new Unite_releves();
	Lignes_op_controle taxons = new Lignes_op_controle();
	Ibmr ibmr = new Ibmr();
	Hashtable<String, String> param = new Hashtable<String, String>();

	/**
	 * Assigne les parametres de recherche
	 * 
	 * @param pParam
	 */
	public void setSearchParam(Hashtable<String, String> pParam) {
		param = pParam;
	}

	/**
	 * Exporte les donnees au format XML
	 */
	public void exportXML() {
		ecrireFichierExport(generateXML(), "xml");
	}

	/**
	 * Genere le contenu du fichier XML
	 * 
	 * @return contenu XML
	 */
	private String generateXML() {
		/*
		 * Initialisations
		 */
		String encodage = (Alisma.isWindowsOs) ? "ISO-8859-15" : "UTF-8";
		String newLine = System.getProperty("line.separator");
		String xml = "<?xml version=\"1.0\" encoding=\"" + encodage + "\" ?>" + newLine + "<operations>" + newLine;
		String key;
		List<Hashtable<String, String>> ldataFils;
		/*
		 * Recupere la liste des operations
		 */
		List<Hashtable<String, String>> listeop = op.getListeReleveComplet(param);
//		if (Alisma.isWindowsOs)
//			listeop = op.encodeAll(listeop);
		for (Hashtable<String, String> operation : listeop) {
			key = operation.get("id_op_controle");
			xml += "<operation>" + newLine;
			/*
			 * ajout des donnees de l'operation
			 */
			xml += op.getXml(operation, true);
			/*
			 * Ajout des donnees concernant l'ibmr
			 */
			xml += ibmr.getXml(ibmr.lireComplet(key));
			/*
			 * traitement des unites de releve
			 */
			
			ldataFils = ur.getListeFromOp(Integer.parseInt(key));
//			if (Alisma.isWindowsOs)
//				ldataFils = ur.encodeAll(ldataFils);
			for (Hashtable<String, String> dataFils : ldataFils) {
				xml += "<unite_releve>" + ur.getXml(dataFils, true) + "</unite_releve>" + newLine;
			}
			/*
			 * Traitement des taxons
			 */
			ldataFils = taxons.getListFromOp(key);
			if (!ldataFils.isEmpty()) {
				for (Hashtable<String, String> dataFils : ldataFils) {
					xml += "<taxon>" + taxons.getXml(dataFils, true) + "</taxon>" + newLine;
				}
			} else
				xml += "<taxon/>" + newLine;
			/*
			 * Fin de traitement de l'operation
			 */
			xml += "</operation>" + newLine;
		}
		xml += "</operations>";
		return xml;
	}

	/**
	 * Fonction permettant d'enregistrer une chaine de texte dans un fichier
	 * 
	 * @param contenu
	 *            texte a sauvegarder
	 * @param suffixe
	 *            suffixe du fichier genere
	 * @return fileName nom du fichier genere
	 */

	String ecrireFichierExport(String contenu, String suffixe) {
		return ecrireFichierExport(contenu, suffixe, false, -1);
	}

	/**
	 * Fonction permettant d'enregistrer une chaine de texte dans un fichier
	 * 
	 * @param contenu
	 *            texte a sauvegarder
	 * @param suffixe
	 *            suffixe du fichier genere
	 * @param silent
	 *            false : affichage des messages, true : mode silencieux
	 */
	String ecrireFichierExport(String contenu, String suffixe, boolean silent, int cle) {
		File f = null;
		String scle = (cle > 0 ? "_"+ (new Integer(cle).toString()) : "" ) ;
		/*
		 * Preparation du chemin d'export
		 */
		String fileName = Parametre.others.get("pathFolderExport") + File.separator
				+ Parametre.others.get("exportFileNamePrefix")
				+ scle
				+ new SimpleDateFormat("_yyyyMMdd").format(new java.util.Date()) + "." + suffixe;
		f = new File(fileName);

		int rep = JOptionPane.YES_OPTION;
		/*
		 * Confirmation de l'ecrasement
		 */
		if (f.exists() && !silent) {
			rep = JOptionPane.showOptionDialog(null, Langue.getString("confirmFileEcrasement"), null,
					JOptionPane.WARNING_MESSAGE, JOptionPane.YES_NO_OPTION, null, null, JOptionPane.NO_OPTION);
		}
		if (rep == JOptionPane.YES_OPTION) {
			try {
				BufferedWriter w = new BufferedWriter(new FileWriter(f));
				w.write(contenu);
				w.close();
				if (!silent) {
					String mess = "<html>" + Langue.getString("exportOKdetail") + "<ul>";
					mess += "<li>" + f.getAbsolutePath() + "</li>";
					mess += "</ul></html>";
					JOptionPane.showMessageDialog(null, mess, Langue.getString("exportOK"),
							JOptionPane.INFORMATION_MESSAGE);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else
			JOptionPane.showMessageDialog(null, Langue.getString("exportKOdetail"), Langue.getString("exportKO"),
					JOptionPane.INFORMATION_MESSAGE);
		return fileName;
	}

	/**
	 * Genere un fichier PDF a partir des parametres fournis a la classe Cree
	 * dans la premiere etape le fichier XML
	 */
	public void exportPdf() {
		exportPdf(Parametre.others.get("exportFileNamePrefix"));
	}

	/**
	 * Genere un fichier PDF a partir des parametres fournis a la classe
	 * 
	 * @param fileNamePrefix
	 *            racine du nom du fichier exporte
	 */
	public void exportPdf(String fileNamePrefix) {
		/*
		 * Teste si une seule fiche est exportee
		 */
		int idOp ;
		String sid = "";
		try {
			if (!param.get("id_op_controle").isEmpty()) {
				idOp = new Integer(param.get("id_op_controle"));
				sid = "_"+param.get("id_op_controle");
			} else 
				idOp = -1;
		}catch (NullPointerException e) {
			idOp = -1;
		}
		/*
		 * Cree le fichier XML
		 */
		File xmlfile = new File(ecrireFichierExport(generateXML(), "xml", true, idOp));
		/*
		 * Rajoute le numero de l'operation si renseignee dans les parametres
		 */
		fileNamePrefix += sid;
		/*
		 * Lancement de la generation PDF
		 */
		FopFactory fopFactory = FopFactory.newInstance();
		FOUserAgent foUserAgent = fopFactory.newFOUserAgent();
		
		File xsltfile = new File(Parametre.others.get("xsltfile_"+Langue.languageSelect));
		String fileNamePdf = Parametre.others.get("pathFolderExport") + File.separator + fileNamePrefix
				+ new SimpleDateFormat("_yyyyMMdd").format(new java.util.Date()) + ".pdf";
		File pdffile = new File(fileNamePdf);
		OutputStream out;
		try {
			out = new BufferedOutputStream(new FileOutputStream(pdffile));
			try {
				TransformerFactory factory = TransformerFactory.newInstance();
				Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, out);

				Source src = new StreamSource(xmlfile);

				Transformer transformer = factory.newTransformer(new StreamSource(xsltfile));
				Result res = new SAXResult(fop.getDefaultHandler());
				transformer.transform(src, res);

			} finally {
				out.close();
				Desktop.getDesktop().open(pdffile);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
