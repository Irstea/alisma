/**
 * 
 */
package reference;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Hashtable;
import java.util.Observable;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.apache.log4j.Logger;

import utils.ComposantAlisma;
import utils.JFrameAlisma;
import utils.Langue;
import utils.Parametre;
import database.Cours_Eau;
import database.Stations;

/**
 * @author quinton
 * 
 *         Saisie des stations
 *
 */
@SuppressWarnings("serial")
public class StationChange extends JFrameAlisma {
	static Logger logger = Logger.getLogger(CoursEauChange.class);
	private JLabel banniere = new JLabel(" ", Parametre.logo, JLabel.TRAILING);
	private GridBagConstraints gbc = new GridBagConstraints();
	private Stations stationDb = new Stations();
	private Cours_Eau coursEauDb = new Cours_Eau();
	private StationForm stationForm = new StationForm();
	private int key = 0;

	public StationChange() {
		super();
		isWindowChange = true;
		/*
		 * Ajout du bandeau
		 */
		JPanel banniere_pane = new JPanel(new FlowLayout());
		((FlowLayout) banniere_pane.getLayout()).setAlignment(FlowLayout.LEFT);
		banniere_pane.setBackground(Parametre.cBanniere);
		banniere.setBackground(Parametre.cBanniere);
		banniere.setFont(new Font(banniere.getFont().getName(), banniere
				.getFont().getStyle(), 15));
		banniere.setText(Langue.getString("station"));
		banniere_pane.add(banniere);
		this.getContentPane().add(banniere_pane, BorderLayout.NORTH);
		/*
		 * Ajout du titre
		 */
		this.setTitle(Langue.getString("station"));
		JPanel contenu = new JPanel(new GridBagLayout());
		gbc.gridx = 0;
		gbc.gridy = 0;
		contenu.setBackground(Parametre.cCentral);
		contenu.add(stationForm.getPane());
		stationForm.addObserver(this);
		contenu.setPreferredSize(new Dimension(550, 500));
		this.getContentPane().add(contenu, BorderLayout.CENTER);
		contenu.getRootPane().setDefaultButton(
				stationForm.buttons.get("boutonValider"));
		this.draw(600, 550);

	}

	@Override
	public void update(Observable arg0, Object arg1) {
		super.update(arg0, arg1);
		switch ((String) arg1) {
		case "valider":
			if (validation() < 3) {
				int newKey = stationDb.write(stationForm.getData(), key);
				if (newKey > 0) {
					this.setMessageInfo(Langue.getString("enregistrementOk"));
					key = newKey;
					setModified(false);
					setChanged();
					notifyObservers("stationHasChanged");
					stationForm.buttons.get("boutonSupprimer").setEnabled(true);
				} else {
					this.setMessageInfo(Langue.getString("enregistrementKo"));
				}
			}
			break;
		case "supprimer":
			int rep = JOptionPane.showConfirmDialog(
					null,
					Langue.getString("warningDelete"),
					Langue.getString("supprimer"),
					JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE,
					new ImageIcon(getClass().getClassLoader().getResource(
							"ressources/warning.png")));
			if (rep == JOptionPane.YES_OPTION) {

				if (stationDb.delete("id_station", String.valueOf(key), true)) {
					setModified(false);
					setChanged();
					notifyObservers("stationHasChanged");
					close();
				} else {
					this.setMessageInfo("deleteKo");
				}
			}
			break;
		case "chercher":
			/*
			 * Recherche des cours d'eau associes
			 */
			Hashtable<String,String> param = new Hashtable<String,String>();
			param.put("cours_eau", stationForm.getData("cours_eau"));
			stationForm.setCoursEau(coursEauDb.getListByParam(param));
			break;
		}

	}

	/**
	 * Charge la cle a traiter
	 * 
	 * @param id
	 */
	public void setKey(Object id) {
		key = (int) id;
		logger.debug("setKey : " + key);
		try {
			if (key > 0) {
				Hashtable<String,String> data = stationDb.read(key);
				stationForm.setData(data);
				stationForm.buttons.get("boutonSupprimer").setEnabled(true);
				/*
				 * Recuperation du cours d'eau correspondant
				 */
				Hashtable<String,String>param = new Hashtable<String,String>();
				param.put("id_cours_eau", data.get("id_cours_eau"));
				stationForm.setCoursEau(coursEauDb.getListByParam(param));				
			}
		} catch (NullPointerException e) {
			key = 0;
		}
		/*
		 * Remise a zero de l'indicateur de modification
		 */
		setModified(false);
	}
	
	public int validation() {
		int validLevel = stationForm.validation();
		int level;
		Double val;
		level = 0;
		Hashtable<String, String> data = stationForm.getData();
		/*
		 * Verification des coordonnees lambert
		 */
		String lambert = Parametre.others.get("lambert");
		if (lambert == null)
			lambert = "false";
		if (lambert.equals("true")) {
			try {
				val = Double.parseDouble(data.get("x"));
				if (val < Double.parseDouble(Parametre.others
						.get("lambert93Emin"))
						|| val > Double.parseDouble(Parametre.others
								.get("lambert93Emax")))
					level = 2;
			} catch (Exception e) {
				level = 3;
			}
			stationForm.setBordure("x", level);
			if (level > validLevel)
				validLevel = level;
			level = 0;
			try {
				val = Double.parseDouble(data.get("y"));
				if (val < Double.parseDouble(Parametre.others
						.get("lambert93Nmin"))
						|| val > Double.parseDouble(Parametre.others
								.get("lambert93Nmax")))
					level = 2;
			} catch (Exception e) {
				level = 3;
			}
			if (level > validLevel)
				validLevel = level;
			stationForm.setBordure("y", level);
		}
		return validLevel;

	}

	/**
	 * Masque de saisie
	 * 
	 * @author quinton
	 *
	 */
	class StationForm extends ComposantAlisma {
		/*
		 * Premiere valeur : cours_eau
		 * Seconde valeur : id_cours_eau
		 */
		private Hashtable <String,String> coursEauList = new Hashtable<String,String>();

		public StationForm() {
			addHidden("id_station");
			addHidden("id_cours_eau");
			Dimension labelDim = new Dimension(150, 25);
			Dimension fieldDim = new Dimension(250, 25);
			addLabel("nomStation", 0, 0, labelDim);
			addTextField("station", 1, 0, 1);
			setDimension("station", fieldDim);
			addLabel("cd_station", 0, 1, labelDim);
			addTextField("cd_station", 1, 1, 1);
			setDimension("cd_station", fieldDim);
			addLabel("X", 0, 2, labelDim);
			addTextField("x", 1, 2, 1);
			setDimension("x", fieldDim);
			addLabel("Y", 0, 3, labelDim);
			addTextField("y", 1, 3, 1);
			setDimension("y", fieldDim);
			addLabel("coursEauNom", 0, 4, labelDim);
			addTextField("cours_eau", 1, 4, 1);
			addButton("boutonChercher", 'R', "chercher", 2, 4, 1);
			addCombo("coursEauList", 1, 5, 1);
			addFieldMandatory("station");
			addFieldMandatory("coursEauList");
			try {
			if (Parametre.others.get("lambert").equals("true")) {
				addFieldMandatory("x");
				addFieldMandatory("y");
			}
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
			addButton("boutonValider", 'V', "valider", 1, 6, 1);
			addButton("boutonSupprimer", 'S', "supprimer", 2, 6, 1);
			buttons.get("boutonSupprimer").setEnabled(false);
		}
		
		public void setCoursEau(List<Hashtable<String,String>> coursEauData) {
			coursEauList.clear();
			String id, libelle;
			JComboBox<Object> cb = getCombo("coursEauList");
			cb.removeAllItems();
			for (int i = 0; i < coursEauData.size(); i++) {
				id = coursEauData.get(i).get("id_cours_eau");
				libelle = coursEauData.get(i).get("cours_eau");
				coursEauList.put(libelle,id);
				cb.addItem(libelle);				
			}
		}
		
		public Hashtable<String,String> getData() {
			Hashtable<String,String> data = super.getData();
			/*
			 * Ajoute la cle du cours d'eau
			 */
			if (!data.get("cours_eau").isEmpty()) {
				data.put("id_cours_eau", coursEauList.get(data.get("coursEauList")));
			} else {
				data.put ("id_cours_eau", "");
			}
			return data;
		}
	}

}
