/**
 * 
 */
package releve;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.util.Hashtable;
import java.util.List;

import org.apache.log4j.Logger;

import database.Unite_releves;
import utils.ComposantAlisma;
import utils.ComposantMeso;

/**
 * @author quinton
 * 
 *         Second onglet
 */
public class Releve_tab2 extends ComposantAlisma {
	int nbUR = 2;
	boolean releve2Visible = true;
	Object obj;
	Unite_releves ur;
	General[] general = new General[2];
	Facies[] facies = new Facies[2];
	Profondeur[] profondeur = new Profondeur[2];
	Eclairement[] eclairement = new Eclairement[2];
	Substrat[] substrat = new Substrat[2];
	Vitesse[] vitesse = new Vitesse[2];
	Dimension dimLabel = new Dimension(320, 20), dimDefault = new Dimension(120, 20);
	int[] idUR = { -1, -1 };
	static Logger logger = Logger.getLogger(Releve_tab2.class);

	public Releve_tab2(int pnbUR, Unite_releves fur) {
		super();
		obj = this;
		nbUR = pnbUR;

		ur = fur;
		/*
		 * Initialisations
		 */
		/*
		 * Boite d'affichage de l'UR
		 */
		ComposantAlisma urBox = new ComposantAlisma();
		urBox.addLabel("libelleVide", 0, 0, dimLabel);
		urBox.addLabel("ur1", 1, 0, dimDefault, GridBagConstraints.CENTER);
		if (nbUR == 2)
			urBox.addLabel("ur2", 2, 0, dimDefault, GridBagConstraints.CENTER);
		/*
		 * Boite General
		 */
		ComposantAlisma generalBox = new ComposantAlisma();
		ComposantAlisma generalLabel = new ComposantAlisma();
		generalBox.setTitle("caracGen");
		generalLabel.addLabel("pc_rec", 0, 0, dimLabel);
		generalLabel.addLabel("typeUR", 0, 1, dimLabel);
		generalLabel.addLabel("longueurUR", 0, 2, dimLabel);
		generalLabel.addLabel("largeurUR", 0, 3, dimLabel);
		generalLabel.addLabel("faciesDominant", 0, 4, dimLabel);
		gbc.gridx = 0;
		gbc.gridy = 0;
		generalBox.addComposant(generalLabel, 0, 0);
		for (int i = 0; i < nbUR; i++) {
			general[i] = new General();
			/*
			 * Definition par defaut des valeurs de type d'ur
			 */
			general[i].setTypeURList(i, null);
			generalBox.addComposant(general[i], i + 1, 0);
		}
		/*
		 * Forcage de la valeur de recouvrement de l'ur1 a 100 dans le cas d'un
		 * ur unique
		 */
		if (nbUR == 1)
			general[0].setValue("pc_ur", "100");
		/*
		 * Boite legende
		 */
		ComposantAlisma legende = new ComposantAlisma();
		legende.addLabel("echelleMeso", 0, 0, null);

		/*
		 * Boite Facies
		 */
		ComposantAlisma faciesBox = new ComposantAlisma();
		ComposantAlisma faciesLabel = new ComposantAlisma();

		faciesBox.setTitle("typeFacies");
		faciesLabel.setDimensionDefault(dimLabel);
		faciesLabel.addLabelList(new String[] { "ch_lentique", "pl_lentique","mouille","fosse_dissipation",
				"ch_lotique","radier","cascade","pl_courant","rapide",
				"autreType", "autreTypeClass" }, 0, 0);

		gbc.gridx = 0;
		faciesBox.addComposant(faciesLabel, gbc);
		gbc.gridx = 1;
		gbc.gridy = 0;
		for (int i = 0; i < nbUR; i++) {
			facies[i] = new Facies();
			faciesBox.addComposant(facies[i], i + 1, 0);
		}

		/*
		 * Boite profondeur
		 */
		ComposantAlisma profondeurBox = new ComposantAlisma(), profondeurLabel = new ComposantAlisma();
		profondeurBox.setTitle("profon");
		profondeurLabel.setDimensionDefault(dimLabel);
		profondeurLabel.addLabelList(new String[] { "p1", "p2", "p3", "p4", "p5" }, 0, 0);
		gbc.gridx = 0;
		gbc.gridy = 0;
		profondeurBox.addComposant(profondeurLabel, gbc);
		for (int i = 0; i < nbUR; i++) {
			profondeur[i] = new Profondeur();
			profondeurBox.addComposant(profondeur[i], i + 1, 0);
		}

		/*
		 * Boite vitesse
		 */
		ComposantAlisma vitesseBox = new ComposantAlisma(), vitesseLabel = new ComposantAlisma();
		vitesseBox.setTitle("vitess");
		vitesseLabel.setDimensionDefault(dimLabel);
		vitesseLabel.addLabelList(new String[] { "v1", "v2", "v3", "v4", "v5" }, 0, 0);
		gbc.gridx = 0;
		gbc.gridy = 0;
		vitesseBox.addComposant(vitesseLabel, gbc);
		for (int i = 0; i < nbUR; i++) {
			vitesse[i] = new Vitesse();
			vitesseBox.addComposant(vitesse[i], i + 1, 0);
		}

		/*
		 * Boite eclairement
		 */
		ComposantAlisma eclairementBox = new ComposantAlisma(), eclairementLabel = new ComposantAlisma();
		eclairementBox.setTitle("eclair");
		eclairementLabel.setDimensionDefault(dimLabel);
		eclairementLabel.addLabelList(new String[] { "tresOmbr", "ombr", "peuOmbr", "eclair", "tresEcl" }, 0, 0);
		gbc.gridx = 0;
		gbc.gridy = 0;
		eclairementBox.addComposant(eclairementLabel, gbc);
		for (int i = 0; i < nbUR; i++) {
			eclairement[i] = new Eclairement();
			eclairementBox.addComposant(eclairement[i], i + 1, 0);
		}

		/*
		 * Boite substrat
		 */
		ComposantAlisma substratBox = new ComposantAlisma(), substratLabel = new ComposantAlisma();
		substratBox.setTitle("typeSubs");
		substratLabel.setDimensionDefault(dimLabel);
		substratLabel.addLabelList(
				new String[] { "vase", "cailloux", "sables", "terre", "blocs", "racines", "debris", "artif" }, 0, 0);
		gbc.gridx = 0;
		gbc.gridy = 0;
		substratBox.addComposant(substratLabel, gbc);
		for (int i = 0; i < nbUR; i++) {
			substrat[i] = new Substrat();
			substratBox.addComposant(substrat[i], i + 1, 0);
		}

		/*
		 * Ajout de toutes les boites au tab
		 */
		this.addComposant(urBox);
		this.addComposant(generalBox);
		this.addComposant(legende);
		this.addComposant(faciesBox);
		this.addComposant(profondeurBox);
		this.addComposant(vitesseBox);
		this.addComposant(eclairementBox);
		this.addComposant(substratBox);
	}

	public void setUniteReleve(Unite_releves iur) {
		ur = iur;
	}

	public int validation() {
		int retour = super.validation();
		/*
		 * Verification que le total de la part de l'UR fait 100 %
		 */
		String part1, part2;
		int level1 = 0, level2 = 0, level = 0;
		part1 = general[0].getData("pc_ur");
		if (part1.isEmpty())
			level1 = 3;
		if (1 == nbUR) {
			try {
				if (!part1.equals("100"))
					level1 = 3;
			} catch (NullPointerException e) {
				level1 = 3;
			}
		} else {
			part2 = general[1].getData("pc_ur");
			if (part2.isEmpty()) {
				level2 = 3;
			} else {
				if (level1 == 0) {
					int total = Integer.parseInt(part1) + Integer.parseInt(part2);
					if (total != 100) {
						level1 = 3;
						level2 = 3;
					}
				}
			}
			general[1].setBordure("pc_ur", level2);
		}
		general[0].setBordure("pc_ur", level1);
		if (level1 > level)
			level = level1;
		if (level2 > level)
			level = level2;
		if (level > retour)
			retour = level;
		return retour;
	}

	/**
	 * Remet a jour la combo en fonction du protocole
	 * 
	 * @param protocole
	 */
	public void setTypeURList(String lProtocole) {
		for (int i = 0; i < nbUR; i++) {
			general[i].setTypeURList(i, lProtocole);
		}
	}

	/**
	 * Classes d'affichage des composants
	 */
	/**
	 * Donnees generales
	 * 
	 * @author quinton
	 * 
	 */
	class General extends ComposantAlisma {

		public General() {
			setDimensionDefault(dimDefault);
			addTextPourcentage("pc_ur", 0, 0, 1);
			addCombo("type_ur", 0, 1, 1, false);
			addTextNumeric("longueur_ur", 0, 2, 1);
			addTextDecimal("largeur_ur", 0, 3, 1);
			addCombo("facies", 0, 4, 1, false);
			addComboItemList("facies", ur.params.get("facies").getArray(), false);
			setFieldMandatory(new String[] { "type_ur", "pc_ur" });
			setFieldNecessary(new String[] {});
			setFieldRecommanded(new String[] { "longueur_ur", "largeur_ur", "facies" });
		}

		/**
		 * Positionne le contenu du combo typeUR en fonction du numero d'UR et
		 * du protocole
		 * 
		 * @param num_ur
		 * @param protocole
		 */
		public void setTypeURList(int i, String protocole) {
			logger.debug("setTypeURList : i = " + String.valueOf(i) + ", protocole = " + protocole);
			int protocole_id;
			try {
				protocole_id = ur.params.get("protocole").getKeyFromValue(protocole);
			} catch (NullPointerException e) {
				protocole_id = 1;
			}
			logger.debug("protocole_id :" + String.valueOf(protocole_id));
			int num_ur = i + 1;
			logger.debug("num_ur : " + String.valueOf(num_ur));
			String[] liste = ur.params.get("type_ur").getArray();
			if (nbUR == 1) {
				addComboItemList("type_ur", new String[] { liste[4], liste[3] }, true);
			} else {
				switch (protocole_id) {
				case 1:
					if (num_ur == 1) {
						addComboItemList("type_ur", new String[] { liste[1] }, true);
					} else
						addComboItemList("type_ur", new String[] { liste[2] }, true);
					break;
				case 2:
					if (num_ur == 1) {
						addComboItemList("type_ur",

						new String[] { liste[5], liste[7], liste[3] }, true);
					} else
						addComboItemList("type_ur", new String[] { liste[6], liste[8], liste[3] }, true);
					break;
				case 3:
					if (num_ur == 1) {
						addComboItemList("type_ur",

						new String[] { liste[1], liste[5], liste[7], liste[3] }, true);
					} else
						addComboItemList("type_ur", new String[] { liste[2], liste[6], liste[8], liste[3] }, true);
					break;
				}

			}
		}

	}

	/**
	 * Saisie des facies
	 * 
	 * @author quinton
	 * 
	 */
	class Facies extends ComposantMeso {

		public Facies() {
			setDimensionDefault(dimDefault);

			addComboMesoList(new String[] { "ch_lentique", "pl_lentique","mouille","fosse_dissipation",
					"ch_lotique","radier","cascade","pl_courant","rapide"
					/*,"autreType"*/
					});
			/*
			 * Rajout du champ autreType
			 */
			addCombo("facies_autre_type", 0, 9, 1, false);
			addComboItemList("facies_autre_type", ur.params.get("facies_autre_type").getArray(), false);
			// addFieldRecommanded("autreType");
			addComboMeso("autretypeclass", 0, 10, 1);
		}

		public int validation() {
			int retour = super.validation();
//			logger.debug("Facies - valeur retour : " + String.valueOf(retour));
//			if (retour == 2)
//				retour = 1;
			/*
			 * Rajout du controle autre type : si libelle saisi, la classe doit
			 * etre renseignee
			 */
			String autreType = getData("facies_autre_type");
			String autretypeclass = getData("autretypeclass");
//			logger.debug("facies - autreType - empty ? " + String.valueOf(autreType.isEmpty()));
//			logger.debug("facies - autretypeclass - empty ? " + String.valueOf(autretypeclass.isEmpty()));
			if (!autreType.isEmpty()) {
				if (autretypeclass.isEmpty()) {
					setBordure("autretypeclass", 3);
					retour = 3;
				} else {
					setBordure("autretypeclass", 0);
				}
				setBordure("facies_autre_type", 0);
			} else {
				if (!autretypeclass.isEmpty()) {
					setBordure("facies_autre_type", 3);
					retour = 3;
				} else {
					setBordure("facies_autre_type", 0);
				}
				setBordure("autretypeclass", 0);
			}
//			logger.debug("facies - valeur retour en fin de validation : " + String.valueOf(retour));
			this.bordure.setBordure(pane, retour);
			return retour;
		}
	}

	class Profondeur extends ComposantMeso {
		public Profondeur() {
			setDimensionDefault(dimDefault);
			addComboMesoList(new String[] { "p1", "p2", "p3", "p4", "p5" });
		}
	}

	class Vitesse extends ComposantMeso {
		public Vitesse() {
			setDimensionDefault(dimDefault);
			addComboMesoList(new String[] { "v1", "v2", "v3", "v4", "v5" });
		}
	}

	class Eclairement extends ComposantMeso {
		public Eclairement() {
			setDimensionDefault(dimDefault);
			addComboMesoList(new String[] { "tres_ombrage", "ombrage", "peu_ombrage", "eclaire", "tres_eclaire" });
		}
	}

	class Substrat extends ComposantMeso {
		public Substrat() {
			setDimensionDefault(dimDefault);
			addComboMesoList(new String[] { "vase_limons", "cailloux_pierres", "sable_graviers", "terre_marne_tourbe",
					"blocs_dalles", "racines", "debris_org", "artificiel" });
		}
	}

	/**
	 * Recupere le taux d'occupation de l'UR dans la station
	 * 
	 * @param num_ur
	 * @return double
	 */
	public double getpcUR(int num_ur) {
		double taux;
		String sValue = general[num_ur - 1].getData("pc_ur");
		if (sValue.isEmpty()) {
			taux = 0.0;
		} else {
			taux = Double.parseDouble(sValue) / 100;
		}
		return taux;
	}

	/**
	 * Retourne les donnees de l'onglet pour l'ur consideree
	 * 
	 * @param num_ur
	 * @return Hashtable<String, String>
	 */
	public Hashtable<String, String> getDataUR(int num_ur) {
		int i = num_ur - 1;
		Hashtable<String, String> data = eclairement[i].getData();
		data = hashtableFusionner(data, facies[i].getData());
		data = hashtableFusionner(data, general[i].getData());
		data = hashtableFusionner(data, profondeur[i].getData());
		data = hashtableFusionner(data, substrat[i].getData());
		data = hashtableFusionner(data, vitesse[i].getData());
		data.put("num_ur", Integer.toString(num_ur));
		return data;
	}

	/**
	 * Mise a jour de l'ensemble des donnees de l'onglet
	 * 
	 * @param List
	 *            <Hashtable<String, String>> lData : liste des donnees
	 */
	public void setDataGlobal(List<Hashtable<String, String>> lData) {
		int num_ur, i;
		for (Hashtable<String, String> ligne : lData) {
			num_ur = Integer.parseInt(ligne.get("num_ur"));
			/*
			 * mise a jour de la valeur de la cle
			 */
			setIdUR(num_ur, Integer.parseInt(ligne.get("id_ur")));
			i = num_ur - 1;
			/*
			 * Mise a jour des donnees
			 */
			eclairement[i].setData(ligne);
			facies[i].setData(ligne);
			general[i].setData(ligne);
			profondeur[i].setData(ligne);
			substrat[i].setData(ligne);
			vitesse[i].setData(ligne);
		}
	}

	/**
	 * Positionne la valeur de la cle de l'UR
	 * 
	 * @param num_ur
	 * @param id
	 */
	public void setIdUR(int num_ur, int id) {
		idUR[num_ur - 1] = id;
	}

	/**
	 * Retourne la valeur de la cle de l'UR consideree
	 * 
	 * @param num_ur
	 * @return
	 */
	public int getIdUR(int num_ur) {
		return idUR[num_ur - 1];
	}

}
