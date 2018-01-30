/**
 * 
 */
package alisma;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import utils.Langue;


/**
 * @author quinton
 * Contient le menu de la fenetre principale
 */
public class MenuMain extends Observable {

	JMenuBar menuBar;
	Object obj;
	
	List<String> lhtMenu = new ArrayList<String>(),
			lhtItem = new ArrayList<String>();
	Hashtable<String, JMenu> htMenu = new Hashtable<String, JMenu>();
	Hashtable<String, JMenuItem> htItem = new Hashtable<String, JMenuItem>();
	
	ActionListener actionListener;
	
	String key;
	Iterator<String> iter;
	JMenu menuCourant;
	JMenuItem itemCourant;

	/**
	 * Constructeur
	 */
	public MenuMain() {
		/*
		 * Initialisations
		 */
		obj = this;
		menuBar = new JMenuBar();
		
		/*
		 * Preparation des items
		 */
		htItem.put("quitter", new JMenuItem());
		htItem.put("taxonReference", new JMenuItem());
//		htItem.put("taxonPerso", new JMenuItem());
		htItem.put("cours_eau", new JMenuItem());
		htItem.put("station", new JMenuItem());
		htItem.put("opConsult", new JMenuItem());
		htItem.put("opNouveau", new JMenuItem());
		htItem.put("dbSave", new JMenuItem());
//		htItem.put("dbImport", new JMenuItem());
//		htItem.put("opExport", new JMenuItem());
//		htItem.put("expLibre", new JMenuItem());
//		htItem.put("expPdf", new JMenuItem());
		htItem.put("aPropos", new JMenuItem());
		
		/*
		 * Instanciation des sous-menus
		 */
		iter = htItem.keySet().iterator();
		while(iter.hasNext()) {
			key = (String) iter.next();
			itemCourant = htItem.get(key);
			itemCourant.addActionListener(getAction(key));
		}
		
		/*
		 * Instanciation des menus
		 */
		lhtMenu.add("fichier");
		lhtMenu.add("param");
		lhtMenu.add("opControle");
		lhtMenu.add("impExp");
		lhtMenu.add("help");
		
		iter = lhtMenu.iterator();		
		while(iter.hasNext()) {
			key = (String) iter.next();
			htMenu.put(key, new JMenu());
			menuCourant = htMenu.get(key);
			menuBar.add(menuCourant);		
		}
		/*
		 * Ajout des sous-menus aux menus
		 */
		menuCourant = htMenu.get("fichier");
		menuCourant.add(htItem.get("quitter"));
		menuCourant = htMenu.get("param");
		menuCourant.add(htItem.get("taxonReference"));
//		menuCourant.add(htItem.get("taxonPerso"));
		menuCourant.add(htItem.get("cours_eau"));
		menuCourant.add(htItem.get("station"));
		menuCourant = htMenu.get("opControle");
		menuCourant.add(htItem.get("opConsult"));
		menuCourant.add(htItem.get("opNouveau"));
		menuCourant = htMenu.get("impExp");
		menuCourant.add(htItem.get("dbSave"));
//		menuCourant.add(htItem.get("dbImport"));
//		menuCourant.add(htItem.get("opExport"));
//		menuCourant.add(htItem.get("expLibre"));
//		menuCourant.add(htItem.get("expPdf"));
		menuCourant = htMenu.get("help");
		menuCourant.add(htItem.get("aPropos"));
		
		/*
		 * Positionnement des libelles
		 */
		setLibelle();
		
		/*
		 * Creation du menu
		 */
		iter = lhtMenu.iterator();		
		while(iter.hasNext()) {
			menuBar.add(htMenu.get((String) iter.next()));		
		}
	}
	
	/**
	 * Declenchement de l'action sur choix dans le menu
	 * @param action
	 */
	public void setAction(String action) {
		setChanged();
		notifyObservers(action);
	}
	/**
	 * Retourne le menu
	 * @return JMenuBar
	 */
	public JMenuBar getMenu() {
		return menuBar;
	}
	
	/**
	 * Creation des actions pour les sous-menus
	 * @param key
	 * @return
	 */
	ActionListener getAction(final String key) {
		return new ActionListener () { 
			public void actionPerformed(ActionEvent arg0){
				((MenuMain) obj).setAction(key);
			}
		};
	}
	
	/**
	 * Definition des libelles
	 */
	public void setLibelle() {
		/*
		 * Traitement des sous-menus
		 */
		iter = htItem.keySet().iterator();
		while(iter.hasNext()) {
			key = (String) iter.next();
			itemCourant = htItem.get(key);
			itemCourant.setText(Langue.getString(key));
			itemCourant.setMnemonic(Langue.getString(key).charAt(0));
		}
		/*
		 * Traitement des menus
		 */
		iter = lhtMenu.iterator();		
		while(iter.hasNext()) {
			key = (String) iter.next();
			menuCourant = htMenu.get(key);
			menuCourant.setText(Langue.getString(key));
			menuCourant.setMnemonic(Langue.getString(key).charAt(0));
		}		
	}
}
