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
import java.util.Iterator;
import java.util.List;
import java.util.Observable;

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
import database.Groupe;
import database.TaxonPerso;

/**
 * @author quinton
 *
 */
@SuppressWarnings("serial")
public class TaxonPersoChange extends JFrameAlisma {
	static Logger logger = Logger.getLogger(TaxonPersoChange.class);
	private JLabel banniere = new JLabel(" ", Parametre.logo, JLabel.TRAILING);
	private GridBagConstraints gbc = new GridBagConstraints();
	private TaxonPerso taxonPersoDb = new TaxonPerso();
	private TaxonForm taxonForm = new TaxonForm();
	private Groupe groupe = new Groupe();
	private String key = "";

	public TaxonPersoChange() {
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
		banniere.setText(Langue.getString("modifTaxon"));
		banniere_pane.add(banniere);
		this.getContentPane().add(banniere_pane, BorderLayout.NORTH);
		/*
		 * Ajout du titre
		 */
		this.setTitle(Langue.getString("modifTaxon"));
		JPanel contenu = new JPanel(new GridBagLayout());
		gbc.gridx = 0;
		gbc.gridy = 0;
		contenu.setBackground(Parametre.cCentral);
		contenu.add(taxonForm.getPane());
		taxonForm.addObserver(this);
		contenu.setPreferredSize(new Dimension(550, 500));
		this.getContentPane().add(contenu, BorderLayout.CENTER);
		contenu.getRootPane().setDefaultButton(
				taxonForm.buttons.get("boutonValider"));
		this.draw(600, 550);

	}

	/**
	 * Charge la cle a traiter
	 * 
	 * @param id
	 */
	public void setKey(Object id) {
		key = (String) id;
		logger.debug("setKey : " + key);
		/*
		 * Mise a jour de la liste des groupes
		 */
		groupe.readData();
		logger.debug("comboData.size: " + groupe.comboData.size());
		taxonForm.setGroup(groupe.comboData);
		/*
		 * Lecture
		 */
		try {
			if (!key.isEmpty()) {
				Hashtable<String, String> data = taxonPersoDb.read(key);
				taxonForm.setData(data);
				taxonForm.buttons.get("boutonSupprimer").setEnabled(true);
				taxonForm.groupSelect();
			} else {
				key = taxonPersoDb.getNewCode();
				taxonForm.setValue("cd_taxon_perso", key);
			}
		} catch (NullPointerException e) {
			key = taxonPersoDb.getNewCode();
			taxonForm.setValue("cd_taxon_perso", key);
		}
		/*
		 * Remise a zero de l'indicateur de modification
		 */
		setModified(false);
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		super.update(arg0, arg1);
		switch ((String) arg1) {
		case "valider":
			if (taxonForm.validation() < 3) {
				String newKey = taxonPersoDb.write(taxonForm.getData(), key);
				if (!newKey.isEmpty()) {
					this.setMessageInfo(Langue.getString("enregistrementOk"));
					key = newKey;
					setModified(false);
					setChanged();
					notifyObservers("taxonPersoHasChanged");
					taxonForm.buttons.get("boutonSupprimer").setEnabled(true);
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

				if (taxonPersoDb.delete("cd_taxon_perso",
						String.valueOf(key), false)) {
					setModified(false);
					setChanged();
					notifyObservers("taxonPersoHasChanged");
					close();
				} else {
					this.setMessageInfo("deleteKo");
				}
			}
			break;
		}

	}

	class TaxonForm extends ComposantAlisma {
		/*
		 * Premiere valeur : cours_eau Seconde valeur : id_cours_eau
		 */
		private Hashtable<String, String> groupeList = new Hashtable<String, String>();

		public TaxonForm() {
			addHidden("id_groupe");
			Dimension labelDim = new Dimension(150, 25);
			Dimension fieldDim = new Dimension(250, 25);
			addLabel("cd", 0, 0, labelDim);
			addTextField("cd_taxon_perso", 1, 0, 1);
			setDimension("cd_taxon_perso", fieldDim);
			addLabel("nomTaxon", 0, 1, labelDim);
			addTextField("nom_taxon_perso", 1, 1, 1);
			setDimension("nom_taxon_perso", fieldDim);
			addLabel("auteur", 0, 2, labelDim);
			addTextField("auteur", 1, 2, 1);
			setDimension("auteur", fieldDim);
			addLabel("cdSandre", 0, 3, labelDim);
			addTextField("cd_sandre", 1, 3, 1);
			setDimension("cd_sandre", fieldDim);
			addLabel("createur", 0, 4, labelDim);
			addTextField("createur", 1, 4, 1);
			addLabel("groupe", 0, 5, labelDim);
			addCombo("groupe", 1, 5, 1);
			addFieldMandatory("nom_taxon_perso");
			addFieldMandatory("createur");
			addFieldMandatory("groupe");
			addButton("boutonValider", 'V', "valider", 1, 6, 1);
			addButton("boutonSupprimer", 'S', "supprimer", 2, 6, 1);
			buttons.get("boutonSupprimer").setEnabled(false);
		}

		public void groupSelect() {
			JComboBox<Object> cb = getCombo("groupe");
			logger.debug("nom_groupe : "+ groupe.getValueFromKey(Integer.valueOf(getData("id_groupe"))));
			cb.setSelectedItem(groupe.getValueFromKey(Integer.valueOf(getData("id_groupe"))));
		}

		public void setGroup(Hashtable<Integer, String> comboData) {
			groupeList.clear();
			String libelle;
			JComboBox<Object> cb = getCombo("groupe");
			cb.removeAllItems();
			for( Iterator<Integer> ii = comboData.keySet().iterator(); ii.hasNext();) { 
				int id = (int) ii.next();				
				libelle = comboData.get(id);
				logger.debug("id:"+id);
				logger.debug("libelle:"+libelle);
				groupeList.put(libelle, String.valueOf(id));
				cb.addItem(libelle);
			}
		}

		public Hashtable<String, String> getData() {
			Hashtable<String, String> data = super.getData();
			/*
			 * Ajoute la cle du groupe
			 */
			if (!data.get("groupe").isEmpty()) {
				data.put("id_groupe", groupeList.get(data.get("groupe")));
			} else {
				data.put("id_groupe", "");
			}
			return data;
		}
	}

}
