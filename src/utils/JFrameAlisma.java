/**
 * 
 */
package utils;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.apache.log4j.Logger;

import alisma.Alisma;

/**
 * @author quinton
 *
 * JFrame de base, pre-configuree
 */
@SuppressWarnings("serial")
public class JFrameAlisma extends JFrame implements Observer {

	public JLabel footer   = new JLabel();
	private int largeur = 800, hauteur = 600;
	List <Observer> listeObs = new ArrayList<Observer> ();
	Boolean hasChanged = false;
	Boolean isModified = false;
	protected Boolean isWindowChange = false;
	WindowListener windowListener;
	static Logger logger = Logger.getLogger(JFrameAlisma.class);

	
	public JFrameAlisma() {
		super();
		/*
		 * Positionnement de l'icone de l'application
		 */
		this.setIconImage(Parametre.iconeImage);
		this.getContentPane().setLayout(new BorderLayout());
		
		/*
		 * Definition du pied de page
		 */
		JPanel footer_panel = new JPanel(new FlowLayout());
		((FlowLayout)footer_panel.getLayout()).setAlignment(FlowLayout.RIGHT);
		footer_panel.setBackground(Parametre.cFooter);
		footer.setFont(new Font(footer.getFont().getName(), footer.getFont().getStyle(), 12));
		footer.setText("Alisma "+ Alisma.VERSION);
		footer_panel.add(footer);

		this.getContentPane().add(footer_panel, BorderLayout.SOUTH);
		
		windowListener = new WindowListener() {
			@Override
			public void windowActivated(WindowEvent e) {
				
			}

			@Override
			public void windowClosed(WindowEvent e) {
				
			}

			@Override
			public void windowClosing(WindowEvent e) {
				close();
			}

			@Override
			public void windowDeactivated(WindowEvent e) {
				
			}

			@Override
			public void windowDeiconified(WindowEvent e) {
				
			}

			@Override
			public void windowIconified(WindowEvent e) {
				
			}

			@Override
			public void windowOpened(WindowEvent e) {
				
			}
		};
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(windowListener);
	}
	
	public void draw() {
		this.setMinimumSize(new Dimension(largeur, hauteur));
		setPosition("C");
		this.setVisible(true);
		this.pack();
	}
	
	public void draw(int pLargeur, int pHauteur){
		hauteur = pHauteur;
		largeur = pLargeur;
		draw();
	}
	
	public void setIsWindowChange() {
		isWindowChange = true;
	}
	

	/**
	 * Fonction fermant la fenetre, en verifiant si aucune modification n'a ete apportee
	 */
	public void close() {
		boolean quitter = true;
		/*
		 * Verification qu'aucune modification ne soit en cours
		 */
		if (isModified && isWindowChange) {
			int rep;
			rep = JOptionPane.showOptionDialog(null,
					Langue.getString("confirmLoseData"), null,
					JOptionPane.WARNING_MESSAGE, JOptionPane.YES_NO_OPTION,
					null, null, JOptionPane.CANCEL_OPTION);
			if (rep != JOptionPane.YES_OPTION)
				quitter = false;
		}
		logger.debug("quitter : "+quitter);
		if (quitter) {
			setChanged();
			notifyObservers("close");
			setVisible(false);
			dispose();
		}
	}
	
	public void setModified (boolean modif) {
		isModified = modif;
	}

	/**
	 * Definit le titre de la fenêtre
	 */
	public void setLibelle() {
		/*
		 * Definition du titre
		 */
		this.setTitle(Langue.getString("alisma"));
	}

	/**
	 * Modifie le message en bas de fenetre
	 * @param message
	 */
	public void setMessageInfo(String message) {
		footer.setText(message);
	}
	
	/**
	 * Fonctions rajoutees pour emuler le fonctionnement de Observable
	 * @param o
	 */
	public void addObserver(Observer o) {
		listeObs.add(o);
	}
	
	public boolean hasChanged() {
		return hasChanged;
	}
	
	public void setChanged() {
		hasChanged = true;
	}
	
	public void notifyObservers(Object obj) {
		if (hasChanged) {
			Observer obs;
			Iterator<Observer> iter = listeObs.iterator();
			while(iter.hasNext()) {
				obs = (Observer) iter.next();
				obs.update(new Observable(), obj);
			}
			clearChanged();
		}
	}
	protected void clearChanged() {
		hasChanged = false;
	}
	
	/**
	 * Positionne la fenetre dans l'ecran
	 * TL : haut a gauche
	 * TR : haut a droite
	 * BL : bas a gauche
	 * BR : bas a droite
	 * C : centre
	 * @param pos
	 */
	public void setPosition (String pos) {
		Rectangle bounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
		switch (pos) {
		case "TL":
			setLocation(bounds.x, bounds.y);
			break;
		case "TR":
			setLocation(bounds.width - getWidth(), bounds.y);
			break;
		case "BL":
			setLocation(bounds.x, bounds.height - getHeight());
			break;
		case "BR":
			setLocation(bounds.width - getWidth(),bounds.height - getHeight());
			break;
		case "C":
		default:
			setLocation ( ((bounds.width - getWidth()) / 2), 
					(bounds.height - getHeight())/2);
		}
	}

	/**
	 * Fonction, a heriter, pour declencher le rafraichissement des donnees
	 */
	public void dataRefresh() {
		
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		switch ((String) arg1) {
		case "change":
			isModified = true;
			break;
		}
		
	}


	/**
	 * Fonction generique, a heriter, pour transmettre la valeur d'une cle
	 * @param key
	 */
	public void setKey (Object key) {
	}
	


}
