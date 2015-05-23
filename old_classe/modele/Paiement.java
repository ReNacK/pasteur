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
public class Paiement {

	private String nature;
	private int numFact = -1;
	private String nomBenef;
	private boolean estFacture;
	private ArrayList<Montant> lmt;
	private float montantTotal;
	private Date datePaiement;
	private Date dateActe;
	private int delai;
	private int colonneNom;
	private int colonneNature;
	private int colonneNum;


	public Date getDateActe() {
		return dateActe;
	}

	public Paiement() {
		this.nature = "";
		this.numFact = 0;
		this.nomBenef = "";
		this.estFacture = false;
		this.montantTotal = 0;
		this.lmt = new ArrayList<>();
	}

	public Paiement(String nature, int numFact, String nomBenef,
			String prenomBenef, boolean estFacture, int montant) {
		super();
		this.nature = nature;
		this.numFact = numFact;
		this.nomBenef = nomBenef;
		this.estFacture = estFacture;
		this.montantTotal = montant;
	}

	/**
	 * @return the estFacture
	 */
	public boolean isEstFacture() {
		boolean temp = true;
		for (int i = 0 ; i < this.lmt.size(); i++){
			temp = temp && this.lmt.get(i).isEstPaye();
		}
		this.estFacture = temp;
		return this.estFacture;
	}

	/**
	 * @param estFacture the estFacture to set
	 */
	public void setEstFacture(boolean estFacture) {
		this.estFacture = estFacture;
		for (int i = 0 ; i < this.lmt.size() ; i++){
			this.lmt.get(i).setEstPaye(estFacture);
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
	 * @return the numFact
	 */
	public int getNumFact() {
		return numFact;
	}

	/**
	 * @param numFact the numFact to set
	 */
	public void setNumFact(int numFact) {
		this.numFact = numFact;
	}

	/**
	 * @return the nomBenef
	 */
	public String getNomBenef() {
		return nomBenef;
	}

	/**
	 * @param nomBenef the nomBenef to set
	 */
	public void setNomBenef(String nomBenef) {
		this.nomBenef = nomBenef;
	}

	public Date getDatePaiement() {
		return datePaiement;
	}

	public void setDatePaiement(Date date) {
		this.datePaiement = date;
	}

	public ArrayList<Montant> getLmt() {
		return lmt;
	}

	public void setLmt(ArrayList<Montant> lmt) {
		this.lmt = lmt;
	}

	public float getMontantTotal() {
		float temp = 0;
		for (int i = 0 ; i < this.lmt.size() ; i++){
			temp = temp + this.lmt.get(i).getMontant();
		}
		this.montantTotal = temp;
		return montantTotal;
	}

	public void setMontantTotal(float montantTotal) {
		this.montantTotal = montantTotal;
	}

	public void addMontant(Montant m){
		this.lmt.add(m);
	}

	public boolean containsMontant(Montant m){
		return this.lmt.contains(m);
	}

	public void setDateActe(Date d) {
		this.dateActe = d;
	}

	public int getDelai(){
		return (int) ((this.datePaiement.getTime() - this.dateActe.getTime()) / (24 * 60 * 60 * 1000));
	}

	public void setDelai(int d){
		this.delai = d;
	}

	public void changeNomClient(String string) {
		//la ligne à changer se trouve dans le premier montant enregistré pour cette facture
		String ligne = this.lmt.get(0).getLigne();
		String[] temp = ligne.split(";");
		temp[this.colonneNom] = string;
		this.nomBenef = string;
		//on change la ligne du montant
		StringBuilder sb = new StringBuilder();
		for(int i = 0 ; i < temp.length ; i++){
			sb.append(temp[i]+";");
		}
		this.lmt.get(0).setLigne(sb.toString());
	}


	public void changeNum(String string) {
		//la ligne à changer se trouve dans le premier montant enregistré pour cette facture
		String ligne = this.lmt.get(0).getLigne();
		String[] temp = ligne.split(";");
		temp[this.colonneNum] = string;
		this.numFact = Integer.parseInt(string);
		//on change la ligne du montant
		StringBuilder sb = new StringBuilder();
		for(int i = 0 ; i < temp.length ; i++){
			sb.append(temp[i]+";");
		}
		this.lmt.get(0).setLigne(sb.toString());
	}


	public void setColonneNom(int nomPatient) {
		this.colonneNom = nomPatient;
	}


	public void setColonneNature(int nature2) {
		this.colonneNature = nature2;
	}
	
	public void setColonneNum(int n){
		this.colonneNum = n;
	}

}
