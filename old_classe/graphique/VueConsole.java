/**
 * 
 */
package graphique;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import modele.Clinique;

/**
 * @author Kevin
 *
 */
public class VueConsole extends JPanel implements Observer {
	
	private Clinique cli;
	private JTextArea jt;

	/**
	 * 
	 */
	public VueConsole(Clinique cli) {
	    
		this.cli = cli;
		this.jt = new JTextArea();
        this.jt.setEditable(false);
        this.setBackground(Color.white);
        this.add(this.jt);
        this.cli.addObserver(this);
	}

	/* (non-Javadoc)
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	@Override
	public void update(Observable o, Object arg) {
		this.jt.setText(this.cli.getMessage());
	}

}
