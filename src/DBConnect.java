import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

//http://www.javaworkspace.com/connectdatabase/connectMysql.do
//http://www.exampledepot.com/egs/java.sql/GetRsData.html
//http://www.stardeveloper.com/articles/display.html?article=2003090401&page=2
//http://stackoverflow.com/questions/2225221/closing-database-connections-in-java
//http://www.java-forums.org/eclipse/10180-connector-j-eclipse-mysql-5-0-x.html
public class DBConnect {
	
	Connection connection = null;
	
	public DBConnect(){
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			connection = DriverManager.getConnection("jdbc:mysql://localhost:8889/HeatmapData","root", "root");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void cleanup(){
		try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public boolean is_integer(String s){
		try{
			Integer.parseInt(s);
			return true;
		} catch (Exception e){
			return false;
		}
	}
	
	public LatLon getLatLon(String zip){
		LatLon ret = null;
		ResultSet resultSet = null;
		Statement statement = null;
		try {
			statement = connection.createStatement();
			if (is_integer(zip)) {
				resultSet = statement.executeQuery("SELECT * FROM placeTable WHERE Zip = " + zip);
				if (resultSet.next()) {
					ret = new LatLon(resultSet.getDouble("Lat"), resultSet.getDouble("Lon"));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (resultSet != null)
					resultSet.close();
				if (statement != null)
					statement.close();
			} catch (SQLException e) {e.printStackTrace();}
		}
		return ret;
	}

	/**
	 * Returns a list of zip codes for each unique donor to the given candidate.
	 * @param candidateName
	 * @return List of zip codes for each unique donor. Zip codes ARE repeated for multiple donors from the
	 * same place.
	 */
	public ArrayList<String> getDonorLocations(String candidateName){
		ArrayList<String> ret = new ArrayList<String>();
		ResultSet resultSet = null;
		Statement statement = null;
		try {
			String candId = getCandidateId(candidateName);
			statement = connection.createStatement();
			resultSet = statement.executeQuery("SELECT SUM(Amount), Zip, ContribID FROM contributionTable WHERE RecipID='"+candId+"' GROUP BY ContribID");
			while (resultSet.next()){
				ret.add(resultSet.getString("Zip"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				resultSet.close();
				statement.close();
			} catch (SQLException e) {e.printStackTrace();}
		}
		return ret;
	}

	private String getCandidateId(String candidateName){
		String ret = null;
		ResultSet resultSet = null;
		Statement statement = null;
		try {
			statement = connection.createStatement();
			resultSet = statement.executeQuery("SELECT CID, FirstLastP FROM candidatesTable WHERE FirstLastP LIKE \""+candidateName+" %\"");
			if (resultSet.next()){
				ret = resultSet.getString("CID");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				resultSet.close();
				statement.close();
			} catch (SQLException e) {e.printStackTrace();}
		}
		return ret;
	}
	
	public static void main(String args[]){
		DBConnect db = new DBConnect();
		//System.out.println(db.getLatLon("05602"));
		//System.out.println(db.getLatLon("12345"));
		//System.out.println(db.getLatLon("55057"));
		System.out.println(db.getCandidateId("Bernie Sanders"));
		System.out.println(db.getDonorLocations("Bernie Sanders"));
		System.out.println(db.is_integer("*7250"));
		System.out.println(db.is_integer(""));
		db.cleanup();
	}
	
}
