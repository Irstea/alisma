/**
 * 
 */
package utils;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

import org.apache.log4j.Logger;

/**
 * @author quinton
 * 
 *         Composant de base, pour les formulaires
 * 
 *         Le composant permet de gerer l'affichage de champs de saisie Les
 *         differents composants integres sont affiches dans un panneau JPanel
 *         gere avec GridPadLayout. Il est autosuffisant, hormis pour la gestion
 *         des libelles, oe l'affichage necessite l'acces e la classe
 *         utils/Langue
 * 
 *         Il est herite d'Observable, et implemente Observer, pour la
 *         recuperation/ transmission d'evenements depuis les composants
 *         integres vers les composants superieurs.
 * 
 *         Il integre les classes suivantes :
 * 
 *         - JPane : un JPanel pre-positionne avec GridBagLayout
 * 
 *         - MaxLength50 et NumericVerifier : deux classes permettant de
 *         verifier les donnees entrees (longueur maxi 50 caracteres pour le
 *         premier, entiers pour le second). En cas de non conformite, le champ
 *         bascule en rouge et le focus refuse de sortir
 * 
 *         - Bordure : permet de dessiner une bordure ou de changer la couleur
 *         de fond d'un composant. Utilise pour mettre en evidence les champs
 *         qui ne repondent pas e une contrainte particuliere (classe copiee e
 *         partir de la classe Utils/Bordure, pour que le composant soit
 *         quasiment auto-suffisant)
 * 
 *         Le composant est coneu pour contenir lui-meme d'autres composants du
 *         meme type
 * 
 *         Les composants integres sont stockes dans listeComposant.
 * 
 *         Les differents champs de saisie sont stockes dans fieldList.
 *         Differents types de champs peuvent etre integres : textField,
 *         Numerique, champ de longueur maxi, textArea, datePicker (selection de
 *         date, herite de JXDatePicker).
 * 
 *         Les champs peuvent etre definis en 4 categories : saisie falcutative,
 *         saisie bloquante (fieldMandatory), saisie indispensable
 *         (fieldNecessary), saisie recommandee (fieldRecommanded). Au cas oe un
 *         champ ne respecte pas ces contraintes, sa bordure prend une couleur
 *         differente.
 * 
 *         Des labels peuvent etre positionnes. Ils sont stockes dans label. Ils
 *         peuvent etre modifies e la volee, par la fonction setLibelle()
 *         (mecanisme e utiliser dans le cas de changement de langue -
 *         translate...)
 * 
 *         La fonction validation() joue tous les contreles pour l'ensemble des
 *         champs et des composants integres, et retourne le niveau maximal
 *         d'anomalie detecte. Elle appelle la fonction validation() des
 *         composants integres (fonction recursive)
 * 
 *         La fonction setLibelle() est egalement une fonction recursive, qui
 *         permet de declencher le changement des labels dans l'ensemble des
 *         composants integres
 * 
 *         setTitle(titre) permet de dessiner une bordure avec un titre autour
 *         du composant
 * 
 *         setTitle(titre, erreur) permet de changer la couleur de la bordure,
 *         si une erreur globale est detectee dans le composant. C'est
 *         equivalent e setTitleWithError() et setTitleWithoutError(), ces deux
 *         fonctions ne pouvant etre declenchees qu'une fois la bordure
 *         initialisee au moins une fois
 * 
 * 
 * 
 */
public class ComposantAlisma extends Observable implements Observer {

	private List<ComposantAlisma> listeComposant = new ArrayList<ComposantAlisma>();
	public Hashtable<String, JLabel> label = new Hashtable<String, JLabel>();
	public Hashtable<String, JComponent> fieldList = new Hashtable<String, JComponent>();
	public List<String> fieldMandatory = new ArrayList<String>(),
			fieldRecommanded = new ArrayList<String>(),
			fieldNecessary = new ArrayList<String>();
	public Hashtable<String, JButton> buttons = new Hashtable<String, JButton>();
	public JPane pane;
	public GridBagConstraints gbc = new GridBagConstraints();
	// int jpX = 0, jpY = 0, largeur = 1;
	private String titre = "";
	private Dimension dimensionDefault = new Dimension(90, 20);
	boolean isModified = false;
	private Object obj;
	public static Logger logger = Logger.getLogger(ComposantAlisma.class);

	/**
	 * Evenements declenches
	 */
	public String actionLibelle = "change";
	ActionListener actionListener;
	PropertyChangeListener textListener;
	protected DocumentListener documentListener;

	public Bordure bordure = new Bordure();

	/**
	 * Classe permettant de dessiner les bordures ou de remplir le contenu d'un
	 * champ
	 * 
	 * @author quinton
	 * 
	 */
	public class Bordure {
		public Bordure() {
		}

		/**
		 * Definit une bordure de couleur autour du composant fourni en
		 * parametre, selon le niveau prevu
		 * 
		 * @param comp
		 * @param level
		 */
		public void setBordure(JComponent comp, int level) {
			switch (level) {
			case 1:
				comp.setBorder(BorderFactory.createLineBorder(Color.BLUE));
				break;
			case 2:
				comp.setBorder(BorderFactory.createLineBorder(Color.ORANGE));
				break;
			case 3:
				comp.setBorder(BorderFactory.createLineBorder(Color.RED));
				break;
			case 0:
			default:
				comp.setBorder(BorderFactory.createLineBorder(Color.GRAY));
			}
		}

		/**
		 * Definit une couleur de fond au composant fourni en parametre, selon
		 * le niveau prevu
		 * 
		 * @param comp
		 * @param level
		 */
		public void setBackground(JComponent comp, int level) {
			switch (level) {
			case 1:
				// comp.set;
				comp.setBackground(Color.BLUE);
				break;
			case 2:
				comp.setBackground(Color.ORANGE);
				break;
			case 3:
				comp.setBackground(new Color(255, 170, 170));
				break;
			case 0:
			default:
				comp.setBackground(Color.WHITE);
			}
		}
	}

	/**
	 * Classe de verification des champs de longueur < 50 caracteres
	 * 
	 * @author quinton
	 * 
	 */
	class MaxLength50 extends InputVerifier {
		@Override
		public boolean verify(JComponent comp) {
			JTextField jt = (JTextField) comp;
			if (jt.getText().length() > 50) {
				bordure.setBackground(jt, 3);
				return false;
			} else {
				bordure.setBackground(jt, 0);
				return true;
			}
		}
	}

	/**
	 * Constructeur
	 */
	public ComposantAlisma() {
		obj = this;
		pane = new JPane();
		/*
		 * Definition des positionnements par defaut
		 */
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.insets = new Insets(4, 4, 4, 4);
		gbc.ipadx = 0;
		gbc.ipady = 0;
		gbc.fill = GridBagConstraints.BOTH;
		/**
		 * Initialisation des listeners par defaut
		 */
		setListeners();
	}

	@Override
	/**
	 * Definition de l'appel aux parents sur modification
	 */
	public void update(Observable arg0, Object arg1) {
		setAction();
	}

	/**
	 * Definition des ecouteurs
	 */
	public void setListeners() {
		actionListener = new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				actionLibelle = "change";
				isModified = true;
				setAction();
			}
		};
		textListener = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent arg0) {
				actionLibelle = "change";
				isModified = true;
				setAction();
			}
		};

		documentListener = new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				actionLibelle = "change";
				isModified = true;
				setAction();
			}

			public void removeUpdate(DocumentEvent e) {
				actionLibelle = "change";
				isModified = true;
				setAction();
			}

			public void insertUpdate(DocumentEvent e) {
				actionLibelle = "change";
				isModified = true;
				setAction();
			}
		};
	}

	/**
	 * Appel des parents, pour leur signifier un changement
	 */
	public void setAction() {
		setChanged();
		notifyObservers(actionLibelle);
	}

	/**
	 * Appel des parents, en fournissant en parametre le texte a declencher
	 * 
	 * @param libelle
	 */
	public void setAction(String libelle) {
		actionLibelle = libelle;
		setAction();
	}

	/**
	 * Panneau graphique, pre-positionne en gridbaglayout
	 */
	public class JPane extends JPanel {
		private static final long serialVersionUID = 1L;

		public JPane() {
			setLayout(new GridBagLayout());
			setBackground(Parametre.cCentral);
		}
	}

	/**
	 * Ajoute un nouveau composant graphique : stocke l'objet dans
	 * listeComposant ajoute le panneau graphique e pane observe le composant
	 * rajoute
	 * 
	 * @param pComposant
	 */
	public void addComposant(ComposantAlisma pComposant) {
		listeComposant.add(pComposant);
		pComposant.addObserver(this);
		pane.add(pComposant.getPane(), gbc);
		/*
		 * Prepare l'affichage a la ligne inferieure
		 */
		gbc.gridy++;
	}

	/**
	 * Redimensionne le composant aux tailles fournies
	 * 
	 * @param x
	 * @param y
	 */
	public void setSize(int x, int y) {
		pane.setSize(x, y);
	}

	public void setSize(Dimension dim) {
		pane.setSize(dim);
	}

	/**
	 * Idem addComposant, mais en rajoutant les contraintes de positionnement
	 * 
	 * @param pComposant
	 * @param pGbc
	 */
	public void addComposant(ComposantAlisma pComposant, GridBagConstraints pGbc) {
		gbc = pGbc;
		this.addComposant(pComposant);
	}

	public void addComposant(ComposantAlisma pComposant, int x, int y) {
		gbc.gridx = x;
		gbc.gridy = y;
		gbc.gridwidth = 1;
		this.addComposant(pComposant);
	}

	/**
	 * Definit les parametres classiques du GridBagConstraint
	 * 
	 * @param pX
	 * @param pY
	 * @param pLargeur
	 */
	public void gbcSetPos(int pX, int pY, int pLargeur) {
		gbc.gridx = pX;
		gbc.gridy = pY;
		gbc.gridwidth = pLargeur;
	}

	/**
	 * Retourne le composant JPanel
	 * 
	 * @return
	 */
	public JPane getPane() {
		return pane;
	}

	/**
	 * Execute la verification des zones de saisie, en interrogeant l'ensemble
	 * des composants inferieurs
	 * 
	 * @return level
	 */
	public int validation() {
		int retour = 0, compRetour = 0, fieldRetour = 0;
		for (int i = 0; i < listeComposant.size(); i++) {
			compRetour = listeComposant.get(i).validation();
			if (compRetour > retour)
				retour = compRetour;
		}
		/*
		 * Verification des champs obligatoires
		 */
		if (fieldMandatory != null) {
			for (String champ : fieldMandatory) {
				fieldRetour = testFieldNotNull(champ, 3);
				if (fieldRetour > retour)
					retour = fieldRetour;
			}
		}
		/*
		 * Verification des champs necessaires pour valider le dossier
		 */
		if (fieldNecessary != null) {
			for (String champ : fieldNecessary) {
				fieldRetour = testFieldNotNull(champ, 2);
				if (fieldRetour > retour)
					retour = fieldRetour;
			}
		}
		/*
		 * Verification des champs conseilles
		 */
		if (fieldRecommanded != null) {
			for (String champ : fieldRecommanded) {
				fieldRetour = testFieldNotNull(champ, 1);
				if (fieldRetour > retour)
					retour = fieldRetour;
			}
		}

		return retour;
	}

	/**
	 * Teste chaque champ fourni pour verifier qu'il ne soit pas nul, et
	 * retourne dans le cas contraire le level fourni en parametre
	 * 
	 * @param nomField
	 * @param level
	 * @return
	 */
	private int testFieldNotNull(String nomField, int level) {
		int fieldRetour = 0;
		JComboBox<?> jcb;
		JTextComponent jtf;
		DatePicker dp;
		/*
		 * Test selon le type d'objet
		 */
		if (fieldList.containsKey(nomField)) {
			switch (fieldList.get(nomField).getClass().getSimpleName()) {
			case "JComboBox":
				jcb = (JComboBox<?>) fieldList.get(nomField);
				if (jcb.getSelectedIndex() == -1)
					fieldRetour = level;
				break;
			case "JTextField":
			case "JTextArea":
				jtf = (JTextComponent) fieldList.get(nomField);
				if (jtf.getText().length() == 0)
					fieldRetour = level;
				break;
			case "DatePicker":
				dp = (DatePicker) fieldList.get(nomField);
				if (dp.getDate() == null)
					fieldRetour = level;
			}
			/*
			 * Positionnement de la bordure
			 */
			bordure.setBordure(fieldList.get(nomField), fieldRetour);
			return fieldRetour;
		} else {
			System.err.println("Champ " + nomField
					+ " inexistant - verifiez votre application");
			return 0;
		}
	}

	/**
	 * Positionne la bordure definie autour du champ specifie
	 * 
	 * @param field
	 *            : nom champ
	 * @param level
	 *            : niveau de bordure
	 */
	public void setBordure(String field, int level) {
		bordure.setBordure(fieldList.get(field), level);
	}

	/**
	 * Definit les libelles dans les objets inferieurs
	 */
	public void setLibelle() {
		for (int i = 0; i < listeComposant.size(); i++) {
			listeComposant.get(i).setLibelle();
			setLabel();
			/*
			 * Redessine la bordure du composant
			 */
			if (titre.length() > 0)
				setTitle(titre);
		}
	}

	/**
	 * Definit les donnees dans le composant
	 * 
	 * @param pData
	 */
	@SuppressWarnings("unchecked")
	public void setData(Hashtable<String, String> pData) {
		String key, value;
		JComboBox<Object> combo;
		JTextComponent texte;
		DatePicker dp;
		JCheckBox jckb;
		JLabel jlb;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		/*
		 * Iteration pour chaque champ fourni
		 */
		for (Entry<String, String> entry : pData.entrySet()) {
			key = entry.getKey();
			value = entry.getValue();
			if (fieldList.containsKey(key)) {
				/*
				 * Traitement differencie selon le type de composant
				 */
				switch (fieldList.get(key).getClass().getSimpleName()) {
				case "JComboBox":
					combo = (JComboBox<Object>) fieldList.get(key);
					combo.setSelectedItem(value);
					break;
				case "JTextField":
				case "JTextArea":
					texte = (JTextComponent) fieldList.get(key);
					texte.setText(value);
					break;
				case "DatePicker":
					dp = (DatePicker) fieldList.get(key);
					try {
						dp.setDate(df.parse(value));
					} catch (ParseException e) {
						dp.setDate(new Date());
					}
					break;
				case "JCheckBox":
					jckb = (JCheckBox) fieldList.get(key);
					if (value == "1") {
						jckb.setSelected(true);
					} else
						jckb.setSelected(false);
					break;
				case "JLabel":
					jlb = (JLabel) fieldList.get(key);
					jlb.setText(value);
					break;
				}
			}
		}
	}

	/**
	 * Recupere toutes les donnees du composant
	 * 
	 * @return
	 */
	public Hashtable<String, String> getData() {
		Hashtable<String, String> data = new Hashtable<String, String>();
		String key;
		for (Entry<String, JComponent> entry : fieldList.entrySet()) {
			key = entry.getKey();

			data.put(key, getData(key));
		}
		return data;
	}

	/**
	 * Recupere les donnees d'un champ
	 * 
	 * @param field
	 *            : nom du champ
	 * @return
	 */
	public String getData(String field) {
		JComponent composant;
		JComboBox<?> jcb;
		DatePicker dp;
		JTextComponent jtc;
		JCheckBox jckb;
		JLabel jlb;
		String data = "";
		composant = fieldList.get(field);
		switch (composant.getClass().getSimpleName()) {
		case "JComboBox":
			jcb = (JComboBox<?>) composant;
			data = (String) jcb.getSelectedItem();
			break;
		case "JTextField":
		case "JTextArea":
			jtc = (JTextComponent) composant;
			data = jtc.getText();
			break;
		case "DatePicker":
			dp = (DatePicker) composant;
			data = dp.getDateSql();
			break;
		case "JCheckBox":
			jckb = (JCheckBox) composant;
			if (jckb.isSelected()) {
				data = "1";
			} else
				data = "0";
			break;
		case "JLabel":
			jlb = (JLabel) composant;
			data = jlb.getText();
			break;
		}
		if (data == null)
			data = "";
		return data;
	}

	/**
	 * Positionne une valeur dans le champ indique
	 * 
	 * @param field
	 * @param value
	 */
	public void setValue(String field, String value) {
		JComponent composant;
		JComboBox<?> jcb;
		DatePicker dp;
		JTextComponent jtc;
		JCheckBox jckb;
		JLabel jlb;
		composant = fieldList.get(field);
		switch (composant.getClass().getSimpleName()) {
		case "JComboBox":
			jcb = (JComboBox<?>) composant;
			jcb.setSelectedItem(value);
			break;
		case "JTextField":
		case "JTextArea":
			jtc = (JTextComponent) composant;
			jtc.getDocument().removeDocumentListener(documentListener);
			jtc.setText(value);
			jtc.getDocument().addDocumentListener(documentListener);
			break;
		case "DatePicker":
			dp = (DatePicker) composant;
			try {
				dp.setDate(new SimpleDateFormat("yyyy-MM-dd").parse(value));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		case "JCheckBox":
			jckb = (JCheckBox) composant;
			if (value.equals("1")) {
				jckb.setSelected(true);
			} else
				jckb.setSelected(false);
			break;
		case "JLabel":
			jlb = (JLabel) composant;
			jlb.setText(value);
			break;
		}
	}

	/**
	 * Definition du titre du formulaire et dessin de la bordure Composant de
	 * type Fieldset
	 * 
	 * @param pTitre
	 *            : nom de l'entree dans le fichier de langue
	 */
	public void setTitle(String pTitre) {
		setTitle(pTitre, false);
	}

	/**
	 * Affiche une bordure
	 */
	public void setTitle() {
		setTitle("", false);
	}

	/**
	 * Cree une bordure avec une couleur bleue en cas d'absence d'erreur, et
	 * rouge dans le cas contraire
	 * 
	 * @param pTitre
	 *            : nom de l'entree dans le fichier de langue
	 * @param erreur
	 */
	public void setTitle(String pTitre, boolean erreur) {
		Color couleur1, couleur2;
		if (erreur) {
			couleur1 = new Color(205, 20, 0);
			couleur2 = new Color(255, 212, 157);
		} else {
			couleur1 = new Color(0, 20, 205);
			couleur2 = new Color(157, 212, 255);
		}
		if (pTitre.length() > 0) {
			titre = pTitre;
			pane.setBorder(BorderFactory.createTitledBorder(BorderFactory
					.createEtchedBorder(EtchedBorder.LOWERED, couleur1,
							couleur2), Langue.getString(pTitre),
					TitledBorder.LEFT, TitledBorder.LEFT));
		} else {
			pane.setBorder(BorderFactory.createTitledBorder(BorderFactory
					.createEtchedBorder(EtchedBorder.LOWERED, couleur1,
							couleur2)));
		}
	}

	/**
	 * Modifie la couleur de la bordure en cas de detection d'erreur
	 */
	public void setTitleWithError() {
		setTitle(titre, true);
	}

	/**
	 * Modifie la couleur de la bordure lorsque l'erreur est levee
	 */
	public void setTitleWithoutError() {
		setTitle(titre, false);
	}

	/**
	 * Definit les dimensions par defaut du composant en fournissant la largeur
	 * et la hauteur
	 * 
	 * @param largeur
	 * @param hauteur
	 */
	public void setDimensionDefault(int largeur, int hauteur) {
		dimensionDefault.width = largeur;
		dimensionDefault.height = hauteur;
	}

	/**
	 * Definit les dimensions par defaut du composant, en fournissant un objet
	 * dimension en parametre
	 * 
	 * @param dim
	 */
	public void setDimensionDefault(Dimension dim) {
		dimensionDefault = dim;
	}

	/**
	 * Definit les dimensions d'un composant a la taille par defaut
	 * 
	 * @param comp
	 */
	public void setComposantDimensionDefault(Component comp) {
		comp.setPreferredSize(dimensionDefault);
	}

	/**
	 * Ajoute un nouveau libelle dans le composant
	 * 
	 * @param libelle
	 *            : nom de l'entree dans le fichier de langue
	 * @param X
	 * @param Y
	 */
	public void addLabel(String libelle, int X, int Y) {
		addLabel(libelle, X, Y, dimensionDefault, GridBagConstraints.EAST);
	}

	public void addLabel(String libelle, int x, int y, Dimension dim) {
		addLabel(libelle, x, y, dim, GridBagConstraints.EAST);
	}

	public void addLabel(String libelle, int x, int y, int z, Dimension dim) {
		addLabel(libelle, x, y, z, dim, GridBagConstraints.EAST);
	}

	/**
	 * Ajoute un nouveau libelle en indiquant la taille
	 * 
	 * @param libelle
	 *            : nom de l'entree dans le fichier de langue
	 * @param x
	 * @param y
	 * @param dim
	 */
	public void addLabel(String libelle, int x, int y, Dimension dim,
			int position) {
		addLabel(libelle, x, y, 1, dim, position);
	}

	public void addLabel(String libelle, int x, int y, int z, Dimension dim,
			int position) {
		JLabel etiquette = new JLabel(Langue.getString(libelle));
		if (dim != null)
			etiquette.setPreferredSize(dim);
		gbcSetPos(x, y, z);
		gbc.anchor = position;
		label.put(libelle, etiquette);
		pane.add(label.get(libelle), gbc);
		gbc.anchor = GridBagConstraints.WEST;
	}

	/**
	 * Ajoute un composant de type label, pour afficher des donnees
	 * 
	 * @param id
	 * @param libelle
	 * @param x
	 * @param y
	 * @param dim
	 */
	public void addLabelAsValue(String id, String libelle, int x, int y,
			Dimension dim) {
		addLabelAsValue(id, libelle, x, y, 1, dim, GridBagConstraints.WEST);
	}

	/**
	 * Ajoute un composant de type label, pour afficher des donnees
	 * 
	 * @param id
	 * @param libelle
	 * @param x
	 * @param y
	 * @param z
	 */
	public void addLabelAsValue(String id, String libelle, int x, int y, int z) {
		addLabelAsValue(id, libelle, x, y, z, dimensionDefault,
				GridBagConstraints.WEST);
	}

	/**
	 * Ajoute un composant de type label, pour afficher des donnees
	 * 
	 * @param id
	 * @param libelle
	 * @param x
	 * @param y
	 * @param dim
	 * @param position
	 */
	public void addLabelAsValue(String id, String libelle, int x, int y,
			Dimension dim, int position) {
		addLabelAsValue(id, libelle, x, y, 1, dim, position);
	}

	/**
	 * Ajoute un composant de type label, pour afficher des donnees
	 * 
	 * @param id
	 * @param libelle
	 * @param x
	 * @param y
	 * @param z
	 * @param dim
	 * @param position
	 */
	public void addLabelAsValue(String id, String libelle, int x, int y, int z,
			Dimension dim, int position) {
		JLabel etiquette = new JLabel(libelle);
		if (dim == null)
			dim = dimensionDefault;
		gbc.anchor = position;
		gbc.anchor = GridBagConstraints.WEST;
		fieldList.put(id, etiquette);
		setDimension(id, dim);
		gbcSetPos(x, y, z);
		pane.add(fieldList.get(id), gbc);
	}

	/**
	 * Rajoute l'ensemble des labels en une seule passe, en colonne
	 * 
	 * @param liste
	 * @param x
	 *            : colonne de positionnement
	 * @param yBase
	 *            : ligne de depart
	 */
	public void addLabelList(String[] liste, int x, int yBase) {
		int y = yBase;
		for (String name : liste) {
			addLabel(name, x, y);
			y++;
		}
	}

	/**
	 * Ajoute un textField non affiche, pour stocker des donnees dans le
	 * composant et les recuperer apres
	 * 
	 * @param id
	 */
	public void addHidden(String id) {
		addHidden(id, "");
	}

	/**
	 * Ajoute un textField non affiche, pour stocker des donnees dans le
	 * composant et les recuperer apres
	 * 
	 * @param id
	 * @param value
	 */
	public void addHidden(String id, String value) {
		fieldList.put(id, new JLabel(value));
	}

	/**
	 * Met a jour les libelles, s'il en existe
	 */
	public void setLabel() {
		String cle;
		Set<String> labelSet = label.keySet();
		Iterator<String> iter = labelSet.iterator();
		while (iter.hasNext()) {
			cle = iter.next();
			label.get(cle).setText(Langue.getString(cle));
		}
		for (Entry<String, JButton> entry : buttons.entrySet()) {
			entry.getValue().setText(Langue.getString(entry.getKey()));
		}

	}

	/**
	 * Ajout des combobox dans le composant, a l'emplacement souhaite
	 * 
	 * @param name
	 * @param X
	 * @param Y
	 * @param Z
	 * @param editable
	 */
	public void addCombo(String name, int X, int Y, int Z, boolean editable) {
		JComboBox<Object> combo = new JComboBox<Object>();
		BoundsPopupMenuListener bpm = new BoundsPopupMenuListener(true, false);
		combo.addPopupMenuListener(bpm);
		combo.addActionListener(actionListener);
		combo.addPropertyChangeListener("value", textListener);
		combo.setEditable(editable);

		fieldList.put(name, combo);
		setDimension(name, dimensionDefault);
		gbcSetPos(X, Y, Z);
		pane.add(fieldList.get(name), gbc);
	}

	/**
	 * Ajoute une comboBox non editable
	 * 
	 * @param name
	 * @param X
	 * @param Y
	 * @param Z
	 */
	public void addCombo(String name, int X, int Y, int Z) {
		addCombo(name, X, Y, Z, false);
	}

	/**
	 * Rajoute les items a une combobox
	 * 
	 * @param comboName
	 * @param liste
	 *            : liste des items dans un array
	 * @param reset
	 *            : true pour reinitialiser la liste
	 */
	public void addComboItemList(String comboName, String[] liste, boolean reset) {
		@SuppressWarnings({ "unchecked" })
		JComboBox<String> combo = (JComboBox<String>) fieldList.get(comboName);
		combo.removeActionListener(actionListener);
		combo.removePropertyChangeListener("value", textListener);
		/*
		 * Suppression des items si demande
		 */
		if (reset) {
			combo.removeAllItems();
		}
		for (String item : liste) {
			combo.addItem(item);
		}
		combo.addActionListener(actionListener);
		combo.addPropertyChangeListener("value", textListener);
	}

	/**
	 * Retourne la comboBox specifiee
	 * 
	 * @param name
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public JComboBox<Object> getCombo(String name) {
		return (JComboBox<Object>) fieldList.get(name);
	}

	/**
	 * Ajoute des textFields dans le composant, a l'emplacement souhaite Si
	 * verifier > 0, rajoute un controle de contenu : 1 : entree numerique
	 * 
	 * @param name
	 * @param X
	 * @param Y
	 * @param Z
	 * @param verifier
	 */
	public void addTextField(String name, int X, int Y, int Z, int verifier) {
		JTextField texte = new JTextField();
		NumericVerifier nv;
		texte.addPropertyChangeListener("value", textListener);
		texte.getDocument().addDocumentListener(documentListener);
		switch (verifier) {
		case 1:
			texte.setInputVerifier(new NumericVerifier());
			texte.setToolTipText(Langue.getString("numericField"));
			break;
		case 2:
			texte.setInputVerifier(new MaxLength50());
			texte.setToolTipText(Langue.getString("longueur50"));
			break;
		case 3:
			nv = new NumericVerifier();
			nv.isPourcentage = true;
			texte.setInputVerifier(nv);
			texte.setToolTipText(Langue.getString("pourcentage"));
			break;
		case 4:
			nv = new NumericVerifier();
			nv.isDecimal = true;
			texte.setInputVerifier(nv);
			texte.setToolTipText(Langue.getString("decimalField"));
			break;
		case 5:
			nv = new NumericVerifier();
			nv.isPourcentage = true;
			nv.isDecimal = true;
			texte.setInputVerifier(nv);
			texte.setToolTipText(Langue.getString("pourcentageDecimal"));
			break;
		}
		texte.setDisabledTextColor(Color.DARK_GRAY);
		/*
		 * Definition de la taille par defaut
		 */
		fieldList.put(name, texte);
		setDimension(name, dimensionDefault);
		gbcSetPos(X, Y, Z);
		pane.add(fieldList.get(name), gbc);
	}

	/**
	 * Ajoute un champ texte simple
	 * 
	 * @param name
	 * @param X
	 * @param Y
	 * @param Z
	 */
	public void addTextField(String name, int X, int Y, int Z) {
		addTextField(name, X, Y, Z, 0);
	}

	/**
	 * Ajoute un champ avec controle de coherence numerique
	 * 
	 * @param name
	 * @param X
	 * @param Y
	 * @param Z
	 */
	public void addTextNumeric(String name, int X, int Y, int Z) {
		addTextField(name, X, Y, Z, 1);
	}

	/**
	 * Ajoute un champ avec controle de coherence numerique dont la valeur doit
	 * etre comprise entre 0 et 100
	 * 
	 * @param name
	 * @param x
	 * @param y
	 * @param z
	 */
	public void addTextPourcentage(String name, int x, int y, int z) {
		addTextField(name, x, y, z, 3);
	}

	/**
	 * Ajoute un champ avec controle de coherence decimale
	 * 
	 * @param name
	 * @param x
	 * @param y
	 * @param z
	 */
	public void addTextDecimal(String name, int x, int y, int z) {
		addTextField(name, x, y, z, 4);
	}

	/**
	 * Ajoute un champ avec controle de coherence decimale, dont la valeur doit
	 * etre comprise entre 0 et 100
	 * 
	 * @param name
	 * @param x
	 * @param y
	 * @param z
	 */
	public void addTextPourcentageDecimal(String name, int x, int y, int z) {
		addTextField(name, x, y, z, 5);
	}

	/**
	 * Ajoute un champ avec controle de longueur
	 * 
	 * @param name
	 * @param X
	 * @param Y
	 * @param Z
	 */
	public void addTextMaxLength50(String name, int X, int Y, int Z) {
		addTextField(name, X, Y, Z, 2);
	}

	/**
	 * Ajoute un textArea avec barres de defilement pre-positionnees
	 * 
	 * @param name
	 * @param X
	 * @param Y
	 * @param Z
	 * @param width
	 * @param height
	 */
	public void addTextArea(String name, int X, int Y, int Z, int width,
			int height) {
		JTextArea jta = new JTextArea();
		jta.setLineWrap(true);
		jta.setDisabledTextColor(Color.DARK_GRAY);
		JScrollPane scroll = new JScrollPane(jta,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setPreferredSize(new Dimension(width, height));
		jta.getDocument().addDocumentListener(documentListener);
		fieldList.put(name, jta);
		gbcSetPos(X, Y, Z);
		pane.add(scroll, gbc);
	}

	/**
	 * Ajoute un composant date
	 * 
	 * @param name
	 * @param pDate
	 * @param X
	 * @param Y
	 * @param Z
	 */
	public void addDatePicker(String name, Date pDate, int X, int Y, int Z) {
		DatePicker dp = new DatePicker(pDate);
		dp.addActionListener(actionListener);
		fieldList.put(name, dp);
		setDimension(name, dimensionDefault);
		gbcSetPos(X, Y, Z);
		pane.add(fieldList.get(name), gbc);
	}

	/**
	 * Ajoute un composant JCheckBox Le composant retourne 1 si selectionne, 0
	 * sinon
	 * 
	 * @param name
	 * @param defaultValue
	 *            : true|false
	 * @param x
	 * @param y
	 * @param z
	 */
	public void addCheckBox(String name, boolean defaultValue, int x, int y,
			int z) {
		JCheckBox jcb = new JCheckBox();
		jcb.addActionListener(actionListener);
		fieldList.put(name, jcb);
		jcb.setSelected(defaultValue);
		gbcSetPos(x, y, z);
		pane.add(fieldList.get(name), gbc);
	}

	/**
	 * Ajoute un composant checkbox, dont la valeur initiale est fournie sous
	 * forme de chaine (1 : selectionne)
	 * 
	 * @param name
	 * @param defaultValue
	 *            : 1 selectionne, autre : non selectionne
	 * @param x
	 * @param y
	 * @param z
	 */
	public void addCheckBox(String name, String defaultValue, int x, int y,
			int z) {
		boolean defaut = false;
		if (defaultValue == "1")
			defaut = true;
		addCheckBox(name, defaut, x, y, z);
	}

	/**
	 * Definit les dimensions preferees pour le champ
	 * 
	 * @param name
	 * @param dim
	 */
	public void setDimension(String name, Dimension dim) {
		fieldList.get(name).setPreferredSize(dim);
	}

	/**
	 * Definit les champs obligatoires
	 * 
	 * @param pField
	 */
	public void setFieldMandatory(String[] pField) {
		for (String name : pField)
			fieldMandatory.add(name);
	}

	/**
	 * Rajoute un champ obligatoire
	 * 
	 * @param field
	 */
	public void addFieldMandatory(String field) {
		fieldMandatory.add(field);
	}

	/**
	 * Definit les champs necessaires pour valider le dossier
	 * 
	 * @param pField
	 */
	public void setFieldNecessary(String[] pField) {
		for (String name : pField)
			fieldNecessary.add(name);
	}

	/**
	 * Rajoute un champ necessaire pour valider le dossier
	 * 
	 * @param field
	 */
	public void addFieldNecessary(String field) {
		fieldNecessary.add(field);
	}

	/**
	 * Definit les champs recommandes
	 * 
	 * @param pField
	 */
	public void setFieldRecommanded(String[] pField) {
		for (String name : pField)
			fieldRecommanded.add(name);
	}

	/**
	 * Rajoute un champ recommande
	 * 
	 * @param field
	 */
	public void addFieldRecommanded(String field) {
		fieldRecommanded.add(field);
	}

	/**
	 * Donne le focus au champ indique
	 * 
	 * @param field
	 */
	public void setFocus(String field) {
		JComponent composant = fieldList.get(field);
		composant.requestFocusInWindow();
	}

	/**
	 * Desactive un champ, et l'affiche en gras pour qu'il ressorte
	 * 
	 * @param field
	 */
	public void setFieldDisabled(String field) {
		JComponent composant = fieldList.get(field);
		if (!composant.getClass().getSimpleName().equals("JLabel")) {
			composant.setEnabled(false);
			
			logger.debug(field + " : " + composant.getClass().getSimpleName());
			composant.setFont(new Font("Arial", Font.BOLD, 14));
		}
	}
	
	public void setFieldVisible(String field, boolean visible) {
		JComponent composant = fieldList.get(field);
		composant.setVisible(visible);
	}

	/**
	 * Fonction utilitaire, permettant de fusionner le second Hashtable dans le
	 * premier
	 * 
	 * @param ori
	 *            : Hashtable d'origine
	 * @param comp
	 *            : Hashtable a fusionner dans ori
	 * @return Hashtable<String, String>
	 */
	public Hashtable<String, String> hashtableFusionner(
			Hashtable<String, String> ori, Hashtable<String, String> comp) {
		for (Entry<String, String> entry : comp.entrySet()) {
			ori.put(entry.getKey(), entry.getValue());
		}
		return ori;
	}

	/**
	 * Ajoute un bouton en precisant le libelle, le mnemonique, l'action e
	 * signaler sur clic
	 * 
	 * @param libelle
	 * @param mnemonic
	 * @param actionEvent
	 * @param x
	 * @param y
	 * @param z
	 */
	public void addButton(String libelle, char mnemonic,
			final String actionEvent, int x, int y, int z) {
		JButton jb = new JButton(Langue.getString(libelle));
		jb.setMnemonic(mnemonic);
		jb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				actionLibelle = actionEvent;
				// setChanged();
				// notifyObservers(actionEvent);
				((ComposantAlisma) obj).setAction();
			}
		});
		gbcSetPos(x, y, z);
		pane.add(jb, gbc);
		buttons.put(libelle, jb);
	}

	/**
	 * Active ou inactive le bouton considere
	 * 
	 * @param libelle
	 *            : texte affiche du bouton
	 * @param enable
	 *            : true|false
	 */
	public void setButtonEnabled(String libelle, boolean enable) {
		buttons.get(libelle).setEnabled(enable);
	}

	/**
	 * Active ou inactive les champs en saisie
	 * 
	 * @param enabled
	 */
	public void setEnabled(boolean enabled) {
		/*
		 * Desactivation des champs de saisie
		 */
		for (Entry<String, JComponent> entry : fieldList.entrySet()) {
			//System.out.println (entry + " : " + entry.getClass().getSimpleName() );

			if (!entry.getValue().getClass().getSimpleName().equals("JLabel"))
				entry.getValue().setEnabled(enabled);
		}
		/*
		 * Desactivation des boutons
		 */
		for (Entry<String, JButton> entry : buttons.entrySet()) {
			entry.getValue().setEnabled(enabled);
		}

		/*
		 * Lancement de la meme operation pour les objets integres
		 */
		for (ComposantAlisma comp : listeComposant) {
			comp.setEnabled(enabled);
		}
	}

}
