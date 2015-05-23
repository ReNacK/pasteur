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
public class ListeMedecin {

	private ArrayList<Medecin> liste_medecin;

	public ListeMedecin(){
		this.liste_medecin = new ArrayList<>();
	}


	public ArrayList<Medecin> getListe_medecin() {
		return liste_medecin;
	}

	public void setListe_medecin(ArrayList<Medecin> liste_medecin) {
		this.liste_medecin = liste_medecin;
	}

	public void add(Medecin med){
		this.liste_medecin.add(med);
	}

	public void remove(Medecin med){
		this.liste_medecin.remove(med);
	}

	public boolean contains(Medecin med){
		return this.contains(med);
	}

	public void chercheImpayes(int plage) {	
		Medecin medecinEnCours;
		Paiement paiementEnCours;
		Facture factureEnCours;
		Montant montantEnCours;
		//pour chaque m�decin
		for(int i = 0 ; i < this.liste_medecin.size() ; i++){
			medecinEnCours = this.liste_medecin.get(i);
			ArrayList<Facture> listeImpayes = new ArrayList<>();
			//pour chaque paiement du m�decin
			for(int j = 0 ; j < medecinEnCours.getLp().size() ; j++){
				paiementEnCours = medecinEnCours.getLp().get(j);
				for( int y = 0 ; y < medecinEnCours.getLf().size() ; y++){
					factureEnCours = medecinEnCours.getLf().get(y);

					if(factureEnCours.getNum() == paiementEnCours.getNumFact()){		
						//il faut trouver le montant correspondant dans les factures
						if(factureEnCours.getMontantTotal() == paiementEnCours.getMontantTotal()){
							factureEnCours.setEstPaye(true);
							paiementEnCours.setEstFacture(true);
						}else{
							for( int k = 0 ; k < paiementEnCours.getLmt().size() ; k++){ //pour chaque montant de chaque paiement
								montantEnCours = paiementEnCours.getLmt().get(k);
								for (int z = 0 ; z < factureEnCours.getLmt().size() ; z++){				
									if(montantEnCours.getMontant() == factureEnCours.getLmt().get(z).getMontant()){
										montantEnCours.setEstPaye(true); //il est factur�
										factureEnCours.getLmt().get(z).setEstPaye(true); // il est pay�
									}
								}
							}
						}
					}
				}
			}
			//ici on a parcouru toutes les factures et paiements du m�decin. On peut donc trouver ses impay�s et les erreurs
			//pour toutes les factures
			Date sysdate = new Date();
			for(int j = 0 ; j < medecinEnCours.getLf().size() ; j++){				
				//si le bool�en estPay� est � faux on l'ajoute � la liste
				factureEnCours = medecinEnCours.getLf().get(j);
				if(this.calculDiffDates(factureEnCours.getDateFacturation(), sysdate) >= plage){
					if(!factureEnCours.isEstPaye()){	
						listeImpayes.add(factureEnCours);
						medecinEnCours.addImpaye(factureEnCours); //comme �a on peut la r�cup�rer pour les stats
					}
				}else{
					factureEnCours.setEstPaye(true);
				}
			}
			medecinEnCours.setListeImpayesTotale(listeImpayes);
		}
	}

	public int calculDiffDates(Date d1, Date d2){
		return (int) ((d2.getTime() - d1.getTime()) / (24 * 60 * 60 * 1000));
	}

	public void chercheErreurPaiement(){
		ArrayList<Integer> facturePossible = new ArrayList<>();
		Medecin medecinEnCours;
		Paiement paiementEnCours;
		Facture factureEnCours;
		//pour chaque m�decin
		for(int i = 0 ; i < this.liste_medecin.size() ; i++){
			medecinEnCours = this.liste_medecin.get(i);
			HashMap<Paiement, ArrayList<Integer>> listeErreurs = new HashMap<>();
			//pour chaque paiement du m�decin
			for(int j = 0 ; j < medecinEnCours.getLp().getLp().size() ; j++){
				paiementEnCours = medecinEnCours.getLp().getLp().get(j);
				//on r�initialise la liste contenant les factures pouvant correspondre au paiement
				facturePossible = new ArrayList<>();
				listeErreurs.put(paiementEnCours, facturePossible);
				//si le bool�en estFactur� est � faux alors erreur, il faut chercher les factures non pay�es se rapprochant du paiement
				if(!paiementEnCours.isEstFacture()){
					//on ajoute le paiement � la hashmap

					//on cherche les factures pouvant correspondre
					for(int k = 0 ; k < medecinEnCours.getLf().getLf().size() ; k++){
						factureEnCours = medecinEnCours.getLf().getLf().get(k);
						//il faut que la nature soit la m�me, ainsi que le nom du client et le m�me montant (voir la m�me date de facturation)
						if(!factureEnCours.isEstPaye()){
							if(paiementEnCours.getNature().equals(factureEnCours.getNature())){
								if(paiementEnCours.getNomBenef().toLowerCase().trim().equals((factureEnCours.getNomClient().toLowerCase()+factureEnCours.getPrenomClient().toLowerCase()).trim())){
									facturePossible.add(factureEnCours.getNum());
								}else{
									Montant montantEnCours = null;
									for (int z = 0 ; z < factureEnCours.getLmt().size() ; z++){
										montantEnCours = factureEnCours.getLmt().get(z);
										for(int y = 0 ; y < paiementEnCours.getLmt().size() ; y++){
											if(montantEnCours.getDateActe() == paiementEnCours.getLmt().get(y).getDateActe() && montantEnCours.getMontant() == paiementEnCours.getLmt().get(y).getMontant()){
												facturePossible.add(factureEnCours.getNum());
											}
										}

									}
								}
							}
						}else if(paiementEnCours.getNumFact() == 0){
							//on doit chercher dans tous les montants des factures voir si un correspond
							Montant montantEnCours = null;
							for (int z = 0 ; z < factureEnCours.getLmt().size() ; z++){
								montantEnCours = factureEnCours.getLmt().get(z);
								for(int y = 0 ; y < paiementEnCours.getLmt().size() ; y++){
									if(montantEnCours.getDateActe() == paiementEnCours.getLmt().get(y).getDateActe() || montantEnCours.getMontant() == paiementEnCours.getLmt().get(y).getMontant()){
										facturePossible.add(factureEnCours.getNum());
									}
								}

							}
						}
					}
				}
			}
			medecinEnCours.setListeFacturesPossibles(listeErreurs);
		}
	}


	public int size(){
		return this.liste_medecin.size();
	}


	public Medecin get(int i){
		return this.liste_medecin.get(i);
	}


	public boolean contains(String string) {
		boolean trouve = false;
		int i = 0;
		while(i < this.liste_medecin.size() && !trouve){
			trouve = string.toLowerCase().contains(this.liste_medecin.get(i).getNom().toLowerCase().trim()); // vrai si la chaine contient le nom du m�decin
			i++;
		}
		return trouve;
	}


	public Medecin get(String string) {
		Medecin trouve = null;
		int i = 0;
		while(i < this.liste_medecin.size() && trouve == null){
			if(this.contains(string)){
				trouve = this.liste_medecin.get(i);
			}
			i++;
		}
		return trouve;
	}


	public Facture getFacture(Medecin medecinEnCours, String string) {
		Facture fact = null;
		int i = 0;
		while(fact == null && i < this.liste_medecin.size()){
			fact = this.liste_medecin.get(i).getFacture(string);
			i++;
		}
		return fact;
	}


	public boolean containsFacture(Medecin medecinEnCours, String string) {
		boolean trouve = false;
		int i = 0;
		while(!trouve && i < this.liste_medecin.size()){
			trouve = this.liste_medecin.get(i).containsFacture(string);
			i++;
		}
		return trouve;
	}

	public Paiement getPaiement(Medecin medecinEnCours, String string) {
		Paiement p = null;
		int i = 0;
		while(p == null && i < this.liste_medecin.size()){
			p = this.liste_medecin.get(i).getPaiement(string);
			i++;
		}
		return p;
	}


	public boolean containsPaiement(Medecin medecinEnCours, String string) {
		boolean trouve = false;
		int i = 0;
		while(!trouve && i < this.liste_medecin.size()){
			trouve = this.liste_medecin.get(i).containsPaiement(string);
			i++;
		}
		return trouve;
	}
}
