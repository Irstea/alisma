/**
 * 
 */
package releve;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.log4j.Logger;

import database.Op_controle;
import database.Stations;
import database.Typo;
import utils.ComposantAlisma;
import utils.GeoTransform;
import utils.Langue;
import utils.Parametre;

/**
 * @author quinton
 * 
 *         onglet 1 en saisie
 */
public class Releve_tab1 extends ComposantAlisma {

	PointPrelevement pointPrelevement;
	Object obj;
	public Op_controle dbOpControle;
	// private Saisi_Releve_Tab1_Query saisi_query = new
	// Saisi_Releve_Tab1_Query();
	int statut = 0, nbUR = 2;
	public General general;
	public Ibmr ibmr;
	public IbmrRobuste ibmrRobuste;
	public String stationName;
	Stations dbStation = new Stations();
	Hashtable<String, Double> lambertBornes = new Hashtable<String, Double>();
	GeoTransform geoTransform = new GeoTransform();
	static Logger logger = Logger.getLogger(Releve_tab1.class);
	boolean lambertVisible = true;
	Dimension dimNormal = new Dimension(110, 20), dimLarge = new Dimension(165, 20);

	public Releve_tab1(int pNbUR, Op_controle oc) {
		nbUR = pNbUR;
		obj = this;
		dbOpControle = oc;
		general = new General(nbUR);
		general.setTitle("donneesGen");
		ibmr = new Ibmr();
		ibmr.setTitle("ibmr");
		ibmrRobuste = new IbmrRobuste();
		ibmrRobuste.setTitle("robustesse");
		pointPrelevement = new PointPrelevement(nbUR);
		pointPrelevement.setTitle("pointPrel");
		this.addComposant(general, 0, 0);
		this.addComposant(ibmr, 0, 1);
		this.addComposant(ibmrRobuste, 0, 2);
		this.addComposant(pointPrelevement, 0, 3);
	}

	public void setOpControle(Op_controle op) {
		dbOpControle = op;
	}

	class General extends ComposantAlisma {
		ItemListener jcb_nom_il, jcb_cd_il;
		Hashtable<String, String[]> stationList = new Hashtable<String, String[]>();
		Object generalObj = this;
		String station_cd_lu, station_id_lu;
		Typo typo = new Typo();
		DocumentListener dl_preleveur = new DocumentListener() {

			@Override
			public void changedUpdate(DocumentEvent arg0) {
				determinateurUpdate();
			}

			@Override
			public void insertUpdate(DocumentEvent arg0) {
				determinateurUpdate();
			}

			@Override
			public void removeUpdate(DocumentEvent arg0) {
				determinateurUpdate();
			}
		};

		public General(int pNbUR) {
			/*
			 * Definition des valeurs mini et maxi des coordonnees lambert
			 */
			String[] lambertListeBorne = { "lambert93Emin", "lambert93Emax", "lambert93Nmin", "lambert93Nmax" };
			for (String borne : lambertListeBorne) {
				try {
					lambertBornes.put(borne, Double.parseDouble(Parametre.getValue("others", borne)));
				} catch (Exception e) {
					lambertBornes.put(borne, 0.0);
				}
			}
			/*
			 * Initialisation de Typo
			 */
			typo.readData();

			/*
			 * Definition des libelles
			 */
			// addLabel("cdStation", 0, 0, null);
			// addLabel("nomStation", 2, 0, null);
			// addLabel("nomRiv", 4, 0, null);
			addLabel("station", 0, 0);
			addLabel("typeNat", 6, 0);
			addLabel("organisme", 0, 1, null);
			addLabel("operateur", 2, 1, null);
			addLabel("producteur", 4, 1, null);
			addLabel("preleveur", 0, 2, null);
			addLabel("determinateur", 4, 2, null);

			addLabel("ref", 0, 3, null);
			addLabel("statut", 2, 3, null);
			addLabel("releveDce", 4, 3);
			addLabel("date", 6, 3, null);

			/*
			 * Definition des champs
			 */
			addTextField("stationSearch", 1, 0, 1);
			addCombo("station", 2, 0, 3);
			addLabelAsValue("nom_rv", "", 5, 0, 1);
			addCombo("typo_id", 7, 0, 1);
			addComboItemList("typo_id", typo.getArray(true), true);
			addTextMaxLength50("organisme", 1, 1, 1);
			addTextMaxLength50("operateur", 3, 1, 1);
			addTextMaxLength50("producteur_code", 5, 1, 1);
			addTextMaxLength50("producteur_name", 6, 1, 2);
			addTextMaxLength50("preleveur_code", 1, 2, 1);
			addTextMaxLength50("preleveur_name", 2, 2, 2);
			addTextMaxLength50("determinateur_code", 5, 2, 1);
			addTextMaxLength50("determinateur_name", 6, 2, 2);
			addDatePicker("date_op", new Date(), 7, 3, 2, new Dimension(120, 20));
			addTextField("ref_dossier", 1, 3, 1);
			addLabelAsValue("statut", "", 3, 3, 1);
			addCombo("releve_dce", 5, 3, 1);
			addComboItemList("releve_dce", new String[] { Langue.getString("oui"), Langue.getString("non") }, true);
			addHidden("id_statut");
			addHidden("uuid");
			/*
			 * Champs pour le calcul SEEE
			 */

			/*
			 * Ajout des listeners
			 */
			final JTextField jtf = (JTextField) fieldList.get("stationSearch");
			jtf.getDocument().addDocumentListener(new DocumentListener() {

				@Override
				public void changedUpdate(DocumentEvent e) {
					stationSearch(jtf.getText());
				}

				@Override
				public void insertUpdate(DocumentEvent e) {
					stationSearch(jtf.getText());
				}

				@Override
				public void removeUpdate(DocumentEvent e) {
					stationSearch(jtf.getText());
				}
			});
			final JTextField prelcode = (JTextField) fieldList.get("preleveur_code");
			prelcode.getDocument().addDocumentListener(dl_preleveur);

			final JTextField prelname = (JTextField) fieldList.get("preleveur_name");
			prelname.getDocument().addDocumentListener(dl_preleveur);
			@SuppressWarnings("unchecked")
			JComboBox<Object> jcb = (JComboBox<Object>) fieldList.get("station");
			jcb.addItemListener(new ItemListener() {

				@Override
				public void itemStateChanged(ItemEvent arg0) {
					if (!stationList.isEmpty()) {
						@SuppressWarnings("unchecked")
						JComboBox<Object> jcb = (JComboBox<Object>) fieldList.get("station");
						try {
							setValue("nom_rv", stationList.get(jcb.getSelectedItem())[3]);
						} catch (NullPointerException e) {
							setValue("nom_rv", "");
						}
					} else
						setValue("nom_rv", "");
				}
			});

			/*
			 * Definition des tailles par defaut
			 */
			Dimension dimNormal = new Dimension(120, 20);
			setDimension("nom_rv", dimNormal);
			setDimension("organisme", dimNormal);
			setDimension("operateur", dimNormal);
			setDimension("ref_dossier", dimNormal);
			setDimension("statut", dimNormal);

			/*
			 * Definition des champs obligatoire
			 */
			setFieldMandatory(new String[] { "station", "organisme", "operateur", "date_op" });

			/*
			 * Mise en place du statut par defaut
			 */
			this.setStatut(statut);
		}

		protected void determinateurUpdate() {
			JTextField code = (JTextField) fieldList.get("determinateur_code");
			JTextField name = (JTextField) fieldList.get("determinateur_name");
			if (code.getText().isEmpty()) {
				code.setText(((JTextField) fieldList.get("preleveur_code")).getText());
			}
			if (name.getText().isEmpty()) {
				name.setText(((JTextField) fieldList.get("preleveur_name")).getText());
			}
		}

		public int validation() {
			int retour = super.validation();
			if (retour < 2) {
				/*
				 * Test si le code station existe
				 */
				try {
					String station_id = stationList.get(getCombo("station").getSelectedItem())[0];
					String station_cd = station_id.equals(station_id_lu) ? station_cd_lu
							: dbStation.getCdStationFromId(station_id);
					station_id_lu = station_id;
					station_cd_lu = station_cd;
					if (station_cd.length() == 0 && dbOpControle.yesNoForKey.get(this.getData("releve_dce")) == 1)
						retour = 2;
				} catch (NullPointerException e) {
					// retour = 2;
				}
				if (retour == 2) {
					setBordure("station", retour);
				}
			}
			return retour;
		}

		public void setDefault() {
			/*
			 * Recuperation des valeurs par defaut du fichier de parametres
			 */
			((JTextField) fieldList.get("preleveur_code")).setText(Parametre.getValue("others", "preleveur_code"));
			((JTextField) fieldList.get("preleveur_name")).setText(Parametre.getValue("others", "preleveur_name"));
			((JTextField) fieldList.get("determinateur_code"))
					.setText(Parametre.getValue("others", "determinateur_code"));
			((JTextField) fieldList.get("determinateur_name"))
					.setText(Parametre.getValue("others", "determinateur_name"));
			((JTextField) fieldList.get("organisme")).setText(Parametre.getValue("others", "organisme"));
			((JTextField) fieldList.get("operateur")).setText(Parametre.getValue("others", "operateur"));
			((JTextField) fieldList.get("producteur_code")).setText(Parametre.getValue("others", "producteur_code"));
			((JTextField) fieldList.get("producteur_name")).setText(Parametre.getValue("others", "producteur_name"));
		}

		public void setData(Hashtable<String, String> data) {
			/*
			 * Desactivation des listeners
			 */
			((JTextField) fieldList.get("preleveur_code")).getDocument().removeDocumentListener(dl_preleveur);
			((JTextField) fieldList.get("preleveur_name")).getDocument().removeDocumentListener(dl_preleveur);

			super.setData(data);

			if (!data.get("typo_id").isEmpty()) {
				this.setValue("typo_id", typo.getValueFromKey(Integer.valueOf(data.get("typo_id"))));
			}
			/*
			 * Mise a jour de releve_dce
			 */
			if (data.get("releve_dce").equals("1")) {
				setValue("releve_dce", Langue.getString("oui"));
			} else
				setValue("releve_dce", Langue.getString("non"));
			/*
			 * Mise a jour de la boite de recherche des stations
			 */
			Hashtable<String, String> ligneStation = dbStation.readByKey("id_station", data.get("id_station"));
			stationSearch(ligneStation.get("cd_station"));
			stationName = ligneStation.get("station");
			/*
			 * Preleveur par defaut
			 */
			JTextField code = (JTextField) fieldList.get("preleveur_code");
			if (code.getText().isEmpty()) {
				String value = Parametre.getValue("others", "preleveur_code");
				if (!value.isEmpty()) {
					code.setText(value);
					((JTextField) fieldList.get("preleveur_name"))
							.setText(Parametre.getValue("others", "preleveur_name"));
					/*
					 * Ajout des valeurs par defaut pour le determinateur
					 */
					if (((JTextField) fieldList.get("determinateur_code")).getText().isEmpty()) {
						((JTextField) fieldList.get("determinateur_code")).setText(value);
						((JTextField) fieldList.get("determinateur_name"))
								.setText(Parametre.getValue("others", "preleveur_name"));
					}
				}
			}
			/*
			 * Reactivation des listeners
			 */
			((JTextField) fieldList.get("preleveur_code")).getDocument().addDocumentListener(dl_preleveur);
			((JTextField) fieldList.get("preleveur_name")).getDocument().addDocumentListener(dl_preleveur);

		}

		public Hashtable<String, String> getData() {
			Hashtable<String, String> data = super.getData();
			/*
			 * Recuperation de la station
			 */
			if (!stationList.isEmpty()) {
				JComboBox<Object> jcb = getCombo("station");
				try {
					data.put("id_station", stationList.get(jcb.getSelectedItem())[0]);
				} catch (NullPointerException e) {
					data.put("id_station", "");
				}
			}
			/*
			 * recuperation de typo
			 */
			data.put("typo_id", String.valueOf(typo.getKeyFromValue(data.get("typo_id"))));
			return data;
		}

		/**
		 * Lance la recherche des stations a partir d'un libelle
		 * 
		 * @param search
		 */
		public void stationSearch(String search) {
			List<Hashtable<String, String>> result = dbStation.getListStationFromSearch(search);
			JComboBox<Object> jcb = getCombo("station");
			jcb.removeAllItems();
			stationList.clear();
			boolean first = true;
			for (int i = 0; i < result.size(); i++) {

				String cd_station;
				try {
					cd_station = result.get(i).get("cd_station").equals(null) ? "" : result.get(i).get("cd_station");
				} catch (NullPointerException e) {
					cd_station = "";
				}
				String ligne[] = { result.get(i).get("id_station"), cd_station, result.get(i).get("station"),
						result.get(i).get("cours_eau") };
				jcb.addItem(cd_station + " " + result.get(i).get("station"));
				stationList.put(cd_station + " " + result.get(i).get("station"), ligne);
				if (first) {
					setValue("nom_rv", result.get(i).get("cours_eau"));
					first = false;
				}
			}
		}

		/**
		 * Mise a jour du statut
		 * 
		 * @param statutId
		 */
		public void setStatut(int statutId) {
			switch (statutId) {
			case 1:
				setValue("statut", Langue.getString("statut1"));
				break;
			case 2:
				setValue("statut", Langue.getString("statut2"));
				break;
			default:
				setValue("statut", Langue.getString("statut0"));
			}
		}
	}

	/**
	 * Seconde boite de l'onglet
	 * 
	 * @author quinton
	 * 
	 */
	class PointPrelevement extends ComposantAlisma {
		Aval aval = new Aval();
		Amont amont = new Amont();

		// ConvertWgs84ToLambert93 convertCoord = new ConvertWgs84ToLambert93();
		public PointPrelevement(int pNbUR) {
			String plambert = Parametre.getValue("others", "lambert");
			if (plambert == null)
				plambert = "false";
			if (plambert.equals("false"))
				lambertVisible = false;
			addLabel("protocol", 0, 0, null);
			addLabel("nbUR", 2, 0, null);
			addLabel("rive", 4, 0, null);
			addLabel("longueur", 0, 3, null);
			addLabel("largeur", 2, 3, null);
			addLabel("altitude", 4, 3, null);
			addLabel("hydro", 0, 4, null);
			addLabel("meteo", 2, 4, null);
			addLabel("turbidite", 4, 4, null);
			addLabel("observations", 0, 6, null);
			/*
			 * Definition des champs
			 */
			addCombo("protocole", 1, 0, 1);
			addTextField("nbUR", 3, 0, 1);
			setValue("nbUR", String.valueOf(pNbUR));
			setFieldDisabled("nbUR");

			addCombo("rive", 5, 0, 1);
			addTextDecimal("longueur", 1, 3, 1);
			addTextDecimal("largeur", 3, 3, 1);
			addTextNumeric("altitude", 5, 3, 1);
			addCombo("hydrologie", 1, 4, 1);
			addCombo("meteo", 3, 4, 1);
			addCombo("turbidite", 5, 4, 1);
			addTextArea("observation", 0, 7, 6, 600, 50);
			addHidden("id_pt_prel");
			/*
			 * Ajout des boites de gestion des coordonnees geographiques
			 */
			this.addComposant(amont, 0, 1, 6);
			this.addComposant(aval, 0, 2, 6);
			/*
			 * Ajout du contenu des tables de parametres dans les combo
			 */
			try {
				logger.debug("Longueur de paramListe: " + dbOpControle.paramList.length);
			} catch (NullPointerException e) {
				logger.error("dbOpControle.paramList non initialise");
			}
			for (String param : dbOpControle.paramList) {
				logger.debug("parametre lu : " + param);
				if (!param.equals("protocole"))
					addComboItemList(param, dbOpControle.params.get(param).getArray(), false);
			}
			/*
			 * Redefinition des protocoles possibles si une seule UR
			 */
			if (pNbUR == 1) {
				addComboItemList("protocole", new String[] { dbOpControle.params.get("protocole").getValueFromKey(1),
						dbOpControle.params.get("protocole").getValueFromKey(3) }, true);
			} else {
				addComboItemList("protocole", dbOpControle.params.get("protocole").getArray(false), false);
			}

			/*
			 * Definition des tailles par defaut
			 */

			setDimension("protocole", dimLarge);
			setDimension("nbUR", dimNormal);
			setDimension("rive", dimNormal);
			setDimension("longueur", dimLarge);
			setDimension("largeur", dimNormal);
			setDimension("altitude", dimNormal);
			setDimension("hydrologie", dimLarge);
			setDimension("meteo", dimNormal);
			setDimension("turbidite", dimNormal);
			/*
			 * Definition des champs obligatoire
			 */
			setFieldMandatory(new String[] { "protocole", "nbUR" });
			setFieldNecessary(new String[] { "longueur" });
			setFieldRecommanded(new String[] { "largeur", "rive", "hydrologie", "meteo", "turbidite", });

		}

		/**
		 * Retourne la liste de toutes les donnees du composant
		 * 
		 * @return Hashtable<String, String>
		 */
		public Hashtable<String, String> getDataGlobal() {
			Hashtable<String, String> data;
			data = hashtableFusionner(this.getData(), amont.getData());
			data = hashtableFusionner(data, aval.getData());
			return data;
		}

		public void setDataGlobal(Hashtable<String, String> data) {
			this.setData(data);
			amont.setData(data);
			aval.setData(data);

		}

	}

	/**
	 * Saisie des coordonnees geographiques du point amont
	 * 
	 * @author quinton
	 *
	 */
	class Amont extends ComposantAlisma {
		public Amont() {
			setTitle("amont");
			addLabel("coordXwgs84", 0, 0, null);
			addLabel("coordYwgs84", 2, 0, null);
			addTextDecimal("wgs84_x", 1, 0, 1);
			addTextDecimal("wgs84_y", 3, 0, 1);
			if (lambertVisible) {
				addLabel("coordX", 0, 1, null);
				addLabel("coordY", 2, 1, null);
				addButton("boutonWgs84", 'C', "calculLambert93", 4, 0, 2);
				addButton("boutonLambert93", 'W', "calculWgs84", 4, 1, 2);
			}
			addTextNumeric("coord_x", 1, 1, 1);
			addTextNumeric("coord_y", 3, 1, 1);
			if (!lambertVisible) {
				setFieldVisible("coord_x", false);
				setFieldVisible("coord_y", false);
			}
			setDimension("coord_x", dimLarge);
			setDimension("coord_y", dimNormal);

			/*
			 * Ajout dynamique des champs selon leur niveau defini
			 */

			String[] fields = new String[] { "coord_x", "coord_y", "wgs84_x", "wgs84_y" };
			for (String field : fields) {
				String param = Parametre.getValue("fieldsLevel", field);
				switch (param) {
				case "mandatory":
					addFieldMandatory(field);
					break;
				case "necessary":
					addFieldNecessary(field);
					break;
				case "recommanded":
					addFieldRecommanded(field);
					break;
				}
			}
		}

		/**
		 * Surcharge de setAction pour lancer le calcul des coordonnees Lambert
		 */
		public void setAction() {
			switch (actionLibelle) {

			case "calculLambert93":
				if (!getData("wgs84_x").isEmpty() && !getData("wgs84_y").isEmpty()) {
					try {
						double[] point = new double[2];
						point[0] = Double.parseDouble(getData("wgs84_x"));
						point[1] = Double.parseDouble(getData("wgs84_y"));
						point = geoTransform.wgs84toLambert93(point);
						setValue("coord_x", String.valueOf((int) point[0]));
						setValue("coord_y", String.valueOf((int) point[1]));
						validation();
					} catch (Exception e) {
					}
				}
				break;
			case "calculWgs84":
				if (!getData("coord_x").isEmpty() && !getData("coord_y").isEmpty()) {
					try {
						double[] point = new double[2];
						point[0] = Double.parseDouble(getData("coord_x"));
						point[1] = Double.parseDouble(getData("coord_y"));
						point = geoTransform.lambert93ToWgs84(point);
						setValue("wgs84_x", String.valueOf((double) Math.round(point[0] * 100000) / 100000));
						setValue("wgs84_y", String.valueOf((double) Math.round(point[1] * 100000) / 100000));
						validation();
					} catch (Exception e) {
					}
				}
				break;
			}

			/*
			 * Repositionnement de la valeur de actionLibelle, pour les autres appels
			 */
			actionLibelle = "change";

			super.setAction();
		}

		public int validation() {
			int rep = 0;
			rep = super.validation();
			double value;
			/*
			 * Teste si les coordonnees lambert sont dans les bonnes valeurs
			 */
			if (!getData("coord_x").isEmpty()) {
				try {
					value = Double.parseDouble(getData("coord_x"));
					if (value < lambertBornes.get("lambert93Emin") || value > lambertBornes.get("lambert93Emax")) {
						setBordure("coord_x", 2);
						if (rep < 2)
							rep = 2;
					}
				} catch (Exception e) {
					setBordure("coord_x", 1);
					rep = 1;
				}
			}

			if (!getData("coord_y").isEmpty()) {
				try {
					value = Double.parseDouble(getData("coord_y"));
					if (value < lambertBornes.get("lambert93Nmin") || value > lambertBornes.get("lambert93Nmax")) {
						setBordure("coord_y", 2);
						if (rep < 2)
							rep = 2;
					}
				} catch (Exception e) {
					setBordure("coord_y", 1);
					rep = 1;
				}
			}
			return rep;
		}

	}

	/**
	 * Saisies des coordonnees geographiques du point aval
	 * 
	 * @author quinton
	 *
	 */
	class Aval extends ComposantAlisma {
		public Aval() {
			setTitle("aval");
			addLabel("coordXwgs84", 0, 0, null);
			addLabel("coordYwgs84", 2, 0, null);
			addTextDecimal("wgs84_x_aval", 1, 0, 1);
			addTextDecimal("wgs84_y_aval", 3, 0, 1);
			if (lambertVisible) {
				addLabel("coordX", 0, 1, null);
				addLabel("coordY", 2, 1, null);
				addButton("boutonWgs84", 'C', "calculLambert93", 4, 0, 2);
				addButton("boutonLambert93", 'W', "calculWgs84", 4, 1, 2);
			}
			addTextNumeric("lambert_x_aval", 1, 1, 1);
			addTextNumeric("lambert_y_aval", 3, 1, 1);
			if (!lambertVisible) {
				setFieldVisible("lambert_x_aval", false);
				setFieldVisible("lambert_y_aval", false);
			}
			setDimension("lambert_x_aval", dimLarge);
			setDimension("lambert_y_aval", dimNormal);
			String[] fields = new String[] { "lambert_x_aval", "lambert_y_aval", "wgs84_x_aval", "wgs84_y_aval" };
			for (String field : fields) {
				String param = Parametre.getValue("fieldsLevel", field);
				switch (param) {
				case "mandatory":
					addFieldMandatory(field);
					break;
				case "necessary":
					addFieldNecessary(field);
					break;
				case "recommanded":
					addFieldRecommanded(field);
					break;
				}
			}
		}

		public int validation() {
			int rep = 0;
			rep = super.validation();
			double value;
			/*
			 * Teste si les coordonnees lambert sont dans les bonnes valeurs
			 */
			if (!getData("lambert_x_aval").isEmpty()) {
				try {
					value = Double.parseDouble(getData("lambert_x_aval"));
					if (value < lambertBornes.get("lambert93Emin") || value > lambertBornes.get("lambert93Emax")) {
						setBordure("coord_x", 2);
						if (rep < 2)
							rep = 2;
					}
				} catch (Exception e) {
					setBordure("lambert_x_aval", 1);
					rep = 1;
				}
			}

			if (!getData("lambert_y_aval").isEmpty()) {
				try {
					value = Double.parseDouble(getData("lambert_y_aval"));
					if (value < lambertBornes.get("lambert93Nmin") || value > lambertBornes.get("lambert93Nmax")) {
						setBordure("coord_y", 2);
						if (rep < 2)
							rep = 2;
					}
				} catch (Exception e) {
					setBordure("lambert_y_aval", 1);
					rep = 1;
				}
			}
			return rep;
		}

		/**
		 * Surcharge de setAction pour lancer le calcul des coordonnees Lambert
		 */
		public void setAction() {
			switch (actionLibelle) {

			case "calculLambert93":
				if (!getData("wgs84_x_aval").isEmpty() && !getData("wgs84_y_aval").isEmpty()) {
					try {
						double[] point = new double[2];
						point[0] = Double.parseDouble(getData("wgs84_x_aval"));
						point[1] = Double.parseDouble(getData("wgs84_y_aval"));
						point = geoTransform.wgs84toLambert93(point);
						setValue("lambert_x_aval", String.valueOf((int) point[0]));
						setValue("lambert_y_aval", String.valueOf((int) point[1]));
						validation();
					} catch (Exception e) {
					}
				}
				break;
			case "calculWgs84":
				if (!getData("lambert_x_aval").isEmpty() && !getData("lambert_y_aval").isEmpty()) {
					try {
						double[] point = new double[2];
						point[0] = Double.parseDouble(getData("lambert_x_aval"));
						point[1] = Double.parseDouble(getData("lambert_y_aval"));
						point = geoTransform.lambert93ToWgs84(point);
						setValue("wgs84_x_aval", String.valueOf((double) Math.round(point[0] * 100000) / 100000));
						setValue("wgs84_y_aval", String.valueOf((double) Math.round(point[1] * 100000) / 100000));
						validation();
					} catch (Exception e) {
					}
				}
				break;
			}

			/*
			 * Repositionnement de la valeur de actionLibelle, pour les autres appels
			 */
			actionLibelle = "change";

			super.setAction();
		}
	}

	/**
	 * Mise a jour du statut du dossier
	 * 
	 * @param statutId
	 */
	public void setStatut(Integer statutId) {
		statut = statutId;
		general.setStatut(statutId);
		general.setValue("id_statut", statutId.toString());
	}

	/**
	 * Recherche si le commentaire est renseigne ou non
	 * 
	 * @return boolean
	 */
	public boolean isCommentaireEmpty() {
		JTextArea obs = (JTextArea) pointPrelevement.fieldList.get("observation");
		return obs.getText().isEmpty();
	}

	/**
	 * Positionne la bordure autour du commentaire
	 * 
	 * @param level
	 */
	public void setCommentaireBordure(int level) {
		pointPrelevement.setBordure("observation", level);
	}

	/**
	 * Reinitialise les zones d'affichage du calcul
	 */
	public void resetCalcul() {
		ibmr.setValue("ibmr_value", "");
		ibmrRobuste.setValue("robustesse_value", "");
		ibmrRobuste.setValue("taxon_robustesse", "");
		ibmrRobuste.setValue("ek_nb_robustesse", "");
		ibmr.setValue("seee_date", "");
		ibmr.setValue("seee_version", "");
		ibmr.setValue("seee_ibmr", "");
		// general.setValue("seee_nbtaxon_contrib", "");
		ibmrRobuste.setValue("seee_robustesse_value", "");
		ibmrRobuste.setValue("seee_taxon_robustesse", "");
		ibmr.setValue("eqr_value", "");
		ibmrRobuste.setValue("robustesse_eqr_value", "");
		ibmr.setValue("classe_etat_libelle", "");
		ibmrRobuste.setValue("robustesse_classe_etat_libelle", "");
	}

	/**
	 * Met a jour les donnees issues du calcul de l'indice
	 * 
	 * @param double
	 *            ibmr
	 * @param double
	 *            robustesse
	 * @param string
	 *            maxTaxon
	 */
	public void setDataCalcul(Hashtable<String, String> data) {
		String[] fields = { "ibmr_value", "eqr_value", "classe_etat_libelle", "classe_etat_id" };
		String[] fieldsRobuste = { "robustesse_value", "robustesse_classe_etat_libelle", "taxon_robustesse",
				"ek_nb_robustesse", "robustesse_eqr_value", "robustesse_classe_etat_id" };
		for (String field : fields) {
			ibmr.setValue(field, data.get(field));
		}
		for (String field : fieldsRobuste) {
			ibmrRobuste.setValue(field, data.get(field));
		}
		ibmr.setQualityColor("classe_etat_libelle", "classe_etat_id");
		ibmrRobuste.setQualityColor("robustesse_classe_etat_libelle", "robustesse_classe_etat_id");

	}

	/**
	 * Recupere l'ensemble des donnees du premier onglet
	 * 
	 * @return Hashtable<String, String>
	 */
	public Hashtable<String, String> getDataGlobal() {
		Hashtable<String, String> data;
		data = hashtableFusionner(general.getData(), pointPrelevement.getDataGlobal());
		data = hashtableFusionner(data, ibmr.getData());
		data = hashtableFusionner (data, ibmrRobuste.getData());
		return data;
	}

	/**
	 * Retourne la valeur de l'IBMR
	 * 
	 * @return
	 */
	public String getIBMR() {
		return ibmr.getData("ibmr_value");
	}

	/**
	 * Renseigne les donnees dans les champs
	 * 
	 * @param data
	 */
	public void setDataGlobal(Hashtable<String, String> data) {
		general.setData(data);
		ibmr.setData(data);
		ibmrRobuste.setData(data);
		pointPrelevement.setDataGlobal(data);
		setStatut(Integer.parseInt(data.get("id_statut")));
	}

	/**
	 * Retourne la valeur du protocole
	 * 
	 * @return
	 */
	public String getProtocole() {
		return pointPrelevement.getData("protocole");
	}

	public int getStatut() {
		return statut;
	}

	public String getIBMRSEEE() {
		return ibmr.getData("seee_ibmr");
	}

	public void setDefault() {
		general.setDefault();
	}

	/**
	 * Classe d'affichage des valeurs de l'IBMR
	 * 
	 * @author quinton
	 *
	 */
	class Ibmr extends ComposantAlisma {
		public Ibmr() {

			addLabel("ibmr", 0, 0);
			addLabel("eqr", 2, 0);
			addLabel("classeEtat", 4, 0);
			addLabel("seeeIbmr", 0, 1, new Dimension(150, 20));
			addLabel("seeeDate", 2, 1);
			addLabel("seeeVersion", 4, 1);

			addLabelAsValue("ibmr_value", "", 1, 0, 1);
			addLabelAsValue("eqr_value", "", 3, 0, 1);
			addLabelAsValue("classe_etat_libelle", "", 5, 0, 1);
			addLabelAsValue("seee_ibmr", "", 1, 1, 1);
			addLabelAsValue("seee_date", "", 3, 1, 1);
			addLabelAsValue("seee_version", "", 5, 1, 1);
			addHidden("classe_etat_id");
		}

		public void setData(Hashtable<String, String> data) {
			super.setData(data);
			this.setValue("seee_date", data.get("seee_date"));
			this.setValue("classe_etat_libelle", data.get("classe_etat_libelle"));
			setQualityColor("classe_etat_libelle", "classe_etat_id");
		}

		void setQualityColor(String field, String level) {
			try {
				int couleur = Integer.parseInt(this.getData(level));
				Color color = Color.white;
				switch (couleur) {
				case 1:
					color = Color.blue;
					break;
				case 2:
					color = Color.green;
					break;
				case 3:
					color = Color.yellow;
					break;
				case 4:
					color = Color.orange;
					break;
				case 5:
					color = Color.red;
				}
				this.setColorBorder(field, color);
			} catch (Exception e) {

			}
		}
	}

	/**
	 * Classe d'affichage des valeurs de l'IBMR robuste
	 * 
	 * @author quinton
	 *
	 */
	class IbmrRobuste extends ComposantAlisma {
		public IbmrRobuste() {
			addLabel("ibmr", 0, 0);
			addLabel("eqr", 2, 0);
			addLabel("classeEtat", 4, 0);
			addLabel("taxonRob", 0, 1);
			addLabel("nbTaxonEKmax", 2, 1);
			addLabel("seeeIbmr", 4, 1, new Dimension(150, 20));
			addLabel("taxonRob", 6, 1);

			addLabelAsValue("robustesse_value", "", 1, 0, 1);
			addLabelAsValue("robustesse_eqr_value", "", 3, 0, 1);
			addLabelAsValue("robustesse_classe_etat_libelle", "", 5, 0, 1);
			addLabelAsValue("taxon_robustesse", "", 1, 1, 1);
			addLabelAsValue("ek_nb_robustesse", "", 3, 1, 1);
			addLabelAsValue("seee_robustesse_value", "", 5, 1, 1);
			addLabelAsValue("seee_taxon_robustesse", "", 7, 1, 1);
			addHidden("robustesse_classe_etat_id");
		}

		public void setData(Hashtable<String, String> data) {
			super.setData(data);
			this.setValue("seee_robustesse_value", data.get("seee_robustesse_value"));
			this.setValue("robustesse_classe_etat_libelle", data.get("robustesse_classe_etat_libelle"));
			this.setValue("robustesse_eqr_value", data.get("robustesse_eqr_value"));
			setQualityColor("robustesse_classe_etat_libelle", "robustesse_classe_etat_id");
		}

		void setQualityColor(String field, String level) {
			try {
				int couleur = Integer.parseInt(this.getData(level));
				Color color = Color.white;
				switch (couleur) {
				case 1:
					color = Color.blue;
					break;
				case 2:
					color = Color.green;
					break;
				case 3:
					color = Color.yellow;
					break;
				case 4:
					color = Color.orange;
					break;
				case 5:
					color = Color.red;
				}
				this.setColorBorder(field, color);
			} catch (Exception e) {

			}
		}
	}
}
