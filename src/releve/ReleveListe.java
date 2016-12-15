/**
 * 
 */
package releve;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Date;
import java.util.Hashtable;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import org.joda.time.DateTime;

import database.Op_controle;
import utils.ComposantAlisma;
import utils.Exportable;
import utils.JFrameAlisma;
import utils.Langue;
import utils.ObservableExtended;
import utils.Parametre;

/**
 * @author quinton
 * 
 *         Classe permettant de visualiser la liste des releves
 * 
 */
public class ReleveListe extends Observable implements Observer, Exportable, ObservableExtended {

	public JFrameAlisma fenetre = new JFrameAlisma();
	private JLabel banniere = new JLabel(" ", Parametre.logo, JLabel.TRAILING);
	ComposantAlisma search = new Search();
	GridBagConstraints gbc = new GridBagConstraints();
	Op_controle operation = new Op_controle();
	JTable table;
	int id;
	String[] columnName = { Langue.getString("id"), Langue.getString("cd_station"), Langue.getString("nomStation"),
			Langue.getString("nomRiv"), Langue.getString("operateur"), Langue.getString("dateListe"),
			Langue.getString("statut"), Langue.getString("releveDce") };

	public ReleveListe() {
		super();
		/*
		 * Ajout du bandeau
		 */
		JPanel banniere_pane = new JPanel(new FlowLayout());
		((FlowLayout) banniere_pane.getLayout()).setAlignment(FlowLayout.LEFT);
		banniere_pane.setBackground(Parametre.cBanniere);
		banniere.setBackground(Parametre.cBanniere);
		banniere.setFont(new Font(banniere.getFont().getName(), banniere.getFont().getStyle(), 15));
		banniere.setText(Langue.getString("listeOp"));
		banniere_pane.add(banniere);
		fenetre.getContentPane().add(banniere_pane, BorderLayout.NORTH);

		/*
		 * Ajout du titre
		 */
		fenetre.setTitle(Langue.getString("titreConsult"));

		JPanel contenu = new JPanel(new GridBagLayout());
		gbc.gridx = 0;
		gbc.gridy = 0;
		search.addObserver(this);
		contenu.add(search.getPane(), gbc);
		contenu.setBackground(Parametre.cCentral);
		fenetre.getContentPane().add(contenu, BorderLayout.CENTER);

		// JTable
		table = new JTable(new DefaultTableModel()) {
			private static final long serialVersionUID = 1L;

			public boolean isCellEditable(int rowIndex, int colIndex) {
				return false;
			}
		};
		table.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				/*
				 * Declenche l'affichage de la fiche en modification
				 */
				if (e.getClickCount() >= 1) {
					Point p = e.getPoint();
					int row = table.rowAtPoint(p);
					int column = table.convertColumnIndexToModel(table.columnAtPoint(p));
					if (row >= 0 && column >= 0) {
						id = Integer.parseInt((String) table.getValueAt(row, 0));
						setChanged();
						notifyObservers("opModif");
					}
				}
			}
		});

		table.setAutoCreateRowSorter(true);
		table.getTableHeader().setReorderingAllowed(false);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getTableHeader().setResizingAllowed(true);

		JScrollPane liste_scroll = new JScrollPane(table);
		liste_scroll.setAlignmentX(Component.LEFT_ALIGNMENT);
		liste_scroll.setPreferredSize(new Dimension(1000, 700));

		gbc.gridy = 1;
		contenu.add(liste_scroll, gbc);
		fenetre.draw(1024, 768);
	}

	public void dataRefresh() {
		initTable();
	}

	private void initTable() {
		((DefaultTableModel) this.table.getModel()).setDataVector(operation.getListeReleve(this.getParam()),
				columnName);
		table.getColumnModel().getColumn(0).setMinWidth(0);
		table.getColumnModel().getColumn(0).setMaxWidth(0);
	}

	class Search extends ComposantAlisma {
		Dimension dimLabel = new Dimension(120, 25);

		public Search() {
			addLabel("stationSearch", 0, 0, 2, new Dimension(250, 25));
			addLabel("statut", 4, 0, dimLabel);
			addLabel("dateDu", 0, 1, dimLabel);
			addLabel("au", 2, 1, dimLabel);
			setDimensionDefault(dimLabel);
			addTextField("zoneSearch", 2, 0, 2);
			addCombo("statut", 5, 0, 1);
			addButton("boutonChercher", 'R', "rechercher", 0, 2, 1);
			addButton("nouveau", 'N', "nouveau", 1, 2, 1);
			addButton("exporter", 'E', "exporter", 2, 2, 1);
			addButton("exportPdf", 'P', "exportPDF", 3, 2, 1);
			addButton("recalculer", 'C', "recalculer", 4, 2, 1);
			addComboItemList("statut", new String[] { "", Langue.getString("statut0"), Langue.getString("statut1") },
					true);
			addDatePicker("fin", new Date(), 3, 1, 1);
			/*
			 * Calcul de la date a l'annee precedente
			 */
			addDatePicker("debut", new DateTime().minusYears(1).toDate(), 1, 1, 1);
			addLabel("releveDce", 4, 1, dimLabel);
			addCombo("releve_dce", 5, 1, 1);
			addComboItemList("releve_dce", new String[] { "", Langue.getString("oui"), Langue.getString("non") }, true);
		}
	}

	@Override
	public void update(Observable o, Object arg) {
		switch ((String) arg) {
		case "rechercher":
			initTable();
			break;
		case "nouveau":
			setChanged();
			notifyObservers("opNouveau");
			break;
		case "exporter":
			/*
			 * Impose le statut valide
			 */
			search.getCombo("statut").setSelectedIndex(2);
			initTable();
			setChanged();
			notifyObservers("exportXml");
			break;
		case "exportPDF":
			search.getCombo("statut").setSelectedIndex(2);
			initTable();
			setChanged();
			notifyObservers("exportPDF");
			break;
		case "recalculer":
			search.getCombo("statut").setSelectedIndex(2);
			initTable();
			setChanged();
			notifyObservers("recalculer");
		}
	}

	/**
	 * Retourne l'identifiant selectionne
	 * 
	 * @return int
	 */
	public int getSelectedId() {
		return id;
	}

	public Object getValue(String value) {
		if (value.equals("getId"))
			return id;
		return null;
	}

	/**
	 * declenche l'affichage des libelles dans la langue choisie
	 */
	public void setLibelle() {
		search.setLabel();
		banniere.setText(Langue.getString("listeOp"));
	}

	@Override
	public Hashtable<String, String> getParam() {
		Hashtable<String, String> data = search.getData();
		/*
		 * Reformatage des valeurs fournies par les combobox
		 */
		if (!data.get("statut").isEmpty()) {
			if (data.get("statut").equals(Langue.getString("statut0"))) {
				data.put("statut", "0");
			} else
				data.put("statut", "1");
		}
		if (!data.get("releve_dce").isEmpty()) {
			if (data.get("releve_dce").equals(Langue.getString("oui"))) {
				data.put("releve_dce", "1");
			} else
				data.put("releve_dce", "0");
		}

		return data;
	}
}
