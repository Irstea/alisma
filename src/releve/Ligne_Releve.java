package releve;

//mod�le de donn�es correspondant � une ligne dans la JTable du releve floristique
public class Ligne_Releve {
	private int     id;
	private String  code;
	private float   pc_ur1;
	private float   pc_ur2;
	private String  ref;
	private String  contrib;
	private String  groupe;
	private String  auteur;
	private String  Csi;
	private String  Ei;
	private String  nom;
	private String cf;

	public Ligne_Releve(int id, String code, float pc_ur1, float pc_ur2,String ref, String contrib, String groupe, String csi, String ei, String nom, String auteur, String cf) {
		this.id = id;
		this.code = code;
		this.pc_ur1 = pc_ur1;
		this.pc_ur2 = pc_ur2;
		this.ref = ref;
		this.contrib = contrib;
		this.groupe = groupe;
		Csi = csi;
		Ei = ei;
		this.nom = nom;
		this.auteur = auteur;
		this.cf = cf;
	}

	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getCode() {
		return code;
	}
	
	public void setCode(String code) {
		this.code = code;
	}
	
	public float getpc_ur1() {
		return pc_ur1;
	}
	
	public void setpc_ur1(float pc_ur1) {
		this.pc_ur1 = pc_ur1;
	}
	
	public float getpc_ur2() {
		return pc_ur2;
	}
	
	public void setpc_ur2(float pc_ur2) {
		this.pc_ur2 = pc_ur2;
	}
	
	public String getRef() {
		return ref;
	}
	
	public void setRef(String ref) {
		this.ref = ref;
	}
	
	public String getContrib() {
		return contrib;
	}
	
	public void setContrib(String contrib) {
		this.contrib = contrib;
	}
	
	public String getGroupe() {
		return groupe;
	}
	
	public void setGroupe(String groupe) {
		this.groupe = groupe;
	}
	
	public String getCsi() {
		return Csi;
	}
	
	public void setCsi(String csi) {
		Csi = csi;
	}
	
	public String getEi() {
		return Ei;
	}
	
	public void setEi(String ei) {
		Ei = ei;
	}
	
	public String getNom() {
		return nom;
	}
	
	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getAuteur() {
		return auteur;
	}

	public void setAuteur(String auteur) {
		this.auteur = auteur;
	}

	public String isCf() {
		return cf;
	}

	public void setCf(String cf) {
		this.cf = cf;
	}
}
