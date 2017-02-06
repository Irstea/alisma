package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;
import java.util.List;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import utils.ConnexionDatabase;

/**
 * Classe de calcul de l'IBMR
 * 
 * @author quinton
 *
 */
public class CalculIBMR {
	static Logger logger = Logger.getLogger(CalculIBMR.class);
	double tauxUR1 = 100, tauxUR2 = 0, txOccup, txOccup1, txOccup2;
	public double ibmr = 0, robustesse = 0;
	double cote_spe, coef_steno;
	double K, EK, EKCS;
	double sumEK = 0, sumEKCS = 0;
	double maxEK = 0;
	public String maxTaxon = "";
	int nbUR = 2;
	List<Hashtable<String, String>> data;
	Hashtable<String, Hashtable<String, Double>> dataCalcule = new Hashtable<String, Hashtable<String, Double>>();
	Hashtable<String, Double> taxonCalcule;

	Statement query;
	ResultSet res;
	Lignes_op_controle lignes;
	Unite_releves ur;
	Ibmr ibmrClass;
	Op_controle op_controle ;

	public CalculIBMR() {

	}

	public void setListTaxon(List<Hashtable<String, String>> taxons) {
		data = taxons;
	}

	public void setTauxUR(double ur1, double ur2) {
		tauxUR1 = ur1;
		tauxUR2 = ur2;
	}

	public void setNbUR(int nb) {
		nbUR = nb;
	}

	public void calculer() {
		/*
		 * Parcours de la liste
		 */
		for (Hashtable<String, String> taxon : data) {
			cote_spe = 0;
			coef_steno = 0;
			/*
			 * Recherche si le taxon rentre dans l'indice
			 */
			if (taxon.get("cote_spe").isEmpty()) {
				/*
				 * Recherche s'il existe un taxon valide (dans le cas d'un
				 * synonyme) qui aurait une cote spe
				 */
				if (!taxon.get("cd_valide").isEmpty()) {
					/*
					 * Verification que les taxons sont bien differents
					 */
					if (!taxon.get("cd_valide").equals(taxon.get("cd_taxon"))) {
						try {
							/*
							 * Recuperation de cote_spe et coef_steno a partir
							 * du cd_valide
							 */
							query = ConnexionDatabase.getConnexion().createStatement();
							res = query.executeQuery("select cote_spe, coef_steno from Taxons_MP "
									+ "where cd_taxon = '" + taxon.get("cd_valide") + "'");
							if (res.first()) {
								cote_spe = (res.getInt(1));
								coef_steno = (res.getInt(2));
							}
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
				}
			} else {
				cote_spe = Double.parseDouble(taxon.get("cote_spe"));
				coef_steno = Double.parseDouble(taxon.get("coef_steno"));
			}
			/*
			 * Lancement du traitement du taxon
			 */
			if (cote_spe > 0 && coef_steno > 0) {
				/*
				 * Calcul du taux d'occupation
				 */
				txOccup1 = (taxon.get("pc_UR1").isEmpty() ? 0 : Double.parseDouble(taxon.get("pc_UR1")));
				if (nbUR == 2) {
					txOccup2 = (taxon.get("pc_UR2").isEmpty() ? 0 : Double.parseDouble(taxon.get("pc_UR2")));
				} else
					txOccup2 = 0;
				txOccup = (txOccup1 / 100 * tauxUR1) + (txOccup2 / 100 * tauxUR2);
				/*
				 * Calcul du coefficient K
				 */
				if (txOccup < 0.001) {
					K = 1;
				} else if (txOccup < 0.01) {
					K = 2;
				} else if (txOccup < 0.1) {
					K = 3;
				} else if (txOccup < 0.5) {
					K = 4;
				} else
					K = 5;
				/*
				 * Calcul des differents indices
				 */
				EK = K * coef_steno;
				EKCS = EK * cote_spe;
				sumEK += EK;
				sumEKCS += EKCS;
				/*
				 * Stockage des calculs pour la robustesse
				 */
				taxonCalcule = new Hashtable<String, Double>();
				taxonCalcule.put("EK", EK);
				taxonCalcule.put("EKCS", EKCS);
				dataCalcule.put(taxon.get("id_taxon"), taxonCalcule);
				logger.debug("id_taxon : "+taxon.get("id_taxon"));
				/*
				 * Enregistrement du taxon au plus fort EK
				 */
				if (EK > maxEK) {
					maxEK = EK;
					maxTaxon = taxon.get("id_taxon");
				}
				logger.debug("maxTaxon : "+maxTaxon);
			}
		}
		/*
		 * Fin de traitement de la liste Calcul de l'IBMR
		 */
		if (sumEK > 0) {
			ibmr = sumEKCS / sumEK;
		} else
			ibmr = 0;
		/*
		 * Calcul de l'arrondi
		 */
		ibmr = Math.floor(ibmr * 100 + 0.5) / 100;
		/*
		 * Calcul de la robustesse
		 */
		sumEKCS = 0;
		sumEK = 0;
		for (Entry<String, Hashtable<String, Double>> entry : dataCalcule.entrySet()) {
			String cle = entry.getKey();
			Hashtable<String, Double> valeur = entry.getValue();
			/*
			 * On elimine le taxon le plus fort
			 */
			if (!cle.equals(maxTaxon)) {
				sumEK += valeur.get("EK");
				sumEKCS += valeur.get("EKCS");
			}
		}
		if (sumEK > 0) {
			robustesse = sumEKCS / sumEK;
		} else
			robustesse = 0;
		/*
		 * Calcul de l'arrondi
		 */
		robustesse = Math.floor(robustesse * 100 + 0.5) / 100;

	}

	/**
	 * Fonction recalculant la liste des IBMR pour l'ensemble des operations
	 * fournies
	 * 
	 * @param operations
	 *            : liste des operations
	 */
	public void recalculListe(List<Hashtable<String, String>> operations) {
		lignes = new Lignes_op_controle();
		ur = new Unite_releves();
		ibmrClass = new Ibmr();
		List<Hashtable<String, String>> unites;
		Hashtable<String, String> ibmrData = new Hashtable<String,String>();
		for (Hashtable<String, String> operation : operations) {
			if (operation.get("id_statut").equals("1")) {
				logger.debug("Recalcul operation : "+operation.get("id_op_controle"));
				/*
				 * Initialisations
				 */
				tauxUR1 = 100;
				tauxUR2 = 0;
				ibmr = 0;
				robustesse = 0;
				maxTaxon = "";
				/*
				 * Recuperation des taxons correspondants
				 */
				setListTaxon(lignes.getListFromOp(operation.get("id_op_controle")));
				/*
				 * Recuperation des unites de releve
				 */
				unites = ur.readListFromKey("id_op_controle", operation.get("id_op_controle"));

				setNbUR(unites.size());
				/*
				 * Lecture des taux par ur
				 */
				for (Hashtable<String, String> unite : unites) {
					if (unite.get("numUR").equals("1")) {
						tauxUR1 = Double.parseDouble(unite.get("pc_UR"));
					} else
						tauxUR2 = Double.parseDouble(unite.get("pc_UR"));
				}
				/*
				 * Lancement du calcul
				 */
				calculer();
				/*
				 * Ecriture du resultat
				 */
				ibmrData.put("id_op_controle", operation.get("id_op_controle"));
				ibmrData.put("ibmr_value", String.valueOf(ibmr));
				ibmrData.put("robustesse_value", String.valueOf(robustesse));
				ibmrData.put("taxon_robustesse", maxTaxon);
				ibmrClass.write(ibmrData, Integer.parseInt(operation.get("id_op_controle")));
			}

		}
	}

	/**
	 * Fonction declenchant le recalcul pour les dossiers correspondants aux parametres fournis
	 * @param param
	 */
	public void recalculListeFromParam(Hashtable<String, String> param) {
		if (op_controle == null)
			op_controle = new Op_controle();
		List<Hashtable<String,String>> data = op_controle.getListeReleveComplet(param);
		logger.debug("Nombre de dossiers a recalculer : "+data.size());
		recalculListe(data);
	}
}
