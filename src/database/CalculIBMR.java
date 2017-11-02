package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
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
	public double ibmr = 0, robustesse = 0, nbEK = 0, ibmrRef = 1;
	double cote_spe, coef_steno;
	double K, EK, EKCS;
	double sumEK = 0, sumEKCS = 0;
	double maxEK = 0;
	public String maxTaxon = "";
	int nbUR = 2;
	List<Hashtable<String, String>> data = new ArrayList<Hashtable<String, String>>();
	Hashtable<String, Hashtable<String, Double>> dataCalcule = new Hashtable<String, Hashtable<String, Double>>();
	Hashtable<String, Double> taxonCalcule;
	Hashtable<String, String> ibmrData = new Hashtable<String, String>();
	List<Hashtable<String, String>> classes;
	Statement query;
	ResultSet res;
	Lignes_op_controle lignes;
	Unite_releves ur;
	Ibmr ibmrClass;
	Op_controle op_controle;
	ClasseEtat classeEtat = new ClasseEtat();

	public CalculIBMR() {
		classes = classeEtat.getListOrderBy("classe_etat_id");
	}

	public void setListTaxon(List<Hashtable<String, String>> taxons) {
		if (!data.isEmpty())
		data.clear();
		data = taxons;
	}

	public void setTauxUR(double ur1, double ur2) {
		tauxUR1 = ur1;
		tauxUR2 = ur2;
	}

	public void setNbUR(int nb) {
		nbUR = nb;
	}

	public Hashtable<String, String> calculer() {
		ibmrData.clear();
		ibmr = 0;
		robustesse = 0;
		maxTaxon = "";
		nbEK = 0;
		sumEK = 0;
		sumEKCS = 0;
		maxEK = 0;
		/*
		 * Parcours de la liste
		 */
		logger.debug("nbUR:" + nbUR + " tauxUR1:" + tauxUR1 + " tauxUR2:" + tauxUR2);
		boolean existContrib = false;
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
				if (!taxon.get("cd_contrib").isEmpty()) {
					/*
					 * Verification que les taxons sont bien differents
					 */
					if (!taxon.get("cd_contrib").equals(taxon.get("id_taxon"))) {
						try {
							/*
							 * Recuperation de cote_spe et coef_steno a partir
							 * du cd_valide
							 */
							logger.debug("recherche cd_contrib pour "+ taxon.get("id_taxon")+ " : "+ taxon.get("cd_contrib"));
							query = ConnexionDatabase.getConnexion().createStatement();
							res = query.executeQuery("select cote_spe, coef_steno from Taxons_MP "
									+ "where cd_taxon = '" + taxon.get("cd_contrib") + "'");
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
			logger.debug("cd_taxon:" + taxon.get("id_taxon") + " cd_contrib:" + taxon.get("cd_contrib") + " - cote_spe:"
					+ cote_spe + " coef_steno:" + coef_steno + " pc_ur1:" + taxon.get("pc_ur1") + " pc_ur2:"
					+ taxon.get("pc_ur2"));
			/*
			 * Lancement du traitement du taxon
			 */
			if (cote_spe > 0 && coef_steno > 0) {
				existContrib = true;
				/*
				 * Calcul du taux d'occupation
				 */
				txOccup1 = (taxon.get("pc_ur1").isEmpty() ? 0 : Double.parseDouble(taxon.get("pc_ur1")));
				if (nbUR == 2) {
					txOccup2 = (taxon.get("pc_ur2").isEmpty() ? 0 : Double.parseDouble(taxon.get("pc_ur2")));
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
				logger.debug("txOccup:" + txOccup + " K:" + K + " EK:" + EK + " EKCS:" + EKCS);
				/*
				 * Stockage des calculs pour la robustesse
				 */
				taxonCalcule = new Hashtable<String, Double>();
				taxonCalcule.put("EK", EK);
				taxonCalcule.put("EKCS", EKCS);
				dataCalcule.put(taxon.get("id_taxon"), taxonCalcule);
				logger.debug("id_taxon : " + taxon.get("id_taxon"));
				/*
				 * Enregistrement du taxon au plus fort EK
				 */
				if (EK > maxEK) {
					maxEK = EK;
					maxTaxon = taxon.get("id_taxon");
				}
				logger.debug("maxTaxon : " + maxTaxon);
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
		 * Forcage a -1 si aucun taxon contributif
		 */
		if (existContrib) {

			logger.debug("sumEK:" + sumEK + " sumEKCS:" + sumEKCS + " ibmr:" + ibmr);

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
				 * Calcul du nombre de taxons ayant le meme EK que le taxon
				 * supprime lors du calcul de resistance
				 */
				if (valeur.get("EK") == maxEK)
					nbEK++;
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
		} else {
			ibmr = -1;
			robustesse = -1;
			maxTaxon = "";
			nbEK = -1;
		}
		/*
		 * Stockage des resultats
		 */
		ibmrData.put("ibmr_value", String.valueOf(ibmr));
		ibmrData.put("robustesse_value", String.valueOf(robustesse));
		ibmrData.put("taxon_robustesse", maxTaxon);
		ibmrData.put("ek_nb_robustesse", String.valueOf(nbEK));

		/*
		 * Calcul de EQR et de la classe d'etat
		 */
		if (existContrib) {
			Double eqr;
			int classeId = 0;
			String classeLibelle = "";
			try {
				eqr = ibmr / ibmrRef;
				for (Hashtable<String, String> classe : classes) {
					Double value = Double.parseDouble(classe.get("classe_etat_seuil"));
					if (eqr > value && classeId == 0) {
						classeId = Integer.parseInt(classe.get("classe_etat_id"));
						classeLibelle = classe.get("classe_etat_libelle");
					}
				}
				eqr = Math.floor(eqr * 100 + 0.5) / 100;
				ibmrData.put("eqr_value", String.valueOf(eqr));
				if (classeId > 0) {
					ibmrData.put("classe_etat_id", String.valueOf(classeId));
					ibmrData.put("classe_etat_libelle", classeLibelle);
				}
				/*
				 * Meme traitement pour la robustesse
				 */
				eqr = robustesse / ibmrRef;
				classeId = 0;
				classeLibelle = "";
				for (Hashtable<String, String> classe : classes) {
					Double value = Double.parseDouble(classe.get("classe_etat_seuil"));
					if (eqr > value && classeId == 0) {
						classeId = Integer.parseInt(classe.get("classe_etat_id"));
						classeLibelle = classe.get("classe_etat_libelle");
					}
				}
				eqr = Math.floor(eqr * 100 + 0.5) / 100;
				ibmrData.put("robustesse_eqr_value", String.valueOf(eqr));
				if (classeId > 0) {
					ibmrData.put("robustesse_classe_etat_id", String.valueOf(classeId));
					ibmrData.put("robustesse_classe_etat_libelle", classeLibelle);
				}
			} catch (Exception e) {
				logger.debug(e.getMessage());
			}
		}
		return ibmrData;

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
		classeEtat = new ClasseEtat();
		List<Hashtable<String, String>> unites;
		for (Hashtable<String, String> operation : operations) {
			ibmrData.clear();
			if (operation.get("id_statut").equals("1")) {
				logger.debug("Recalcul operation : " + operation.get("id_op_controle"));
				/*
				 * Initialisations
				 */
				tauxUR1 = 100;
				tauxUR2 = 0;
				ibmr = 0;
				robustesse = 0;
				maxTaxon = "";
				sumEK = 0;
				sumEKCS = 0;
				maxEK = 0;
				dataCalcule.clear();
				try {
					ibmrRef = Double.parseDouble(operation.get("ibmr_ref"));
				} catch (NullPointerException e) {

				} catch (Exception e) {
					logger.debug(e.getMessage());
				}
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
					if (unite.get("numur").equals("1")) {
						tauxUR1 = Double.parseDouble(unite.get("pc_ur")) / 100;
					} else
						tauxUR2 = Double.parseDouble(unite.get("pc_ur")) / 100;
				}
				/*
				 * Lancement du calcul
				 */
				ibmrData = calculer();
				/*
				 * Ecriture du resultat
				 */
				ibmrClass.write(ibmrData, Integer.parseInt(operation.get("id_op_controle")));
			}

		}
	}

	/**
	 * Fonction declenchant le recalcul pour les dossiers correspondants aux
	 * parametres fournis
	 * 
	 * @param param
	 */
	public void recalculListeFromParam(Hashtable<String, String> param) {
		if (op_controle == null)
			op_controle = new Op_controle();
		List<Hashtable<String, String>> data = op_controle.getListeReleveComplet(param);
		logger.debug("Nombre de dossiers a recalculer : " + data.size());
		recalculListe(data);
	}
}
