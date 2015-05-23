/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphique;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;

import modele.Clinique;

/**
 *
 * @author imane
 */
public class ButtonBar extends JPanel {

    private MyButton facture, payement, exportfile, stat, export, find;
    public static final int NUM_BUTTON = 5; // nombre de boutons
    private final Clinique cli;

    public ButtonBar(Clinique cli2) {
        super(new BorderLayout(10, 0));

        //JToolBar toolbar = new JToolBar("Menu");
        this.cli = cli2;
        JPanel toolbar = new JPanel();
        toolbar.setLayout(new GridLayout(1, 5, 0, 0));

        add(toolbar, BorderLayout.CENTER);

        //TODO Faire avec chemin relatif ! 
        facture = new MyButton("Ajouter une facture", "images/add.png");
        payement = new MyButton("Ajouter un payement", "images\\pay.png");
        exportfile = new MyButton("Enregistrer les modifications", "images\\exp.png"); //TODO : Trouver une image
        stat = new MyButton("Voir les statistiques", "images\\stat.png");
        export = new MyButton("Exporter les statistiques", "images\\exp.png");
        find = new MyButton("Trouver les impayés", "images\\find.png");
        JCheckBox box = new JCheckBox("Afficher toutes les factures");
        JCheckBox box2 = new JCheckBox("Afficher tous les payements");
        toolbar.add(facture);
        toolbar.add(payement);
        toolbar.add(exportfile);
        toolbar.add(find);
        toolbar.add(stat);
        toolbar.add(export);
        toolbar.add(box);
        toolbar.add(box2);
        this.add(toolbar);

        facture.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                JFrame proprietaire = new JFrame();
                int resultat = cli.getFileChooser().showDialog(proprietaire, "Valider votre choix de fichier de facturation");
                if (resultat == JFileChooser.APPROVE_OPTION) {
                    File[] tableauFichiers = cli.getFileChooser().getSelectedFiles();
                    for (int i = 0; i < tableauFichiers.length; i++) {
                        cli.addFacture(tableauFichiers[i]);		//permet de charger plusieurs fois des fichiers
                    }
                    cli.getFileChooser().setCurrentDirectory(cli.getFileChooser().getSelectedFile());
                    cli.setChargementFichierFacture(true);
                }
            }
        });
        payement.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                JFrame proprietaire = new JFrame();
                int resultat = cli.getFileChooser().showDialog(proprietaire, "Valider votre choix de fichier de paiement");
                if (resultat == JFileChooser.APPROVE_OPTION) {
                    File[] tableauFichiers = cli.getFileChooser().getSelectedFiles();
                    for (int i = 0; i < tableauFichiers.length; i++) {
                        cli.addPaiement(tableauFichiers[i]);		//permet de charger plusieurs fois des fichiers
                    }
                    cli.getFileChooser().setCurrentDirectory(cli.getFileChooser().getSelectedFile());
                    cli.setChargementFichierPaiement(true);
                }
            }
        });

        exportfile.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                //TODO : Voir comment intéragir avec la méthode pour ajouter le nom du fichier et le sauvegarder
                File workingDirectory = new File(System.getProperty("user.dir"));
                JFileChooser saveFile = new JFileChooser();
                saveFile.setCurrentDirectory(workingDirectory);
                int resultat = saveFile.showSaveDialog(null);
                if(resultat == JFileChooser.APPROVE_OPTION){
                	String filename = saveFile.getSelectedFile().getPath();
                	cli.exporterFactures(filename);
                }
               
            }

        });

        stat.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                VueChoixStats vs = new VueChoixStats(null, "Choix des statistiques", true, cli);
                vs.setVisible(true);
            }
        });
        export.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                VueChoixStats vs = new VueChoixStats(null, "Choix des statistiques", true, cli);
                vs.setVisible(true);
            }
        });
        box.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent arg0) {
                if (arg0.SELECTED == 1) {
                    cli.setAfficherToutesFactures(true);
                } else {
                    cli.setAfficherToutesFactures(false);
                }
            }
        });
        box.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent arg0) {
                if (arg0.SELECTED == 1) {
                    cli.setAfficherTousPaiements(true);
                } else {
                    cli.setAfficherTousPaiements(false);
                }
            }
        });
        find.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                new VueChoixRecoupement(cli);
            }
        });
    }
}
