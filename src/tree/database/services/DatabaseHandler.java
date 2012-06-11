package tree.database.services;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;

import tree.database.updateChecker;
import tree.database.data.Comment;
import tree.database.data.Group;
import tree.database.data.Tree;
import tree.database.data.User;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class DatabaseHandler{

	private Connection connection;
	private static String URL = "jdbc:postgresql://seclab10.medien.uni-weimar.de/baumdb?user=worker&password=mindless&ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";
	
	public ArrayList<Tree> getTreeList(float[] userlocation, double radius, User user){
		ResultSet rs;
		
    	try {
    		Log.i("postgres","Go Postgres!");
			Class.forName("org.postgresql.Driver");
			Connection conn = DriverManager.getConnection(URL);
			
			PreparedStatement ps = conn.prepareStatement("SELECT tid, long, lat, date, size, age, name FROM trees;");
			rs = ps.executeQuery();
		
			Log.i(this.getClass().getSimpleName(), ps.toString());
			
			ArrayList<Tree> treelist = new ArrayList<Tree>();
			
		    while(rs.next()){
		    	float[] location = {rs.getFloat(3), rs.getFloat(2)};
		    	Tree t = new Tree(rs.getInt(1), rs.getString(7), rs.getInt(6), rs.getDouble(5), location, rs.getDate(4));
		    	treelist.add(t);
    		}
			rs.close();
			ps.close();
			//get pictures if the rights are enought
			if(user.Rights >= User.SHOW_PICTURES){
				for(int i = 0; i < treelist.size(); ++i){
					ps = conn.prepareStatement("SELECT image FROM images WHERE (tid = ?);");
					ps.setInt(1, treelist.get(i).ID);
					rs = ps.executeQuery();
					ArrayList<Bitmap> images = new ArrayList<Bitmap>();
					while(rs.next()){
						byte[] bytes = rs.getBytes(1);
						images.add(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
					}
					treelist.get(i).Images = images;
					rs.close();
					ps.close();
				}
			}			
			//check, if the statement changes DB entries
			conn.close();
			return treelist;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	Log.i(this.getClass().getSimpleName(), user.toString());
		
		return new ArrayList<Tree>();
	}
	
	public boolean addTree(int uid, Bitmap image, String name, float[] loc, double size, int age){
		try {
    		Log.i("postgres","Go Postgres!");
			Class.forName("org.postgresql.Driver");
			Connection conn = DriverManager.getConnection(URL);
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			image.compress(Bitmap.CompressFormat.JPEG, 85, baos);
			
			PreparedStatement ps = conn.prepareStatement("INSERT INTO trees (long, lat, date, uid, size, age, name) VALUES (?, ?, ?, ?, ?, ?, ?);");
			ps.setDouble(1, loc[1]);
			ps.setDouble(2, loc[0]);
			Date d = new Date();
			ps.setDate(3, (java.sql.Date) d);
			ps.setInt(4, uid);
			ps.setDouble(5, size);
			ps.setInt(6, age);
			ps.setString(7, name);
			
			if(ps.executeUpdate() >= 1){
				return true;
			}
			ps.close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
    	return false;
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
	
	public boolean addUser(User user){
		
		try {
    		Log.i("postgres","Go Postgres!");
			Class.forName("org.postgresql.Driver");
			Connection conn = DriverManager.getConnection(URL);
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			user.Avatar.compress(Bitmap.CompressFormat.JPEG, 85, baos);
			
			PreparedStatement ps = conn.prepareStatement("INSERT INTO users (name, pw, rights, avatar) VALUES (?, ?, ?, ?);");
			ps.setString(1, user.Name);
			ps.setString(2, user.Password);
			ps.setInt(3, user.Rights);
			ps.setBytes(4, baos.toByteArray());
			
			if(ps.executeUpdate() >= 1){
				return true;
			}
			ps.close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		Log.i(this.getClass().getSimpleName(), "Add");
		
    	return false;
	}
	
	public User getUser(String name){
		ResultSet rs;
		
		User user = new User();
		int count = 0;
    	
    	try {
    		Log.i("postgres","Go Postgres!");
			Class.forName("org.postgresql.Driver");
			Connection conn = DriverManager.getConnection(URL);
			
			PreparedStatement ps = conn.prepareStatement("SELECT uid, name, pw, rights FROM users WHERE (name = ?);");
//			PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE (name = ? AND pw = ?);");
			ps.setString(1, name);
			rs = ps.executeQuery();
		
			Log.i(this.getClass().getSimpleName(), ps.toString());
			
			while(rs.next()){
				count++;
			    user.ID = rs.getInt(1);
			    user.Name = rs.getString(2);
			    user.Password = rs.getString(3);
			    user.Rights = rs.getInt(4);

			}
			
			rs.close();
			ps.close();
			//check, if the statement changes DB entries
			conn.close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	Log.i(this.getClass().getSimpleName(), user.toString());
    	if(count == 0){
    		Log.e(this.getClass().getSimpleName(), "no User in DB found with this name");
    		user = null;
    	}
    	return user;
	}
	
	public User getUserPicture(User user){
		ResultSet rs;
		
    	try {
    		Log.i("postgres","Go Postgres!");
			Class.forName("org.postgresql.Driver");
			Connection conn = DriverManager.getConnection(URL);
			
			PreparedStatement ps = conn.prepareStatement("SELECT avatar FROM users WHERE (name = ? AND pw = ?);");
			ps.setString(1, user.Name);
			ps.setString(2, user.Password);
			
			Log.i(this.getClass().getSimpleName(), ps.toString());
			
			rs = ps.executeQuery();
					
		    rs.next();
		    byte[] bytes = rs.getBytes(1);
		    user.Avatar = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
		    
			rs.close();
			ps.close();
			//check, if the statement changes DB entries
			conn.close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	
    	return user;
	}
	
	public boolean setUserPassword(int uid, String newpassword){
		try {
    		Log.i("postgres","Go Postgres!");
			Class.forName("org.postgresql.Driver");
			Connection conn = DriverManager.getConnection(URL);
			
			PreparedStatement ps = conn.prepareStatement("UPDATE users SET pw=? WHERE uid = ?;");
			ps.setString(1, newpassword);
			ps.setInt(2, uid);
			
			if(ps.executeUpdate() >= 1){
				return true;
			}
			ps.close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	return false;
	}
	
	public boolean addComment(int uid, int tid, Comment comment){
		return true;
	}
	
	public ArrayList<Comment> getCommentList(int tid){
		System.out.println("commentlist");
		return new ArrayList<Comment>();
	}
	
	private void processQuery(String statement){
		ResultSet rs;
    	
    	try {
    		Log.i("postgres","Go Postgres!");
			Class.forName("org.postgresql.Driver");
			Connection conn = DriverManager.getConnection(URL);
			
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
