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
					"jdbc:mysql://localhost:3306/treningsbase","******", "******" );
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
					//Opprett treningsøkt
					break;
				case 'o':
					//Legg inn ny øvelse
					break;
				case 'l':
					//List opp øvelser som er lagt inn
					break;
				case 's':
					//Vis statistikk for de siste 30 dagene
					showStatistics();
					break;
				case 'b':
					//Vis beste treningsøkt
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