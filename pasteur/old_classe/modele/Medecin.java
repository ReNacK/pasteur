/**
 * 
 */
package modele;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * @author Kevin
 *
 */
public class Medecin {

	private String nom;
	private String prenom;
	private int num;
	private int charge;
	private ListeFacture lf;
	private ListePaiement lp;
	public ArrayList<Facture> listeImpayesTotale;
	public HashMap<Paiement, ArrayList<Integer>> listeFacturesPossibles;
	private String ligne;
	private int numLigne;
	
	public ArrayList<Facture> getListeImpayesTotale() {
		return listeImpayesTotale;
	}

	public void setListeImpayesTotale(ArrayList<Facture> listeImpayes) {
		this.listeImpayesTotale = listeImpayes;
	}

	public HashMap<Paiement, ArrayList<Integer>> getListeFacturesPossibles() {
		return listeFacturesPossibles;
	}

	public void setListeFacturesPossibles(HashMap<Paiement, ArrayList<Integer>> listeFacturesPossibles) {
		this.listeFacturesPossibles = listeFacturesPossibles;
	}

	public Medecin(){
		this.lf = new ListeFacture();
		this.lp = new ListePaiement();
		this.listeFacturesPossibles = new HashMap<Paiement, ArrayList<Integer>>();
		this.listeImpayesTotale = new ArrayList<Facture>();
	}
	
	public void addFacture(Facture f){
		this.lf.add(f);
	}
	
	public void addPaiement(Paiement p) {
		this.lp.add(p);
	}
	
	/**
	 * @return the nom
	 */
	public String getNom() {
		return nom;
	}
	/**
	 * @param nom the nom to set
	 */
	public void setNom(String nom) {
		this.nom = nom;
	}
	/**
	 * @return the prenom
	 */
	public String getPrenom() {
		return prenom;
	}
	/**
	 * @param prenom the prenom to set
	 */
	public void setPrenom(String prenom) {
		this.prenom = prenom;
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
	 * @return the charge
	 */
	public int getCharge() {
		return charge;
	}
	/**
	 * @param charge the charge to set
	 */
	public void setCharge(int charge) {
		this.charge = charge;
	}

	/**
	 * @return the lf
	 */
	public ListeFacture getLf() {
		return lf;
	}
	/**
	 * @param lf the lf to set
	 */
	public void setLf(ListeFacture lf) {
		this.lf = lf;
	}
	/**
	 * @return the lp
	 */
	public ListePaiement getLp() {
		return lp;
	}
	/**
	 * @param lp the lp to set
	 */
	public void setLp(ListePaiement lp) {
		this.lp = lp;
	}
	
	public int nbFacturesParMois(int mois, int annee){
		int m = mois;
		int nb = 0;
		Date dateEnCours = null;
		for(int i = 0 ; i < this.lf.size() ; i ++){
			dateEnCours = this.lf.getLf().get(i).getDateFacturation();
			if(dateEnCours.getMonth()+1 == m && dateEnCours.getYear()+1900 == annee){
				nb++;
			}
		}
		return nb;
	}

	public Facture getFacture(String string) {
		Facture fact = null;
		for (int i = 0 ; i < this.lf.size() ; i++){
			if(this.lf.get(i).getNum() == Integer.parseInt(string)){
				fact = this.lf.get(i);
			}
		}
		return fact;
	}

	public boolean containsFacture(String string) {
		boolean trouve = false;
		int i = 0;
		while(!trouve && i < this.lf.size() && !string.isEmpty()){
			trouve = this.lf.get(i).getNum() == Integer.parseInt(string);
			i++;	
		}
		return trouve;
	}
	
	public Paiement getPaiement(String string) {
		Paiement p = null;
		for (int i = 0 ; i < this.lp.size() ; i++){
			if(this.lp.get(i).getNumFact() == Integer.parseInt(string)){
				p = this.lp.get(i);
			}
		}
		return p;
	}

	public boolean containsPaiement(String string) {
		boolean trouve = false;
		int i = 0;
		while(!trouve && i < this.lp.size()){
			trouve = this.lp.get(i).getNumFact() == Integer.parseInt(string);
			i++;
		}
		return trouve;
	}

	public void addImpaye(Facture factureEnCours) {
		this.listeImpayesTotale.add(factureEnCours);
	}

	public String getLigne() {
		return ligne;
	}

	public void setLigne(String ligne2) {
		this.ligne = ligne2;
	}

	public int getNumLigne() {
		return numLigne;
	}

	public void setNumLigne(int numLigne) {
		this.numLigne = numLigne;
	}

	public Number nbFacturesImapyesParMois(int moisEnCours, int anneeEnCours) {
		int m = moisEnCours;
		int nb = 0;
		Date dateEnCours = null;
		for(int i = 0 ; i < this.listeImpayesTotale.size() ; i ++){
			dateEnCours = this.listeImpayesTotale.get(i).getDateFacturation();
			if(dateEnCours.getMonth()+1 == m && dateEnCours.getYear()+1900 == anneeEnCours){
				nb++;
			}
		}
		return nb;
	}
	
}
