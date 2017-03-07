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
					"jdbc:mysql://localhost:3306/treningsbase","****", "****" );
			/*Statement stmt=con.createStatement();  
			ResultSet rs=stmt.executeQuery("select * from Treningsokter");  
			while(rs.next())  
				System.out.println(rs.getDate("dato").toString());
			con.close();  */
			System.out.println("Successfully connected to database!");
			while(true){
				scanner = new Scanner(System.in);
				System.out.println("Hva vil du gjøre? \n" + String.format("%-50s", "Opprett ny treningsøkt") +  "- t\n"
						+ String.format("%-50s", "Legge inn nye øvelser") + "- o\n"
						+ String.format("%-50s", "Liste opp tilgjengelige øvelser") + "- l\n"
						+ String.format("%-50s", "Vise statistikk for de siste 30 dagene") + "- s\n"
						+ String.format("%-50s", "Vis beste treningsøkt") +  "- b\n");
				char c = scanner.nextLine().charAt(0);
				
				switch(c){
				case 't':
					//Opprett treningsÃ¸kt
					break;
				case 'o':
					addExercise();
					//Legg inn ny Ã¸velse
					break;
				case 'l':
					//List opp Ã¸velser som er lagt inn
					getAllExercises();
					break;
				case 's':
					//Vis statistikk for de siste 30 dagene
					showStatistics();
					break;
				case 'b':
					//Vis beste treningsÃ¸kt
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



	private static void addExercise() {
		scanner = new Scanner(System.in);
		System.out.println("Du har valgt å legge til en øvelse \n"
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
		System.out.println("Det var totalt " + totalNumberOkts + " Ã¸velser fordelt pÃ¥ " + totalNumberTrainings + " Ã¸kter");
		System.out.println("Totalt ble det lÃ¸pt " + formatter.format(totalDistance) + " km pÃ¥ " + hours + " timer og " + minutes + " minutter");
		System.out.println("Det betyr en gjennomsnittshastighet pÃ¥ " + formatter.format(totalDistance/hours) + "km/t!");
		
		Map<String, Long> counts = 
				ovelsesList.stream().collect(Collectors.groupingBy(e -> e.getOvelse(), Collectors.counting()));
		Map<String, Double> tid = 
				ovelsesList.stream().collect(Collectors.groupingBy(e -> e.getOvelse(), Collectors.summingDouble(e -> e.getTid())));
		Map<String, Double> lengde = 
				ovelsesList.stream().collect(Collectors.groupingBy(e -> e.getOvelse(), Collectors.summingDouble(e -> e.getLengde())));
		
		System.out.println("\nInfo om individuelle Ã¸velser: ");
		Iterator<String> it = counts.keySet().iterator();
		while(it.hasNext()){
			String s = it.next();
			System.out.println(String.format("%-30s", ("Ã˜velse: " + s)) + " total lengde: " + lengde.get(s) + "km,\t total tid: " + tid.get(s) + " minutter");
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
}