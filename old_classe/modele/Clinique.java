package modele;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Observable;
import java.util.Map.Entry;

import javax.swing.JFileChooser;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

public class Clinique extends Observable{

	private int charge_totale;
	private ArrayList<File> liste_factures;
	private ArrayList<File> liste_paiement; 
	private boolean existe_erreur; //vrai si paiement sans facture
	private ListeMedecin lm;
	private ArrayList<Facture> listeImpayesTotale;
	private HashMap<Paiement, ArrayList<Integer>> listeFacturesPossibles;
	private boolean afficherStats;
	private int plage;
	private Date debutStats;
	private Date finStats;
	private ArrayList<String> listeStatsVoulues;
	private JFileChooser fileChooser = new JFileChooser ();	//mettre le chooser ici permet de l'ouvrir sur le dernier dossier ouvert
	private boolean chargementFichierFacture;
	private boolean chargementFichierPaiement;

	private boolean afficherTousPaiements;
	private boolean afficherToutesFactures;
	private String message;
	private HashMap<File, HashMap<Integer, String>> lignesFacture; //sauvegarde les lignes non traitées des fichiers de facturation
	private HashMap<File, HashMap<Integer, String>> lignesPaiement; //sauvegarde les lignes non traitées des fichiers de paiement

	public HashMap<File, HashMap<Integer, String>> getLignesPaiement() {
		return lignesPaiement;
	}


	public void setLignesPaiement(
			HashMap<File, HashMap<Integer, String>> lignesPaiement) {
		this.lignesPaiement = lignesPaiement;
	}


	public HashMap<File, HashMap<Integer, String>> getLignesFacture() {
		return lignesFacture;
	}


	public void setLignesFacture(
			HashMap<File, HashMap<Integer, String>> lignesFacture) {
		this.lignesFacture = lignesFacture;
	}


	public Clinique(){
		this.lm = new ListeMedecin();
		this.listeImpayesTotale = new ArrayList<>();
		this.listeFacturesPossibles = new HashMap<>();
		this.listeStatsVoulues = new ArrayList<>();
		this.fileChooser.setMultiSelectionEnabled(true);
		this.liste_factures = new ArrayList<>();
		this.liste_paiement = new ArrayList<>();
		this.lignesFacture = new HashMap<>();
		this.lignesPaiement = new HashMap<>();
	}


	public void addMedecin(Medecin m){
		this.lm.add(m);
	}

	/**
	 * @return the lm
	 */
	public ListeMedecin getLm() {
		return lm;
	}

	/**
	 * @param lm the lm to set
	 */
	public void setLm(ListeMedecin lm) {
		this.lm = lm;
		this.update();
	}

	/**
	 * @return the listeImpayesTotale
	 */
	public ArrayList<Facture> getListeImpayesTotale() {
		for(int i = 0 ; i < this.lm.size() ; i++){
			ArrayList<Facture> ajout = this.lm.get(i).getListeImpayesTotale();
			for(int j = 0 ; j < ajout.size() ; j++){
				this.listeImpayesTotale.add(ajout.get(j));
			}
		}
		return listeImpayesTotale;
	}

	/**
	 * @param listeImpayesTotale the listeImpayesTotale to set
	 */
	public void setListeImpayesTotale(ArrayList<Facture> listeImpayesTotale) {
		this.listeImpayesTotale = listeImpayesTotale;
		this.update();
	}

	public int getCharge_totale() {
		return charge_totale;
	}

	public void setCharge_totale(int charge_totale) {
		this.charge_totale = charge_totale;
		this.update();
	}

	public ArrayList<File> getListe_facture() {
		return liste_factures;
	}

	public void setListe_facture(ArrayList<File> liste_facture) {
		this.liste_factures = liste_facture;
		this.update();
	}

	public void addFacture(File file){
		if(!this.liste_factures.contains(file)){
			this.liste_factures.add(file);
		}
	}

	public ArrayList<File> getListe_paiement() {
		return liste_paiement;
	}

	public void setListe_paiement(ArrayList<File> liste_paiement) {
		this.liste_paiement = liste_paiement;
		this.update();
	}

	public void addPaiement(File file){
		if(!this.liste_paiement.contains(file)){
			this.liste_paiement.add(file);
		}
	}

	public boolean isExiste_erreur() {
		return existe_erreur;
	}

	public void setExiste_erreur(boolean existe_erreur) {
		this.existe_erreur = existe_erreur;
		this.update();
	}

	//on cherche les impay�s
	public void chercheImpayes(int plage){
		this.lm.chercheImpayes(plage);
		this.update();

	}

	//on cherche les erreurs
	public void chercheErreurs(){
		this.lm.chercheErreurPaiement();
		this.update();
	}

	public void run(){	
		if(this.chargementFichierFacture && this.chargementFichierPaiement){
			this.XLStoCSV(); //on parse les fichiers de facturation
			this.parseFactures();
			this.parsePaiements();
		}
		StringBuilder sb = new StringBuilder();
		this.chercheImpayes(this.plage); //permet de placer les bool�ens estPay� et estFactur�
		this.chercheErreurs(); //r�cup�re la liste des factures pouvant potentiellement correspondre � un paiement sans facture
		//parcours de la hashMap
		for(int i = 0 ; i < this.lm.size() ; i++){
			sb.append("\nDocteur : "+this.lm.get(i).getNom()+"\n");
			if(this.listeImpayesTotale.size() == 0){
				sb.append("Tout a bien été payé");
			}else{
				for(int j = 0 ; j < this.lm.get(i).getListeImpayesTotale().size() ; j++){
					sb.append("La facture numéro "+this.listeImpayesTotale.get(j).getNum()+" n'a pas été payée\n");
				}
			}
			for(Entry<Paiement, ArrayList<Integer>> entry : this.lm.get(i).getListeFacturesPossibles().entrySet()) {
				Paiement cle = entry.getKey();
				ArrayList<Integer> valeur = entry.getValue();
				sb.append("\nERREUR : présence de paiement sans facture.\n");
				sb.append("\n\nLe paiement au nom de "+cle.getNomBenef()+" et au montant de "+cle.getMontantTotal()+" peut correspondre aux factures :\n");
				//pour chaque cl� (c'est � dire chaque paiement sans facture) on affiche les factures pouvant correspondre	
				if(valeur.size() == 0){
					sb.append("\nAucune correspondance trouvée. \n");
				}else{
					for(int j = 0 ; j < valeur.size() ; j++){
						sb.append("\t numéro "+valeur.get(j).intValue()+" \n");
					}
				}
			}
			sb.append("\n*************************************************************\n");
		}

		this.message = sb.toString();
	}

	public String getMessage() {
		return message;
	}


	public void setMessage(String message) {
		this.message = message;
	}


	private void parseFactures() {
		this.listeImpayesTotale = new ArrayList<>();
		this.listeFacturesPossibles = new HashMap<>();
		File f;
		int numLigne;
		boolean indicateursOK = false;
		String prenomClientEnCours = null;
		String nomClientEnCours = null;

		for (int i = 0 ; i < this.liste_factures.size() ; i++){ //pour chaque fichier de facturation
			HashMap<Integer, String> lignesASauvegarder = new HashMap<Integer, String>();
			this.lignesFacture.put(this.liste_factures.get(i), lignesASauvegarder);
			numLigne = -1;
			//il faut réinitialiser les flags pour chaque fichier
			int dossier = -1, nomPatient = -1, prenomPatient = -1, noFacture = -1, dateFacture = -1, dateActe = -1, acte = -1, caisse = -1, mutuelle = -1, assure = -1;
			Medecin medecinEnCours = null;
			Facture factureEnCours = null;
			indicateursOK = false;
			f = this.liste_factures.get(i);
			BufferedReader lecteurAvecBuffer = null;
			String ligne;


			try{
				lecteurAvecBuffer = new BufferedReader(new FileReader(f));
			}catch(FileNotFoundException exc){
				System.out.println("Erreur d'ouverture");
			}
			try{
				while ((ligne = lecteurAvecBuffer.readLine()) != null){
					numLigne ++;
					boolean traite = false;
					String[] morceaux = ligne.split(";", -1);
					if(!indicateursOK){ //�a ne sert � rien de rerentrer dans cette partie si les indicateurs sont d�j� plac�s pour ce fichier
						for (int j = 0 ; j < morceaux.length ; j++){ //on fait le tour des morceaux pour placer les variables FLAG
							String temp = Normalizer.normalize(morceaux[j], Normalizer.Form.NFD); // pour �viter les probl�mes avec les accents
							temp = temp.replaceAll("[^\\p{ASCII}]", "");
							if(morceaux[j].toLowerCase().contains("dossier n")){  //je n'utilise pas de switch pour �tre s�r de ne pas avoir de probl�me avec la version de java
								dossier = j;
							}else if(temp.toLowerCase().contains("prenom")){
								prenomPatient = j;
							}else if(morceaux[j].toLowerCase().contains("nom")){
								nomPatient = j;
							}else if(morceaux[j].toLowerCase().contains("date facture")){
								dateFacture = j;
							}else if(morceaux[j].toLowerCase().contains("facture")){
								noFacture = j;
							}else if(morceaux[j].toLowerCase().contains("date acte")){
								dateActe = j;
							}else if(morceaux[j].toLowerCase().contains("acte")){
								acte = j;
							}else if(morceaux[j].toLowerCase().contains("caisse")){
								caisse = j;
							}else if(morceaux[j].toLowerCase().contains("mutuelle")){
								mutuelle = j;
							}else if(temp.toLowerCase().contains("assure")){
								assure = j;
							}
						}
						if(dossier != 1 && nomPatient != -1 && prenomPatient != -1 && noFacture != -1 && dateActe != -1 && dateFacture != -1 && acte != -1 && caisse != -1 && mutuelle != -1 && assure != -1){
							indicateursOK = true;
						}
					}
					//maintenant il faut cr�er les m�decins
					boolean estMedecin = false;
					int compteurVide = 0;
					for(int j = 0 ; j < morceaux.length ; j++){
						if(morceaux[j].isEmpty()){
							compteurVide++;
						}
						if(morceaux[j].toLowerCase().contains("date")){//si on repere le mot date, alors ce ne peut �tre un m�decin
							compteurVide = 0;
							break;
						}
					}
					if(compteurVide > 8){ //on peut penser que s'il y a au moins 7 colonnes vides et que ce n'est pas le total d'un dossier, c'est un m�decin
						Medecin med = new Medecin();
						med.setNom(morceaux[2]);
						try{
							med.setNum(Integer.parseInt(morceaux[0]));
						}catch(NumberFormatException e){
							lignesASauvegarder.put(numLigne, ligne);
							continue;
						}
						estMedecin = true;
						medecinEnCours = med;
						traite = true;
						med.setNumLigne(numLigne);
						med.setLigne(ligne);
						this.lm.add(med);
					}else{
						//maintenant il faut cr�er les factures en fonction des indicateurs
						if(!estMedecin && indicateursOK && !ligne.toLowerCase().contains("total praticien") && !ligne.toLowerCase().contains("total général") && !ligne.toLowerCase().contains("total dossier") && !ligne.toLowerCase().contains("clinique louis pasteur")){ //si on n'a pas cr�� de m�decin
							try{
								Integer.parseInt(morceaux[dossier]);//si la colonne num�ro de dossier contient un entier
							}catch(NumberFormatException e){
								if(!morceaux[dossier].isEmpty()){
									lignesASauvegarder.put(numLigne, ligne);
									continue; //on passe � la ligne suivante
								}
							}
							if(factureEnCours != null && !morceaux[caisse].isEmpty() && morceaux[noFacture].isEmpty()){//si on est l� et que la colonne est vide, �a veut dire qu'il s'agit de la m�me facture que la ligne pr�c�dente		
								if(Float.parseFloat(morceaux[caisse]) >= 0){//ajout d'un nouveau montant à une facture existante
									Montant m = new Montant(Float.parseFloat(morceaux[caisse]) + Float.parseFloat(morceaux[mutuelle]), false, numLigne);
									m.setNature(morceaux[acte]);
									m.setFact(factureEnCours);
									m.setLigne(ligne);
									m.setColonneNature(acte);
									m.setColonneMontant(caisse, mutuelle);
									factureEnCours.addMontant(m);
									traite = true;
								}else{
									lignesASauvegarder.put(numLigne, ligne);
									continue;
								}
							}else{
								if(medecinEnCours.containsFacture(morceaux[noFacture])){//ajout d'un nouveau montant à une facture existante
									Facture f2 = medecinEnCours.getFacture(morceaux[noFacture]);
									Montant m = new Montant(Float.parseFloat(morceaux[caisse]) + Float.parseFloat(morceaux[mutuelle]), false, numLigne);
									f2.addMontant(m);
									m.setFact(factureEnCours);
									m.setLigne(ligne);
									m.setColonneNature(acte);
									m.setColonneMontant(caisse, mutuelle);
									factureEnCours = f2;
									traite = true;
								}else{
									if(!morceaux[nomPatient].isEmpty()){
										prenomClientEnCours = morceaux[prenomPatient];
										nomClientEnCours = morceaux[nomPatient];
									}

									if( !morceaux[caisse].isEmpty() && Float.parseFloat(morceaux[caisse]) >= 0){
										Facture fact = new Facture();
										fact.setNomClient(nomClientEnCours);
										fact.setPrenomClient(prenomClientEnCours);
										fact.setNature(morceaux[acte]);
										fact.setColonneNature(acte);
										fact.setColonneNomClient(nomPatient, prenomPatient);
										fact.setColonneNum(noFacture);
										SimpleDateFormat test = new SimpleDateFormat("dd/MM/yyyy"); //attention au format !!!
										Date d = null;
										Date d1 = null;
										try {
											d = test.parse(morceaux[dateFacture]);
											fact.setDateFacturation(d);
											d1 = test.parse(morceaux[dateActe]);
											fact.setDateActe(d1);
										} catch (ParseException e1) {
											// TODO Auto-generated catch block
											e1.printStackTrace();
										}
										fact.setNum(Integer.parseInt(morceaux[noFacture]));
										Montant m = new Montant(Float.parseFloat(morceaux[caisse]) + Float.parseFloat(morceaux[mutuelle]), false, numLigne);
										m.setLigne(ligne);
										m.setNumLigne(numLigne);
										fact.addMontant(m); //montant � rembourser = somme des colonnes caisse + mutuelle
										factureEnCours = fact;
										medecinEnCours.addFacture(fact);
										traite = true;
									}else{
										lignesASauvegarder.put(numLigne, ligne);
										continue;
									}
								}
							}
						}else{
							lignesASauvegarder.put(numLigne, ligne);
							continue;
						}
					}
					if(!traite){
						lignesASauvegarder.put(numLigne, ligne);
						continue;
					}
				}
				lecteurAvecBuffer.close();
			}catch (IOException e){
				e.printStackTrace();
			}
		}
	}

	private void parsePaiements() {
		File f;
		int numLigne;
		boolean indicateursOK = false;

		for (int i = 0 ; i < this.liste_paiement.size() ; i++){ //pour chaque fichier de paiements
			HashMap<Integer, String> lignesASauvegarder = new HashMap<Integer, String>();
			this.lignesPaiement.put(this.liste_factures.get(i), lignesASauvegarder);
			numLigne = -1;
			//il faut réinitialiser les FLAGS pour chaque fichier
			int nomPatient = -1,  noFacture = -1, datePaiement = -1, nature = -1, dateActe = -1, montant = -1;
			Medecin medecinEnCours = null;
			indicateursOK = false;
			f = this.liste_paiement.get(i);
			BufferedReader lecteurAvecBuffer = null;
			String ligne;

			try{
				final InputStream fileReader = new FileInputStream(f);
				// Création d'un lecteur 
				Reader reader = new InputStreamReader(fileReader, "ISO-8859-1"); 
				// Création d'un lecteur plus intelligent. Il lira ligne par ligne au lieu de caractère par caractère 
				lecteurAvecBuffer = new BufferedReader(reader);
			}catch(FileNotFoundException exc){
				System.out.println("Erreur d'ouverture");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try{
				while ((ligne = lecteurAvecBuffer.readLine()) != null){
					ligne = ligne.replaceAll(",", ".");
					numLigne++;
					String[] morceaux = ligne.split(";", -1);
					//d'abord il faut savoir avec quel m�decin on travaille
					if(morceaux[0].toLowerCase().contains("destinataire")){
						String temp[] = morceaux[0].split(":");
						//System.out.println(temp[1]);
						if(this.lm.contains(temp[1])){
							medecinEnCours = this.lm.get(temp[1]);
						}else{
							medecinEnCours = new Medecin();
							medecinEnCours.setNom(temp[1]);
							this.lm.add(medecinEnCours);
							medecinEnCours.setNumLigne(numLigne);
							medecinEnCours.setLigne(ligne);
						}
					}

					if(!indicateursOK){ //�a ne sert � rien de rerentrer dans cette partie si les indicateurs sont d�j� plac�s pour ce fichier
						for (int j = 0 ; j < morceaux.length ; j++){ //on fait le tour des morceaux pour placer les variables FLAG
							String temp = Normalizer.normalize(morceaux[j], Normalizer.Form.NFD); // pour �viter les probl�mes avec les accents
							temp = temp.replaceAll("[^\\p{ASCII}]", "");
							if(temp.toLowerCase().contains("beneficiaire")){
								nomPatient = j;
							}else if(temp.toLowerCase().contains("paiement")){
								datePaiement = j;
							}else if(temp.toLowerCase().contains("facture")){
								noFacture = j;
							}else if(temp.toLowerCase().contains("nature")){
								nature = j;
							}else if(temp.toLowerCase().contains("acte")){
								dateActe = j;
							}else if(temp.toLowerCase().contains("montant")){
								montant = j;
							}
						}
						if(nomPatient != -1 && noFacture != -1 && dateActe != -1 && datePaiement != -1 && nature != -1 && montant != -1){
							indicateursOK = true;
						}
						lignesASauvegarder.put(numLigne, ligne);
					}
					//maintenant il faut cr�er les factures en fonction des indicateurs
					if(indicateursOK && !ligne.toLowerCase().contains("total") && morceaux.length >= 7 && !morceaux[dateActe].isEmpty()){ //si on n'a pas cr�� de m�decin
						if(!morceaux[noFacture].equals("SNF") && !morceaux[noFacture].contains("facture") && this.lm.containsPaiement(medecinEnCours, morceaux[noFacture])){
							Paiement p = this.lm.getPaiement(medecinEnCours, morceaux[noFacture]);
							Montant m = new Montant(Float.parseFloat(morceaux[montant]), false, numLigne);
							m.setNature(morceaux[nature]);
							m.setPaiement(p);
							m.setColonneMontant(montant, -1);
							m.setColonneNature(nature);
							p.addMontant(m);
							m.setNumLigne(numLigne);
							m.setLigne(ligne);
							SimpleDateFormat test = new SimpleDateFormat("dd/MM/yyyy"); //attention au format !!!
							Date d = null;
							Date d1 = null;
							try {
								d = test.parse(morceaux[datePaiement]);
								p.setDatePaiement(d);
								d1 = test.parse(morceaux[dateActe]);
								p.setDateActe(d1);
							} catch (ParseException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}else{
							try{
								Float.parseFloat(morceaux[montant]);//si la colonne num�ro de dossier contient un entier
							}catch(NumberFormatException e){
								if(!morceaux[montant].isEmpty()){
									lignesASauvegarder.put(numLigne, ligne);
									continue; //on passe � la ligne suivante
								}
							}
							String nomClientEnCours = morceaux[nomPatient];
							Paiement pai = new Paiement();
							pai.setNomBenef(nomClientEnCours);
							pai.setNature(morceaux[nature]);
							pai.setColonneNature(nature);
							pai.setColonneNom(nomPatient);
							
							SimpleDateFormat test = new SimpleDateFormat("dd/MM/yyyy"); //attention au format !!!
							Date d = null;
							Date d1 = null;
							try {
								d = test.parse(morceaux[datePaiement]);
								pai.setDatePaiement(d);
								d1 = test.parse(morceaux[dateActe]);
								pai.setDateActe(d1);
							} catch (ParseException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							if(!ligne.contains("SNF")){
								pai.setNumFact(Integer.parseInt(morceaux[noFacture]));
							}else{
								pai.setNumFact(0);
								pai.setNomBenef("???????");
							}
							//Il faut taiter les SNF
							Montant m = new Montant(Float.parseFloat(morceaux[montant]), false, numLigne);
							pai.addMontant(m);
							m.setPaiement(pai);
							m.setLigne(ligne);
							m.setNature(morceaux[nature]);
							m.setNumLigne(numLigne);
							m.setColonneMontant(montant, -1);
							m.setColonneNature(nature);
							medecinEnCours.addPaiement(pai);
						}
					}else{
						lignesASauvegarder.put(numLigne, ligne);
					}
				}
				lecteurAvecBuffer.close();
			}catch (IOException e){
				e.printStackTrace();
			}
		}
	}

	public void setAfficherStats(boolean b) {
		this.afficherStats = b;
		this.update();
	}

	/**
	 * @return the listeFacturesPossibles
	 */
	public HashMap<Paiement, ArrayList<Integer>> getListeFacturesPossibles() {
		return listeFacturesPossibles;
	}

	/**
	 * @param listeFacturesPossibles the listeFacturesPossibles to set
	 */
	public void setListeFacturesPossibles(HashMap<Paiement, ArrayList<Integer>> listeFacturesPossibles) {
		this.listeFacturesPossibles = listeFacturesPossibles;
		this.update();
	}

	/**
	 * @return the afficherStats
	 */
	public boolean isAfficherStats() {
		return afficherStats;
	}

	/**
	 * Mets � jour les vues
	 */
	public void update() {
		this.setChanged();
		this.notifyObservers();
	}

	public int getPlage() {
		return plage;
	}

	public void setPlage(int plage) {
		this.plage = plage;
		this.update();
	}

	public int calculDiffDates(Date d1, Date d2){
		return (int) ((d2.getTime() - d1.getTime()) / (24 * 60 * 60 * 1000));
	}

	public Date getDebutStats() {
		return debutStats;
	}

	public void setDebutStats(Date debutStats) {
		this.debutStats = debutStats;
		this.update();
	}

	public Date getFinStats() {
		return finStats;
	}

	public void setFinStats(Date finStats) {
		this.finStats = finStats;
		this.update();
	}

	public ArrayList<String> getListeStatsVoulues() {
		return listeStatsVoulues;
	}

	public void setListeStatsVoulues(ArrayList<String> listeStatsVoulues) {
		this.listeStatsVoulues = listeStatsVoulues;
		this.update();
	}

	public void addStatsVoulue(String s){
		this.listeStatsVoulues.add(s);
	}

	public void removeStatsVoulue(String s){
		this.listeStatsVoulues.remove(s);
	}

	public JFileChooser getFileChooser() {
		return this.fileChooser;
	}

	public void XLStoCSV() {
		for (int i = 0 ; i < this.liste_factures.size() ; i++){
			String fileName = this.liste_factures.get(i).getAbsolutePath();
			//on r�cup�re l'extension
			String extension = "";
			int j = fileName.lastIndexOf(".");
			if (j > 0) {
				extension = fileName.substring(j+1);
			}
			//si c'est un xls alors on le transforme en csv avant de le traiter
			if(extension.equals("xls")){
				// For storing data into CSV files
				StringBuffer data = new StringBuffer();
				try 
				{
					String newName = this.liste_factures.get(i).getAbsolutePath();//on renomme le lfichier avec un .csv
					int k = fileName.lastIndexOf(".");
					if (k > 0) {
						newName = fileName.substring(0, k)+".csv";
					}

					FileOutputStream fos = new FileOutputStream(newName);

					// Get the workbook object for XLS file
					HSSFWorkbook workbook = new HSSFWorkbook(new FileInputStream(fileName));
					// Get first sheet from the workbook
					HSSFSheet sheet = workbook.getSheetAt(0);
					Cell cell;
					Row row;

					// Iterate through each rows from first sheet
					Iterator<Row> rowIterator = sheet.iterator();
					while (rowIterator.hasNext()) 
					{
						row = rowIterator.next();
						// For each row, iterate through each columns
						Iterator<Cell> cellIterator = row.cellIterator();
						while (cellIterator.hasNext()) 
						{
							cell = cellIterator.next();

							switch (cell.getCellType()) 
							{
							case Cell.CELL_TYPE_BOOLEAN:
								data.append(cell.getBooleanCellValue() + ";");
								break;

							case Cell.CELL_TYPE_NUMERIC:
								String temp = Normalizer.normalize(cell.toString(), Normalizer.Form.NFD); // pour �viter les probl�mes avec les accents
								temp = temp.replaceAll("[^\\p{ASCII}]", "");
								if(temp.toString().contains("janv")){
									data.append(cell.toString().replaceAll("-janv.-", "/01/") + ";");
								}else if(temp.toString().contains("fev")){
									data.append(cell.toString().replaceAll("-févr.-", "/02/") + ";");
								}else if(temp.toString().contains("mars")){
									data.append(cell.toString().replaceAll("-mars-", "/03/") + ";");
								}else if(temp.toString().contains("avr")){
									data.append(cell.toString().replaceAll("-avr.-", "/04/") + ";");
								}else if(temp.toString().contains("mai")){
									data.append(cell.toString().replaceAll("-mai-", "/05/") + ";");
								}else if(temp.toString().contains("juin")){
									data.append(cell.toString().replaceAll("-juin-", "/06/") + ";");
								}else if(temp.toString().contains("juil")){
									data.append(cell.toString().replaceAll("-juil.-", "/07/") + ";");
								}else if(temp.toString().contains("aout")){
									data.append(cell.toString().replaceAll("-août-", "/08/") + ";");
								}else if(temp.toString().contains("sept")){
									data.append(cell.toString().replaceAll("-sept.-", "/09/") + ";");
								}else if(temp.toString().contains("oct")){
									data.append(cell.toString().replaceAll("-oct.-", "/10/") + ";");
								}else if(temp.toString().contains("nov")){
									data.append(cell.toString().replaceAll("-nov.-", "/11/") + ";");
								}else if(temp.toString().contains("dec")){
									data.append(cell.toString().replaceAll("-déc.-", "/12/") + ";");
								}else{
									data.append(cell.toString() + ";");
								}
								break;

							case Cell.CELL_TYPE_STRING:
								data.append(cell.getStringCellValue() + ";");
								break;

							case Cell.CELL_TYPE_BLANK:
								data.append("" + ";");
								break;

							default:
								data.append(cell + ";");
							}      
						}
						data.append('\n');
					}
					File newFile = new File(newName);
					fos.write(data.toString().getBytes());
					fos.close();
					this.liste_factures.set(i, newFile);//on met � jour la liste de fichiers en remplacant le xls par le csv
				}
				catch (FileNotFoundException e) 
				{
					e.printStackTrace();
				}
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}else if (!extension.equals("csv")){//si ce n'est ni un xls ni un csv alors on ne sait pas le traiter
				//erreur
			}
		}
		//c'est moche mais on fait pareil pour les fichiers de paiements
		for (int i = 0 ; i < this.liste_paiement.size() ; i++){
			String fileName = this.liste_paiement.get(i).getAbsolutePath();
			//on r�cup�re l'extension
			String extension = "";
			int j = fileName.lastIndexOf(".");
			if (j > 0) {
				extension = fileName.substring(j+1);
			}
			//si c'est un xls alors on le transforme en csv avant de le traiter
			if(extension.equals("xls")){
				// For storing data into CSV files
				StringBuffer data = new StringBuffer();
				try 
				{
					String newName = this.liste_paiement.get(i).getAbsolutePath();//on renomme le lfichier avec un .csv
					int k = fileName.lastIndexOf(".");
					if (k > 0) {
						newName = fileName.substring(0, k)+".csv";
					}

					FileOutputStream fos = new FileOutputStream(newName);

					// Get the workbook object for XLS file
					HSSFWorkbook workbook = new HSSFWorkbook(new FileInputStream(fileName));
					// Get first sheet from the workbook
					HSSFSheet sheet = workbook.getSheetAt(0);
					Cell cell;
					Row row;

					// Iterate through each rows from first sheet
					Iterator<Row> rowIterator = sheet.iterator();
					while (rowIterator.hasNext()) 
					{
						row = rowIterator.next();
						// For each row, iterate through each columns
						Iterator<Cell> cellIterator = row.cellIterator();
						while (cellIterator.hasNext()) 
						{
							cell = cellIterator.next();

							switch (cell.getCellType()) 
							{
							case Cell.CELL_TYPE_BOOLEAN:
								data.append(cell.getBooleanCellValue() + ";");
								break;

							case Cell.CELL_TYPE_NUMERIC:
								String temp = Normalizer.normalize(cell.toString(), Normalizer.Form.NFD); // pour �viter les probl�mes avec les accents
								temp = temp.replaceAll("[^\\p{ASCII}]", "");
								if(temp.toString().contains("janv")){
									data.append(cell.toString().replaceAll("-janv.-", "/01/") + ";");
								}else if(temp.toString().contains("fev")){
									data.append(cell.toString().replaceAll("-févr.-", "/02/") + ";");
								}else if(temp.toString().contains("mars")){
									data.append(cell.toString().replaceAll("-mars-", "/03/") + ";");
								}else if(temp.toString().contains("avr")){
									data.append(cell.toString().replaceAll("-avr.-", "/04/") + ";");
								}else if(temp.toString().contains("mai")){
									data.append(cell.toString().replaceAll("-mai-", "/05/") + ";");
								}else if(temp.toString().contains("juin")){
									data.append(cell.toString().replaceAll("-juin-", "/06/") + ";");
								}else if(temp.toString().contains("juil")){
									data.append(cell.toString().replaceAll("-juil.-", "/07/") + ";");
								}else if(temp.toString().contains("aout")){
									data.append(cell.toString().replaceAll("-août-", "/08/") + ";");
								}else if(temp.toString().contains("sept")){
									data.append(cell.toString().replaceAll("-sept.-", "/09/") + ";");
								}else if(temp.toString().contains("oct")){
									data.append(cell.toString().replaceAll("-oct.-", "/10/") + ";");
								}else if(temp.toString().contains("nov")){
									data.append(cell.toString().replaceAll("-nov.-", "/11/") + ";");
								}else if(temp.toString().contains("dec")){
									data.append(cell.toString().replaceAll("-déc.-", "/12/") + ";");
								}else{
									data.append(cell.toString() + ";");
								}
								break;

							case Cell.CELL_TYPE_STRING:
								data.append(cell.getStringCellValue() + ";");
								break;

							case Cell.CELL_TYPE_BLANK:
								data.append("" + ";");
								break;

							default:
								data.append(cell + ";");
							}      
						}
						data.append('\n');
					}
					File newFile = new File(newName);
					fos.write(data.toString().getBytes());
					fos.close();
					this.liste_paiement.set(i, newFile);//on met � jour la liste de fichiers en remplacant le xls par le csv
				}
				catch (FileNotFoundException e) 
				{
					e.printStackTrace();
				}
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}else if (!extension.equals("csv")){//si ce n'est ni un xls ni un csv alors on ne sait pas le traiter
				//erreur
			}
		}
	}

	public ArrayList<File> getListe_factures() {
		return liste_factures;
	}

	public void setListe_factures(ArrayList<File> liste_factures) {
		this.liste_factures = liste_factures;
		this.update();
	}

	public boolean isAfficherTousPaiements() {
		return afficherTousPaiements;
	}

	public void setAfficherTousPaiements(boolean afficherTousPaiements) {
		this.afficherTousPaiements = afficherTousPaiements;
		this.update();
	}

	public boolean isAfficherToutesFactures() {
		return afficherToutesFactures;
	}

	public void setAfficherToutesFactures(boolean afficherToutesFactures) {
		this.afficherToutesFactures = afficherToutesFactures;
		this.update();
	}

	public void setFileChooser(JFileChooser fileChooser) {
		this.fileChooser = fileChooser;
		this.update();
	}

	public boolean isChargementFichierPaiement() {
		return chargementFichierPaiement;
	}

	public void setChargementFichierPaiement(boolean chargementFichierPaiement) {
		this.chargementFichierPaiement = chargementFichierPaiement;
		this.update();
	}

	public boolean isChargementFichierFacture() {
		return chargementFichierFacture;
	}

	public void setChargementFichierFacture(boolean chargementFichierFacture) {
		this.chargementFichierFacture = chargementFichierFacture;
		this.update();
	}

	//pour effectuer un nouveau recoupement
	public void reset() {
		this.liste_factures = new ArrayList<>();
		this.liste_paiement = new ArrayList<>();
	}


	public void exporterFactures(String path) {
		//on va chercher le chemin et le nom du fichier et on me tout ca dans un String
		String adressedufichier = path +".csv"; //TODO remplacer test par le nom du fichier

		//on met try si jamais il y a une exception
		try
		{
			/**
			 * BufferedWriter a besoin d un FileWriter, 
			 * les 2 vont ensemble, on donne comme argument le nom du fichier
			 * true signifie qu on ajoute dans le fichier (append), on ne marque pas par dessus 

			 */
			FileWriter fw = new FileWriter(adressedufichier);

			// le BufferedWriter output auquel on donne comme argument le FileWriter fw cree juste au dessus
			BufferedWriter output = new BufferedWriter(fw);
			Medecin medecinEnCours = null;
			Facture factureEnCours;
			Montant montantEnCours;
			int numLigne = 0;
			boolean ecriture = false;
			for(Entry<File, HashMap<Integer, String>> entry : this.lignesFacture.entrySet()) {//pour chaque fichier
				File cle = entry.getKey();
				HashMap<Integer, String> valeur = entry.getValue();//on récupère la liste des lignes à modifier
				do{
					ecriture = false;
					if(valeur.containsKey(numLigne)){
						//on écrit la ligne correspondante
						output.write(valeur.get(numLigne)+"\n");
						numLigne++;
						ecriture = true;
					}else{
						//System.out.println("dans le else1");
						for(int i = 0 ; i < this.lm.size() ; i++){
							medecinEnCours = this.lm.get(i);
							if(medecinEnCours.getNumLigne() == numLigne){
								//on écrit la ligne correspondante
								output.write(medecinEnCours.getLigne()+"\n");
								numLigne++;
								ecriture = true;
								break;
							}else{
								for(int j = 0 ; j < medecinEnCours.getLf().size() ; j++){
									factureEnCours = medecinEnCours.getLf().get(j);
									for (int k = 0 ; k < factureEnCours.getLmt().size() ; k++){
										montantEnCours = factureEnCours.getLmt().get(k);
										if(montantEnCours.getNumLigne() == numLigne){
											//on écrit la ligne correspondante
											output.write(montantEnCours.getLigne()+"\n");
											numLigne++;
											ecriture = true;
											//break;
										}
									}
								}
							}		
						}

					}
				}while(ecriture);
			}

			//on marque dans le fichier ou plutot dans le BufferedWriter qui sert comme un tampon(stream)

			//on peut utiliser plusieurs fois methode write

			//output.flush();
			//ensuite flush envoie dans le fichier, ne pas oublier cette methode pour le BufferedWriter

			output.close();
			//et on le ferme
			System.out.println("fichier créé");
		}
		catch(IOException ioe){
			System.out.print("Erreur : ");
			ioe.printStackTrace();
		}
	}
	
	public void exporterPaiements(){
		//on va chercher le chemin et le nom du fichier et on me tout ca dans un String
				String adressedufichier = System.getProperty("user.dir") + "/"+ "testP.csv";
				System.out.println(System.getProperty("user.dir") + "/"+ "test.csv");

				//on met try si jamais il y a une exception
				try
				{
					/**
					 * BufferedWriter a besoin d un FileWriter, 
					 * les 2 vont ensemble, on donne comme argument le nom du fichier
					 * true signifie qu on ajoute dans le fichier (append), on ne marque pas par dessus 

					 */
					FileWriter fw = new FileWriter(adressedufichier);

					// le BufferedWriter output auquel on donne comme argument le FileWriter fw cree juste au dessus
					BufferedWriter output = new BufferedWriter(fw);
					Medecin medecinEnCours = null;
					Paiement paiementEnCours;
					Montant montantEnCours;
					int numLigne = 0;
					boolean ecriture = false;
					for(Entry<File, HashMap<Integer, String>> entry : this.lignesPaiement.entrySet()) {//pour chaque fichier
						File cle = entry.getKey();
						HashMap<Integer, String> valeur = entry.getValue();//on récupère la liste des lignes à modifier
						do{
							ecriture = false;
							if(valeur.containsKey(numLigne)){
								System.out.println("dedzn");
								//on écrit la ligne correspondante
								output.write(valeur.get(numLigne)+"\n");
								numLigne++;
								ecriture = true;
							}else{
								System.out.println("dedzn2");
								//System.out.println("dans le else1");
								for(int i = 0 ; i < this.lm.size() ; i++){
									medecinEnCours = this.lm.get(i);
									System.out.println(medecinEnCours.getNumLigne());
									if(medecinEnCours.getNumLigne() == numLigne){
										System.out.println("dedzn3");
										//on écrit la ligne correspondante
										output.write(medecinEnCours.getLigne()+"\n");
										numLigne++;
										ecriture = true;
										break;
									}else{
										System.out.println("dedzn4");
										for(int j = 0 ; j < medecinEnCours.getLp().size() ; j++){
											paiementEnCours = medecinEnCours.getLp().get(j);
											for (int k = 0 ; k < paiementEnCours.getLmt().size() ; k++){
												montantEnCours = paiementEnCours.getLmt().get(k);
												if(montantEnCours.getNumLigne() == numLigne){
													//on écrit la ligne correspondante
													output.write(montantEnCours.getLigne()+"\n");
													numLigne++;
													ecriture = true;
													//break;
												}
											}
										}
									}		
								}

							}
						}while(ecriture);
					}

					//on marque dans le fichier ou plutot dans le BufferedWriter qui sert comme un tampon(stream)

					//on peut utiliser plusieurs fois methode write

					output.flush();
					//ensuite flush envoie dans le fichier, ne pas oublier cette methode pour le BufferedWriter

					output.close();
					//et on le ferme
					System.out.println("fichier créé");
				}
				catch(IOException ioe){
					System.out.print("Erreur : ");
					ioe.printStackTrace();
				}
	}


	public Number getNbImpayesParMois(int moisEnCours, int anneeEnCours) {
		int m = moisEnCours;
		int nb = 0;
		Date dateEnCours = null;
		for(int i = 0 ; i < this.lm.size() ; i++){//on met à jour la liste
			ArrayList<Facture> ajout = this.lm.get(i).getListeImpayesTotale();
			for(int j = 0 ; j < ajout.size() ; j++){
				if(!this.listeImpayesTotale.contains(ajout.get(j))){
					this.listeImpayesTotale.add(ajout.get(j));
				}
			}
		}
		for(int i = 0 ; i < this.listeImpayesTotale.size() ; i ++){
			dateEnCours = this.listeImpayesTotale.get(i).getDateFacturation();
			if(dateEnCours.getMonth()+1 == m && dateEnCours.getYear()+1900 == anneeEnCours){
				nb++;
			}
		}
		return nb;
	}
}

