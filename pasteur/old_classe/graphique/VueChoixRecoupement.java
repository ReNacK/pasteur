/**
 * 
 */
package graphique;

import java.util.Observable;
import java.util.Observer;

import javax.swing.JOptionPane;

import modele.Clinique;

/**
 * @author Kevin
 *
 */
public class VueChoixRecoupement implements Observer{
	
	private Clinique cli;
	
	public VueChoixRecoupement(Clinique cli){
		this.cli = cli;
	    int res = -1;
	    boolean testPlageOK;
	    do{
	    	JOptionPane jop = new JOptionPane();
	    	String nom = jop.showInputDialog(null, "Combien de jours après la date de facturation voulez-vous utiliser pour détecter les impayés ?", "Plage de recoupement", JOptionPane.QUESTION_MESSAGE);
		    try{
		    	res = Integer.parseInt(nom);
		    	testPlageOK = (res >= 0);
		    }catch(NumberFormatException e){
		    	JOptionPane jop2 = new JOptionPane();
		    	jop2.showMessageDialog(null, "Vous devez rentrer un nombre", "Erreur", JOptionPane.ERROR_MESSAGE);            
		    	testPlageOK = false;
		    }
	    }while(!testPlageOK);
	    this.cli.setPlage(res);
	    this.cli.run();
	    this.cli.update();
		this.cli.setChargementFichierFacture(false);
		this.cli.setChargementFichierPaiement(false);

	}

	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		
	}

}
