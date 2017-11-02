/**
 * 
 */
package releve;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;
import java.util.Observable;

import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.InputVerifier;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import database.TaxonView;
import utils.Langue;
import utils.NumericVerifier;

/**
 * @author quinton
 * 
 *         Table des taxons releves
 */
public class ReleveListeTable extends Observable implements TableModelListener {

	Object obj;
	JTable table;
	int nbUR = 2;
	int[] columnsSize = new int[] { 80, 30, 250, 120, 70, 75, 55, 40, 40, 65,
			65, 0 };
	String[] columnsName = new String[] { Langue.getString("code"),
			Langue.getString("cf"), Langue.getString("nom"),
			Langue.getString("auteur"), Langue.getString("codeRef"),
			Langue.getString("cdContrib"), Langue.getString("groupe"),
			Langue.getString("csi"), Langue.getString("ei"),
			Langue.getString("rec1"), Langue.getString("rec2"), "" };
	ModeleReleveListe modele;
	List<String> ligne;
	TableCellRenderer rendererSuppr;
	JLabel jl_colsuppr;
	boolean enabled = true;
	TaxonView taxonViewDb = new TaxonView();

	class JComponentTableCellRenderer implements TableCellRenderer {
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			return (JComponent) value;
		}
	}

	/**
	 * Initialisation
	 */
	public ReleveListeTable() {
		obj = this;
		init();
	}

	public ReleveListeTable(int pNbUR) {
		obj = this;
		nbUR = pNbUR;
		init();
	}

	/**
	 * Appel des parents, pour leur signifier un changement
	 */
	public void setAction() {
		setChanged();
		notifyObservers("change");
	}

	/**
	 * Script d'initialisation de la classe
	 */
	public void init() {
		modele = new ModeleReleveListe();
		table = new JTable(modele) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			// Implement table header tool tips.
			protected JTableHeader createDefaultTableHeader() {
				return new JTableHeader(columnModel) {
					private static final long serialVersionUID = 1L;

					public String getToolTipText(MouseEvent e) {
						java.awt.Point p = e.getPoint();
						int index = columnModel.getColumnIndexAtX(p.x);
						int realIndex = columnModel.getColumn(index)
								.getModelIndex();
						return columnsName[realIndex];
					}

				};
			}
		};
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setAutoCreateRowSorter(true);

		/*
		 * Definition du tri des colonnes
		 */
		/*
		 * Comparateur de type chaine
		 */
		Comparator<String> stringComparator = new Comparator<String>() {

			@Override
			public int compare(String arg0, String arg1) {
				return arg0.compareTo(arg1);
			}
		};

		/*
		 * Comparateur de type numérique
		 */
		Comparator<String> numericComparator = new Comparator<String>() {
			@Override
			public int compare(String arg0, String arg1) {
				int retour = 0;
				if (arg0.isEmpty() && !arg1.isEmpty()) {
					retour = 1;
				} else if (arg1.isEmpty() && !arg0.isEmpty()) {
					retour = -1;
				} else if (!arg1.isEmpty() && !arg0.isEmpty()) {
					Double num0 = Double.parseDouble(arg0);
					Double num1 = Double.parseDouble(arg1);
					if (num1 > num0) {
						retour = 1;
					} else if (num0 > num1) {
						retour = -1;
					}
				}
				return retour;
			}
		};

		/*
		 * Définition du comparateur à utiliser
		 */
		table.setAutoCreateRowSorter(true);
		TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(
				table.getModel());
		table.setRowSorter(sorter);
		for (int i = 0; i < 11; i++) {
			switch (i) {
			case 0:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
				sorter.setComparator(i, stringComparator);
				break;
			case 7:
			case 8:
			case 9:
			case 10:
				sorter.setComparator(i, numericComparator);
				break;
			}
		}

		/*
		 * Definition des largeurs des colonnes
		 */
		TableColumn colonne = null;
		int i;
		for (i = 0; i < 11; i++) {
			colonne = table.getColumnModel().getColumn(i);
			colonne.setPreferredWidth(columnsSize[i]);
		}
		table.setFillsViewportHeight(true);
		/*
		 * Definition du nombre de lignes affichees par defaut
		 */
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setAutoscrolls(true);

		/*
		 * Definition des zones editables
		 */
		colonne = table.getColumnModel().getColumn(1);
		JCheckBox jcb_confer = new JCheckBox();
		colonne.setCellEditor(new DefaultCellEditor(jcb_confer));

		NumericVerifier nv = new NumericVerifier();
		nv.isDecimal = true;
		nv.isPourcentage = true;
		for (i = 9; i < 11; i++) {
			colonne = table.getColumnModel().getColumn(i);
			/*
			 * JTextField texte = new JTextField(); texte.setInputVerifier(nv);
			 * texte.setToolTipText(Langue.getString("pourcentageDecimal"));
			 */
			colonne.setCellEditor(new CellEditor(nv));
		}

		/*
		 * Suppression de la colonne de l'UR2
		 */
		if (nbUR == 1) {
			colonne = table.getColumnModel().getColumn(10);
			colonne.setPreferredWidth(0);
		}
		/*
		 * Rajout d'une icone dans la colonne de suppression
		 */
		colonne = table.getColumnModel().getColumn(11);
		ImageIcon icone = new ImageIcon(getClass().getClassLoader()
				.getResource("ressources/delete.png"));
		jl_colsuppr = new JLabel(icone);
		jl_colsuppr.setToolTipText(Langue.getString("supprimer"));
		rendererSuppr = new JComponentTableCellRenderer();
		colonne.setHeaderRenderer(rendererSuppr);
		colonne.setCellRenderer(rendererSuppr);
		colonne.setHeaderValue(jl_colsuppr);
		colonne.setPreferredWidth(columnsSize[11]);

		/*
		 * Ajout d'un evenement pour gerer la suppression de la ligne
		 */
		table.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					if (table.columnAtPoint(e.getPoint()) == 11 && enabled) {
						/*
						 * Message de confirmation
						 */
						int rep = JOptionPane.showConfirmDialog(null, Langue
								.getString("warningDeleteLigneTaxon"), Langue
								.getString("supprimer"),
								JOptionPane.YES_NO_OPTION,
								JOptionPane.QUESTION_MESSAGE,
								new ImageIcon(getClass().getClassLoader()
										.getResource("ressources/warning.png")));
						if (rep == JOptionPane.YES_OPTION) {
							/*
							 * Lancement de la suppression de la ligne
							 */
							int row = table.convertRowIndexToModel(table
									.getSelectedRow());
							modele.deleteRow(row);
							setAction();
						}
					}
				}
			}
		});
	}

	public JTable getTable() {
		return table;
	}

	/**
	 * Retourne l'ensemble des donnees du tableau
	 * 
	 * @return List<Hashtable<String, String>> data
	 */
	public List<Hashtable<String, String>> getListData() {
		return modele.getDataAsHashtable();
	}

	/**
	 * Classe Modele pour la table des taxons releves
	 * 
	 * @author quinton
	 * 
	 */
	class ModeleReleveListe extends AbstractTableModel {
		private static final long serialVersionUID = 1L;

		/*
		 * Stockage des donnees
		 */
		List<Object> data = new ArrayList<Object>();

		public String getColumnName(int c) {
			return columnsName[c];
		}

		@Override
		public int getColumnCount() {
			return columnsName.length;
		}

		@Override
		public int getRowCount() {
			return data.size();
		}

		@SuppressWarnings("unchecked")
		@Override
		/**
		 * Retourne la valeur de la cellule selectionnee
		 */
		public Object getValueAt(int rowIndex, int columnIndex) {
			ligne = (List<String>) data.get(rowIndex);
			switch (columnIndex) {
			case 1:
				/*
				 * Traitement de la zone cf
				 */
				if (ligne.get(columnIndex) == "1") {
					return true;
				} else {
					return false;
				}
			case 11:
				/*
				 * Traitement de l'icone de suppression de la ligne
				 */
				return jl_colsuppr;
			default:
				/*
				 * Retourne la valeur de la colonne
				 */
				return ligne.get(columnIndex);
			}
		}

		@SuppressWarnings("unchecked")
		public void setValueAt(Object value, int row, int col) {
			if (row > data.size()) {
				ligne = new ArrayList<String>();
				data.add(ligne);
			} else {
				ligne = (List<String>) data.get(row);
			}
			if (col != 1) {
				ligne.set(col, (String) value);
			} else {
				if ((Boolean) value == true) {
					ligne.set(1, "1");
				} else
					ligne.set(1, "0");
			}

			fireTableCellUpdated(row, col);
			((ReleveListeTable) obj).setAction();
		}

		public Class<? extends Object> getColumnClass(int c) {
			int nbColonne = 11;
			if (nbUR == 1)
				nbColonne = 10;
			if (c < nbColonne)
				return getValueAt(0, c).getClass();
			return Boolean.TYPE;
		}

		/**
		 * Indique les cellules modifiables
		 */
		public boolean isCellEditable(int row, int col) {
			if (col == 9 || /* col == 1 || */(col == 10 && nbUR == 2)) {
				return true;
			} else {
				return false;
			}
		}

		/**
		 * Rajout technique d'une ligne
		 * 
		 * @param pLigne
		 */
		public void addRow(List<String> pLigne) {
			data.add(pLigne);
			fireTableDataChanged();
		}

		/**
		 * Supprime une ligne dans le tableau
		 * 
		 * @param i
		 */
		public void deleteRow(int i) {
			data.remove(i);
			fireTableRowsDeleted(i, i);
		}

		/**
		 * fonction retournant les donnees dans un objet list<Hashtable<String,
		 * String>>
		 * 
		 * @return ArrayList<Hashtable<String, String>>
		 */
		@SuppressWarnings("unchecked")
		public List<Hashtable<String, String>> getDataAsHashtable() {
			List<Hashtable<String, String>> liste = new ArrayList<Hashtable<String, String>>();
			Hashtable<String, String> ligne;
			List<String> lData;
			/*
			 * Traitement de chaque ligne
			 */
			for (Object item : modele.data) {
				lData = (ArrayList<String>) item;
				ligne = new Hashtable<String, String>();
				ligne.put("id_taxon", lData.get(0));
				ligne.put("cf", lData.get(1));
				ligne.put("nom_taxon", lData.get(2));
				ligne.put("auteur", lData.get(3));
				ligne.put("cd_valide", lData.get(4));
				ligne.put("cd_contrib", lData.get(5));
				ligne.put("nom_groupe", lData.get(6));
				ligne.put("cote_spe", lData.get(7));
				ligne.put("coef_steno", lData.get(8));
				ligne.put("pc_ur1", lData.get(9));
				if (nbUR == 2) {
					ligne.put("pc_ur2", lData.get(10));
				} else
					ligne.put("pc_ur2", "");
				ligne.put("id_ligne_op_controle", lData.get(11));
				/*
				 * Rajoute la ligne a la liste
				 */
				liste.add(ligne);
			}
			return liste;
		}
	}

	@Override
	public void tableChanged(TableModelEvent arg0) {
	}

	/**
	 * Ajout d'une nouvelle ligne dans le tableau
	 * 
	 * @param lData
	 */
	@SuppressWarnings("unchecked")
	public void addLigne(Hashtable<String, String> lData) {
		/*
		 * Verification de la presence du code taxon, et recuperation des
		 * informations associees
		 */
		if (!lData.get("id_taxon").isEmpty()) {
			/*
			 * Recherche si le taxon n'a pas ete deja saisi
			 */
			boolean error = false;
			for (Object item : modele.data) {
				ligne = (ArrayList<String>) item;
				if (ligne.get(0).equals(lData.get("id_taxon"))
						&& ligne.get(1).equals(lData.get("cf")))
					error = true;
			}
			if (error) {
				/*
				 * On a un doublon : l'insertion est impossible
				 */
				JFrame frame = new JFrame();
				JOptionPane.showMessageDialog(frame,
						Langue.getString("doublonTaxon"),
						Langue.getString("titreDoublonTaxon"),
						JOptionPane.ERROR_MESSAGE);
			} else {
				/*
				 * C'est un nouveau taxon, on continue
				 */
				/*
				 * Recuperation du nom du taxon
				 */
				Hashtable<String, String> data = taxonViewDb.getTaxon(lData
						.get("id_taxon"));

				if (!data.isEmpty()) {
					/*
					 * Rajout de la ligne dans le tableau
					 */
					ligne = new ArrayList<String>();
					ligne.add(data.get("cd_taxon"));
					ligne.add(lData.get("cf"));
					ligne.add(data.get("nom_taxon"));
					ligne.add((data.get("auteur") != null ? data.get("auteur")
							: (data.get("auteurp") != null ? data
									.get("auteurp") : "")));
					ligne.add((data.get("cd_valide") != null ? data
							.get("cd_valide") : ""));
					ligne.add((data.get("cd_contrib") != null ? data
							.get("cd_contrib") : ""));
					ligne.add((data.get("nom_groupe") != null ? data
							.get("nom_groupe") : ""));
					/*
					 * Recuperation des coefs soit dans le taxon, soit dans le
					 * taxon valide
					 */
					if (data.get("cote_spe") != null) {
						ligne.add(data.get("cote_spe"));
						ligne.add(data.get("coef_steno"));
					} else {
						/*
						 * Recuperation des donnees du taxon valide
						 */
						ligne.add((data.get("cote_spe_valide") != null ? data
								.get("cote_spe_valide") : ""));
						ligne.add((data.get("coef_steno_valide") != null ? data
								.get("coef_steno_valide") : ""));
					}
					ligne.add(lData.get("pc_ur1"));
					ligne.add(lData.get("pc_ur2"));
					ligne.add(lData.get("id_ligne_op_controle"));
					modele.addRow(ligne);
				}
			}
		}
	}
	/**
	 * Reinitialise la table des especes
	 */
	public void resetTableData() {
		for(int i=0; i < modele.getRowCount();i++)
			modele.deleteRow(i);
		ligne.clear();
		modele.data.clear();
	}

	/**
	 * Remplace la valeur de la cle de l'enregistrement
	 * 
	 * @param int numLigne
	 * @param int key
	 */
	@SuppressWarnings("unchecked")
	public void setTaxonKey(int numLigne, int key) {
		ligne = (List<String>) modele.data.get(numLigne);
		ligne.set(11, String.valueOf(key));
	}

	/**
	 * Classe utilisee pour les tris dans le tableau - String
	 * 
	 * @author quinton
	 * 
	 */
	/*
	 * public class StringComparator implements Comparator<String> { public int
	 * compare(String o1, String o2) { if (o1 == "") o1 = "0"; if (o2 == "") o2
	 * = "0"; if (Integer.parseInt(o1) < Integer.parseInt(o2)) return -1; if
	 * (Integer.parseInt(o1) > Integer.parseInt(o2)) return 1; return 0; } }
	 */
	/**
	 * Classe utilisee pour les tris dans le tableau - int
	 * 
	 * @author quinton
	 * 
	 */
	/*
	 * public class IntComparator implements Comparator<Integer> { public int
	 * compare(Integer o1, Integer o2) { if (o1 < o2) return -1; if (o1 > o2)
	 * return 1; return 0; } }
	 */
	@SuppressWarnings("serial")
	private class CellEditor extends DefaultCellEditor {

		InputVerifier verifier = null;

		public CellEditor(InputVerifier verifier) {
			super(new JTextField());
			this.verifier = verifier;

		}

		@Override
		public boolean stopCellEditing() {
			return verifier.verify(editorComponent) && super.stopCellEditing();
		}

	}

	public void setEnabled(boolean enabled) {
		table.setEnabled(enabled);
		this.enabled = enabled;
	}

}
