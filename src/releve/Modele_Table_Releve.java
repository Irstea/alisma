package releve;

import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.table.AbstractTableModel;

import utils.Langue;


//modele de la JTable correspondant au releve floristique
public class Modele_Table_Releve extends AbstractTableModel{

	private static final long serialVersionUID = 1L;
	private int nbUR;
	private int id = 1;

	private ArrayList<Ligne_Releve> lignes = new ArrayList<Ligne_Releve>();
	private ArrayList<String> colonnes = new ArrayList<String>();

	public Modele_Table_Releve(int nbUR){
		this.nbUR = nbUR;
		
		colonnes.add(Langue.getString("num"));
		colonnes.add(Langue.getString("code"));
		colonnes.add(Langue.getString("cf"));
		colonnes.add(Langue.getString("nom"));
		colonnes.add(Langue.getString("auteur"));
		colonnes.add(Langue.getString("rec1"));
		if(nbUR == 2)
			colonnes.add(Langue.getString("rec2"));
		colonnes.add(Langue.getString("codeRef"));
		colonnes.add(Langue.getString("cdContrib"));
		colonnes.add(Langue.getString("groupe"));
		colonnes.add(Langue.getString("csi"));
		colonnes.add(Langue.getString("ei"));		
		colonnes.add("");
	}

	public int getColumnCount() {
		return colonnes.size();
	}	

	public int getRowCount() {
		return lignes.size();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		if(nbUR == 2)
			switch(columnIndex){
			case 0:
				return lignes.get(rowIndex).getId();
			case 1:
				return lignes.get(rowIndex).getCode();
			case 2:
				return lignes.get(rowIndex).isCf();
			case 3:
				return lignes.get(rowIndex).getNom();
			case 4:
				return lignes.get(rowIndex).getAuteur();
			case 5:
				return lignes.get(rowIndex).getpc_ur1();
			case 6:
				return lignes.get(rowIndex).getpc_ur2();
			case 7:
				return lignes.get(rowIndex).getRef();
			case 8:
				return lignes.get(rowIndex).getContrib();
			case 9:
				return lignes.get(rowIndex).getGroupe();
			case 10:
				return lignes.get(rowIndex).getCsi();
			case 11:
				return lignes.get(rowIndex).getEi();
			default:
				return null;
			}
		else
			switch(columnIndex){
			case 0:
				return lignes.get(rowIndex).getId();
			case 1:
				return lignes.get(rowIndex).getCode();
			case 2:
				return lignes.get(rowIndex).isCf();
			case 3:
				return lignes.get(rowIndex).getNom();
			case 4:
				return lignes.get(rowIndex).getAuteur();
			case 5:
				return lignes.get(rowIndex).getpc_ur1();
			case 6:
				return lignes.get(rowIndex).getRef();
			case 7:
				return lignes.get(rowIndex).getContrib();
			case 8:
				return lignes.get(rowIndex).getGroupe();
			case 9:
				return lignes.get(rowIndex).getCsi();
			case 10:
				return lignes.get(rowIndex).getEi();
			default:
				return null;
			}
	}

	public String getColumnName(int columnIndex) {
		return colonnes.get(columnIndex);
	}

	public void addLigne(Ligne_Releve ligne) {
		lignes.add(ligne);
		id++;
		fireTableRowsInserted(lignes.size() -1, lignes.size() -1);
	}

	public void removeLigne(int rowIndex) {
		lignes.remove(rowIndex);
		id--;
		fireTableRowsDeleted(rowIndex, rowIndex);
	}

	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if(nbUR == 2 && (columnIndex == 5 || columnIndex == 6))
			return true;
		else if (nbUR == 1 && columnIndex == 5)
			return true;
		else
			return false;
	}

	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		Ligne_Releve ligne = lignes.get(rowIndex);
		//Edition des pourcentages de recouvrement
		if(aValue != null && aValue.toString().matches("([1-9]?[0-9]([\\.,][0-9]*)?)|100")){
			switch(columnIndex){
			case 0:
				ligne.setId((Integer)aValue);
				break;
			case 5:
				ligne.setpc_ur1(Float.parseFloat((String)aValue));
				break;
			case 6:
				if(nbUR == 2)
					ligne.setpc_ur2(Float.parseFloat((String)aValue));
				break;
			}
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Class getColumnClass(int columnIndex){
		switch(columnIndex){
			case 11:
				if(nbUR == 1)
					return ImageIcon.class;
				else
					return Object.class;
			case 12:
				if(nbUR == 2)
					return ImageIcon.class;
				else
					return Object.class;
			default:
				return Object.class;
		}
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
