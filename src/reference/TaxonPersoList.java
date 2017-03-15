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
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.apache.log4j.Logger;

import utils.ComposantAlisma;
import utils.JFrameAlisma;
import utils.Langue;
import utils.ObservableExtended;
import utils.Parametre;
import database.TaxonPerso;

public class TaxonPersoList extends Observable implements Observer,
		ObservableExtended {

	public JFrameAlisma fenetre = new JFrameAlisma();
	private JLabel banniere = new JLabel(" ", Parametre.logo, JLabel.TRAILING);
	private GridBagConstraints gbc = new GridBagConstraints();
	private TableData table;
	private ComposantAlisma search = new Search();
	private TaxonPerso taxonPersoDb = new TaxonPerso();
	static Logger logger = Logger.getLogger(TaxonList.class);
	private String[] columnName = { Langue.getString("cd"),
			Langue.getString("nom"), Langue.getString("auteur"),
			Langue.getString("groupe"), Langue.getString("createur"),
			Langue.getString("cdSandreTab"), Langue.getString("date") };

	private String selectedId = "";

	public TaxonPersoList() {
		super();
		/*
		 * Ajout du bandeau
		 */
		JPanel banniere_pane = new JPanel(new FlowLayout());
		((FlowLayout) banniere_pane.getLayout()).setAlignment(FlowLayout.LEFT);
		banniere_pane.setBackground(Parametre.cBanniere);
		banniere.setBackground(Parametre.cBanniere);
		banniere.setFont(new Font(banniere.getFont().getName(), banniere
				.getFont().getStyle(), 15));
		banniere.setText(Langue.getString("taxonPerso"));
		banniere_pane.add(banniere);
		fenetre.getContentPane().add(banniere_pane, BorderLayout.NORTH);

		/*
		 * Ajout du titre
		 */
		fenetre.setTitle(Langue.getString("taxonPerso"));

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
						notifyObservers("taxonPersoChange");
					}
				}
			}
		});


		JScrollPane liste_scroll = new JScrollPane(table);
		liste_scroll.setAlignmentX(Component.LEFT_ALIGNMENT);
		liste_scroll.setMinimumSize(new Dimension(900, 600));
		liste_scroll.setPreferredSize(new Dimension(1000, 700));

		gbc.gridy = 1;
		contenu.add(liste_scroll, gbc);
		contenu.getRootPane().setDefaultButton(
				search.buttons.get("boutonChercher"));
		initTable();
		fenetre.draw(1024, 768);

	}

	private void initTable() {
		((DefaultTableModel) this.table.getModel())
				.setDataVector(
						taxonPersoDb.getListByParamToTable(this.getParam()),
						columnName);
	}

	public Object getValue(String value) {
		logger.debug("TaxonPerso.getValue " + value);
		return selectedId;
	}

	class Search extends ComposantAlisma {
		Dimension dimLabel = new Dimension(120, 25);

		public Search() {
			Dimension dimLabel = new Dimension(250, 25);
			addLabel("nomCodeTaxon", 0, 0, 2, dimLabel);
			setDimensionDefault(dimLabel);
			addTextField("taxon", 2, 0, 2);
			addButton("boutonChercher", 'R', "rechercher", 2, 2, 1);
			addButton("boutonModif", 'M', "modifier", 1, 2, 1);
			addButton("boutonNouveau", 'N', "nouveau", 0, 2, 1);
		}

		/**
		 * Renvoie les parametres de recherche saisis
		 * 
		 * @return Hashtable<String, String>
		 */
		public Hashtable<String, String> getParam() {
			return getData();
		}
	}

	public Hashtable<String, String> getParam() {
		return search.getData();
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		logger.debug("action demandée : " + arg1);
		switch ((String) arg1) {
		case "rechercher":
			initTable();
			break;
		case "modifier":
			selectedId = table.getId();
			setChanged();
			notifyObservers("taxonPersoChange");
			break;
		case "nouveau":
			selectedId = "";
			setChanged();
			notifyObservers("taxonPersoChange");
			break;
		}
	}

	public void dataRefresh() {
		initTable();
	}

	/**
	 * Table d'affichage de la liste
	 * 
	 * @author quinton
	 *
	 */
	@SuppressWarnings("serial")
	class TableData extends JTable {

		public TableData(DefaultTableModel defaultTableModel) {
			super(defaultTableModel);
			setAutoCreateRowSorter(true);
			setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			((DefaultTableModel) getModel()).setDataVector(new String[1][7],
					columnName);
			getTableHeader().setResizingAllowed(true);
			getColumnModel().getColumn(0).setPreferredWidth(70);
			getColumnModel().getColumn(1).setPreferredWidth(255);
			getColumnModel().getColumn(2).setPreferredWidth(215);
			getColumnModel().getColumn(3).setPreferredWidth(60);
			getColumnModel().getColumn(4).setPreferredWidth(90);
			getColumnModel().getColumn(5).setPreferredWidth(100);
			getColumnModel().getColumn(1).setCellRenderer(
					new ItalicCellRenderer());
			TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(
					this.getModel());
			setRowSorter(sorter);
			sorter.setComparator(4, new StringComparator());
			sorter.setComparator(5, new StringComparator());
			sorter.setComparator(6, new StringComparator());
		}

		/**
		 * Retourne l'identifiant selectionne
		 * 
		 * @return id
		 */
		public String getId() {
			String id = "";
			try {
				id = (String) getValueAt(getSelectedRow(), 0);
			} catch (ClassCastException e) {
				logger.debug(getValueAt(getSelectedRow(), 0));
			} catch (IndexOutOfBoundsException e1) {
			}
			logger.debug("id : " + id);
			return id;
		}

		public String getCdStation() {
			String cd = "";
			try {
				cd = (String) getValueAt(getSelectedRow(), 1);
			} catch (IndexOutOfBoundsException e1) {
			}
			return cd;
		}

		public boolean isCellEditable(int rowIndex, int colIndex) {
			return false;
		}

	}

	@SuppressWarnings("serial")
	public class ItalicCellRenderer extends DefaultTableCellRenderer {
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			super.getTableCellRendererComponent(table, value, isSelected,
					hasFocus, row, column);
			setFont(getFont().deriveFont(Font.ITALIC));
			return this;
		}
	}

	public class StringComparator implements Comparator<String> {

		@Override
		public int compare(String o1, String o2) {
			if (Float.parseFloat(o1) < Float.parseFloat(o2))
				return -1;
			if (Float.parseFloat(o1) > Float.parseFloat(o2))
				return 1;
			return 0;
		}
	}

	public class IntComparator implements Comparator<Integer> {

		public int compare(Integer o1, Integer o2) {
			if (o1 < o2)
				return -1;
			if (o1 > o2)
				return 1;
			return 0;
		}
	}

}
