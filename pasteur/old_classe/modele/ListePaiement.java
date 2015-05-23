/**
 * 
 */
package modele;

import java.util.ArrayList;

/**
 * @author Kevin
 *
 */
public class ListePaiement {
	
	private ArrayList<Paiement> lp;
	
	public ListePaiement(){
		this.lp = new ArrayList<>();
	}

	/**
	 * @return the lp
	 */
	public ArrayList<Paiement> getLp() {
		return lp;
	}

	/**
	 * @param lp the lp to set
	 */
	public void setLp(ArrayList<Paiement> lp) {
		this.lp = lp;
	}
	
	public void add(Paiement p){
		this.lp.add(p);
	}
	
	public void remove(Paiement p){
		this.lp.remove(p);
	}
	
	/**
	 * @param p
	 * @return
	 */
	public boolean contains(Paiement p){
		return this.lp.contains(p);
	}

	public int size() {
		return this.lp.size();
	}

	public Paiement get(int j) {
		return this.lp.get(j);
	}	

}
