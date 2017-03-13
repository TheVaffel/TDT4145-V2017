package project;
import java.sql.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;  

class LoadDriver{  
	
	static Scanner scanner;
	static Connection con;
	
	public static void main(String args[]){  
		try{  
			Class.forName("com.mysql.jdbc.Driver");  
			
			con=DriverManager.getConnection(  
					"jdbc:mysql://localhost:3306/prosjekt2","****", "****" );
			/*Statement stmt=con.createStatement();  
			ResultSet rs=stmt.executeQuery("select * from Treningsokter");  
			while(rs.next())  
				System.out.println(rs.getDate("dato").toString());
			con.close();  */
			System.out.println("Successfully connected to database!");
			while(true){
				scanner = new Scanner(System.in);
				System.out.println("Hva vil du gj�re? \n" + String.format("%-50s", "Opprett ny trenings�kt") +  "- t\n"
						+ String.format("%-50s", "Legge inn nye �velser") + "- o\n"
						+ String.format("%-50s", "Liste opp tilgjengelige �velser") + "- l\n"
						+ String.format("%-50s", "Vise statistikk for de siste 30 dagene") + "- s\n"
						+ String.format("%-50s", "Vis beste trenings�kt") +  "- b\n");
				char c = scanner.nextLine().charAt(0);
				
				switch(c){
				case 't':
					addSession();
					break;
				case 'o':
					addExercise();
					//Legg inn ny øvelse
					break;
				case 'l':
					//List opp øvelser som er lagt inn
					getAllExercises();
					break;
				case 's':
					//Vis statistikk for de siste 30 dagene
					showStatistics();
					break;
				case 'b':
					showBestWorkout();
					break;
				}
			}
		}catch(Exception e){ System.out.println(e);}
		finally{
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}  
	
	
	
	private static void getAllExercises() {
		try {
			Statement getEx=con.createStatement(); 
			ResultSet ex=getEx.executeQuery("select * from ovelser");  
			
			for (int i = 0; i < 90; i++)
				System.out.print("#");
			System.out.println();
			System.out.printf("%-15s %-30s %-15s %-15s %-15s %n", "Navn", "Beskrivelse", "Belastning", "Repetisjoner", "Sett");
			for (int i = 0; i < 90; i++)
				System.out.print("#");
			System.out.println();
			while(ex.next())  
				System.out.printf("%-15s %-30s %-15d %-15d %-15d %n",ex.getString("Navn"), ex.getString("Beskrivelse"), 
						ex.getInt("Belastning"), ex.getInt("Repetisjoner"), ex.getInt("Sett"));
			for (int i = 0; i < 90; i++)
				System.out.print("#");
			System.out.println();
			System.out.println("\n \n");
			getEx.close();
			
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
		
	}

	public static void addSession(){
		boolean cont = true;
		while(cont){
			// Får feil lengere ned dersom oktID ikke plasseres utenfor try-catch
			int oktID = -1;
			try{
				Statement stmt=con.createStatement();
				ResultSet rs=stmt.executeQuery("select max(oktID) from Treningsokter;");
				oktID = rs.getInt(1);
				stmt.close();
				oktID += 1;
			} catch (SQLException sqle) {
				sqle.printStackTrace();
			}
			scanner = new Scanner(System.in);
			System.out.println("Du har valgt å legge til en treningsøkt \n");
			System.out.println("Skriv inn din unike brukerID: \n");
			int brukerID = scanner.nextInt();
			System.out.println("Tid (HH:MM:SS): \n");
			String tid = scanner.nextLine();
			System.out.println("Dato (YYYY-MM-DD): \n");
			String dato = scanner.nextLine();
			System.out.println("Form: \n");
			int form = scanner.nextInt();
			System.out.println("Prestasjon: \n");
			int prestasjon = scanner.nextInt();
			System.out.println("Notater til treningsøkten: \n");
			String notat = scanner.nextLine();
			
			Treningsokt okt = new Treningsokt(oktID, tid, dato, form, prestasjon, notat, brukerID);
			
			try {
				Statement stmt=con.createStatement(); 
				
				String sqlInsert = "INSERT INTO Treningsokter (oktID, tid, dato, form, prestasjon, notat, brukerID) " +
		                   "VALUES ('"+okt.oktID +"','"+okt.tid+"',"+
		                   okt.dato +","+okt.form +
		                   ","+okt.prestasjon +" ,"+okt.notat +","+ okt.brukerID+");";
		      stmt.executeUpdate(sqlInsert);
		      stmt.close();
				
			} catch (SQLException sqle) {
				sqle.printStackTrace();
			}
			System.out.print("Vil du legge inn en økt til? (1/0): \n");
			int answer = scanner.nextInt();
			cont = (1 == answer);
		}
	}

	private static void addExercise() {
		scanner = new Scanner(System.in);
		System.out.println("Du har valgt � legge til en �velse \n"
				+ "Vennligst skriv inn et unikt navn: ");
		String navn = scanner.nextLine();
		System.out.println("Beskrivelse: \n");
		String beskr = scanner.nextLine();
		System.out.println("Belastning: \n");
		int belastning = Integer.parseInt(scanner.nextLine());
		System.out.println("Repetisjoner: \n");
		int rep = Integer.parseInt(scanner.nextLine());
		System.out.println("Sett: \n");
		int sett = Integer.parseInt(scanner.nextLine());
		
		Ovelse ov = new Ovelse(navn, beskr, belastning, rep, sett);
		
		try {
			Statement stmt=con.createStatement(); 
			
			String sqlInsert = "INSERT INTO ovelser (navn, beskrivelse, belastning, repetisjoner, sett) " +
	                   "VALUES ('"+ov.navn +"','"+ov.beskrivelse+"',"+
	                   ov.belastning +","+ov.repetisjoner +
	                   ","+ov.sett +");";
	      stmt.executeUpdate(sqlInsert);
	      stmt.close();
			
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
		
	}



	public static void showStatistics() throws SQLException{
		Calendar today = Calendar.getInstance();
		today.set(Calendar.HOUR_OF_DAY, 0);
		Date date = new Date(today.getTimeInMillis() - 1000L*60L*60L*24L*30L); //30 days ago
		String formattedDate = "'" + new SimpleDateFormat("YYYY-MM-dd").format(date) + "'";
		Statement stmt=con.createStatement();  
		ResultSet rs=stmt.executeQuery("select * from Treningsokter"
				+ " where dato >= " + formattedDate ); 
		int totalNumberOkts = 0;
		double totalDistance = 0;
		double totalTime = 0;
		ArrayList<Ovelsesresultat> ovelsesList = new ArrayList<Ovelsesresultat>();
		while(rs.next()) {
			int i = rs.getInt(1);
			Statement stmt2 = con.createStatement();
			ResultSet crs = stmt2.executeQuery("select * from Ovelsesresultater " + 
					"where oktId=" + i);
			while(crs.next()){

				Ovelsesresultat ov = new Ovelsesresultat();
				
				ov.tid =  crs.getDouble("tid");
				totalTime += ov.tid;
				ov.lengde = crs.getDouble("lengde");
				totalDistance += ov.lengde;
				ov.ovelse = crs.getString("ovelse");

				ovelsesList.add(ov);
				//System.out.println("Ovelse: " + s);
			}
			crs.last();
			totalNumberOkts += crs.getRow();
			crs.close();
		}
		rs.last();
		int totalNumberTrainings = rs.getRow();
		NumberFormat formatter = new DecimalFormat("#0.00");
		int hours = (int)(totalTime/60);
		int minutes = (int)totalTime - 60*hours;
		System.out.println("De siste 30 dagene: ");
		System.out.println("Det var totalt " + totalNumberOkts + " øvelser fordelt på " + totalNumberTrainings + " økter");
		System.out.println("Totalt ble det løpt " + formatter.format(totalDistance) + " km på " + hours + " timer og " + minutes + " minutter");
		System.out.println("Det betyr en gjennomsnittshastighet på " + formatter.format(totalDistance/hours) + "km/t!");
		
		Map<String, Long> counts = 
				ovelsesList.stream().collect(Collectors.groupingBy(e -> e.getOvelse(), Collectors.counting()));
		Map<String, Double> tid = 
				ovelsesList.stream().collect(Collectors.groupingBy(e -> e.getOvelse(), Collectors.summingDouble(e -> e.getTid())));
		Map<String, Double> lengde = 
				ovelsesList.stream().collect(Collectors.groupingBy(e -> e.getOvelse(), Collectors.summingDouble(e -> e.getLengde())));
		
		System.out.println("\nInfo om individuelle øvelser: ");
		Iterator<String> it = counts.keySet().iterator();
		while(it.hasNext()){
			String s = it.next();
			System.out.println(String.format("%-30s", ("Øvelse: " + s)) + " total lengde: " + lengde.get(s) + "km,\t total tid: " + tid.get(s) + " minutter");
		}
		rs.close();
		System.out.print("\n\n");
	}
	
public static void showBestWorkout() throws SQLException{
		
		Statement stmt=con.createStatement();  
		ResultSet rs=stmt.executeQuery("select * from Treningsokter"); 
		scanner = new Scanner(System.in);
		System.out.println("Hva vil du finne beste av? \n" 
				+ String.format("%-50s", "Beste form") + "- f\n"
				+ String.format("%-50s", "Beste prestasjon") +  "- p\n");
		char c = scanner.nextLine().charAt(0);
		Statement s2 = con.createStatement();
		ResultSet beste;
		ResultSet besteTrening;
		switch(c){
		case 'f':
			beste= s2.executeQuery("SELECT MAX( form ) FROM Treningsokter"); 
			while(beste.next()){
				int bestForm=beste.getInt(1);
				Statement  s3 = con.createStatement();
				besteTrening= s3.executeQuery("SELECT * FROM Treningsokter " + "where form = " + bestForm);
				s3.close();
			}
			s2.close();
			break;
		case 'p':
			beste= s2.executeQuery("SELECT MAX(prestasjon) FROM Treningsokter"); 
			while(beste.next()){
				int bestPrestasjon=beste.getInt(1);
				Statement  s3 = con.createStatement();
				besteTrening= s3.executeQuery("SELECT * FROM Treningsokter " + "where prestasjon = " + bestPrestasjon);
				s3.close();
			}
			s2.close();
			break;
		}

		rs.close();
		System.out.print("\n\n");
	}

	
	static class Ovelse{
		public String navn;
		public String beskrivelse;
		public int belastning;
		public int repetisjoner;
		public int sett;
		public float kalorierPerKilo;
		
		public Ovelse(String navn, String beskrivelse, int belastning, int repetisjoner, int sett){
			this.navn = navn;
			this.beskrivelse = beskrivelse;
			this.belastning = belastning;
			this.repetisjoner = repetisjoner;
			this.sett = sett;
		}
	}
	
	
	static class Ovelsesresultat{
		public int oktID;
		public String ovelse;
		public double tid;
		public double lengde;
		
		public double getLengde(){
			return lengde;
		}
		
		public String getOvelse(){
			return ovelse;
		}
		
		public double getTid(){
			return tid;
		}
	}
	
	static class Treningsokt{
		public int oktID;
		public int brukerID;
		public String tid;
		public String dato;
		public int form;
		public int prestasjon;
		public String notat;
		
		public Treningsokt(int oktID, String tid, String dato, int form, int prestasjon, String notat, int brukerID){
			this.oktID = oktID;
			this.brukerID = brukerID;
			this.tid = tid;
			this.dato = dato;
			this.form = form;
			this.prestasjon = prestasjon;
			this.notat = notat;
		}
		
		public String getTid(){
			// Skal jeg her konvertere til double
			return tid;
		}
		
		public String getDato(){
			// Samme som over
			return dato;
		}
		
		public int getForm(){
			return form;
		}
		
		public int getPrestasjon(){
			return prestasjon;
		}
		
		public String getNotat(){
			return notat;
		}
	}
}