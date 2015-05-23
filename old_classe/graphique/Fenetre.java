/**
 *
 */
package graphique;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;
import modele.Clinique;

/**
 * @author Kevin
 *
 */
public class Fenetre extends JPanel {

    private Clinique cli;
    private JFrame fenetre;

    /**
     *
     */
    public Fenetre(Clinique cli) {
        this.setLookAndFeel();
        this.cli = cli;
        this.run();
    }

    public void run() {
        this.fenetre = new JFrame();
        Toolkit outil = getToolkit();
        //D�finit un titre pour notre fen�tre
        this.fenetre.setTitle("Analyse des impayés");
        //D�finit sa taille : 400 pixels de large et 100 pixels de haut
        this.fenetre.setSize(outil.getScreenSize());
        //Nous demandons maintenant � notre objet de se positionner au centre
        this.fenetre.setLocationRelativeTo(null);
        //Termine le processus lorsqu'on clique sur la croix rouge
        this.fenetre.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Barre de boutons
        ButtonBar buttonbarre = new ButtonBar(this.cli);
        this.fenetre.add(BorderLayout.NORTH, buttonbarre);

        //on cr�e les onglets
        // On cree un panel � onglets
        JTabbedPane onglets = new JTabbedPane();
        // un premier onglet
        JPanel onglet1 = new JPanel();
        onglet1.setLayout(new BorderLayout());
        VueFacture vf = new VueFacture(this.cli);
        onglet1.add(vf);
        onglets.addTab("Factures impayées", null, onglet1, "Factures impayées");

        // un deuxieme onglet
        JPanel onglet2 = new JPanel();
        onglet2.setLayout(new BorderLayout());
        VuePaiement vp = new VuePaiement(this.cli);
        onglet2.add(vp);
        onglets.addTab("Erreur paiement", null, onglet2, "Paiements sans factures");
        // et voila, il faut en plus  ajouter le panel � onglet dans le panel principal
        this.fenetre.add(onglets, BorderLayout.CENTER);

        //ajout de la barre de menu
        this.setLayout(null);
        VueMenu vmenu = new VueMenu(this.cli);
        this.fenetre.setJMenuBar(vmenu);

        //ajout de la console pour afficher les messages
        VueConsole vconsole = new VueConsole(this.cli);
        JScrollPane jsVm = new JScrollPane(vconsole);
        jsVm.setPreferredSize(new Dimension(200, 200));

        this.fenetre.add(jsVm, BorderLayout.SOUTH);
        //Et enfin, la rendre visible
        this.fenetre.setVisible(true);
    }

    protected void setLookAndFeel() {
        try {
            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }

        } catch (InstantiationException e) {

        } catch (ClassNotFoundException e) {

        } catch (UnsupportedLookAndFeelException e) {

        } catch (IllegalAccessException e) {
        }
    }
}
