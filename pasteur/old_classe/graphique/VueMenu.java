/**
 * 
 */
package graphique;

import java.util.Observable;
import java.util.Observer;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import modele.Clinique;

/**
 * @author Kevin
 *
 */
public class VueMenu extends JMenuBar implements Observer {

	private Clinique cli;
	
	/**
	 * 
	 */
	public VueMenu(Clinique cli) {
		this.cli = cli;
		//cr�� un menu
		JMenu menu = new JMenu("Fichier") ;
		
		//cr�� les �l�ments du menu et s'y rajoute
		JMenuItem jmi1 = new JMenuItem("Charger un/des fichier(s) de facturation") ;
	    jmi1.addActionListener(new EcouteurMenu(this.cli));
		JMenuItem jmi2 = new JMenuItem("Charger un/des fichier(s) de paiement") ;
	    jmi2.addActionListener(new EcouteurMenu(this.cli));
	    menu.add(jmi1) ;
		menu.add(jmi2) ;
		add(menu);
		
		//cr�ation du menu "outils"
		menu = new JMenu("Outils");
		JMenuItem jmi3 = new JMenuItem("Recoupement des fichiers") ;
		//on place un �couteur sur l'�l�ment du menu
		jmi3.addActionListener(new EcouteurMenu(this.cli));
		menu.add(jmi3);
		JMenuItem jmi4 = new JMenuItem("Afficher les statistiques") ;
		//on place un �couteur sur l'�l�ment du menu
		jmi4.addActionListener(new EcouteurMenu(this.cli));
		menu.add(jmi4);
		JMenuItem jmi5 = new JMenuItem("Générer les statistiques dans un PDF") ;
		//on place un �couteur sur l'�l�ment du menu
		jmi5.addActionListener(new EcouteurMenu(this.cli));
		menu.add(jmi5);
		JMenuItem jmi6 = new JMenuItem("Exporter fichier(s) de facturation") ;
		//on place un �couteur sur l'�l�ment du menu
		jmi6.addActionListener(new EcouteurMenu(this.cli));
		menu.add(jmi6);
		JMenuItem jmi7 = new JMenuItem("Exporter fichier(s) de paiements") ;
		//on place un �couteur sur l'�l�ment du menu
		jmi7.addActionListener(new EcouteurMenu(this.cli));
		menu.add(jmi7);
		add(menu);
		menu = new JMenu("Options");
		JCheckBoxMenuItem cbMenuItem = new JCheckBoxMenuItem("Afficher toutes les factures");
		cbMenuItem.addActionListener(new EcouteurMenu(this.cli));
		//cbMenuItem.setMnemonic(KeyEvent.VK_C);
		menu.add(cbMenuItem);
		JCheckBoxMenuItem cbMenuItem2 = new JCheckBoxMenuItem("Afficher tous les paiements");
		cbMenuItem2.addActionListener(new EcouteurMenu(this.cli));
		//cbMenuItem.setMnemonic(KeyEvent.VK_C);
		menu.add(cbMenuItem2);
		add(menu);
		this.cli.addObserver(this);
	}

	
	/* (non-Javadoc)
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	@Override
	public void update(Observable arg0, Object arg1) {

	}

}
