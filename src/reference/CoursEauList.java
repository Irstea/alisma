package reference;

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
import java.util.Hashtable;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import org.apache.log4j.Logger;

import database.Cours_Eau;
import utils.ComposantAlisma;
import utils.JFrameAlisma;
import utils.Langue;
import utils.ObservableExtended;
import utils.Parametre;


@SuppressWarnings("serial")
public class CoursEauList extends Observable implements Observer, ObservableExtended{
	public JFrameAlisma fenetre = new JFrameAlisma();
	private JLabel banniere = new JLabel(" ", Parametre.logo, JLabel.TRAILING);
	private GridBagConstraints gbc = new GridBagConstraints();
	private TableData table;
	private ComposantAlisma search = new Search();
	private Cours_Eau coursEauDb = new Cours_Eau();
	static Logger logger = Logger.getLogger(CoursEauList.class);
	private String[] columnName = { Langue.getString("id"), Langue.getString("cours_eau")};
	private int selectedId = 0;


	public CoursEauList() {
		super();
		/*
		 * Ajout du bandeau
		 */
		JPanel banniere_pane = new JPanel(new FlowLayout());
		((FlowLayout) banniere_pane.getLayout()).setAlignment(FlowLayout.LEFT);
		banniere_pane.setBackground(Parametre.cBanniere);
		banniere.setBackground(Parametre.cBanniere);
		banniere.setFont(new Font(banniere.getFont().getName(), banniere.getFont().getStyle(), 15));
		banniere.setText(Langue.getString("coursEau"));
		banniere_pane.add(banniere);
		fenetre.getContentPane().add(banniere_pane, BorderLayout.NORTH);

		/*
		 * Ajout du titre
		 */
		fenetre.setTitle(Langue.getString("coursEau"));

		JPanel contenu = new JPanel(new GridBagLayout());
		gbc.gridx = 0;
		gbc.gridy = 0;
		search.addObserver(this);
		contenu.add(search.getPane(), gbc);
		contenu.setBackground(Parametre.cCentral);
		fenetre.getContentPane().add(contenu, BorderLayout.CENTER);

		// JTable
		table = new TableData(new DefaultTableModel()); 
		table.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				/*
				 * Declenche l'affichage de la fiche en modification
				 */
				if (e.getClickCount() >= 2) {
					Point p = e.getPoint();
					int row = table.rowAtPoint(p);
					int column = table.convertColumnIndexToModel(table.columnAtPoint(p));
					if (row >= 0 && column >= 0) {
						selectedId = table.getId();
						setChanged();
						notifyObservers("coursEauChange");
					}
				}
			}
		});


		JScrollPane liste_scroll = new JScrollPane(table);
		liste_scroll.setAlignmentX(Component.LEFT_ALIGNMENT);
		liste_scroll.setPreferredSize(new Dimension(500, 700));

		gbc.gridy = 1;
		contenu.add(liste_scroll, gbc);
		initTable();
		contenu.getRootPane().setDefaultButton(search.buttons.get("boutonChercher"));
		fenetre.draw(512, 768);

	}
	
	private void initTable() {
		((DefaultTableModel) this.table.getModel()).setDataVector(coursEauDb.searchByName(this.getParam()), columnName);
		table.getColumnModel().getColumn(0).setMinWidth(0);
		table.getColumnModel().getColumn(0).setMaxWidth(0);
	}
	
	public Object getValue(String value) {
		logger.debug("CoursEauList.getValue "+value);
		return selectedId;
	}
	
	class Search extends ComposantAlisma {
		Dimension dimLabel = new Dimension(120, 25);

		public Search() {
			addLabel("triCoursEau", 0, 0, 2, new Dimension(250, 25));
			setDimensionDefault(dimLabel);
			addTextField("cours_eau", 2, 0, 2);
			addButton("boutonChercher", 'R', "rechercher", 0, 1, 1);
			addButton("boutonModif", 'M', "modifier", 1, 1, 1);
			addButton("boutonNouveau", 'N', "nouveau", 2, 1, 1);
		}

		/**
		 * Renvoie les parametres saisis
		 * @return Hashtable<String, String>
		 */
		public Hashtable<String, String> getParam() {
			Hashtable<String, String> param = new Hashtable<String, String>();
			param.put("cours_eau", getData("zoneSearch"));
			return param;
		}
	}

	public Hashtable<String, String> getParam() {
		return search.getData();
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		switch ((String) arg1) {
		case "rechercher":
			initTable();
			break;
		case "modifier":
			selectedId = table.getId();
			setChanged();
			notifyObservers("coursEauChange");
			break;
		case "nouveau":
			selectedId = 0;
			setChanged();
			notifyObservers("coursEauChange");
			break;
		}
		
	}
	public void dataRefresh() {
		initTable();
	}

	/**
	 * Table d'affichage de la liste
	 * @author quinton
	 *
	 */
	class TableData extends JTable {

		public TableData(DefaultTableModel defaultTableModel) {
			super(defaultTableModel);
			setAutoCreateRowSorter(true);
			getTableHeader().setReorderingAllowed(false);
			setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			getTableHeader().setResizingAllowed(true);	
		}

		/**
		 * Retourne l'identifiant selectionne
		 * @return id sous forme de String
		 */
		public int getId() {
			int id = 0;
			try {
				id = Integer.parseInt( (String) getValueAt(getSelectedRow(), 0));
			} catch (ClassCastException e) {
				logger.debug(getValueAt(getSelectedRow(), 0));
			} catch (IndexOutOfBoundsException e1) {}
			logger.debug("id : "+id);
			return id;
		}

		public boolean isCellEditable(int rowIndex, int colIndex) {
			return false;
		}
		
	}

}
