/**
 * 
 */
package alisma;

import import_export.Backup;
import import_export.ExportOp;
import import_export.ImportCSV;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.log4j.Logger;

import database.CalculIBMR;
import database.Cours_Eau;
import database.Op_controle;
import database.Stations;
import database.Taxon;
import reference.CoursEauChange;
import reference.CoursEauList;
import reference.StationChange;
import reference.StationList;
import reference.TaxonList;
import reference.TaxonPersoChange;
import reference.TaxonPersoList;
import releve.ReleveListe;
import releve.Releve_frame;
import utils.ConnexionDatabase;
import utils.Exportable;
import utils.FileChooser;
import utils.JFrameAlisma;
import utils.Langue;
import utils.ObservableExtended;
import utils.Parametre;

/**
 * @author quinton
 * 
 *         Genere l'affichage de la premiere fenetre
 */
public class Controleur implements Observer {
	MainFenetre mainFenetre = new MainFenetre();
	Hashtable<String, JFrameAlisma> fenetres = new Hashtable<String, JFrameAlisma>();
	List<Releve_frame> releves = new ArrayList<Releve_frame>();
	Langue langue;
	Releve_frame srf;
	ReleveListe releveListe;
	ExportOp exportOp;
	CalculIBMR calculIbmr;
	CoursEauList coursEauList;
	StationList stationList;
	TaxonList taxonList;
	TaxonPersoList taxonPersoList;
	Op_controle opControle;
	Stations station;
	Taxon taxon;
	Cours_Eau coursEau;
	static Logger logger = Logger.getLogger(Controleur.class);
	ObservableExtended obs;

	public Controleur() {
		/*
		 * Lancement de l'ecoute des evenements de la fenetre
		 */
		mainFenetre.fenetre.setPosition("TL");
		mainFenetre.addObserver(this);
		/*
		 * Initialisation de la connexion a la base de donnees
		 */
		new ConnexionDatabase();
		if (ConnexionDatabase.stateConnect == false) {
			JOptionPane.showMessageDialog(null, ConnexionDatabase.messageConnect, Langue.getString("aPropos"),
					JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
	}

	/**
	 * Traitement des commandes transmises au controleur
	 */
	public void update(Observable observable, Object pCommand) {
		String commande = (String) pCommand;
		logger.info(commande);
		boolean openFenetre = false;
		Exportable e;
		Hashtable<String, String> param;
		switch (commande) {
		/*
		 * Fermeture de l'application
		 */
		case "closeAll":
		case "quitter":
			close();
			break;
		case "taxonReference":
			/*
			 * Affichage des taxons de reference
			 */
			if (taxonList != null) {
				if (taxonList.fenetre.isVisible() == false) {
					openFenetre = true;
				}
			} else {
				openFenetre = true;
			}
			if (openFenetre) {
				taxonList = new TaxonList();
			}
			fenetres.put("taxonList", taxonList.fenetre);
			taxonList.fenetre.setPosition("TR");
			taxonList.addObserver(this);
			break;
		case "taxonPerso":
			/*
			 * Affichage des taxons personnels
			 */
			if (taxonPersoList != null) {
				if (taxonPersoList.fenetre.isVisible() == false) {
					openFenetre = true;
				}
			} else {
				openFenetre = true;
			}
			if (openFenetre) {
				taxonPersoList = new TaxonPersoList();
			}
			fenetres.put("taxonPersoList", taxonPersoList.fenetre);
			taxonPersoList.fenetre.setPosition("TR");
			taxonPersoList.addObserver(this);
			break;

		case "taxonPersoChange":
			/*
			 * Ouverture de la fenetre de modification d'un taxon personnel
			 */
			if (fenetres.get("taxonPersoChange") != null) {
				if (((JFrameAlisma) fenetres.get("taxonPersoChange")).isVisible() == false)
					openFenetre = true;
			} else {
				openFenetre = true;
			}
			if (openFenetre) {
				fenetres.put("taxonPersoChange", new TaxonPersoChange());
			}
			logger.debug("taxonPersoChange - openFenetre : " + openFenetre);
			fenetres.get("taxonPersoChange").setPosition("C");
			obs = (ObservableExtended) observable;
			logger.debug(obs.getClass().getName());
			logger.debug("getValue(cd_taxon_perso)" + obs.getValue("cd_taxon_perso"));
			fenetres.get("taxonPersoChange").setKey(obs.getValue("cd_taxon_perso"));
			fenetres.get("taxonPersoChange").addObserver(this);
			break;
		case "taxonPersoHasChanged":
			/*
			 * Declenchement des actions pour informer qu'un taxon a ete ecrit
			 */
			if (taxonPersoList != null) {
				taxonPersoList.dataRefresh();
			}
			break;

		case "cours_eau":
			/*
			 * Affichage des cours d'eau
			 */
			if (coursEauList != null) {
				if (coursEauList.fenetre.isVisible() == false) {
					openFenetre = true;
				}
			} else {
				openFenetre = true;
			}
			if (openFenetre) {
				coursEauList = new CoursEauList();
			}
			fenetres.put("CoursEauList", coursEauList.fenetre);
			coursEauList.fenetre.setPosition("TR");
			coursEauList.addObserver(this);
			break;

		case "coursEauHasChanged":
			/*
			 * Declenchement des actions pour informer qu'un cours d'eau a ete ecrit
			 */
			if (coursEauList != null) {
				coursEauList.dataRefresh();
			}
			break;

		case "coursEauChange":
			/*
			 * Ouverture de la fenetre de modification d'un cours d'eau
			 */
			if (fenetres.get("CoursEauChange") != null) {
				if (((JFrameAlisma) fenetres.get("CoursEauChange")).isVisible() == false) {
					openFenetre = true;
				}
			} else {
				openFenetre = true;
			}
			if (openFenetre) {
				fenetres.put("CoursEauChange", new CoursEauChange());
			}
			fenetres.get("CoursEauChange").setPosition("C");
			obs = (ObservableExtended) observable;
			logger.debug(obs.getClass().getName());
			logger.debug("getValue(id_cours_eau)" + obs.getValue("id_cours_eau"));
			fenetres.get("CoursEauChange").setKey(obs.getValue("id_cours_eau"));
			fenetres.get("CoursEauChange").addObserver(this);
			break;

		case "station":
			/*
			 * Affichage des stations
			 */
			/*
			 * Affichage des cours d'eau
			 */
			if (stationList != null) {
				if (stationList.fenetre.isVisible() == false) {
					openFenetre = true;
				}
			} else {
				openFenetre = true;
			}
			if (openFenetre) {
				stationList = new StationList();
			}
			fenetres.put("stationList", stationList.fenetre);
			stationList.fenetre.setPosition("TR");
			stationList.addObserver(this);
			break;

		case "stationChange":
			/*
			 * Ouverture de la fenetre de modification d'une station
			 */
			if (fenetres.get("StationChange") != null) {
				if (((JFrameAlisma) fenetres.get("StationChange")).isVisible() == false) {
					openFenetre = true;
				}
			} else {
				openFenetre = true;
			}
			if (openFenetre) {
				fenetres.put("StationChange", new StationChange());
			}
			fenetres.get("StationChange").setPosition("C");
			obs = (ObservableExtended) observable;
			logger.debug(obs.getClass().getName());
			logger.debug("getValue(id_station)" + obs.getValue("id_station"));
			fenetres.get("StationChange").setKey(obs.getValue("id_station"));
			fenetres.get("StationChange").addObserver(this);
			break;

		case "stationHasChanged":
			/*
			 * Declenchement des actions pour informer qu'une station a ete ecrite
			 */
			if (stationList != null) {
				stationList.dataRefresh();
			}
			break;

		case "opConsult":
			/*
			 * Affichage de la fenetre de recherche des releves
			 */
			if (releveListe != null) {
				releveListe.fenetre.draw();
				releveListe.fenetre.toFront();
				releveListe.fenetre.setState(JFrame.NORMAL);
			} else {
				releveListe = new ReleveListe();
				releveListe.addObserver(this);
				releveListe.fenetre.setPosition("TL");
			}
			break;

		case "opNouveau":
			/*
			 * Creation d'un nouveau releve
			 */
			srf = new Releve_frame(-1);
			srf.addObserver(this);
			releves.add(srf);
			srf.fenetre.setPosition("TL");
			srf.display();
			break;

		case "opModif":
			/*
			 * Modification d'un releve
			 */
			obs = (ObservableExtended) observable;
			int id = (int) obs.getValue("getId");
			srf = new Releve_frame(id);
			srf.addObserver(this);
			releves.add(srf);
			srf.fenetre.setPosition("TL");
			srf.display();
			break;

		case "releveHasChanged":
			/*
			 * Traitement a realiser lorsqu'un releve a ete modifie
			 */
			if (releveListe != null) {
				releveListe.dataRefresh();
			}
			break;

		case "dbSave":
			/*
			 * Lancement de la sauvegarde de la base de donnees
			 */
			new Backup().sauveBase();
			break;

		case "exportXml":
			e = (Exportable) observable;
			param = e.getParam();
			exportXml(param);
			break;

		case "exportPDF":
			e = (Exportable) observable;
			param = e.getParam();
			exportPDF(param);
			break;

		case "exportSEEE":
			e = (Exportable) observable;
			param = e.getParam();
			exportSEEE(param);
			break;

		case "importSEEE":
			ObservableExtended oe = (ObservableExtended) observable;
			importSEEE(oe.getValue("filename"));
			break;

		case "calculSEEEsw":
			e = (Exportable) observable;
			param = e.getParam();
			calculSEEEsw(param);
			break;

		case "recalculer":
			e = (Exportable) observable;
			param = e.getParam();
			recalculer(param);
			break;

		case "aPropos":
			/*
			 * Affichage de la boite de dialogue "a propos"
			 */
			apropos();
			break;

		case "anglais":
			/*
			 * Basculement des libelles en anglais
			 */
			langue = new Langue();
			langue.setLanguage("en_US");
			resetLibelle();
			break;

		case "francais":
			/*
			 * Basculement des libelles en francais
			 */
			langue = new Langue();
			langue.setLanguage("fr_FR");
			resetLibelle();
			break;

		case "importXml":
			importXml();
			break;

		case "importTaxon":
			importTaxon();
			break;

		case "importParam":
			importParam();
			break;

		case "importCourseau":
			importCourseau();
			break;

		case "importStation":
			importStation();
			break;

		}
	}

	private void importSEEE(Object object) {
		try {
			ImportCSV csv = new ImportCSV();
			csv.importSeeeFromFilename(object.toString());
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), Langue.getString("exportKO"),
					JOptionPane.INFORMATION_MESSAGE);
		}
	}

	private void importTaxon() {
		FileChooser fc = new FileChooser();
		String filename = fc.getFile(null);
		if (!filename.isEmpty()) {
			logger.debug(filename);
			if (taxon == null) {
				taxon = new Taxon();
			}
			boolean result = taxon.importFromCsv(filename, ';');
			if (result) {
				JOptionPane.showMessageDialog(null, Langue.getString("importOk"));
			} else {
				JOptionPane.showMessageDialog(null,
						Langue.getString("importKo") + System.getProperty("line.separator") + taxon.getMessage());
			}
		}
	}

	private void importParam() {
		FileChooser fc = new FileChooser();
		String filename = fc.getFile(null);
		if (!filename.isEmpty()) {
			if (taxon == null) {
				taxon = new Taxon();
			}
			logger.debug(filename);
			boolean result = taxon.importParamFromCsv(filename, ';');
			if (result) {
				JOptionPane.showMessageDialog(null, Langue.getString("importOk"));
			} else {
				JOptionPane.showMessageDialog(null,
						Langue.getString("importKo") + System.getProperty("line.separator") + taxon.getMessage());
			}
		}
	}

	private void importCourseau() {
		FileChooser fc = new FileChooser();
		String filename = fc.getFile(null);
		if (!filename.isEmpty()) {
			logger.debug(filename);
			if (coursEau == null) {
				coursEau = new Cours_Eau();
			}
			boolean result = coursEau.importFromCsv(filename, ';');
			if (result) {
				JOptionPane.showMessageDialog(null, Langue.getString("importOk"));
			} else {
				JOptionPane.showMessageDialog(null,
						Langue.getString("importKo") + System.getProperty("line.separator") + coursEau.getMessage());
			}
		}
	}

	private void importStation() {
		FileChooser fc = new FileChooser();
		String filename = fc.getFile(null);
		if (!filename.isEmpty()) {
			logger.debug(filename);
			if (station == null) {
				station = new Stations();
			}
			boolean result = station.importFromCsv(filename, ';');
			if (result) {
				JOptionPane.showMessageDialog(null, Langue.getString("importOk"));
			} else {
				JOptionPane.showMessageDialog(null,
						Langue.getString("importKo") + System.getProperty("line.separator") + station.getMessage());
			}
		}
	}

	private void calculSEEEsw(Hashtable<String, String> param) {
		if (exportOp == null)
			exportOp = new ExportOp();
		ImportCSV csv = new ImportCSV();
		exportOp.setSearchParam(param);
		try {
			csv.importSeeeFromFileContent(exportOp.getSeeeCalcul());
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), Langue.getString("calculSEEE"),
					JOptionPane.INFORMATION_MESSAGE);
		}

	}

	private void exportSEEE(Hashtable<String, String> param) {
		if (exportOp == null) {
			exportOp = new ExportOp();
		}
		exportOp.setSearchParam(param);
		try {
			exportOp.exportSEEE();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), Langue.getString("exportKO"),
					JOptionPane.INFORMATION_MESSAGE);
		}
	}

	/**
	 * Recalcul de l'ibmr
	 * @param param
	 */
	private void recalculer(Hashtable<String, String> param) {
		if (calculIbmr == null) {
			calculIbmr = new CalculIBMR();
		}
		calculIbmr.recalculListeFromParam(param);

	}

	/**
	 * declenche l'export au format PDF
	 * 
	 * @param param
	 */
	private void exportPDF(Hashtable<String, String> pParam) {
		if (exportOp == null)
			exportOp = new ExportOp();
		exportOp.setSearchParam(pParam);
		exportOp.exportPdf();
	}

	/**
	 * Generation de l'export XML
	 * 
	 * @param param
	 */
	private void exportXml(Hashtable<String, String> pParam) {
		if (exportOp == null)
			exportOp = new ExportOp();
		exportOp.setSearchParam(pParam);
		exportOp.exportXML();
	}

	/**
	 * Declenchement de l'import de dossiers externes
	 */
	private void importXml() {
		FileChooser fc = new FileChooser();
		String filename = fc.getFile(null, new FileNameExtensionFilter("Fichiers XML", "xml"));
		if (!filename.isEmpty()) {
			logger.debug(filename);
			if (opControle == null) {
				opControle = new Op_controle();
			}
			boolean result = opControle.importFromXml(filename);
			if (result) {
				JOptionPane.showMessageDialog(null, Langue.getString("importOk"));
			} else {
				JOptionPane.showMessageDialog(null, Langue.getString("importKo"));
			}
		}

	}

	public void close() {
		/*
		 * Verification que les fenetres filles soient fermees
		 */
		boolean bFenetreOuverte = false, quitter = true;
		Set<String> sfenetres = fenetres.keySet();
		/*
		 * Traitement des fenetres de type JFrameAlisma
		 */
		JFrameAlisma jfaFille;
		Iterator<String> iter = sfenetres.iterator();
		while (iter.hasNext()) {
			jfaFille = fenetres.get(iter.next());
			if (jfaFille.isShowing()) {
				bFenetreOuverte = true;
			}
		}
		/*
		 * Traitement des fenetres de saisie des releves
		 */
		Iterator<Releve_frame> iterReleve = releves.iterator();
		while (iterReleve.hasNext()) {
			srf = iterReleve.next();
			if (srf.fenetre.isShowing()) {
				bFenetreOuverte = true;
			}
		}

		/*
		 * Fenetre de selection des releves
		 */
		if (releveListe != null) {
			if (releveListe.fenetre.isShowing()) {
				bFenetreOuverte = true;
			}
		}

		if (bFenetreOuverte) {
			int rep;
			rep = JOptionPane.showOptionDialog(null, Langue.getString("confirmQuitApplication"), null,
					JOptionPane.WARNING_MESSAGE, JOptionPane.YES_NO_OPTION, null, null, JOptionPane.CANCEL_OPTION);
			if (rep != JOptionPane.YES_OPTION)
				quitter = false;
		}

		if (quitter == true) {
			/*
			 * Lancement de la verification de la derniere sauvegarde
			 */
			Backup backup = new Backup();
			if (backup.isBackupNecessary() == true) {
				backup.sauveBase();
			}
			/*
			 * fermeture des fenetres ouvertes
			 */
			iter = sfenetres.iterator();
			while (iter.hasNext()) {
				jfaFille = fenetres.get(iter.next());
				if (jfaFille.isShowing()) {
					jfaFille.close();
				}
			}
			iterReleve = releves.iterator();
			while (iterReleve.hasNext()) {
				srf = iterReleve.next();
				if (srf.fenetre.isShowing()) {
					srf.fenetre.close();
				}
			}

			/*
			 * Fermeture de l'application
			 */
			/*
			 * Arret de la connexion en cas de hsqldb
			 */
			if (Parametre.getValue("database", "dbtype").equals("hsqldb")) {
				ConnexionDatabase.shutdown();
			}
			System.exit(0);
		}

	}

	/**
	 * Affichage de la fenetre A propos...
	 */
	public void apropos() {
		String nl = System.getProperty("line.separator");
		String message = Langue.getString("aProposL1") + nl + Langue.getString("aProposL2") + nl
				+ Langue.getString("aProposL3") + nl + Langue.getString("aProposL4") + nl
				+ Langue.getString("aProposL5");
		ImageIcon icone = new ImageIcon(getClass().getClassLoader().getResource("ressources/logo.png"));
		JOptionPane.showMessageDialog(null, message, Langue.getString("aPropos"), JOptionPane.PLAIN_MESSAGE, icone);

	}

	/**
	 * Reaffichage des libelles dans les fenetres, suite au changement de langue
	 */
	public void resetLibelle() {
		Set<String> sfenetres = fenetres.keySet();
		JFrameAlisma jfaFille;
		Iterator<String> iter = sfenetres.iterator();
		iter = sfenetres.iterator();
		/*
		 * Modification des libelles de chaque fenetre fille
		 */
		while (iter.hasNext()) {
			jfaFille = fenetres.get(iter.next());
			if (jfaFille.isShowing()) {
				jfaFille.setLibelle();
			}
		}
		/*
		 * Modification des libelles de la fenetre principale
		 */
		mainFenetre.setLibelle();
	}

}
