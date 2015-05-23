package graphique;

//code provenant de http://www.javaworld.com/article/2077430/core-java/set-the-jtable.html

import java.awt.Component;
import java.awt.Color;
import java.util.ArrayList;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import modele.Clinique;

public class CustomTableCellRenderer extends DefaultTableCellRenderer 
{
	
	private int numTable;


	public CustomTableCellRenderer(int i) {
		this.numTable = i;
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) 
    {
		
        Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if(this.numTable == 1 && VueFacture.filtrage || this.numTable == 2 && VuePaiement.filtrage){
	        if(table.getModel().getValueAt(row, 5).toString().equals("false")){
	        	cell.setBackground(Color.red);
	        }else if(!isSelected){
	        	cell.setBackground(Color.white);
	        }

       }
        return cell;
    }
}
