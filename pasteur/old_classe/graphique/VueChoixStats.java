package graphique;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.JTextField;
import javax.swing.JDialog;

import modele.Clinique;

public class VueChoixStats extends JDialog implements Observer {
	private boolean sendData;
	private JLabel nomLabel, moisFinLabel, anneeFinLabel, moisDebutLabel, anneeDebutLabel, cheveuxLabel, ageLabel, tailleLabel,taille2Label, icon;
	private JCheckBox tranche1, tranche2, tranche3, tranche4;
	private JComboBox moisFin, anneeFin, moisDebut, anneeDebut, cheveux;



	private Clinique cli;


	public VueChoixStats(JFrame parent, String title, boolean modal, Clinique cli){
		super(parent, title, modal);
		this.cli = cli;
		this.setSize(550, 270);
		this.setLocationRelativeTo(null);
		this.setResizable(false);
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		this.initComponent();
	}

	public int calculDiffDates(Date d1, Date d2){
		return (int) ((d2.getTime() - d1.getTime()) / (24 * 60 * 60 * 1000));
	}

	private void initComponent(){
		//Ic�ne
		JPanel panIcon = new JPanel();
		panIcon.setBackground(Color.white);
		panIcon.setLayout(new BorderLayout());

		//La date de d�but
		JPanel panDebut = new JPanel();
		panDebut.setBackground(Color.white);
		panDebut.setPreferredSize(new Dimension(320, 60));
		panDebut.setBorder(BorderFactory.createTitledBorder("Date de début de la plage voulue"));
		moisDebut = new JComboBox<Integer>();
		for (int i = 1 ; i < 13 ; i ++){
			moisDebut.addItem(i);
		}
		anneeDebut = new JComboBox<Integer>();
		Date d = new Date();
		for (int i = 80 ; i <= d.getYear() ; i ++){
			anneeDebut.addItem(i+1900);
		}
		moisDebutLabel = new JLabel("Mois : ");
		panDebut.add(moisDebutLabel);
		panDebut.add(moisDebut);
		anneeDebutLabel = new JLabel("Année : ");
		panDebut.add(anneeDebutLabel);
		panDebut.add(anneeDebut);

		//La date de fin
		JPanel panFin = new JPanel();
		panFin.setBackground(Color.white);
		panFin.setPreferredSize(new Dimension(320, 60));
		panFin.setBorder(BorderFactory.createTitledBorder("Date de fin de la plage voulue"));
		moisFin = new JComboBox<Integer>();
		for (int i = 1 ; i < 13 ; i ++){
			moisFin.addItem(i);
		}
		anneeFin = new JComboBox<Integer>();
		for (int i = 80 ; i <= d.getYear() ; i ++){
			anneeFin.addItem(i+1900);
		}
		moisFinLabel = new JLabel("Mois : ");
		panFin.add(moisFinLabel);
		panFin.add(moisFin);
		anneeFinLabel = new JLabel("Année : ");
		panFin.add(anneeFinLabel);
		panFin.add(anneeFin);

		//Choix des stats 
		JPanel panStats = new JPanel();
		panStats.setBackground(Color.white);
		panStats.setBorder(BorderFactory.createTitledBorder("Choix des statistiques"));
		panStats.setPreferredSize(new Dimension(440, 60));
		List<JCheckBox> buttons = new ArrayList<>();
		tranche1 = new JCheckBox("Stats 1");
		tranche2 = new JCheckBox("Stats 2");
		tranche3 = new JCheckBox("Stats 3");
		tranche4 = new JCheckBox("Stats 4");

		buttons.add(tranche1);
		buttons.add(tranche2);
		buttons.add(tranche3);
		buttons.add(tranche4);
		for ( JCheckBox checkbox : buttons ) {
			checkbox.addItemListener(new ItemListener() {

				@Override
				public void itemStateChanged(ItemEvent e) {
					if (e.getStateChange() == ItemEvent.SELECTED){
						cli.addStatsVoulue(((JCheckBox)e.getSource()).getText());
					}else if(e.getStateChange() == ItemEvent.DESELECTED){
						cli.removeStatsVoulue(((JCheckBox)e.getSource()).getText());
					}
				}
			});
		}
			panStats.add(tranche1);
			panStats.add(tranche2);
			panStats.add(tranche3);
			panStats.add(tranche4);


			JPanel content = new JPanel();
			content.setBackground(Color.white);
			content.add(panDebut);
			content.add(panFin);
			content.add(panStats);

			JPanel control = new JPanel();
			JButton okBouton = new JButton("OK");

			okBouton.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent arg0) {  
					SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy"); //attention au format !!!
					Date debut = null;
					Date fin = null;
					try {
						debut = format.parse("01/"+moisDebut.getSelectedItem()+"/"+anneeDebut.getSelectedItem());
						fin = format.parse("01/"+moisFin.getSelectedItem()+"/"+anneeFin.getSelectedItem());
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if(calculDiffDates(debut, fin) < 0){
						JOptionPane jop2 = new JOptionPane();
						jop2.showMessageDialog(null, "La date de début soit être inférieure à la date de fin", "Erreur", JOptionPane.ERROR_MESSAGE);
					}else{
						cli.setDebutStats(debut);
						cli.setFinStats(fin);
						cli.setAfficherStats(true);
						setVisible(false);
						new VueStats(cli);
					}
				}

				public String getStats(){
					return (tranche1.isSelected()) ? tranche1.getText() : 
						(tranche2.isSelected()) ? tranche2.getText() : 
							(tranche3.isSelected()) ? tranche3.getText() : 
								(tranche4.isSelected()) ? tranche4.getText() : 
									tranche1.getText();  
				}

			});

			JButton cancelBouton = new JButton("Annuler");
			cancelBouton.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent arg0) {
					setVisible(false);
				}      
			});

			control.add(okBouton);
			control.add(cancelBouton);

			this.getContentPane().add(panIcon, BorderLayout.WEST);
			this.getContentPane().add(content, BorderLayout.CENTER);
			this.getContentPane().add(control, BorderLayout.SOUTH);
		}  	


		@Override
		public void update(Observable arg0, Object arg1) {
			// TODO Auto-generated method stub

		}

	}
