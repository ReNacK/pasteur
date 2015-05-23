/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphique;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.regex.Pattern;

import javax.swing.DefaultRowSorter;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import modele.Clinique;
import modele.Facture;
import modele.Medecin;
import modele.Montant;

/**
 *
 * @author Imane
 */
public class VueFacture extends JPanel implements Observer{
	private Clinique cli;
	private JTable table;
	private ArrayList<Object> listPropre; //liste contenant l'objet correspondant à la ligne
	public static boolean filtrage = true;

	public VueFacture(Clinique cli) {
		this.cli = cli;
		this.cli.addObserver(this);
		this.listPropre = new ArrayList<>();
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		this.removeAll();
		/* cr�ation d'instance */
		DefaultTableModel model = new DefaultTableModel();
		this.table = new JTable(model);
		table.setAutoCreateRowSorter(true);
		final JTextField filterTextField = new JTextField();
		filterTextField.setPreferredSize(new Dimension(200, 200));
		filterTextField.getDocument().addDocumentListener(
				new DocumentListener() {
 
					@Override
					public void removeUpdate(DocumentEvent e) {
						applyTableFilter(filterTextField.getText());
					}
 
					@Override
					public void insertUpdate(DocumentEvent e) {
						applyTableFilter(filterTextField.getText());
					}
 
					@Override
					public void changedUpdate(DocumentEvent e) {
						applyTableFilter(filterTextField.getText());
					}
				});

		model.setColumnIdentifiers(new String[]{"Nom", "Prénom",  "Numéro facture",  "Nature", "Montant","erreur"});
		//on rajoute une ligne pour chaque facture impay�e
		Facture f = null;
		int erreur = 0;
		if(this.cli.isAfficherToutesFactures()){
			for (int i = 0 ; i < this.cli.getLm().size() ; i++){
				Medecin medecinEnCours = this.cli.getLm().get(i);
				model.addRow(new Object[]{medecinEnCours.getNum(), medecinEnCours.getNom(), medecinEnCours.getPrenom(), "","", true});
				for (int j = 0 ; j< medecinEnCours.getLf().size() ; j++){
					f = medecinEnCours.getLf().get(j);
					if(f.getLmt().size() == 1){
						model.addRow(new Object[]{f.getNomClient(), f.getPrenomClient(),f.getNum(),f.getNature(), f.getMontantTotal(), f.getLmt().get(0).isEstPaye()});
						listPropre.add(f);
						erreur++;

					}else{
						//on parcourt la liste des montants
						for (int k = 0 ; k < f.getLmt().size() ; k++){
							Montant m = f.getLmt().get(k);
							if(k == 0){
								model.addRow(new Object[]{f.getNomClient(), f.getPrenomClient(),f.getNum(),f.getNature(), m.getMontant(), m.isEstPaye()});
								listPropre.add(f);
								erreur++;
							}else{
								model.addRow(new Object[]{f.getNomClient(), f.getPrenomClient(),"","", m.getMontant(), m.isEstPaye()});
								listPropre.add(m);
								erreur++;
							}
						}

					}

				}
			}
		}else{
			for (int i = 0 ; i < this.cli.getLm().size() ; i++){
				Medecin medecinEnCours = this.cli.getLm().get(i);
				for (int j = 0 ; j< medecinEnCours.getListeImpayesTotale().size() ; j++){
					f = medecinEnCours.getListeImpayesTotale().get(j);

					if(!f.isEstPaye()){
						//on parcourt la liste des montants
						for (int k = 0 ; k < f.getLmt().size() ; k++){
							if(!f.getLmt().get(k).isEstPaye()){			
								if(k == 0){
									model.addRow(new Object[]{f.getNomClient(), f.getPrenomClient(),f.getNum(),f.getNature(), f.getLmt().get(k).getMontant(), f.getLmt().get(k).getNumLigne(), f.getLmt().get(k).isEstPaye()});
									listPropre.add(f);
								}else{
									model.addRow(new Object[]{"","","",f.getLmt().get(k).getNature(), f.getLmt().get(k).getMontant(), f.getLmt().get(k).isEstPaye()});
									listPropre.add(f.getLmt().get(k));
								}
							}
						}
					}
				}
			}
		}
		table.getColumnModel().getColumn(5).setMinWidth(0); //comme ça on peut utiliser les numéros de ligne pour l'exportation du fichier mais l'utilisateur ne les voit pas
		table.getColumnModel().getColumn(5).setMaxWidth(0);
		if(this.cli.isAfficherToutesFactures()){
			this.renderer();
		}
		model.addTableModelListener(new TableModelListener() {
			
			@Override
			public void tableChanged(TableModelEvent e) {
				int col = e.getColumn();
				int ligne = 0;
				TableModel model = (TableModel)e.getSource();

				if(e.getFirstRow() == e.getLastRow()){
					ligne = e.getFirstRow();
				}
		        Object data = model.getValueAt(ligne, col);
		        Object obj = listPropre.get(ligne);

		        if(obj instanceof Facture){
		        	switch(col){
		        	case 0 :
		        		((Facture)obj).changeNomClient(data.toString());
		        		((Facture)obj).setEstPaye(false);
		        		break;
		        	case 1 :
		        		((Facture)obj).changePrenomClient(data.toString());
		        		((Facture)obj).setEstPaye(false);
		        		break;
		        	case 2 :
		        		int num = 0;
		        		try{
		        			num = Integer.parseInt(data.toString());
		        		}catch(NumberFormatException e1){
		        			JOptionPane.showMessageDialog(null,
		        				    "Vous devez rentrer un entier",
		        				    "Erreur",
		        				    JOptionPane.ERROR_MESSAGE);
		        		}
		        		((Facture)obj).changeNum(num);
		        		((Facture)obj).setEstPaye(false);
		        		break;
		        	case 3 :
		        		((Facture)obj).changeNature(data.toString());
		        		((Facture)obj).setEstPaye(false);
		        		break;
		        	case 4 :
		        		float num1 = 0;
		        		try{
		        			num1 = Float.parseFloat(data.toString());
		        			
		        		}catch(NumberFormatException e1){
		        			JOptionPane.showMessageDialog(null,
		        				    "Vous devez rentrer un entier",
		        				    "Erreur",
		        				    JOptionPane.ERROR_MESSAGE);
		        		}
		        		((Facture)obj).getLmt().get(0).changeMontant(num1);
		        		((Facture)obj).setEstPaye(false);
		        		break;
		        	}
		        }else if (obj instanceof Montant){
		        	switch(col){
		        	case 1 :

		        		int num = 0;
		        		try{
		        			num = Integer.parseInt(data.toString());
		        		}catch(NumberFormatException e1){
		        			JOptionPane.showMessageDialog(null,
		        				    "Vous devez rentrer un entier",
		        				    "Erreur",
		        				    JOptionPane.ERROR_MESSAGE);
		        		}
		        		((Montant)obj).getFact().changeNum(num);
		        		((Montant)obj).setEstPaye(false);
		        		break;
		        	case 2 :
		        		((Montant)obj).changeNature(data.toString());
		        		((Montant)obj).setEstPaye(false);
		        		break;
		        	/*case 3 :
		        		float num1 = 0;
		        		try{
		        			num1 = Float.parseFloat(data.toString());
		        		}catch(NumberFormatException e1){
		        			JOptionPane.showMessageDialog(null,
		        				    "Vous devez rentrer un entier",
		        				    "Erreur",
		        				    JOptionPane.ERROR_MESSAGE);
		        		}
		        		((Montant)obj).changeMontant(num1);
		        		((Montant)obj).setEstPaye(false);
		        		break;*/
		        	}
		        }
			}
		});
		this.add(new JScrollPane(table), BorderLayout.CENTER);
		this.add(filterTextField, BorderLayout.SOUTH);
	}
	
	private void renderer() {
		for( int i = 0 ; i < table.getModel().getColumnCount() ; i++){
			table.getColumnModel().getColumn(i).setCellRenderer(new CustomTableCellRenderer(1));	
		}
	}

	public void applyTableFilter(String filterText) {
		// On escape le texte afin que son contenu ne soit pas consid�r� comme
		// une regexp
		String escapedFilterText = Pattern.quote(filterText);
		// On ajoute les wildcards a gauche et a droite
		String completeFilterText = ".*" + escapedFilterText + ".*";
		// On applique le filtre a la JTable
		this.filtrage = filterText.isEmpty();
		((DefaultRowSorter) table.getRowSorter()).setRowFilter(RowFilter.regexFilter(filterText.toUpperCase()));
		//this.renderer();
	}
}

