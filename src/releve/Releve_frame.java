/**
 * 
 */
package releve;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import org.apache.log4j.Logger;

import alisma.Alisma;
import database.CalculIBMR;
import database.Ibmr;
import database.Lignes_op_controle;
import database.Op_controle;
import database.Points_prelev;
import database.Unite_releves;
import utils.Exportable;
import utils.JFrameAlisma;
import utils.Langue;
import utils.Parametre;

/**
 * @author quinton
 * 
 *         Classe de saisie des releves
 */
public class Releve_frame extends Observable implements Observer, Exportable {

	static Logger logger = Logger.getLogger(Releve_frame.class);
	public JFrameAlisma fenetre;
	boolean isModif = false, isResetCalcul = false;
	int keyOp = -1, nbUR = 2, keyPointPrelev = -1;
	String pathImage = "ressources/";
	List<ImageIcon> imageIcons = new ArrayList<ImageIcon>();
	String[] imageIconName = { "ok-green-24.png", "warning-blue-24.png",
			"warning-yellow-24.png", "error-red-24.png" };

	/*
	 * Ecriture en bdd
	 */
	Op_controle dbOpControle = new Op_controle();
	Lignes_op_controle dbLigneControle = new Lignes_op_controle();
	Points_prelev dbPointPrelev = new Points_prelev();
	Unite_releves dbUR = new Unite_releves();
	Ibmr ibmr = new Ibmr();
	/*
	 * Calcul IBMR
	 */
	CalculIBMR calculIbmr = new CalculIBMR();

	/*
	 * onglets
	 */
	private JTabbedPane tabs = new JTabbedPane();
	JPanel jpaneTab1 = new JPanel(), jpaneTab2 = new JPanel(),
			jpaneTab3 = new JPanel();
	Releve_tab1 tab_1;
	Releve_tab2 tab_2;
	Releve_tab3 tab_3;

	/*
	 * Affichage
	 */
	JScrollPane scrollPane2, scrollPane3;

	private JLabel banniere = new JLabel("", Parametre.logo, JLabel.TRAILING);
	private JLabel footer = new JLabel();

	private JMenu file_menu = new JMenu();

	private JMenuItem save_menu_item = new JMenuItem();
	private JMenuItem calcul_menu_item = new JMenuItem();
	private JMenuItem close_menu_item = new JMenuItem();

	private JButton jbValider, jbCalculer, jbModifier, jbSupprimer, jbPDF;

	private Dimension dimDefault = new Dimension(1024, 768);

	/*
	 * Divers
	 */
	private String protocole = "";

	/**
	 * Initialisation 
	 * @param id : cle du releve
	 */
	public Releve_frame(int id) {
		/*
		 * Preparation des icones
		 */
		for (int i = 0; i < imageIconName.length; i++) {
			imageIcons.add(i, new ImageIcon(getClass().getClassLoader()
					.getResource(pathImage + imageIconName[i])));
		}
		/*
		 * Initialisation des menus
		 */
		keyOp = id;
		/*
		 * Si l'identifiant est < 0, on demande le nombre d'UR du releve
		 */
		if (keyOp < 0) {
			JFrame frame = new JFrame();
			Object[] options = { "UR unique", "2 UR" };
			nbUR = JOptionPane.showOptionDialog(frame,
					Langue.getString("selectionNbUR"),
					Langue.getString("titreSelectionNbUR"),
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
					null, // do not use a custom Icon
					options, // the titles of buttons
					options[1]); // default button title
			if (nbUR == -1)
				nbUR = 1;
			nbUR++;

		} else 
			nbUR = dbUR.getNbReleve(keyOp);
		if (nbUR > 2)
			nbUR = 2;
		logger.debug("Nbre UR retenu : "+nbUR);

		/*
		 * Initialisation de la fenetre
		 */
		fenetre = new JFrameAlisma();
		fenetre.setIsWindowChange();
		menus();

		fenetre.getRootPane().addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				resizeComponent();
			}
		});
		/*
		 * Ajout du bandeau
		 */
		JPanel banniere_pane = new JPanel(new FlowLayout());
		((FlowLayout) banniere_pane.getLayout()).setAlignment(FlowLayout.LEFT);
		banniere_pane.setBackground(Parametre.cBanniere);
		banniere.setFont(new Font(banniere.getFont().getName(), banniere
				.getFont().getStyle(), 15));
		/*
		 * Ajout du bouton de validation
		 */
		jbValider = new JButton(Langue.getString("save"));
		jbValider.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				saveData();
			}
		});
		/*
		 * Ajout du bouton de calcul
		 */
		jbCalculer = new JButton(Langue.getString("calculer"));
		jbCalculer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				calculIBMR();
			}
		});
		/*
		 * Ajout du bouton permettant de modifier la fiche
		 */
		jbModifier = new JButton(Langue.getString("boutonModif"));
		jbModifier.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (tab_1.getStatut() < 3) {
					tab_1.setEnabled(true);
					tab_2.setEnabled(true);
					tab_3.setEnabled(true);
					/*
					 * Desactivation du bouton 
					 */
					jbModifier.setEnabled(false);
				}
				
			} });
		/*
		 * Ajout du bouton permettant de supprimer la fiche
		 */
		jbSupprimer = new JButton(Langue.getString("supprimer"));
		jbSupprimer.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				int rep = JOptionPane.showConfirmDialog(null, Langue
						.getString("warningRmOp"), Langue
						.getString("supprimer"),
						JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE,
						new ImageIcon(getClass().getClassLoader()
								.getResource("ressources/warning.png")));
				if (rep == JOptionPane.YES_OPTION) {
					dbOpControle.deleteOperation(keyOp, keyPointPrelev);
					/*
					 * Fermeture de la fenetre
					 */
					setObserversHasChanged();
					fenetre.close();
				}
			}});
		jbPDF = new JButton(Langue.getString("exportPdf"));
		jbPDF.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				setChanged();
				notifyObservers("exportPDF");
			}
		});
		
		/*
		 * Ajout de la banniere
		 */
		banniere_pane.add(banniere);
		banniere_pane.add(jbValider);
		banniere_pane.add(jbCalculer);
		banniere_pane.add(jbModifier);
		banniere_pane.add(jbSupprimer);
		banniere_pane.add(jbPDF);
		if(keyOp == -1) {
			jbSupprimer.setEnabled(false);
			jbPDF.setEnabled(false);
		}
		fenetre.getContentPane().add(banniere_pane, BorderLayout.NORTH);
		/*
		 * Remplissage des tabs, avec positionnement en haut de fenetre
		 */
		tabs.setBackground(Parametre.cCentral);
		tabs.setFont(new Font(tabs.getFont().getName(), tabs.getFont()
				.getStyle(), 13));
		/*
		 * Tab 1
		 */
		tab_1 = new Releve_tab1(nbUR, dbOpControle);
		jpaneTab1.setBackground(Parametre.cCentral);
		tab_1.setSize(fenetre.getWidth() - 50, fenetre.getHeight() - 200);
		jpaneTab1.add(tab_1.getPane(), BorderLayout.NORTH);
		tabs.add("", jpaneTab1);
		/*
		 * Tab 2
		 */
		JPanel sp2 = new JPanel();
		tab_2 = new Releve_tab2(nbUR, dbUR);
		sp2.add(tab_2.getPane(), JPanel.TOP_ALIGNMENT);
		sp2.setBackground(Parametre.cCentral);
		scrollPane2 = new JScrollPane(sp2,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane2.getVerticalScrollBar().setUnitIncrement(10);
		jpaneTab2.setBackground(Parametre.cCentral);
		jpaneTab2.add(scrollPane2, BorderLayout.NORTH);
		tabs.add("", jpaneTab2);
		/*
		 * Tab 3
		 */
		JPanel sp3 = new JPanel();
		tab_3 = new Releve_tab3(nbUR, dbUR);
		sp3.add(tab_3.getPane(), JPanel.TOP_ALIGNMENT);
		sp3.setBackground(Parametre.cCentral);
		scrollPane3 = new JScrollPane(sp3,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane3.getVerticalScrollBar().setUnitIncrement(10);
		jpaneTab3.setBackground(Parametre.cCentral);
		jpaneTab3.add(scrollPane3, BorderLayout.NORTH);
		tabs.add("", jpaneTab3);
		/*
		 * Rajout des observations des fils
		 */
		tab_1.addObserver(this);
		tab_2.addObserver(this);
		tab_3.addObserver(this);

		/*
		 * Ajout du tabs
		 */
		fenetre.getContentPane().add(tabs, BorderLayout.CENTER);

		/*
		 * Ajout du pied de fenetre
		 */
		JPanel footer_panel = new JPanel(new FlowLayout());
		((FlowLayout) footer_panel.getLayout()).setAlignment(FlowLayout.RIGHT);
		footer_panel.setBackground(Parametre.cBanniere);
		footer.setFont(new Font(footer.getFont().getName(), footer.getFont()
				.getStyle(), 12));
		footer_panel.add(footer);
		fenetre.getContentPane().add(footer_panel, BorderLayout.SOUTH);
		/*
		 * Affichage des libelles
		 */
		this.setLibelle();

		/*
		 * Lecture des informations liees a l'operation (modification ou
		 * consultation)
		 */
		if (keyOp > -1) {
			/*
			 * Lecture des infos generales sur l'opControle
			 */
			Hashtable<String, String> ibmrData = ibmr.read(keyOp);
			Hashtable <String, String> data = dbOpControle.lire(keyOp);
			if (!data.get("id_pt_prel").isEmpty()) {
				data = tab_1.hashtableFusionner(data, dbPointPrelev.read(data.get("id_pt_prel")));
				data = tab_1.hashtableFusionner(data, ibmrData);
				
			}
			tab_1.setDataGlobal(data);
			/*
			 * Mise a jour de la combo typeUR en fonction du protocole
			 */
			tab_2.setTypeURList(tab_1.getProtocole());
			/*
			 * Lecture des unites de releve
			 */
			List<Hashtable<String, String>> lData = dbUR.readListFromKey("id_op_controle", keyOp);
			List<Hashtable<String, String>> urComplet = new ArrayList<Hashtable<String,String>>();
			for (Hashtable<String, String> ur : lData) {
				urComplet.add(dbUR.setValueCombo(ur));
			}
			tab_2.setDataGlobal(urComplet);
			tab_3.setDataUr(urComplet);
			/*
			 * Mise a jour des informations de calcul dans l'onglet 3
			 */
			/*Double ibmr, robustesse;
			String max_taxon;
			try {
				ibmr = Double.parseDouble(tab_1.general.getData("IBMR"));
			} catch (Exception e) {
				ibmr = 0.0;
			}
			try {
				robustesse = Double.parseDouble(tab_1.general.getData("robustesse"));
			} catch (Exception e) {
				robustesse = 0.0;
			}
			try {
				max_taxon = tab_1.general.getData("taxonRobust");
			} catch (Exception e) {
				max_taxon = "";
			}
			tab_3.setDataCalcul(ibmr, robustesse,max_taxon);
			*/
			tab_3.setDataIbmr(ibmrData);
			/*
			 * Lecture des lignes de taxons
			 */
			tab_3.setDataTaxon(dbLigneControle.readListFromKey("id_op_controle", keyOp));
		} 

		/*
		 * Lancement d'une validation a vide, pour pre-positionner les bordures
		 */
		validation();
		isModif = false;
		fenetre.setModified(isModif);
		setSaveOk(false);
		isResetCalcul = false;
		/*
		 * Desactivation de la modification si le statut n'est pas "en saisie"
		 */
		if (tab_1.getStatut() > 0) {
			tab_1.setEnabled(false);
			tab_2.setEnabled(false);
			tab_3.setEnabled(false);
		} else {
			/*
			 * Desactivation du bouton permettant de basculer en saisie
			 */
			jbModifier.setEnabled(false);
		}
		/*
		 * Redefinition du titre
		 */
		if (keyOp == -1) {
			fenetre.setTitle(Langue.getString("alisma")+" - "+Langue.getString("opNouveau"));
		} else {
			fenetre.setTitle(Langue.getString("alisma")+" - " 
		+ tab_1.stationName
		+ " - " + tab_1.general.getData("date_op"));
		}
		fenetre.setPosition("TL");
		fenetre.pack();
	}

	private void menus() {
		JMenuBar menu_bar = new JMenuBar();
		save_menu_item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveData();
			}
		});

		calcul_menu_item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				calculIBMR();
			}
		});

		close_menu_item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fenetre.close();
			}
		});

		file_menu.add(save_menu_item);
		file_menu.add(calcul_menu_item);
		file_menu.add(close_menu_item);

		menu_bar.add(file_menu);

		fenetre.setJMenuBar(menu_bar);
	}

	public void setLibelle() {

		fenetre.setTitle(Langue.getString("titre"));

		file_menu.setText(Langue.getString("fichier"));
		// open_menu_item.setText(Langue.getString("ouvrir"));
		save_menu_item.setText(Langue.getString("save"));
		calcul_menu_item.setText(Langue.getString("calculer"));
		close_menu_item.setText(Langue.getString("close"));

		banniere.setText(Langue.getString("titre") );
		footer.setText("Alisma "+ Alisma.VERSION);

		tabs.setTitleAt(0, Langue.getString("donneesGenerales"));

		tab_1.setLibelle();
		tab_2.setLibelle();
		tab_3.setLibelle();

		tabs.setTitleAt(1, Langue.getString("uniteReleve"));
		tabs.setTitleAt(2, Langue.getString("relevesFlo"));
	}

	/**
	 * Declenche l'affichage
	 * 
	 * @return int keyOp
	 */
	public int display() {
		fenetre.setLocation(0, 0);

		Dimension dimEcran = Toolkit.getDefaultToolkit().getScreenSize();
		if (dimEcran.height < dimDefault.height
				|| dimEcran.width < dimDefault.width) {
			fenetre.setPreferredSize(dimEcran);
		} else
			fenetre.setPreferredSize(dimDefault);
		fenetre.pack();
		resizeComponent();
		fenetre.setVisible(true);
		/*
		 * Comme c'est l'ouverture, on force la modification a false
		 */
		isModif = false;
		fenetre.setModified(isModif);
		return keyOp;
	}

	public void resizeComponent() {
		Dimension dim = new Dimension(fenetre.getWidth()-20,
				fenetre.getHeight() - 200);
		scrollPane2.setPreferredSize(dim);
		scrollPane2.revalidate();
		scrollPane3.setPreferredSize(dim);
		tab_3.resizeComponent(dim);
		scrollPane3.revalidate();
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		switch ((String) arg1.toString()) {
		case "change":
			/*
			 * Verification si le protocole a change ou non
			 */
			String lProtocole = tab_1.getProtocole();
			if (!lProtocole.equals(protocole)) {
				tab_2.setTypeURList(lProtocole);
				protocole = lProtocole;
			}
			/*
			 * Lancement des tests de validation
			 */
			validation();
			if (isModif && !isResetCalcul) {
				tab_1.resetCalcul();
				tab_3.resetCalcul();
				tab_1.setStatut(0);
				isResetCalcul = true;
			}
			break;
		}

	}

	/**
	 * Lancement du controle des zones
	 * 
	 * @return
	 */
	private int validation() {
		int result = 0, rep = 0, rep3 = 0;
		rep = tab_1.validation();
		tabs.setIconAt(0, imageIcons.get(rep));
		if (rep > result)
			result = rep;
		rep = tab_2.validation();
		tabs.setIconAt(1, imageIcons.get(rep));
		if (rep > result)
			result = rep;
		rep3 = tab_3.validation();
		tabs.setIconAt(2, imageIcons.get(rep3));
		if (rep > result)
			result = rep;
		/*
		 * Controles croises
		 * 
		 * Recherche si des taxons ont ete saisis, et si la zone commentaire est
		 * vide
		 */
		if (tab_3.getNbTaxons() == 0 && tab_1.isCommentaireEmpty()) {
			tab_1.setCommentaireBordure(2);			
			if (2 > result)
				result = 2;			
			if (rep3 < result)
				rep3 = result;
			tabs.setIconAt(2, imageIcons.get(rep3));
		} else
			tab_1.setCommentaireBordure(0);
		/*
		 * En principe, l'appel a validation correspond a une modification
		 */
		isModif = true;
		fenetre.setModified(isModif);
		/*
		 * Lance le recalcul des coefficients dans le 3eme onglet
		 */
		tab_3.calculIndicateurs();
		/*
		 * Desactivation du menu ou des boutons de validation
		 */
		boolean saveOk = true;
		if (result > 2)
			saveOk = false;
		setSaveOk(saveOk);
		if (result < 2) {
			setCalculOk(true);
		} else {
			setCalculOk(false);
		}
		if (result < 2 && !tab_1.getIBMR().isEmpty()) {
			tab_1.setStatut(1);
			/*
			 * Desactivation du bouton calcul pour donner une alerte a l'utilisateur
			 */
			setCalculOk(false);
		} else
			tab_1.setStatut(0);
		return result;

	}

	/**
	 * Active ou desactive les fonctions permettant de realiser la sauvegarde
	 * 
	 * @param saveOk
	 */
	private void setSaveOk(boolean saveOk) {
		save_menu_item.setEnabled(saveOk);
		jbValider.setEnabled(saveOk);
		jbPDF.setEnabled(!saveOk);
	}

	/**
	 * Active ou desactive les objets graphiques permettant de declencher le
	 * calcul
	 * 
	 * @param calculOk
	 */
	private void setCalculOk(boolean calculOk) {
		jbCalculer.setEnabled(calculOk);
		calcul_menu_item.setEnabled(calculOk);
	}

//	private void close() {
//		boolean quitter = true;
//		/*
//		 * Verification qu'aucune modification ne soit en cours
//		 */
//		if (isModif) {
//			int rep;
//			rep = JOptionPane.showOptionDialog(null,
//					Langue.getString("confirmLoseData"), null,
//					JOptionPane.WARNING_MESSAGE, JOptionPane.YES_NO_OPTION,
//					null, null, JOptionPane.CANCEL_OPTION);
//			if (rep != JOptionPane.YES_OPTION)
//				quitter = false;
//		}
//
//		if (quitter == true) {
//			fenetre.setVisible(false);
//			fenetre.dispose();
//		}
//	}

	/**
	 * Sauvegarde des donnees
	 */
	public void saveData() {
		int valid = validation();
		Hashtable<String, String> data;
		List<Hashtable<String, String>> listTaxons;
		/*
		 * Lancement du calcul de l'IBMR
		 */
		/*
		 * if (valid < 2) { calculIBMR(); }
		 */
		if (valid < 3) {
			/*
			 * Gestion du statut
			 */
			int statut = 0;
			if (valid <= 1 && !tab_1.getIBMR().isEmpty()) {
				statut = 1;
			}
			tab_1.setStatut(statut);
			/*
			 * Lancement de la sauvegarde
			 */
			/*
			 * Recuperation des donnees du premier onglet et ecriture de
			 * Points_prelev et Op_controle
			 */
			data = tab_1.getDataGlobal();
			try {
			keyPointPrelev = new Integer(data.get("id_pt_prel"));
			}catch (Exception e) {
				keyPointPrelev = -1;
			}
			keyPointPrelev = dbPointPrelev.ecrire(data, keyPointPrelev);
			data.put("id_pt_prel", String.valueOf(keyPointPrelev));
			tab_1.pointPrelevement.setValue("id_pt_prel", String.valueOf(keyPointPrelev));
			logger.debug("id_op_controle before ecrire() : "+keyOp);
			keyOp = dbOpControle.ecrire(data, keyOp);
			logger.debug("id_op_controle after ecrire() : "+keyOp);
			/*
			 * Recuperation des donnees par UR et ecriture de Unites_releves
			 */
			for (int i = 1; i <= nbUR; i++) {
				data = tab_2.getDataUR(i);
				data = tab_3.hashtableFusionner(data, tab_3.getDataUR(i));
				data.put("id_op_controle", String.valueOf(keyOp));
				tab_2.setIdUR(i,  dbUR.ecrire(data, tab_2.getIdUR(i)));
			}
			/*
			 * Recuperation des donnees IBMR
			 */
			ibmr.ecrire(tab_3.getDataIbmr(), keyOp);
			
			/*
			 * Recuperation des donnees des taxons
			 */
			listTaxons = tab_3.getListTaxon();
			
			/*for (Hashtable<String, String> item : listTaxons) {
				item.put("id_op_controle", String.valueOf(keyOp));
				key = dbLigneControle.ecrire(item);
				tab_3.setTaxonKey(numLigne, key);
				numLigne++;
			}*/
			/*
			 * Ecriture des taxons
			 */
			listTaxons = dbLigneControle.ecrireList(listTaxons, keyOp);
			int numLigne = 0;
			/*
			 * Affectation de la cle dans l'onglet
			 */
			for (Hashtable<String, String> item : listTaxons) {
				tab_3.setTaxonKey(numLigne, Integer.parseInt(item.get("id_ligne_op_controle")));
				numLigne++;
			}

			/*
			 * Reinitialisation de l'indicateur de modification
			 */
			isModif = false;
			fenetre.setModified(isModif);
			isResetCalcul = false;
			setSaveOk(false);
			if (statut == 1) 
				setCalculOk(false);
			
			/*
			 * Activation du bouton supprimer
			 */
			if (keyOp > -1)
				jbSupprimer.setEnabled(true);
			/*
			 * Declenchement du rafraichissement de la liste des releves
			 */
			setObserversHasChanged();
		}
	}

	/**
	 * Informe les observateurs que le releve a change
	 */
	void setObserversHasChanged() {
		setChanged();
		notifyObservers("releveHasChanged");
	}

	/**
	 * Calcul de l'IBMR
	 */
	void calculIBMR() {
		/*
		 * Recuperation des taux de recouvrement de chaque UR
		 */
		if (nbUR == 1) {
			calculIbmr.setTauxUR(1, 0);
		} else {
			calculIbmr.setTauxUR(tab_2.getpcUR(1),tab_2.getpcUR(2) );
		}
		
		/*
		 * Recuperation de l'ensemble des taxons declares
		 */
		calculIbmr.setListTaxon(tab_3.getListTaxon());
		/*
		 * Lancement du calcul
		 */
		calculIbmr.calculer();
		/*
		 * Fin des calculs - mise a jour des champs
		 */
		tab_1.setDataCalcul(calculIbmr.ibmr, calculIbmr.robustesse, calculIbmr.maxTaxon);
		tab_3.setDataCalcul(calculIbmr.ibmr, calculIbmr.robustesse, calculIbmr.maxTaxon);
		tab_1.setStatut(1);
		isModif = true;
		fenetre.setModified(isModif);
		setSaveOk(true);
	}

	@Override
	public Hashtable<String, String> getParam() {
		Hashtable <String, String> param = new Hashtable<String, String>();
		param.put("id_op_controle", ((Integer) keyOp).toString());
		return param;
	}
}
