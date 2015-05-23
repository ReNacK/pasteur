package modele;

import java.text.DecimalFormat;
import java.util.Date;

public class Montant {
	private float montant;
	private boolean estPaye;
	private int numLigne;
	private Date dateActe;
	private String nature;
	private Facture fact;
	private Paiement paiement;
	private String ligne;
	private int colonneMontant;
	private int colonneNature;
	private int colonneCaisse;
	
	public Paiement getPaiement() {
		return paiement;
	}

	public void setPaiement(Paiement paiement) {
		this.paiement = paiement;
	}

	public Facture getFact() {
		return fact;
	}

	public void setFact(Facture fact) {
		this.fact = fact;
	}

	public String getNature() {
		return nature;
	}

	public void setNature(String nature) {
		this.nature = nature;
	}

	public Montant(float m, boolean b, int l){
		DecimalFormat df = new DecimalFormat("########.00");
		String str = df.format(m);
		this.montant = Float.parseFloat(str.replace(',', '.'));
		this.numLigne = l;
		this.estPaye = b;
	}

	public float getMontant() {
		return this.montant;
	}

	public void setMontant(float montant) {
		this.montant = montant;
	}

	public boolean isEstPaye() {
		return estPaye;
	}

	public void setEstPaye(boolean estPaye) {
		this.estPaye = estPaye;
	}

	public int getNumLigne() {
		return this.numLigne;
	}

	public void setNumLigne(int numLigne) {
		this.numLigne = numLigne;
	}
	
	public Date getDateActe() {
		return dateActe;
	}

	public void setDateActe(Date dateActe) {
		this.dateActe = dateActe;
	}

	public String getLigne() {
		return this.ligne;
	}
	
	public void setLigne(String l){
		this.ligne = l;
	}

	public void setColonneNature(int acte) {
		this.colonneNature = acte;
	}

	public void setColonneMontant(int caisse, int mutuelle) {
		if(mutuelle == -1){
			this.colonneMontant = mutuelle;
		}
		this.colonneCaisse = caisse;
	}

	public void changeMontant(float num1) {
		//la ligne à changer se trouve dans le premier montant enregistré pour cette facture
		String ligne = this.getLigne();
		String[] temp = ligne.split(";");
		temp[this.colonneMontant] = num1+"";
		this.montant = Float.parseFloat(temp[this.colonneMontant]);
		//on change la ligne du montant
		StringBuilder sb = new StringBuilder();
		for(int i = 0 ; i < temp.length ; i++){
			sb.append(temp[i]+";");
		}
		this.setLigne(sb.toString());
	}

	public void changeNature(String string) {
		//la ligne à changer se trouve dans le premier montant enregistré pour cette facture
		String ligne = this.getLigne();
		String[] temp = ligne.split(";");
		temp[this.colonneNature] = string;
		this.nature = temp[this.colonneNature];
		//on change la ligne du montant
		StringBuilder sb = new StringBuilder();
		for(int i = 0 ; i < temp.length ; i++){
			sb.append(temp[i]+";");
		}
		this.setLigne(sb.toString());
	}

}
