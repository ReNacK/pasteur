/**
 * 
 */
package graphique;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuItem;

import modele.Clinique;

/**
 * @author Kevin
 *
 */
public class EcouteurMenu implements ActionListener {

	private Clinique cli;
	private static String dernier_dossier;
	/**
	 * 
	 */
	public EcouteurMenu(Clinique cli) {
		this.cli = cli;
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		JMenuItem b = (JMenuItem)e.getSource();
		//on r�cup�re le menu sur lequel l'utilisateur a cliqu� et r�agit en fonction
		String text = b.getText();
		if(text.equals(("Charger un/des fichier(s) de facturation"))){
			JFrame proprietaire = new JFrame();
            int resultat = cli.getFileChooser().showDialog(proprietaire, "Valider votre choix de fichier de facturation");
            if(resultat == JFileChooser.APPROVE_OPTION){
                File[] tableauFichiers = cli.getFileChooser().getSelectedFiles();
                for (int i = 0 ; i < tableauFichiers.length ; i++){
                	cli.addFacture(tableauFichiers[i]);		//permet de charger plusieurs fois des fichiers
                }
                cli.getFileChooser().setCurrentDirectory(cli.getFileChooser().getSelectedFile());
                cli.setChargementFichierFacture(true);
            }
		}else if(text.equals("Charger un/des fichier(s) de paiement")){
			JFrame proprietaire = new JFrame();
            int resultat = cli.getFileChooser().showDialog(proprietaire, "Valider votre choix de fichier de paiement");
            if(resultat == JFileChooser.APPROVE_OPTION){
                File[] tableauFichiers = cli.getFileChooser().getSelectedFiles();
                for (int i = 0 ; i < tableauFichiers.length ; i++){
                	cli.addPaiement(tableauFichiers[i]);		//permet de charger plusieurs fois des fichiers
                }
                cli.getFileChooser().setCurrentDirectory(cli.getFileChooser().getSelectedFile());
                cli.setChargementFichierPaiement(true);
            }
		}else if(text.equals("Recoupement des fichiers")){
	    	new VueChoixRecoupement(this.cli);
	    }else if(text.equals("Afficher les statistiques")){
	    	VueChoixStats vs = new VueChoixStats(null, "Choix des statistiques", true, this.cli);
	    	vs.setVisible(true);
	    }else if(text.equals("Générer les statistiques dans un PDF")){
	    	this.cli.setAfficherStats(false);
	    	new VueStats(this.cli);
	    }else if(text.equals("Afficher toutes les factures")){
	    	this.cli.setAfficherToutesFactures(true);
	    }else if(text.equals("Afficher tous les paiements")){
	    	this.cli.setAfficherTousPaiements(true);
	    }else if(text.equals("Exporter fichier(s) de facturation")){
	    	File workingDirectory = new File(System.getProperty("user.dir"));
            JFileChooser saveFile = new JFileChooser();
            saveFile.setCurrentDirectory(workingDirectory);
            int resultat =  saveFile.showSaveDialog(null);
            if(resultat == JFileChooser.APPROVE_OPTION){
            	String filename = saveFile.getSelectedFile().getPath();     	
    	    	this.cli.exporterFactures(filename);
            }

	    }else if(text.equals("Exporter fichier(s) de paiements")){
	    	this.cli.exporterPaiements();
	    }
	}

}
