/**
 * 
 */
package alisma;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Observable;
import java.util.Observer;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import utils.JFrameAlisma;
import utils.Langue;
import utils.Parametre;

/**
 * @author quinton
 * 
 *         fenetre principale de l'application
 */
public class MainFenetre extends Observable implements Observer {
	MenuMain menu;
	Fenetre fenetre;
	MainFenetre mf;

	public MainFenetre() {
		mf = this;
		menu = new MenuMain();
		fenetre = new Fenetre();
		fenetre.addObserver(this);
	}

	/**
	 * Ajout des observateurs de la classe
	 */
	public void addObserver(Observer obj) {
		super.addObserver(obj);
		menu.addObserver(obj);

	}

	/**
	 * Basculement en mode public de la fonction setChanged()
	 */
	public void setChanged() {
		super.setChanged();
	}

	/**
	 * Redefinition des libelles de la fenetre
	 */
	public void setLibelle() {
		menu.setLibelle();
		fenetre.setLibelle();
	}

	@SuppressWarnings("serial")
	class Fenetre extends JFrameAlisma {
		/**
		 * 
		 */

		JPanel buttons_panel = new JPanel(new GridBagLayout());
		JLabel banniere = new JLabel(), languageChoice = new JLabel();

		public Fenetre() {
			this.setJMenuBar(menu.getMenu());
			/*
			 * Mise en place de la banniere
			 */
			GridBagConstraints gbc = new GridBagConstraints();
			JPanel banniere_pane = new JPanel(new FlowLayout());
			((FlowLayout) banniere_pane.getLayout())
					.setAlignment(FlowLayout.LEFT);
			banniere_pane.setBackground(Parametre.cBanniere);
			banniere.setFont(new Font(banniere.getFont().getName(), banniere
					.getFont().getStyle(), 15));
			banniere_pane.add(banniere);
			this.getContentPane().add(banniere_pane, BorderLayout.NORTH);

			/*
			 * Selection de la langue
			 */

			GridBagConstraints gbcStandard = new GridBagConstraints();

			buttons_panel.add(languageChoice, gbcStandard);
			languageChoice.setText(Langue.getString("choixLangage"));

			JLabel anglais = new JLabel(new ImageIcon(getClass()
					.getClassLoader().getResource(
							"ressources/UnitedKingdom.png")));
			anglais.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					mf.setChanged();
					((Observable) mf).notifyObservers("anglais");
				}
			});
			JLabel francais = new JLabel(new ImageIcon(getClass()
					.getClassLoader().getResource("ressources/France.png")));
			francais.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					mf.setChanged();
					((Observable) mf).notifyObservers("francais");
				}
			});
			gbcStandard.gridx = 1;
			buttons_panel.add(francais, gbcStandard);
			gbcStandard.gridx = 2;
			buttons_panel.add(anglais, gbcStandard);
			buttons_panel.add(new JLabel(new ImageIcon(getClass()
					.getClassLoader().getResource("ressources/alisma_logo2.jpg"))),
					initConstraints(gbc, 0, 1, 3));
			buttons_panel.add(new JLabel(new ImageIcon(getClass()
					.getClassLoader().getResource("ressources/img1.jpg"))),
					initConstraints(gbc, 0, 2, 1));
			buttons_panel.add(new JLabel(new ImageIcon(getClass()
					.getClassLoader().getResource("ressources/img2.jpg"))),
					initConstraints(gbc, 1, 2, 1));
			buttons_panel.add(new JLabel(new ImageIcon(getClass()
					.getClassLoader().getResource("ressources/img3.jpg"))),
					initConstraints(gbc, 2, 2, 1));

			/*
			 * Ajout des boutons de selection de la langue
			 */

			this.getContentPane().add(buttons_panel);

			/*
			 * Definition des libelles
			 */
			this.setLibelle();

			/*
			 * Lancement de l'affichage de la fenetre
			 */
			super.draw(650, 600);
		}

		/**
		 * Definition des libelles (non-Javadoc)
		 * 
		 * @see utils.JFrameAlisma#setLibelle()
		 */
		public void setLibelle() {
			/*
			 * Definition du libelle du pied de fenetre
			 */
			footer.setText(Langue.getString("alisma") + " " + Alisma.VERSION);
			languageChoice.setText(Langue.getString("choixLangage"));
			banniere.setText("<html>" + Langue.getString("banniere")
					+ "<br><br>" + Langue.getString("menuPrincipal")
					+ "</html>");
			super.setLibelle();
		}

		/*
		 * contraintes pour les elements du gridbaglayout
		 */
		private GridBagConstraints initConstraints(GridBagConstraints gbc,
				int x, int y, int w) {
			gbc.gridx = x;
			gbc.gridy = y;
			gbc.insets.top = 20;
			gbc.insets.right = 4;
			gbc.insets.bottom = 10;
			gbc.insets.left = 4;
			gbc.gridwidth = w;
			return gbc;
		}

	}

	@Override
	public void update(Observable arg0, Object arg1) {
		setChanged();
		if (arg1.equals("close")) 
			arg1 = "closeAll";
		notifyObservers(arg1);

	}

}
