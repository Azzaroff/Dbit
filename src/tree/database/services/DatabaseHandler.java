package tree.database.services;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import tree.database.updateChecker;
import tree.database.data.Group;
import tree.database.data.Tree;
import android.graphics.Bitmap;
import android.location.Location;
import android.util.Log;

public class DatabaseHandler{

	private Connection connection;
	
	public ArrayList<Tree> getTreeList(Location loc, double radius){
		return new ArrayList<Tree>();
	}
	
	public boolean addTree(Bitmap image, String name, Location loc, double size, int age){
		return true;
	}
	
	public boolean updateTree(Tree tree){
		return true;
	}
	
	public ArrayList<Group> getGroupList(){
		return new ArrayList<Group>();
	}
	
	public boolean addGroup(String name, String password){
		return true;
	}
	
	public boolean updateGroup(Group group){
		return true;
	}
	
	public boolean addUser(String name, String password, int rights, Bitmap avatar){
		
		try {
    		Log.i("postgres","Go Postgres!");
			Class.forName("org.postgresql.Driver");
			String url = "jdbc:postgresql://seclab10.medien.uni-weimar.de/baumdb?user=worker&password=mindless&ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";
			Connection conn = DriverManager.getConnection(url);
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			avatar.compress(Bitmap.CompressFormat.JPEG, 85, baos);
			
			PreparedStatement ps = conn.prepareStatement("INSERT INTO users (name, pw, rights, avatar) VALUES (?, ?, ?, ?);");
			ps.setString(1, name);
			ps.setString(2, password);
			ps.setInt(3, rights);
			ps.setBinaryStream(4, new ByteArrayInputStream(baos.toByteArray()));
			
			if(ps.executeUpdate() >= 1){
				return true;
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		Log.i(this.getClass().getSimpleName(), "Add");
		
    	return false;
	}
	
	public boolean getUser(String name, String password){
		return true;
	}
	
	private void processQuery(String statement){
		ResultSet rs;
    	
    	try {
    		Log.i("postgres","Go Postgres!");
			Class.forName("org.postgresql.Driver");
			String url = "jdbc:postgresql://seclab10.medien.uni-weimar.de/baumdb?user=worker&password=mindless&ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";
			Connection conn = DriverManager.getConnection(url);
			
			Statement st = conn.createStatement();
			//checks if it was an sql statement containing insert into, update or delete
			updateChecker uc = new updateChecker();
			if(uc.checkUpdate(statement) != null){
				st.executeUpdate(statement);
			}else{				
				rs = st.executeQuery(statement);
				ResultSetMetaData rsmd = rs.getMetaData();
			
			    int numCols = rsmd.getColumnCount();
				
				while (rs.next()) {
					Log.i("postgres","getting one column");
					String row = "";
				    for (int i = 1; i <= numCols; i++) {
				    	 row = row + "| " + rs.getString(i);
					}
				    row = row + " |";
				    Log.i("postgres",row);
				}
				rs.close();
				st.close();
				//check, if the statement changes DB entries
			}
			conn.close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
}
