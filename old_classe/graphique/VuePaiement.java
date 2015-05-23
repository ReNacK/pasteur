/**
 * 
 */
package graphique;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.regex.Pattern;

import javax.swing.DefaultRowSorter;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
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
import modele.Paiement;

/**
 * @author Kevin
 *
 */
public class VuePaiement extends JPanel implements Observer {

	private Clinique cli;
	private JTable table;
	private ArrayList<Object> listPropre;
	public static boolean filtrage = true;
	
    public VuePaiement(Clinique cli) {
    	this.cli = cli;
    	this.cli.addObserver(this);
    	this.listPropre = new ArrayList<>();
    }
	
	/* (non-Javadoc)
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
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
		model.setColumnIdentifiers(new String[]{"Nom", "Numéro facture",  "Nature", "Montant", "ligne", "erreur"});
		int erreur = 0;
		//on rajoute une ligne pour chaque facture impay�e
		Paiement p = null;
		for (int i = 0 ; i < this.cli.getLm().size(); i++){
			Medecin medecinEnCours = this.cli.getLm().get(i);
			model.addRow(new Object[]{medecinEnCours.getNum(), medecinEnCours.getNom(), medecinEnCours.getPrenom(), "","", true});
			listPropre.add(medecinEnCours);
			erreur++;
			for (int j = 0 ; j < medecinEnCours.getLp().size() ; j++){
				p = medecinEnCours.getLp().get(j);	
				if(this.cli.isAfficherTousPaiements()){
					for (int k = 0 ; k < p.getLmt().size() ; k++){
						Montant m = p.getLmt().get(k);
						if(k == 0){
							model.addRow(new Object[]{p.getNomBenef(),p.getNumFact(),p.getNature(), m.getMontant(), m.getNumLigne(), m.isEstPaye()});
							erreur++;
						}else{
							model.addRow(new Object[]{"","","", m.getMontant(), m.getNumLigne(), m.isEstPaye()});
							erreur++;
						}
						listPropre.add(p);
					}				
				}else{
					if(!p.isEstFacture()){	//on n'affiche que les paiements sans facture
						for (int k = 0 ; k < p.getLmt().size() ; k++){
							if(k == 0){
								model.addRow(new Object[]{p.getNomBenef(),p.getNumFact(),p.getNature(), p.getLmt().get(k).getMontant(), p.getLmt().get(k).getNumLigne(), p.getLmt().get(k).isEstPaye()});
							}else{
								model.addRow(new Object[]{"","","", p.getLmt().get(k).getMontant(), p.getLmt().get(k).getMontant(), p.getLmt().get(k).getNumLigne(), p.getLmt().get(k).isEstPaye()});
							}
							listPropre.add(p);
						}
					}
				}
			}
		}
		if(this.cli.isAfficherTousPaiements()){
			for( int i = 0 ; i < table.getModel().getColumnCount() ; i++){
				table.getColumnModel().getColumn(i).setCellRenderer(new CustomTableCellRenderer(2));	
			}
		}
		table.getColumnModel().getColumn(5).setMinWidth(0); //comme ça on peut utiliser les numéros de ligne pour l'exportation du fichier mais l'utilisateur ne les voit pas
		table.getColumnModel().getColumn(5).setMaxWidth(0);
		table.getColumnModel().getColumn(4).setMinWidth(0); //comme ça on peut utiliser les numéros de ligne pour l'exportation du fichier mais l'utilisateur ne les voit pas
		table.getColumnModel().getColumn(4).setMaxWidth(0);

		
		table.addMouseListener(new MouseAdapter() {
		    @Override
		    public void mouseReleased(MouseEvent e) {
		        int r = table.rowAtPoint(e.getPoint());
		        int rowindex = table.getSelectedRow();
		        if (rowindex < 0)
		            return;
		        if (e.isPopupTrigger() && e.getComponent() instanceof JTable ) {
		        	Object obj = listPropre.get(r);
		        	if(obj instanceof Paiement || obj instanceof Montant){
		        		ArrayList<Integer> list = cli.getListeFacturesPossibles().get(obj);
			    		JPopupMenu popup = new JPopupMenu();
			    		JMenu item = new JMenu("Liste factures possibles");
			    		
			    		for(int i = 0 ; i < list.size() ; i++){
			    			JMenuItem sm1 = new JMenuItem(list.get(i)+"");
				    		item.add(sm1);
			    		}	
			    		popup.add(item);
			            popup.show(e.getComponent(), e.getX(), e.getY());
		        	}
		        	
		        }
		    }
		});
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

		        if(obj instanceof Paiement){
		        	switch(col){
		        	case 0 :
		        		((Paiement)obj).changeNomClient(data.toString());
		        		((Paiement)obj).setEstFacture(false);
		        		break;
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
		        		((Paiement)obj).changeNum(data.toString());
		        		((Paiement)obj).setEstFacture(false);
		        		break;
		        	case 2 :
		        		((Paiement)obj).getLmt().get(0).changeNature(data.toString());
		        		((Paiement)obj).setEstFacture(false);
		        		break;
		        	case 3 :
		        		float num1 = 0;
		        		try{
		        			num1 = Float.parseFloat(data.toString());
		        			
		        		}catch(NumberFormatException e1){
		        			JOptionPane.showMessageDialog(null,
		        				    "Vous devez rentrer un entier",
		        				    "Erreur",
		        				    JOptionPane.ERROR_MESSAGE);
		        		}
		        		((Paiement)obj).getLmt().get(0).changeMontant(num1);
		        		((Paiement)obj).setEstFacture(false);
		        		break;
		        	}
		        }
			}
		});
        this.add(new JScrollPane(table), BorderLayout.CENTER);
        this.add(filterTextField, BorderLayout.SOUTH);
	}
	
	public void applyTableFilter(String filterText) {
		// On escape le texte afin que son contenu ne soit pas considéré comme
		// une regexp
		String escapedFilterText = Pattern.quote(filterText);
		// On ajoute les wildcards a gauche et a droite
		String completeFilterText = ".*" + escapedFilterText + ".*";
		// On applique le filtre a la JTable
		this.filtrage = filterText.isEmpty();
		((DefaultRowSorter) table.getRowSorter()).setRowFilter(RowFilter.regexFilter(filterText.toUpperCase()));
	}

}
