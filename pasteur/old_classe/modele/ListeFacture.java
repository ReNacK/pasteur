/**
 * 
 */
package modele;

import java.util.ArrayList;

/**
 * @author Kevin
 *
 */
public class ListeFacture {
	
	private ArrayList<Facture> lf;
	
	public ListeFacture(){
		this.lf = new ArrayList<>();
	}

	/**
	 * @return the lf
	 */
	public ArrayList<Facture> getLf() {
		return lf;
	}

	/**
	 * @param lf the lf to set
	 */
	public void setLf(ArrayList<Facture> lf) {
		this.lf = lf;
	}
	
	public void add(Facture fact){
		this.lf.add(fact);
	}
	
	public void remove(Facture fact){
		this.lf.remove(fact);
	}
	
	public boolean contains(Facture fact){
		return this.lf.contains(fact);
	}
	
	public int size(){
		return this.lf.size();
	}
	
	public Facture get(int i){
		return this.lf.get(i);
	}

}
