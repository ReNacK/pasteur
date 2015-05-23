/**
 * 
 */
package modele;

import java.util.ArrayList;
import java.util.Date;

/**
 * @author Kevin
 *
 */
public class Facture {
	private int num;
	private boolean estPaye;
	private String nature;
	private String nomClient;
	private String prenomClient;
	private Date dateFacturation;
	private ArrayList<Montant> lmt;
	private float montantTotal;
	private Date dateActe;
	private int colonneNum;
	private int colonneNature;
	private int colonneNomClient;
	private int colonnePrenomClient;
	private int delai;
	
	
	public ArrayList<Montant> getLmt() {
		return lmt;
	}

	public void setLmt(ArrayList<Montant> lmt) {
		this.lmt = lmt;
	}

	public Date getDateActe() {
		return dateActe;
	}

	public Facture() {
		this.num = 0;
		this.lmt = new ArrayList<>();
		this.estPaye = false;
		this.nature = "";
		this.nomClient = "";
		this.montantTotal = 0;
	}

	public float getMontantTotal() {
		float temp = 0;
		for (int i = 0 ; i < this.lmt.size() ; i++){
			temp = temp + this.lmt.get(i).getMontant();
		}
		this.montantTotal = temp;
		return montantTotal;
	}

	public void setMontantTotal(float f) {
		this.montantTotal = f;
	}

	public Facture(int num, float montant, boolean estPaye,
			String nature, String nomClient) {
		super();
		this.num = num;
		this.lmt = new ArrayList<>();
		this.estPaye = estPaye;
		this.nature = nature;
		this.nomClient = nomClient;
	}

	/**
	 * @return the num
	 */
	public int getNum() {
		return num;
	}
	
	/**
	 * @param num the num to set
	 */
	public void setNum(int num) {
		this.num = num;
	}
	
	/**
	 * @return the estPaye
	 */
	public boolean isEstPaye() {
		boolean temp = true;
		for (int i = 0 ; i < this.lmt.size(); i++){
			temp = temp && this.lmt.get(i).isEstPaye();
		}
		this.estPaye = temp;
		return this.estPaye;
	}
	
	/**
	 * @param estPaye the estPaye to set
	 */
	public void setEstPaye(boolean estPaye) {
		this.estPaye = estPaye;
		for (int i = 0 ; i < this.lmt.size() ; i++){
			this.lmt.get(i).setEstPaye(estPaye);
		}
	}
	
	/**
	 * @return the nature
	 */
	public String getNature() {
		return nature;
	}
	
	/**
	 * @param nature the nature to set
	 */
	public void setNature(String nature) {
		this.nature = nature;
	}

	/**
	 * @return the nomClient
	 */
	public String getNomClient() {
		return nomClient;
	}
	
	/**
	 * @param nomClient the nomClient to set
	 */
	public void setNomClient(String nomClient) {
		this.nomClient = nomClient;
	}
	
	/**
	 * @return the dateFacturation
	 */
	public Date getDateFacturation() {
		return dateFacturation;
	}

	/**
	 * @param dateFacturation the dateFacturation to set
	 */
	public void setDateFacturation(Date dateFacturation) {
		this.dateFacturation = dateFacturation;
	}
	
	public void addMontant(Montant m){
		this.lmt.add(m);
	}


	public void setDateActe(Date d1) {
		this.dateActe = d1;
	}

	public String getPrenomClient() {
		return prenomClient;
	}

	public void setPrenomClient(String prenomClient) {
		this.prenomClient = prenomClient;
	}


	public void setColonneNomClient(int nomPatient, int prenomPatient) {
		this.colonneNomClient = nomPatient;
		this.colonnePrenomClient = prenomPatient;
	}

	public void setColonneNature(int acte) {
		this.colonneNature = acte;
	}

	public void setColonneNum(int noFacture) {
		this.colonneNum = noFacture;
	}
	
	public int getColonneNomClient() {
		return colonneNomClient;
	}

	public void setColonneNomClient(int colonneNomClient) {
		this.colonneNomClient = colonneNomClient;
	}

	public int getColonnePrenomClient() {
		return colonnePrenomClient;
	}

	public void setColonnePrenomClient(int colonnePrenomClient) {
		this.colonnePrenomClient = colonnePrenomClient;
	}

	public int getColonneNum() {
		return colonneNum;
	}

	public int getColonneNature() {
		return colonneNature;
	}
	
	public int getDelai(){
		return (int) ((this.dateFacturation.getTime() - this.dateActe.getTime()) / (24 * 60 * 60 * 1000));
	}
	
	public void setDelai(int d){
		this.delai = d;
	}

	public void changeNum(int num2) {
		//la ligne à changer se trouve dans le premier montant enregistré pour cette facture
		String ligne = this.lmt.get(0).getLigne();
		String[] temp = ligne.split(";");
		temp[this.colonneNum] = num2+"";
		this.num = Integer.parseInt(temp[this.colonneNum]);
		//on change la ligne du montant
		StringBuilder sb = new StringBuilder();
		for(int i = 0 ; i < temp.length ; i++){
			sb.append(temp[i]+";");
		}
		this.lmt.get(0).setLigne(sb.toString());
	}

	public void changeNature(String string) {
		//la ligne à changer se trouve dans le premier montant enregistré pour cette facture
		String ligne = this.lmt.get(0).getLigne();
		String[] temp = ligne.split(";");
		temp[this.colonneNature] = string;
		this.nature = temp[this.colonneNature];
		//on change la ligne du montant
		StringBuilder sb = new StringBuilder();
		for(int i = 0 ; i < temp.length ; i++){
			sb.append(temp[i]+";");
		}
		this.lmt.get(0).setLigne(sb.toString());
	}

	public void changePrenomClient(String string) {
		//la ligne à changer se trouve dans le premier montant enregistré pour cette facture
				String ligne = this.lmt.get(0).getLigne();
				String[] temp = ligne.split(";");
				temp[this.colonnePrenomClient] = string;
				this.prenomClient = temp[this.colonnePrenomClient];
				//on change la ligne du montant
				StringBuilder sb = new StringBuilder();
				for(int i = 0 ; i < temp.length ; i++){
					sb.append(temp[i]+";");
				}
				this.lmt.get(0).setLigne(sb.toString());
	}

	public void changeNomClient(String string) {
		//la ligne à changer se trouve dans le premier montant enregistré pour cette facture
		String ligne = this.lmt.get(0).getLigne();
		String[] temp = ligne.split(";");
		temp[this.colonneNomClient] = string;
		this.nomClient = temp[this.colonneNomClient];
		//on change la ligne du montant
		StringBuilder sb = new StringBuilder();
		for(int i = 0 ; i < temp.length ; i++){
			sb.append(temp[i]+";");
		}
		this.lmt.get(0).setLigne(sb.toString());
	}
	
}
