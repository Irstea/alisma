/**
 * 
 */
package releve;

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
	public String stationName;
	Stations dbStation = new Stations();
	Hashtable<String, Double> lambertBornes = new Hashtable<String, Double>();
	GeoTransform geoTransform = new GeoTransform();
	static Logger logger = Logger.getLogger(Releve_tab1.class);

	public Releve_tab1(int pNbUR, Op_controle oc) {
		nbUR = pNbUR;
		obj = this;
		dbOpControle = oc;
		general = new General(nbUR);
		general.setTitle("donneesGen");
		pointPrelevement = new PointPrelevement(nbUR);
		pointPrelevement.setTitle("pointPrel");
		this.addComposant(general, 0, 0);
		this.addComposant(pointPrelevement, 0, 1);
	}

	public void setOpControle(Op_controle op) {
		dbOpControle = op;
	}

	class General extends ComposantAlisma {
		ItemListener jcb_nom_il, jcb_cd_il;
		Hashtable<String, String[]> stationList = new Hashtable<String, String[]>();
		Object generalObj = this;
		String station_cd_lu, station_id_lu;

		public General(int pNbUR) {
			/*
			 * Definition des valeurs mini et maxi des coordonnees lambert
			 */
			String[] lambertListeBorne = { "lambert93Emin", "lambert93Emax", "lambert93Nmin", "lambert93Nmax" };
			for (String borne : lambertListeBorne) {
				try {
					lambertBornes.put(borne, Double.parseDouble(Parametre.others.get(borne)));
				} catch (Exception e) {
					lambertBornes.put(borne, 0.0);
				}
			}

			/*
			 * Definition des libelles
			 */
			// addLabel("cdStation", 0, 0, null);
			// addLabel("nomStation", 2, 0, null);
			// addLabel("nomRiv", 4, 0, null);
			addLabel("station", 0, 0);
			addLabel("organisme", 0, 1, null);
			addLabel("operateur", 2, 1, null);
			addLabel("date", 4, 1, null);
			addLabel("ref", 0, 2, null);
			addLabel("statut", 2, 2, null);
			addLabel("ibmr", 0, 3);
			addLabel("robustesse", 2, 3);
			addLabel("taxonRob", 4, 3);
			addLabel("releveDce", 4, 2);
			addLabel("seeeIbmr", 0, 4);
			addLabel("robustesse", 2, 4);
			addLabel("taxonRob", 4, 4);
			addLabel("seeeDate", 0, 5);
			addLabel("seeeVersion", 2, 5);

			/*
			 * Definition des champs
			 */
			addTextField("stationSearch", 1, 0, 1);
			addCombo("station", 2, 0, 3);
			addLabelAsValue("nom_rv", "", 5, 0, 1);
			addTextMaxLength50("organisme", 1, 1, 1);
			addTextMaxLength50("operateur", 3, 1, 1);
			addDatePicker("date_op", new Date(), 5, 1, 1);
			addTextField("ref_dossier", 1, 2, 1);
			addLabelAsValue("statut", "", 3, 2, 1);
			addLabelAsValue("ibmr_value", "", 1, 3, 1);
			addLabelAsValue("robustesse_value", "", 3, 3, 1);
			addLabelAsValue("taxon_robustesse", "", 5, 3, 1);
			addCombo("releve_dce", 5, 2, 1);
			addComboItemList("releve_dce", new String[] { Langue.getString("oui"), Langue.getString("non") }, true);
			addHidden("id_statut");
			/*
			 * Champs pour le calcul SEEE
			 */
			addLabelAsValue("seee_ibmr", "", 1, 4, 1);
			addLabelAsValue("seee_robustesse_value", "", 3, 4, 1);
			addLabelAsValue("seee_taxon_robustesse", "", 5, 4, 1);
			addLabelAsValue("seee_date", "", 1, 5, 1);
			addLabelAsValue("seee_version", "", 3, 5, 1);

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

		public void setData(Hashtable<String, String> data) {
			super.setData(data);
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
		boolean lambertVisible = true;

		// ConvertWgs84ToLambert93 convertCoord = new ConvertWgs84ToLambert93();
		public PointPrelevement(int pNbUR) {
			String plambert = Parametre.others.get("lambert");
			if (plambert == null)
				plambert = "false";
			if (plambert.equals("false"))
				lambertVisible = false;
			addLabel("protocol", 0, 0, null);
			addLabel("nbUR", 2, 0, null);
			addLabel("coordXwgs84", 0, 1, null);
			addLabel("coordYwgs84", 2, 1, null);
			if (lambertVisible) {
				addLabel("coordX", 0, 2, null);
				addLabel("coordY", 2, 2, null);
			}
			addLabel("rive", 4, 2, null);
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
			addTextDecimal("wgs84_x", 1, 1, 1);
			addTextDecimal("wgs84_y", 3, 1, 1);
			if (lambertVisible)
				addButton("boutonWgs84", 'C', "calculLambert93", 4, 1, 2);
			addTextNumeric("coord_x", 1, 2, 1);
			addTextNumeric("coord_y", 3, 2, 1);
			if (!lambertVisible) {
				setFieldVisible("coord_x", false);
				setFieldVisible("coord_y", false);
			}
			addCombo("rive", 5, 2, 1);
			addTextDecimal("longueur", 1, 3, 1);
			addTextDecimal("largeur", 3, 3, 1);
			addTextNumeric("altitude", 5, 3, 1);
			addCombo("hydrologie", 1, 4, 1);
			addCombo("meteo", 3, 4, 1);
			addCombo("turbidite", 5, 4, 1);
			addTextArea("observation", 0, 7, 6, 600, 50);
			addHidden("id_pt_prel");
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
			} else
				addComboItemList("protocole", dbOpControle.params.get("protocole").getArray(false), false);

			/*
			 * Definition des tailles par defaut
			 */
			Dimension dimNormal = new Dimension(120, 20), dimLarge = new Dimension(165, 20);
			setDimension("protocole", dimLarge);
			setDimension("nbUR", dimNormal);
			setDimension("coord_x", dimLarge);
			setDimension("coord_y", dimNormal);
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
			setFieldRecommanded(new String[] { "longueur", "largeur", "rive", "hydrologie", "meteo", "turbidite", });
			/*
			 * Ajout dynamique des champs selon leur niveau defini
			 */
			String[] fields = { "coord_x", "coord_y", "wgs84_x", "wgs84_y" };
			for (String field : fields) {
				String param = Parametre.fieldsLevel.get(field);
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

		/**
		 * Surcharge de setAction pour lancer le calcul des coordonnees Lambert
		 */
		public void setAction() {
			if (actionLibelle == "calculLambert93") {
				if (!getData("wgs84_x").isEmpty() && !getData("wgs84_y").isEmpty()) {
					try {
						double[] point = new double[2];
						point[0] = Double.parseDouble(getData("wgs84_x"));
						point[1] = Double.parseDouble(getData("wgs84_y"));
						point = geoTransform.wgs84toLambert93(point);
						setValue("coord_x", String.valueOf((int) point[0]));
						setValue("coord_y", String.valueOf((int) point[1]));
					} catch (Exception e) {
					}
				}

				/*
				 * Repositionnement de la valeur de actionLibelle, pour les
				 * autres appels
				 */
				actionLibelle = "change";
			}
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
		general.setValue("ibmr_value", "");
		general.setValue("robustesse_value", "");
		general.setValue("taxon_robustesse", "");
		general.setValue("seee_date", "");
		general.setValue("seee_version", "");
		general.setValue("seee_ibmr", "");
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
	public void setDataCalcul(double ibmr, double robustesse, String maxTaxon) {
		general.setValue("ibmr_value", String.valueOf(ibmr));
		general.setValue("robustesse_value", String.valueOf(robustesse));
		general.setValue("taxon_robustesse", maxTaxon);
	}

	/**
	 * Recupere l'ensemble des donnees du premier onglet
	 * 
	 * @return Hashtable<String, String>
	 */
	public Hashtable<String, String> getDataGlobal() {
		Hashtable<String, String> data;
		data = hashtableFusionner(general.getData(), pointPrelevement.getData());
		return data;
	}

	/**
	 * Retourne la valeur de l'IBMR
	 * 
	 * @return
	 */
	public String getIBMR() {
		return general.getData("ibmr_value");
	}

	/**
	 * Renseigne les donnees dans les champs
	 * 
	 * @param data
	 */
	public void setDataGlobal(Hashtable<String, String> data) {
		general.setData(data);
		pointPrelevement.setData(data);
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
		return general.getData("seee_ibmr");
	}
}
