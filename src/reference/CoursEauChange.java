package reference;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Hashtable;
import java.util.Observable;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.apache.log4j.Logger;

import database.Cours_Eau;
import utils.ComposantAlisma;
import utils.JFrameAlisma;
import utils.Langue;
import utils.Parametre;

/**
 * Fenetre de modification d'un cours d'eau
 * 
 * @author quinton
 *
 */
@SuppressWarnings("serial")
public class CoursEauChange extends JFrameAlisma {
	static Logger logger = Logger.getLogger(CoursEauChange.class);
	private JLabel banniere = new JLabel(" ", Parametre.logo, JLabel.TRAILING);
	private GridBagConstraints gbc = new GridBagConstraints();
	private Cours_Eau coursEauDb = new Cours_Eau();
	private CoursEauForm coursEauForm = new CoursEauForm();
	private int key = 0;

	public CoursEauChange() {
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
		banniere.setText(Langue.getString("coursEau"));
		banniere_pane.add(banniere);
		this.getContentPane().add(banniere_pane, BorderLayout.NORTH);
		/*
		 * Ajout du titre
		 */
		this.setTitle(Langue.getString("coursEau"));
		JPanel contenu = new JPanel(new GridBagLayout());
		gbc.gridx = 0;
		gbc.gridy = 0;
		contenu.setBackground(Parametre.cCentral);
		contenu.add(coursEauForm.getPane());
		coursEauForm.addObserver(this);
		contenu.setPreferredSize(new Dimension(550, 250));
		this.getContentPane().add(contenu, BorderLayout.CENTER);
		contenu.getRootPane().setDefaultButton(
				coursEauForm.buttons.get("boutonValider"));
		this.draw(600, 280);

	}

	@Override
	public void update(Observable arg0, Object arg1) {
		super.update(arg0, arg1);
		switch ((String) arg1) {
		case "valider":
			if (coursEauForm.validation() < 3) {
				int newKey = coursEauDb.write(coursEauForm.getData(), key);
				if (newKey > 0) {
					this.setMessageInfo(Langue.getString("enregistrementOk"));
					key = newKey;
					setModified(false);
					setChanged();
					notifyObservers("coursEauHasChanged");
					coursEauForm.buttons.get("boutonSupprimer")
							.setEnabled(true);
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

				if (coursEauDb
						.delete("id_cours_eau", String.valueOf(key), true)) {
					setModified(false);
					setChanged();
					notifyObservers("coursEauHasChanged");
					close();
				} else {
					this.setMessageInfo("deleteKo");
				}
			}
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
				coursEauForm.setData(coursEauDb.read(key));
				coursEauForm.buttons.get("boutonSupprimer").setEnabled(true);
			}
		} catch (NullPointerException e) {
			key = 0;
		}
		/*
		 * Remise a zero de l'indicateur de modification
		 */
		setModified(false);
	}

	public Hashtable<String, String> getData() {
		return coursEauForm.getData();
	}

	/**
	 * Masque de saisie
	 * 
	 * @author quinton
	 *
	 */
	class CoursEauForm extends ComposantAlisma {
		public CoursEauForm() {
			addHidden("id_cours_eau");
			addLabel("coursEauNom", 0, 0, new Dimension(150, 25));
			addTextField("cours_eau", 1, 0, 1);
			setDimension("cours_eau", new Dimension(250, 25));
			addFieldMandatory("cours_eau");
			addButton("boutonValider", 'V', "valider", 2, 0, 1);
			addButton("boutonSupprimer", 'S', "supprimer", 2, 1, 1);
			buttons.get("boutonSupprimer").setEnabled(false);
		}
	}

}
