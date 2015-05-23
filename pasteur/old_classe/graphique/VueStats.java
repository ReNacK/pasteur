package graphique;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.ui.RefineryUtilities;

import com.itextpdf.awt.PdfGraphics2D;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;
import java.awt.GridLayout;

import modele.Clinique;
import modele.Facture;
import modele.Medecin;
import modele.Paiement;

public class VueStats extends JFrame{
	
	private Clinique cli;
	private JFrame fen;
	

	public VueStats(Clinique cli){
		this.fen = new JFrame("Statistiques");
		this.cli = cli;
		/////////////METTRE UN GRIDLAYOUT///////////////////////////
		Date debut = this.cli.getDebutStats();
		Date fin = this.cli.getFinStats();
		JPanel pan = new JPanel();
                pan.setLayout(new GridLayout(3, 2, 0, 0));
//////////////////////CHARGE DE TRAVAIL PAR MEDECIN/////////////////////////////	
		JLabel titre1 = new JLabel("Charge de travail pour chaque médecin par mois");//titre
		
		//cr�ation des donn�es pour l'affichage
		DefaultCategoryDataset data = new DefaultCategoryDataset();
		Medecin medEnCours = null;
		for(int i = 0 ; i < this.cli.getLm().size() ; i++){
			medEnCours = this.cli.getLm().get(i);
			for (int anneeEnCours = debut.getYear()+1900 ; anneeEnCours <= fin.getYear()+1900; anneeEnCours++){
				for (int moisEnCours = 1 ; moisEnCours <= 12 ; moisEnCours++){
					if(anneeEnCours == debut.getYear()+1900 && moisEnCours < debut.getMonth()+1){
						//continue; //si la date est inf�rieure � la date de d�but
					}else if(anneeEnCours == fin.getYear()+1900 && moisEnCours > fin.getMonth()+1){
						//continue; //si la date est sup�rieure � la date de fin
					}else{
						data.addValue(medEnCours.nbFacturesParMois(moisEnCours,anneeEnCours), medEnCours.getNom(), moisEnCours +" "+anneeEnCours);
					}
				}
			}
		}
		JFreeChart chart = ChartFactory.createBarChart(
	            "Charge de travail pour chaque médecin par mois",         // chart title
	            "Mois",               // domain axis label
	            "Charge de travail",                  // range axis label
	            data,                  // data
	            PlotOrientation.VERTICAL, // orientation
	            true,                     // include legend
	            true,                     // tooltips?
	            false                     // URLs?
	        );
		ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(500, 270));
        pan.add(chartPanel);
        //pan.setContentPane(chartPanel);
//////////////////////////////////FIN CHARGE DE TRAVAIL//////////////////////////////
//////////////////////////////////DEBUT ENCOURS CLINIQUE/////////////////////////////////
        JLabel titre2 = new JLabel("Encours par mois pour la clinique");//titre
		
		//cr�ation des donn�es pour l'affichage
		DefaultCategoryDataset data2 = new DefaultCategoryDataset();
			for (int anneeEnCours = debut.getYear()+1900 ; anneeEnCours <= fin.getYear()+1900; anneeEnCours++){
				for (int moisEnCours = 1 ; moisEnCours <= 12 ; moisEnCours++){
					if(anneeEnCours == debut.getYear()+1900 && moisEnCours < debut.getMonth()+1){
						//continue; //si la date est inf�rieure � la date de d�but
					}else if(anneeEnCours == fin.getYear()+1900 && moisEnCours > fin.getMonth()+1){
						//continue; //si la date est sup�rieure � la date de fin
					}else{
						data2.addValue(this.cli.getNbImpayesParMois(moisEnCours, anneeEnCours), "Clinique", moisEnCours +" "+anneeEnCours);
					}
				}
			}
		JFreeChart chart2 = ChartFactory.createBarChart(
	            "Encours par mois pour la clinique",         // chart title
	            "Mois",               // domain axis label
	            "Encours pour la clinique",                  // range axis label
	            data2,                  // data
	            PlotOrientation.VERTICAL, // orientation
	            true,                     // include legend
	            true,                     // tooltips?
	            false                     // URLs?
	        );
		ChartPanel chartPanel2 = new ChartPanel(chart2);
        chartPanel2.setPreferredSize(new Dimension(500, 270));
        pan.add(chartPanel2);
///////////////////////////////////FIN ENCOURS CLINIQUE////////////////////////////
//////////////////////////////////DEBUT ENCOURS MEDECIN////////////////////////////
        JLabel titre3 = new JLabel("Encours par mois pour chaque médecin");//titre
		
		//cr�ation des donn�es pour l'affichage
		DefaultCategoryDataset data3 = new DefaultCategoryDataset();
		Medecin medEnCours3 = null;
		for(int i = 0 ; i < this.cli.getLm().size() ; i++){
			medEnCours3 = this.cli.getLm().get(i);
			for (int anneeEnCours = debut.getYear()+1900 ; anneeEnCours <= fin.getYear()+1900; anneeEnCours++){
				for (int moisEnCours = 1 ; moisEnCours <= 12 ; moisEnCours++){
					if(anneeEnCours == debut.getYear()+1900 && moisEnCours < debut.getMonth()+1){
						//continue; //si la date est inf�rieure � la date de d�but
					}else if(anneeEnCours == fin.getYear()+1900 && moisEnCours > fin.getMonth()+1){
						//continue; //si la date est sup�rieure � la date de fin
					}else{
						data3.addValue(medEnCours3.nbFacturesImapyesParMois(moisEnCours,anneeEnCours), medEnCours3.getNom(), moisEnCours +" "+anneeEnCours);
					}
				}
			}
		}
		JFreeChart chart3 = ChartFactory.createBarChart(
	            "Encours par mois pour chaque médecin",         // chart title
	            "Mois",               // domain axis label
	            "Encours par médecin",                  // range axis label
	            data3,                  // data
	            PlotOrientation.VERTICAL, // orientation
	            true,                     // include legend
	            true,                     // tooltips?
	            false                     // URLs?
	        );
		ChartPanel chartPanel3 = new ChartPanel(chart3);
        chartPanel3.setPreferredSize(new Dimension(500, 270));
        pan.add(chartPanel3);
/////////////////////////////////FIN ENCOURS MEDECIN//////////////////////////////
/////////////////////////////////DEBUT RATIO IMPAYES/TOTAL//////////////////////////
        int total = 0;
        int totalImp = 0;
        Medecin medecinEnCours4 = null;
        for (int i = 0 ; i < this.cli.getLm().size() ; i++){
        	medecinEnCours4 = this.cli.getLm().get(i);
        	totalImp = totalImp + medecinEnCours4.getListeImpayesTotale().size();
        	total = total + medecinEnCours4.getLf().size();
        }
        DefaultPieDataset pieDataset = new DefaultPieDataset();
        pieDataset.setValue("Impayés", totalImp);
        pieDataset.setValue("Payés", total - totalImp);

        JFreeChart pieChart = ChartFactory.createPieChart("Nombre de factures impayées",
        pieDataset, true, true, true);
        ChartPanel cPanel = new ChartPanel(pieChart);
        pan.add(cPanel); 
/////////////////////////////////FIN RATIO IMPAYES/TOTAL/////////////////////////////
/////////////////////////////////DEBUT DELAIS//////////////////////////////////////////////
        JLabel titre5 = new JLabel("Encours par mois pour chaque médecin");//titre
		
		//cr�ation des donn�es pour l'affichage
		DefaultCategoryDataset data5 = new DefaultCategoryDataset();
		Medecin medEnCours5 = null;
		int delaiFact = 0;
		int delaiPai = 0;
		Facture factureEnCours;
		int nbFact = 0;
		int nbPai = 0;
		Paiement paiementEnCours;
		for(int i = 0 ; i < this.cli.getLm().size() ; i++){
			medEnCours5 = this.cli.getLm().get(i);
			nbFact = nbFact + medEnCours5.getLf().size();
			for(int j = 0 ; j < medEnCours5.getLf().size() ; j++){
				factureEnCours = medEnCours5.getLf().get(j);
				delaiFact = delaiFact + factureEnCours.getDelai();
			}
			nbPai = nbPai + medEnCours5.getLp().size();
			for(int j = 0 ; j < medEnCours5.getLp().size() ; j++){
				paiementEnCours = medEnCours5.getLp().get(j);
				delaiPai = delaiPai + paiementEnCours.getDelai();
			}
			data5.addValue(delaiFact/nbFact, "Délai moyen entre acte et facturation", "Délais");
			data5.addValue(delaiPai/nbPai, "Délai moyen entre acte et paiement", "Délais");

		}
		JFreeChart chart5 = ChartFactory.createBarChart(
	            "Delais moyens entre acte et facturation",         // chart title
	            "Délais moyens en jours",               // domain axis label
	            "Jours",                  // range axis label
	            data5,                  // data
	            PlotOrientation.VERTICAL, // orientation
	            true,                     // include legend
	            true,                     // tooltips?
	            false                     // URLs?
	        );
		ChartPanel chartPanel5 = new ChartPanel(chart5);
        chartPanel5.setPreferredSize(new Dimension(500, 270));
        pan.add(chartPanel5);
/////////////////////////////////FIN DELAIS///////////////////////////////////////////////
/////////////////////////////////FIN AJOUT STATS///////////////////////////////        
        this.fen.add(pan); // on ajoute le panel � la JFrame
        this.fen.pack();
        RefineryUtilities.centerFrameOnScreen(this.fen);
        if(this.cli.isAfficherStats()){
        	this.fen.setVisible(true);
        }else{
        	try {
				this.createPdf("statistiques.pdf", pan);
			} catch (IOException | DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        this.cli.setAfficherStats(false); //on r�initialise le FLAG
	}
	
	public int calculDiffDates(Date d1, Date d2){
		return (int) ((d2.getTime() - d1.getTime()) / (24 * 60 * 60 * 1000));
	}

	
	public void createPdf(String filename, JPanel pan) throws IOException, DocumentException {
    	// step 1
        Document document = new Document(new Rectangle(pan.getWidth(), pan.getHeight()));
        // step 2
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filename));
        // step 3
        document.open();
        // step 4
        PdfContentByte canvas = writer.getDirectContent();
        Graphics2D g2 = new PdfGraphics2D(canvas, pan.getWidth(), pan.getHeight());
        pan.paint(g2);
        g2.dispose();
        // step 5
        document.close();
	}

	
}
