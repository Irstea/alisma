package utils;

public class Ligne_Id_Nom {
	
	private String id,nom;

	public Ligne_Id_Nom(String id, String nom){
		this.id = id;
		this.nom = nom;
	}
	
	public String getId(){ return id; }
	public String getNom(){ return nom; }
	public void setNom(String n){ nom = n; }
	public String toString(){ return nom; }
}