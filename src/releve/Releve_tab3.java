/**
 * 
 */
package releve;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import database.TaxonView;
import database.Unite_releves;
import utils.ComposantAlisma;
import utils.Langue;
import utils.Parametre;

/**
 * @author quinton
 * 
 *         3eme onglet du releve
 * 
 */
public class Releve_tab3 extends ComposantAlisma {
	Object obj;
	Unite_releves ur;
	Dimension dimLabel = new Dimension(220, 20);
	VegeDroite[] vd = new VegeDroite[2];
	VegeGauche[] vg = new VegeGauche[2];
	ReleveListeTable cTable;
	ComposantAlisma ibmr = new ComposantAlisma();
	ComposantAlisma newLine;
	JScrollPane jsp_table;
	TaxonView taxonViewDb = new TaxonView();
	public Indice indice = new Indice();
	public Groupe groupe = new Groupe();
	public Taxon taxon = new Taxon();
	public Coef coef = new Coef();
	int nbUR = 2;


	public Releve_tab3(int pnbUR, Unite_releves fur) {
		obj = this;
		nbUR = pnbUR;
		ur = fur;
		/*
		 * Dessin de la boite de vegetation
		 */
		ComposantAlisma vl1 = new ComposantAlisma(), vl2 = new ComposantAlisma(), vegetalBox = new ComposantAlisma();
		vegetalBox.setTitle("vegetalisation");
		vegetalBox.setDimensionDefault(100, 20);

		/*
		 * Initialisation de la nouvelle ligne
		 */
		newLine = new NewLine();
		/*
		 * Positionne les noms d'ur en tete de colonne
		 */
		vegetalBox.addLabel("ur1", 1, 0);
		vegetalBox.addLabel("ur1", 4, 0);
		if (nbUR == 2) {
			vegetalBox.addLabel("ur2", 2, 0);
			vegetalBox.addLabel("ur2", 5, 0);
		} else {
			vegetalBox.addLabel("libelleVide", 2, 0);
			vegetalBox.addLabel("libelleVide", 5, 0);
		}

		vl1.setDimensionDefault(dimLabel);
		vl1.addLabelList(new String[] { "pc_veg", "periph", "flottante",
				"immerg", "helophytes" }, 0, 0);
		vegetalBox.addComposant(vl1, 0, 1);
		vl2.setDimensionDefault(dimLabel);
		vl2.addLabelList(new String[] { "heterotro", "algues", "pcbryo",
				"pterido", "phanero" }, 0, 0);
		vegetalBox.addComposant(vl2, 3, 1);

		/*
		 * Creation des composants pour decrire la vegetation
		 */
		for (int i = 0; i < nbUR; i++) {
			vg[i] = new VegeGauche();
			vegetalBox.addComposant(vg[i], i + 1, 1);
			vd[i] = new VegeDroite();
			vegetalBox.addComposant(vd[i], i + 4, 1);
		}

		/*
		 * Composant d'affichage de l'indice
		 */
		ibmr.setTitle("indice");
		GridBagConstraints lgbc = new GridBagConstraints();
		lgbc.anchor = GridBagConstraints.NORTHWEST;
		ibmr.addComposant(indice, lgbc);
		lgbc.gridx = 1;
		lgbc.gridy = 0;
		ibmr.addComposant(coef, lgbc);
		lgbc.gridx = 2;
		lgbc.gridy = 0;
		ibmr.addComposant(groupe, lgbc);
		lgbc.gridx = 3;
		lgbc.gridy = 0;
		ibmr.addComposant(taxon, lgbc);

		/*
		 * Gestion de la liste des taxons
		 */
		cTable = new ReleveListeTable(nbUR);
		jsp_table = new JScrollPane(cTable.getTable());
		cTable.addObserver(this);

		/*
		 * ajout des composants pour l'affichage
		 */
		addComposant(vegetalBox, 0, 0);
		addComposant(ibmr, 0, 1);
		addComposant(newLine, 0, 2);
		gbc.gridy = 3;
		gbc.fill = GridBagConstraints.BOTH;
		getPane().add(jsp_table, gbc);

	}

	public void setUniteReleve(Unite_releves iur) {
		ur = iur;
	}

	/**
	 * Surcharge de la fonction de validation
	 */
	public int validation() {
		int retour = super.validation();
		/*
		 * if (retour < 2 && cTable.getListData().isEmpty()) { retour = 2; }
		 */
		return retour;
	}

	/**
	 * Bascule les champs pour les rendre actifs ou inactifs
	 */
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		newLine.setEnabled(enabled);
		cTable.setEnabled(enabled);
	}

	/**
	 * Met a jour les donnees issues du calcul de l'indice
	 * 
	 * @param double ibmr
	 * @param double robustesse
	 * @param string
	 *            maxTaxon
	 */
	public void setDataCalcul(Hashtable<String, String> data) {
		String [] fields = {"ibmr_value", "robustesse_value", "taxon_robustesse"};
		try {
		for (String field : fields)
			indice.setValue(field, data.get(field));
		Double ibmr = Double.parseDouble(data.get("ibmr_value"));
		Double robustesse = Double.parseDouble(data.get("robustesse_value"));
		indice.setValue("nivTroph", getNiveauTrophique(ibmr));
		indice.setValue("nivTrophRob", getNiveauTrophique(robustesse));
		indice.setValue("niveau_trophique_id", getIdNiveauTrophique(ibmr));
		indice.setValue("robustesse_niveau_trophique_id",
				getIdNiveauTrophique(robustesse));
		} catch (Exception e) {
			logger.debug(e.getMessage());
		}

	}

	/**
	 * Calcul des donnees affichees dans le tableau des resultats, hormis
	 * l'indice, et mise a jour des donnees dans les tableaux
	 */
	public void calculIndicateurs() {
		Hashtable<String, Double> cs = new Hashtable<String, Double>();
		Hashtable<String, Double> coefs = new Hashtable<String, Double>();
		Hashtable<String, Integer> groupes = new Hashtable<String, Integer>();
		Hashtable<String, Integer> taxons = new Hashtable<String, Integer>();
		Double value;
		String valString;
		/*
		 * Initialisations
		 */
		cs.put("nb", 0.0);
		cs.put("total", 0.0);
		cs.put("min", 256.0);
		cs.put("max", -1.0);
		cs.put("moy", 0.0);
		coefs.put("nb", 0.0);
		coefs.put("total", 0.0);
		coefs.put("min", 256.0);
		coefs.put("max", -1.0);
		coefs.put("moy", 0.0);
		groupes.put("HET", 0);
		groupes.put("ALG", 0);
		groupes.put("BRI", 0);
		groupes.put("PTE", 0);
		groupes.put("PHA", 0);
		groupes.put("LIC", 0);
		taxons.put("nb", 0);
		taxons.put("contrib", 0);
		taxons.put("steno1", 0);
		taxons.put("steno2", 0);
		taxons.put("steno3", 0);

		/*
		 * iteration sur les donnees du tableau des especes
		 */
		List<Hashtable<String, String>> ldata = cTable.getListData();
		Hashtable<String, String> data;
		Iterator<Hashtable<String, String>> ildata = ldata.iterator();
		while (ildata.hasNext()) {
			data = ildata.next();
			/*
			 * Calcul mini, maxi
			 */
			if (!data.get("cote_spe").isEmpty()) {
				try {
					value = Double.parseDouble(data.get("cote_spe"));
				} catch (Exception e) {
					value = 0.0;
				}
				cs = coefPopulate(cs, value);
			}
			if (!data.get("coef_steno").isEmpty()) {
				try {
					value = Double.parseDouble(data.get("coef_steno"));
				} catch (Exception e) {
					value = 0.0;
				}
				coefs = coefPopulate(coefs, value);
			}
			/*
			 * Traitement du groupe d'appartenance
			 */
			switch (data.get("nom_groupe")) {
			case "HET":
				groupes.put("HET", groupes.get("HET") + 1);
				break;
			case "ALG":
				groupes.put("ALG", groupes.get("ALG") + 1);
				break;
			case "BRh":
			case "BRm":
			case "BRl":
				groupes.put("BRI", groupes.get("BRI") + 1);
				break;
			case "PTE":
				groupes.put("PTE", groupes.get("PTE") + 1);
				break;
			case "PHe":
			case "PHg":
			case "PHx":
			case "PHy":
				groupes.put("PHA", groupes.get("PHA") + 1);
				break;
			case "LIC":
				groupes.put("LIC", groupes.get("LIC") + 1);
				break;
			}
			/*
			 * Traitement des taxons
			 */
			taxons.put("nb", taxons.get("nb") + 1);
			/*
			 * Nbre de taxons contributifs
			 */

			if (!data.get("cote_spe").isEmpty()
					|| !data.get("coef_steno").isEmpty())
				taxons.put("contrib", taxons.get("contrib") + 1);
			/*
			 * Nbre de taxons par coef steno
			 */
			if (!data.get("coef_steno").isEmpty()) {
				valString = data.get("coef_steno");
				try {
					taxons.put("steno" + valString,
							taxons.get("steno" + valString) + 1);
				} catch (Exception e) {
				}
			}
		}
		/*
		 * Fin de traitement des lignes Calcul de la moyenne
		 */
		calculMoyenne(coefs);
		calculMoyenne(cs);
		/*
		 * Mise a jour des donnees dans la fenetre
		 */
		if (cs.get("min") == 256)
			cs.put("min", 0.0);
		if (cs.get("max") == -1)
			cs.put("max", 0.0);
		if (coefs.get("min") == 256)
			coefs.put("min", 0.0);
		if (coefs.get("max") == -1)
			coefs.put("max", 0.0);
		coef.setValue("cs_min", cs.get("min").toString());
		coef.setValue("cs_max", cs.get("max").toString());
		coef.setValue("cs_moy", cs.get("moy").toString());
		coef.setValue("coef_min", coefs.get("min").toString());
		coef.setValue("coef_max", coefs.get("max").toString());
		coef.setValue("coef_moy", coefs.get("moy").toString());
		groupe.setValue("nbtaxon_het", groupes.get("HET").toString());
		groupe.setValue("nbtaxon_alg", groupes.get("ALG").toString());
		groupe.setValue("nbtaxon_bry", groupes.get("BRI").toString());
		groupe.setValue("nbtaxon_pte", groupes.get("PTE").toString());
		groupe.setValue("nbtaxon_pha", groupes.get("PHA").toString());
		groupe.setValue("nbtaxon_lic", groupes.get("LIC").toString());
		taxon.setValue("nbtaxon_total", taxons.get("nb").toString());
		taxon.setValue("nbtaxon_contrib", taxons.get("contrib").toString());
		taxon.setValue("nbtaxon_steno1", taxons.get("steno1").toString());
		taxon.setValue("nbtaxon_steno2", taxons.get("steno2").toString());
		taxon.setValue("nbtaxon_steno3", taxons.get("steno3").toString());
	}

	/**
	 * Extrait les differentes valeurs pour les coefficients
	 * 
	 * @param coef
	 * @param value
	 * @return Hashtable<String, Double>
	 */
	Hashtable<String, Double> coefPopulate(Hashtable<String, Double> coef,
			Double value) {
		coef.put("nb", coef.get("nb") + 1);
		coef.put("total", coef.get("total") + value);
		if (value < coef.get("min"))
			coef.put("min", value);
		if (value > coef.get("max"))
			coef.put("max", value);
		return coef;
	}

	/**
	 * Calcule la moyenne pour les coefficients
	 * 
	 * @param coef
	 * @return Hashtable<String, Double>
	 */
	Hashtable<String, Double> calculMoyenne(Hashtable<String, Double> coef) {
		if (coef.get("nb") > 0) {
			Double moyenne = coef.get("total") / coef.get("nb");
			/*
			 * Arrondi
			 */
			Integer moyInt = (int) (moyenne * 100);
			moyenne = moyInt.doubleValue() / 100;
			coef.put("moy", moyenne);
		}
		return coef;
	}

	/**
	 * Calcul des niveau trophiques
	 * 
	 * @param ibmr
	 * @return string
	 */
	String getNiveauTrophique(double ibmr) {
		String libelle = "";
		if (ibmr > 0) {
			if (ibmr > 14) {
				libelle = Langue.getString("tresFaible");
			} else if (ibmr > 12) {
				libelle = Langue.getString("faible");
			} else if (ibmr > 10) {
				libelle = Langue.getString("moyen");
			} else if (ibmr > 8) {
				libelle = Langue.getString("fort");
			} else
				libelle = Langue.getString("tresEleve");
		}
		return libelle;
	}

	/**
	 * Retourne le niveau trophique sous forme numerique, pour stockage en base
	 * 
	 * @param ibmr
	 * @return int
	 */
	String getIdNiveauTrophique(double ibmr) {
		String id = "";
		if (ibmr > 0) {
			if (ibmr > 14) {
				id = "1";
			} else if (ibmr > 12) {
				id = "2";
			} else if (ibmr > 10) {
				id = "3";
			} else if (ibmr > 8) {
				id = "4";
			} else
				id = "5";
		}
		return id;
	}

	/**
	 * Redimensionne la fenetre aux dimensions indiquees
	 * 
	 * @param dim
	 */
	public void resizeComponent(Dimension dim) {
		this.setSize(dim);
	}

	/**
	 * Donnees concernant la vegetalisation - colonne de gauche
	 * 
	 * @author quinton
	 * 
	 */
	class VegeGauche extends ComposantAlisma {

		public VegeGauche() {
			addTextPourcentageDecimal("pc_vegetalisation", 0, 0, 1);
			addCombo("periphyton", 0, 1, 1, false);
			addComboItemList("periphyton", ur.params.get("periphyton")
					.getArray(), false);
			addTextPourcentage("pc_flottante", 0, 2, 1);
			addTextPourcentage("pc_immerg", 0, 3, 1);
			addTextPourcentage("pc_helophyte", 0, 4, 1);
			addFieldNecessary("pc_vegetalisation");
		}
	}

	/**
	 * Donnees concernant la vegetalisation - colonne de droite
	 * 
	 * @author quinton
	 * 
	 */
	class VegeDroite extends ComposantAlisma {

		public VegeDroite() {
			addTextPourcentage("pc_heterot", 0, 0, 1);
			addTextPourcentage("pc_algues", 0, 1, 1);
			addTextPourcentage("pc_bryo", 0, 2, 1);
			addTextPourcentage("pc_lichen", 0, 3, 1);
			addTextPourcentage("pc_phanero", 0, 4, 1);
		}
	}

	/**
	 * Affichage de l'indice
	 * 
	 * @author quinton
	 *
	 */
	class Indice extends ComposantAlisma {
		public Indice() {
			setTitle();
			Dimension dimData = new Dimension(70, 12);
			Dimension dimLabel = new Dimension(110, 12);
			addLabel("ibmr", 0, 0, dimLabel);
			addLabel("nivTroph", 0, 1, dimLabel);
			addLabel("robustesse", 0, 2, dimLabel);
			addLabel("nivTroph", 0, 3, dimLabel);
			addLabel("taxonRob", 0, 4, dimLabel);
			addLabelAsValue("ibmr_value", "", 1, 0, dimData);
			addLabelAsValue("nivTroph", "", 1, 1, dimData);
			addLabelAsValue("robustesse_value", "", 1, 2, dimData);
			addLabelAsValue("nivTrophRob", "", 1, 3, dimData);
			addLabelAsValue("taxon_robustesse", "", 1, 4, dimData);
			addLabelAsValue("vide", "", 1, 5, dimData);
			addLabelAsValue("vide", "", 1, 6, dimData);
			addHidden("niveau_trophique_id", "");
			addHidden("robustesse_niveau_trophique_id", "");
		}
	}

	/**
	 * Affichage des differents coefficients de l'indice
	 * 
	 * @author quinton
	 *
	 */
	class Coef extends ComposantAlisma {
		public Coef() {
			setTitle("");
			Dimension dimData = new Dimension(110, 12);
			Dimension dimLabel = new Dimension(90, 12);
			addLabel("coteSpe", 1, 0, dimData);
			addLabel("coefSteno", 2, 0, dimData);
			addLabel("moy", 0, 1, dimLabel);
			addLabel("mini", 0, 2, dimLabel);
			addLabel("maxi", 0, 3, dimLabel);
			addLabelAsValue("cs_moy", "", 1, 1, dimData);
			addLabelAsValue("cs_min", "", 1, 2, dimData);
			addLabelAsValue("cs_max", "", 1, 3, dimData);
			addLabelAsValue("coef_moy", "", 2, 1, dimData);
			addLabelAsValue("coef_min", "", 2, 2, dimData);
			addLabelAsValue("coef_max", "", 2, 3, dimData);
			addLabelAsValue("vide", "", 1, 4, dimData);
			addLabelAsValue("vide", "", 1, 5, dimData);
			addLabelAsValue("vide", "", 1, 6, dimData);
		}
	}

	/**
	 * Affichage des differents groupes de l'indice
	 * 
	 * @author quinton
	 *
	 */
	class Groupe extends ComposantAlisma {
		public Groupe() {
			setTitle();
			Dimension dimData = new Dimension(80, 12);
			Dimension dimLabel = new Dimension(60, 12);
			addLabel("groupe", 0, 0, dimData);
			addLabel("nbTaxon", 1, 0, dimData);
			addLabel("het", 0, 1, dimLabel);
			addLabel("alg", 0, 2, dimLabel);
			addLabel("bryo", 0, 3, dimLabel);
			addLabel("pte", 0, 4, dimLabel);
			addLabel("phan", 0, 5, dimLabel);
			addLabel("lic", 0, 6, dimLabel);
			addLabelAsValue("nbtaxon_het", "", 1, 1, dimData);
			addLabelAsValue("nbtaxon_alg", "", 1, 2, dimData);
			addLabelAsValue("nbtaxon_bry", "", 1, 3, dimData);
			addLabelAsValue("nbtaxon_pte", "", 1, 4, dimData);
			addLabelAsValue("nbtaxon_pha", "", 1, 5, dimData);
			addLabelAsValue("nbtaxon_lic", "", 1, 6, dimData);
		}
	}

	/**
	 * Affichage du nombre de taxons de l'indice
	 * 
	 * @author quinton
	 *
	 */
	class Taxon extends ComposantAlisma {
		public Taxon() {
			setTitle();
			Dimension dimData = new Dimension(80, 12);
			Dimension dimLabel = new Dimension(110, 12);
			addLabel("nbTaxon", 1, 0, dimData);
			addLabel("total", 0, 1, dimLabel);
			addLabel("contributif", 0, 2, dimLabel);
			addLabel("steno1", 0, 3, dimLabel);
			addLabel("steno2", 0, 4, dimLabel);
			addLabel("steno3", 0, 5, dimLabel);
			addLabelAsValue("nbtaxon_total", "", 1, 1, dimData);
			addLabelAsValue("nbtaxon_contrib", "", 1, 2, dimData);
			addLabelAsValue("nbtaxon_steno1", "", 1, 3, dimData);
			addLabelAsValue("nbtaxon_steno2", "", 1, 4, dimData);
			addLabelAsValue("nbtaxon_steno3", "", 1, 5, dimData);
			addLabelAsValue("vide", "", 1, 6, dimData);
		}
	}

	/**
	 * Composant permettant de rajouter une nouvelle ligne dans le tableau
	 * 
	 * @author quinton
	 * 
	 */
	class NewLine extends ComposantAlisma {

		DocumentListener dl_id_taxon, dl_nom_taxon;
		ActionListener al_addligne;
		ComposantAlisma obj;
		JTextField jtf;
		String cd_taxon = null, nom_taxon = null;
		private JCheckBox isCf = new JCheckBox();
		private JButton addLigne;
		private JTextField pc_UR1 = new JTextField();
		private JTextField pc_UR2 = new JTextField();

		/**
		 * Constructeur
		 */
		public NewLine() {
			obj = this;
			/*
			 * Initialisation des listeners
			 */
			addListeners();
			/*
			 * Ajout des libelles
			 */
			setDimensionDefault(80, 20);
			Dimension dcf = new Dimension(30, 20);
			addLabel("code", 0, 0);
			addLabel("nomTaxon", 2, 0);
			addLabel("ur1", 4, 0, dcf);
			if (nbUR == 2)
				addLabel("ur2", 6, 0, dcf);
			addLabel("cf", 8, 0, dcf);

			/*
			 * Ajout des boutons
			 */
			addLigne = new JButton(Langue.getString("ajouter"));
			addLigne.setPreferredSize(new Dimension(90, 20));
			addLigne.addActionListener(al_addligne);
			addLigne.setEnabled(false);
			gbc.gridx = 10;
			gbc.gridy = 0;
			getPane().add(addLigne, gbc);
			gbc.gridx = 11;

			/*
			 * Ajout des champs
			 */
			Dimension dimPcUR = new Dimension(30, 20);
			addTextField("id_taxon", 1, 0, 1);
			setDimension("id_taxon", new Dimension(70, 20));
			addTextField("nom_taxon", 3, 0, 1);
			setDimension("nom_taxon", new Dimension(150, 20));
			addTextPourcentageDecimal("pc_UR1", 5, 0, 1);
			setDimension("pc_UR1", dimPcUR);
			if (nbUR == 2) {
				addTextPourcentageDecimal("pc_UR2", 7, 0, 1);
				setDimension("pc_UR2", dimPcUR);
			}
			addCheckBox("cf", false, 9, 0, 1);
			fieldList.get("cf").setBackground(Parametre.cCentral);

			/*
			 * Rajout des listeners
			 */

			jtf = (JTextField) fieldList.get("id_taxon");
			jtf.getDocument().addDocumentListener(dl_id_taxon);
			jtf = (JTextField) fieldList.get("nom_taxon");
			jtf.getDocument().addDocumentListener(dl_nom_taxon);
		}

		/**
		 * Definition des ecouteurs
		 */
		void addListeners() {
			/*
			 * Ecouteur pour le code taxon
			 */
			dl_id_taxon = new DocumentListener() {
				public void changedUpdate(DocumentEvent e) {
					setNomTaxonFromCode();
				}

				public void removeUpdate(DocumentEvent e) {
					setNomTaxonFromCode();
				}

				public void insertUpdate(DocumentEvent e) {
					setNomTaxonFromCode();
				}
			};

			/*
			 * Ecouteur pour le nom du taxon
			 */

			dl_nom_taxon = new DocumentListener() {
				public void changedUpdate(DocumentEvent e) {
					setCodeTaxonFromNom();
				}

				public void removeUpdate(DocumentEvent e) {
					setCodeTaxonFromNom();
				}

				public void insertUpdate(DocumentEvent e) {
					setCodeTaxonFromNom();
				}
			};

			/*
			 * Ecouteur pour le bouton addLigne
			 */
			al_addligne = new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					Hashtable<String, String> data = new Hashtable<String, String>();
					data.put("id_taxon", cd_taxon);
					data.put("cf", obj.getData("cf"));
					data.put("pc_UR1", obj.getData("pc_UR1"));
					if (nbUR == 2) {
						data.put("pc_UR2", obj.getData("pc_UR2"));
					} else {
						data.put("pc_UR2", "");
					}
					data.put("id_ligne_op_controle", "-1");
					/*
					 * Ajout de la ligne dans la table
					 */
					cTable.addLigne(data);
					/*
					 * Reinitialise la ligne de saisie
					 */
					reset();
				}
			};
		}


		/**
		 * Reintialise la ligne de saisie
		 */
		void reset() {
			/*
			 * Reinitialisation des zones
			 */
			setValue("id_taxon", "");
			setValue("cf", "0");
			setValue("pc_UR1", "");
			if (nbUR == 2)
				setValue("pc_UR2", "");
			setValue("nom_taxon", "");
			cd_taxon = "";
			addLigne.setEnabled(false);
			/*
			 * Repositionnement du focus
			 */
			setFocus("id_taxon");
		}

		/**
		 * Fixe le contenu de la zone nom_taxon a partir du code saisi
		 */
		private void setNomTaxonFromCode() {
			String id = obj.getData("id_taxon");
			JTextField jtfnom = (JTextField) obj.fieldList.get("nom_taxon");
			JTextField jtfid = (JTextField) obj.fieldList.get("id_taxon");
			jtfnom.getDocument().removeDocumentListener(dl_nom_taxon);
			jtfid.getDocument().removeDocumentListener(dl_id_taxon);
			/*
			 * Mise a vide du champ nom
			 */
			jtfnom.setText(null);
			if (!id.isEmpty()) {
				/*
				 * Recuperation du nom du taxon
				 */
				Hashtable<String, String> ligne = taxonViewDb
						.getFirstFromCd(id);
				/*
				 * Suppression de l'ecouteur pour la zone nom
				 */
				jtfnom.getDocument().removeDocumentListener(dl_nom_taxon);
				try {
					if (!ligne.isEmpty()) {
						/*
						 * Affectation du nom du taxon et stockage en variable
						 * globale de classe
						 */
						nom_taxon = ligne.get("nom_taxon");
						jtf.setText(nom_taxon);
						cd_taxon = ligne.get("cd_taxon");
						addLigne.setEnabled(true);
					} else {
						nom_taxon = "";
						cd_taxon = "";
						addLigne.setEnabled(false);
					}
				} catch (NullPointerException e) {
					nom_taxon = "";
					cd_taxon = "";
					addLigne.setEnabled(false);
				}
			}
			/*
			 * Reactivation de l'ecouteur pour la zone nom
			 */
			jtfnom.getDocument().addDocumentListener(dl_nom_taxon);
			jtfid.getDocument().addDocumentListener(dl_id_taxon);

		}

		/**
		 * Recherche le code taxon a partir du nom
		 */
		private void setCodeTaxonFromNom() {
			String nom = obj.getData("nom_taxon");
			logger.debug("nom_taxon : "+nom);
			if (!nom.isEmpty()) {
			JTextField jtfnom = (JTextField) obj.fieldList.get("nom_taxon");
			JTextField jtfid = (JTextField) obj.fieldList.get("id_taxon");
			jtfnom.getDocument().removeDocumentListener(dl_nom_taxon);
			jtfid.getDocument().removeDocumentListener(dl_id_taxon);

			
			/*
			 * Mise a vide du champ nom
			 */
			jtfid.setText(null);
			
				/*
				 * Suppression de l'ecouteur pour la zone nom
				 */
				Hashtable<String, String> ligne = taxonViewDb
						.getFirstFromName(nom);
				try {
					if (!ligne.isEmpty()) {
						/*
						 * Affectation du nom du taxon et stockage en variable
						 * globale de classe
						 */
						nom_taxon = ligne.get("nom_taxon");
						cd_taxon = ligne.get("cd_taxon");
						jtfid.setText(cd_taxon);
						addLigne.setEnabled(true);
					} else {
						cd_taxon = "";
						nom_taxon = "";
						addLigne.setEnabled(false);
					}
				} catch (NullPointerException e) {
					cd_taxon = "";
					nom_taxon = "";
					addLigne.setEnabled(false);
				}
				/*
				 * Reactivation de l'ecouteur pour la zone nom
				 */
				jtfnom.getDocument().addDocumentListener(dl_nom_taxon);
				jtfid.getDocument().addDocumentListener(dl_id_taxon);

			}

			

			/*
			 * Ajoute une ligne dans le tableau
			 */
			al_addligne = new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					if (!cd_taxon.isEmpty()) {
						Hashtable<String, String> lData = new Hashtable<String, String>();
						lData.put("cd_taxon", cd_taxon);
						if (isCf.isSelected()) {
							lData.put("cf", "1");
						} else
							lData.put("cf", "0");
						lData.put("pc_UR1", pc_UR1.getText());
						lData.put("pc_UR2", pc_UR2.getText());
						cTable.addLigne(lData);
					}
				}
			};
		}
	}

	/**
	 * Retourne le nombre de taxons saisis
	 * 
	 * @return int
	 */
	public int getNbTaxons() {
		return cTable.getTable().getRowCount();
	}

	/**
	 * Retourne la liste des taxons saisis
	 * 
	 * @return List<Object>
	 */
	public List<Hashtable<String, String>> getListTaxon() {
		return cTable.getListData();
	}

	/**
	 * Retourne les donnees correspondant a l'UR
	 * 
	 * @param numUR
	 * @return Hashtable<String, String>
	 */
	public Hashtable<String, String> getDataUR(int numUR) {
		int i = numUR - 1;
		return hashtableFusionner(vg[i].getData(), vd[i].getData());
	}

	/**
	 * Positionne la cle generee lors de l'ecriture dans le taxon
	 * 
	 * @param numLigne
	 * @param key
	 */
	public void setTaxonKey(int numLigne, int key) {
		cTable.setTaxonKey(numLigne, key);
	}

	/**
	 * Met a jour les donnees de l'UR
	 * 
	 * @param List
	 *            <Hashtable<String, String>> lData
	 */
	public void setDataUr(List<Hashtable<String, String>> lData) {
		int i;
		for (Hashtable<String, String> ligne : lData) {
			i = Integer.parseInt(ligne.get("numUR")) - 1;
			vg[i].setData(ligne);
			vd[i].setData(ligne);
		}
	}

	/**
	 * Met a jour les donnees dans la table des taxons
	 * 
	 * @param List
	 *            <Hashtable<String, String>> lData
	 */
	public void setDataTaxon(List<Hashtable<String, String>> lData) {
		for (Hashtable<String, String> ligne : lData) {
			cTable.addLigne(ligne);
		}
	}

	/**
	 * Reinitialise les indices
	 */
	public void resetCalcul() {
		resetDataIbmr();
	}

	public void setDataIbmr(Hashtable<String, String> ibmrData) {
		coef.setData(ibmrData);
		groupe.setData(ibmrData);
		indice.setData(ibmrData);
		taxon.setData(ibmrData);
	}

	public void resetDataIbmr() {
		for (Enumeration<String> keys = indice.fieldList.keys(); keys
				.hasMoreElements();) {
			String key = keys.nextElement();
			indice.setValue(key, "");
		}
		for (Enumeration<String> keys = coef.fieldList.keys(); keys
				.hasMoreElements();) {
			String key = keys.nextElement();
			coef.setValue(key, "");
		}
		for (Enumeration<String> keys = groupe.fieldList.keys(); keys
				.hasMoreElements();) {
			String key = keys.nextElement();
			groupe.setValue(key, "");
		}
		for (Enumeration<String> keys = taxon.fieldList.keys(); keys
				.hasMoreElements();) {
			String key = keys.nextElement();
			taxon.setValue(key, "");
		}

	}

	/**
	 * Retourne les donnees correspondant a l'ibmr
	 * 
	 * @return
	 */
	public Hashtable<String, String> getDataIbmr() {
		Hashtable<String, String> data = indice.getData();
		data = hashtableFusionner(data, coef.getData());
		data = hashtableFusionner(data, groupe.getData());
		data = hashtableFusionner(data, taxon.getData());
		return data;
	}

}
