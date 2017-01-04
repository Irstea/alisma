/**
 * 
 */
package utils;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JDialog;

import org.apache.log4j.Logger;

/**
 * @author quinton
 *
 */
public class JDialogAlisma extends JDialog {
	public JDialogAlisma obj;
	public static Logger logger = Logger.getLogger(JDialogAlisma.class);
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int hauteur, largeur;
	boolean isMax = false;

	/*
	 * Gestion des evenements de fermeture
	 */
	class MyWindowListener implements WindowListener {
		public void windowActivated(WindowEvent e) {
		}

		public void windowClosed(WindowEvent e) {
		}

		public void windowClosing(WindowEvent e) {
			close();
		}

		public void windowDeactivated(WindowEvent e) {
		}

		public void windowDeiconified(WindowEvent e) {
		}

		public void windowIconified(WindowEvent e) {
		}

		public void windowOpened(WindowEvent e) {
		}
	}
	
	public JDialogAlisma() {
		super();
		obj = this;
		this.setModal(true);
		/*
		 * Positionnement de l'icone de l'application
		 */
		try {
		this.setIconImage(Parametre.iconeImage);
		} catch (Exception e) {
			logger.error ( e.getMessage());
		}
	}
	
	/**
	 * Fermeture propre de la fenetre modale
	 */
	public void close() {
		this.setVisible(false);
		this.dispose();
	}
	
	/**
	 * Ouverture de la fenetre
	 */
	public void draw() {
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		if (isMax) {
			this.setSize(new Dimension (largeur, hauteur));
		} else
		this.setMinimumSize(new Dimension(largeur, hauteur));
		this.setLocation(d.width/2 - this.getWidth()/2, d.height/2 - this.getHeight()/2);
		this.pack();
	}
	
	/**
	 * Ouverture de la fenetre en indiquant les dimensions de base
	 * @param pLargeur
	 * @param pHauteur
	 */
	public void draw(int pLargeur, int pHauteur){
		hauteur = pHauteur;
		largeur = pLargeur;
		draw();
	}
	
	public void drawMax(int pLargeur, int pHauteur){
		hauteur = pHauteur;
		largeur = pLargeur;
		isMax = true;
		draw();
	}
}
