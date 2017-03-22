package import_export;

import java.awt.Desktop;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Authenticator;
import java.net.ConnectException;
import java.net.PasswordAuthentication;
import java.text.SimpleDateFormat;
import java.util.Hashtable;
import java.util.List;

import javax.swing.JOptionPane;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
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
import org.apache.log4j.Logger;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;

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
	static Logger logger = Logger.getLogger(ExportOp.class);
	Op_controle op = new Op_controle();
	Unite_releves ur = new Unite_releves();
	Lignes_op_controle taxons = new Lignes_op_controle();
	Ibmr ibmr = new Ibmr();
	Hashtable<String, String> param = new Hashtable<String, String>();
	String newLine = System.getProperty("line.separator");

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
		 * Recupere la liste des operations
		 */
		List<Hashtable<String, String>> listeop = op.getListeReleveComplet(param);
		if (!listeop.isEmpty()) {
			/*
			 * Initialisations
			 */
			String encodage = (Alisma.isWindowsOs) ? "ISO-8859-15" : "UTF-8";
			String newLine = System.getProperty("line.separator");
			String xml = "<?xml version=\"1.0\" encoding=\"" + encodage + "\" ?>" + newLine;
			/*
			 * Operations
			 */
			xml = xml + "<operations>" + newLine;
			/*
			 * Versions
			 */
			xml = xml + "<versions>" + newLine;
			xml = xml + "<softwareVersion>" + Alisma.VERSIONNUMBER + "</softwareVersion>" + newLine;
			xml = xml + "</versions>" + newLine;
			/*
			 * Traitement des opérations
			 */
			String key;
			List<Hashtable<String, String>> ldataFils;

			// if (Alisma.isWindowsOs)
			// listeop = op.encodeAll(listeop);
			for (Hashtable<String, String> operation : listeop) {
				key = operation.get("id_op_controle");
				logger.debug("operation en traitement:" + key);
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
				// if (Alisma.isWindowsOs)
				// ldataFils = ur.encodeAll(ldataFils);
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
		} else {
			JOptionPane.showMessageDialog(null, Langue.getString("noItemSelected"), Langue.getString("exportKO"),
					JOptionPane.INFORMATION_MESSAGE);
			return "";
		}
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
		String scle = (cle > 0 ? "_" + (new Integer(cle).toString()) : "");
		String fileName = "";
		/*
		 * Preparation du chemin d'export
		 */
		/*
		 * Test du dossier d'exportation
		 */
		String folderPath = Parametre.others.get("pathFolderExport");
		File folder = new File(folderPath);
		if (folder.isDirectory()) {

			fileName = folderPath + File.separator + Parametre.others.get("exportFileNamePrefix") + scle
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
		} else {
			JOptionPane.showMessageDialog(null, Langue.getString("folderNotFound"), Langue.getString("exportKO"),
					JOptionPane.INFORMATION_MESSAGE);
		}
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
		String folderPath = Parametre.others.get("pathFolderExport");
		File folder = new File(folderPath);
		if (folder.isDirectory()) {
			/*
			 * Teste si une seule fiche est exportee
			 */
			int idOp;
			String sid = "";
			try {
				if (!param.get("id_op_controle").isEmpty()) {
					idOp = new Integer(param.get("id_op_controle"));
					sid = "_" + param.get("id_op_controle");
				} else
					idOp = -1;
			} catch (NullPointerException e) {
				idOp = -1;
			}
			/*
			 * Cree le fichier XML
			 */
			String xml = generateXML();
			if (!xml.isEmpty()) {
				File xmlfile = new File(ecrireFichierExport(xml, "xml", true, idOp));
				/*
				 * Rajoute le numero de l'operation si renseignee dans les
				 * parametres
				 */
				fileNamePrefix += sid;
				/*
				 * Lancement de la generation PDF
				 */
				FopFactory fopFactory = FopFactory.newInstance();
				FOUserAgent foUserAgent = fopFactory.newFOUserAgent();

				File xsltfile = new File(Parametre.others.get("xsltfile_" + Langue.languageSelect));
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
			} else {
				JOptionPane.showMessageDialog(null, Langue.getString("folderNotFound"), Langue.getString("exportKO"),
						JOptionPane.INFORMATION_MESSAGE);
			}
		}

	}

	/**
	 * Prepare le fichier CSV pour calcul manuel de l'indicateur aupres du SEEE
	 */
	public String exportSEEE() {
		return exportSEEE(false);
	}

	public String exportSEEE(boolean silent) {
		String content = generateContentForSEEE();
		String newLine = System.getProperty("line.separator");
		if (content.length() > 0) {
			String filename = ecrireFichierExport(content, "csv", true, -1);
			if (!silent) {
				String mess = Langue.getString("exportSEEEok");
				JOptionPane.showMessageDialog(null, mess + newLine + filename, Langue.getString("exportOK"),
						JOptionPane.INFORMATION_MESSAGE);
			}
			return filename;
		} else {
			JOptionPane.showMessageDialog(null, Langue.getString("noDossiers"), Langue.getString("exportKO"),
					JOptionPane.INFORMATION_MESSAGE);
			return "";
		}
	}

	/**
	 * Genere le contenu du fichier utilise pour calculer les indicateurs aupres
	 * du SEEE
	 * 
	 * @return String : CSV contenant les infos a transmettre
	 */
	String generateContentForSEEE() {

		String tab = "\t";
		List<Hashtable<String, String>> listeop = op.getListeReleveComplet(param);
		if (!listeop.isEmpty()) {
			String key, dateOp, ligne;
			List<Hashtable<String, String>> ldataTaxons, ldataUR;
			String[] pcUR = new String[2];
			String[] taxonpc = new String[2];
			/*
			 * Preparation de la ligne d'entete
			 */
			String content = "CODE_OPERATION" + tab + "CODE_STATION" + tab + "DATE" + tab + "CODE_TAXON" + tab + "UR"
					+ tab + "POURCENTAGE_FACIES" + tab + "RESULTAT" + newLine;

			for (Hashtable<String, String> operation : listeop) {
				key = operation.get("id_op_controle");
				ldataTaxons = taxons.getListFromOp(key);
				/*
				 * Formatage de la date
				 */
				dateOp = operation.get("date_op");
				/*
				 * Recuperation des donnees concernant le point de prelevement
				 */
				ldataUR = ur.getListeFromOp(Integer.parseInt(key));
				int i = 0;
				pcUR[0] = "";
				pcUR[1] = "";

				for (Hashtable<String, String> ur : ldataUR) {
					pcUR[i] = ur.get("pc_UR");
					if (pcUR[i].equals(""))
						pcUR[i] = "0";
					i++;
				}
				/*
				 * Lecture des taxons
				 */
				for (Hashtable<String, String> taxon : ldataTaxons) {
					ligne = key + tab + operation.get("cd_station") + tab + dateOp + tab + taxon.get("id_taxon") + tab;
					taxonpc[0] = taxon.get("pc_UR1");
					taxonpc[1] = taxon.get("pc_UR2");
					for (int j = 0; j < 2; j++)
						if (taxonpc[j].equals("") || taxonpc[j].equals("null"))
							taxonpc[j] = "0";
					/*
					 * traitement de l'UR
					 */
					if (i == 1) {
						/*
						 * UR unique
						 */
						ligne += "FU" + tab + pcUR[0] + tab + taxonpc[0] + newLine;
						content += ligne;
					} else {
						/*
						 * Traitement des deux UR
						 */

						content += ligne + "F1" + tab + pcUR[0] + tab + taxonpc[0] + newLine;
						content += ligne + "F2" + tab + pcUR[1] + tab + taxonpc[1] + newLine;
					}
				}
			}

			return content;
		} else
			return "";
	}

	/**
	 * interrogation du service web du SEEE pour calculer les indicateurs
	 * 
	 * @return String resultat: contenu du calcul fourni par le SEEE
	 */
	public String getSeeeCalcul() {
		String resultat = "";
		String url, resource, indicateur, version;
		try {
			url = Parametre.seee.get("url");
		} catch (Exception e) {
			url = "http://seee.eaufrance.fr";
		}
		try {
			resource = Parametre.seee.get("resourceIbmrCalc");
		} catch (Exception e) {
			resource = "/api/calcul/";
		}
		try {
			indicateur = Parametre.seee.get("indicator");
		} catch (Exception e) {
			indicateur = "IBMR";
		}
		try {
			version = Parametre.seee.get("version");
		} catch (Exception e) {
			version = "1.1.0";
		}
		ClientConfig config = new ClientConfig();
		try {
			/*
			 * Recherche si le passage par un proxy est requis
			 */
			if (Parametre.seee.get("proxyEnabled").equals("true")) {
				logger.debug("seee : interrogation via proxy " + Parametre.seee.get("proxyHost") + ":"
						+ Parametre.seee.get("proxyPort"));
				System.setProperty("http.proxyHost", Parametre.seee.get("proxyHost"));
				System.setProperty("http.proxyPort", Parametre.seee.get("proxyPort"));
				if (!Parametre.seee.get("proxyUser").isEmpty()) {
					Authenticator.setDefault(new Authenticator() {
						@Override
						public PasswordAuthentication getPasswordAuthentication() {
							return new PasswordAuthentication(Parametre.seee.get("proxyUser"),
									Parametre.seee.get("proxyPassword").toCharArray());
						}
					});
					System.setProperty("http.proxyUser", Parametre.seee.get("proxyUser"));
					System.setProperty("http.proxyPassword", Parametre.seee.get("proxyPassword"));
				}
			}
		} catch (Exception e) {
			/*
			 * Pas d'utilisation du proxy
			 */
		}
		/*
		 * Creation du client
		 */
		try {
			Client client = ClientBuilder.newClient(config);
			String filename = exportSEEE(true);
			if (filename.length() > 0) {
				final FileDataBodyPart filePart = new FileDataBodyPart("alisma", new File(filename));
				logger.debug(filePart);
				@SuppressWarnings("resource")
				final MultiPart multipart = new FormDataMultiPart().field("indicateur", indicateur)
						.field("version", version).bodyPart(filePart);
				WebTarget target = client.target(url).register(MultiPartFeature.class).path(resource);
				final Response response = target.request().post(Entity.entity(multipart, multipart.getMediaType()));
				if (response.getStatus() <= 200) {
					resultat = response.readEntity(String.class);
					logger.debug("resultat ws:" + resultat);
				} else
					throw new Exception(response.getStatusInfo().getReasonPhrase());
			} else
				throw new Exception(Langue.getString("noDossiers"));

		} catch (ConnectException e) {
			logger.error(e.getMessage());
			JOptionPane.showMessageDialog(null,
					Langue.getString("exportSEEEerror") + newLine + Langue.getString("connectError"),
					Langue.getString("exportKO"), JOptionPane.INFORMATION_MESSAGE);
		} catch (Exception e) {
			logger.error(e.getMessage());
			JOptionPane.showMessageDialog(null, Langue.getString("exportSEEEerror") + newLine + e.getMessage(),
					Langue.getString("exportKO"), JOptionPane.INFORMATION_MESSAGE);
		}

		return resultat;
	}
}
