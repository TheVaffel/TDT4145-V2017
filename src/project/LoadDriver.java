package project;
import java.sql.*;  

class LoadDriver{  
	public static void main(String args[]){  
	try{  
		Class.forName("com.mysql.jdbc.Driver");  

		Connection con=DriverManager.getConnection(  
				"jdbc:mysql://localhost:3306/treningsbase","******", "******" );  
		//here sonoo is database name, root is username and password  
		Statement stmt=con.createStatement();  
		ResultSet rs=stmt.executeQuery("select * from Ovelser");  
		while(rs.next())  
			System.out.println(rs.getString(1)+"  "+rs.getString(2)+"  "+rs.getInt(3) + "  " + rs.getInt(4) + "  " + rs.getInt(5) + "  " + rs.getDouble(6));  
		con.close();  
		}catch(Exception e){ System.out.println(e);}  
	}  
}  
