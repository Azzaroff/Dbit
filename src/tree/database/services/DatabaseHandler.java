package tree.database.services;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import tree.database.R;
import tree.database.data.Comment;
import tree.database.data.Group;
import tree.database.data.Tree;
import tree.database.data.User;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class DatabaseHandler{

	private Connection connection;
	private static String URL = "jdbc:postgresql://seclab10.medien.uni-weimar.de/baumdb?user=worker&password=mindless&ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";
	
	public ArrayList<Tree> getTreeList(float[] userlocation, double radius, User user, Activity activity){
		ResultSet rs;
		
    	try {
    		Log.i("postgres","Go Postgres!");
			Class.forName("org.postgresql.Driver");
			Connection conn = DriverManager.getConnection(URL);
			
			PreparedStatement ps = conn.prepareStatement("SELECT tid, long, lat, date, size, age, name FROM trees;");
			rs = ps.executeQuery();
			
//			select trees.tid, image_result.img FROM trees INNER JOIN (select tree_has_picture.tid, images.img from tree_has_picture inner join images on tree_has_picture.pid = images.pid) AS image_result ON trees.tid = image_result.tid;
		
			Log.i(this.getClass().getSimpleName(), ps.toString());
			
			HashMap<Integer, Tree> tempmap = new HashMap<Integer, Tree>();
			int count = 0;
		    while(rs.next()){
		    	Double[] location = {rs.getDouble(3), rs.getDouble(2)};
		    	Tree t = new Tree(rs.getInt(1), rs.getString(7), rs.getInt(6), rs.getDouble(5), location, rs.getTimestamp(4));
		    	tempmap.put(t.ID, t);
		    	++count;
    		}
		    Log.i(this.getClass().getSimpleName(), "Number of trees in DB: "+(count-1));
			rs.close();
			ps.close();
			//get pictures if the rights are enought
			if(user.Rights >= User.SHOW_PICTURES){
				Log.i(this.getClass().getSimpleName(), "get pictures from database");
				ps = conn.prepareStatement("SELECT tree_has_picture.tid, images.img FROM tree_has_picture INNER JOIN images ON tree_has_picture.pid = images.pid;");
				rs = ps.executeQuery();
				Log.i(this.getClass().getSimpleName(), ps.toString());
				while(rs.next()){
					byte[] bytes = rs.getBytes(2);
					tempmap.get(rs.getInt(1)).Images.add(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
				}
				rs.close();
				ps.close();
			}			
			//check, if the statement changes DB entries
			conn.close();
			for(Tree t : tempmap.values()){
				if(t.Images == null || t.Images.size() == 0){
					//there are no images to this tree
					t.Images.add(BitmapFactory.decodeResource(activity.getResources(), R.drawable.baum));
				}
			}
			return new ArrayList<Tree>(tempmap.values());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	Log.i(this.getClass().getSimpleName(), user.toString());
		
		return new ArrayList<Tree>();
	}
	
	public boolean addTree(int uid, Bitmap image, String name, float[] loc, double size, int age){
		boolean success = false;
		try {
			int tid = 0;
    		Log.i("postgres","Go Postgres!");
			Class.forName("org.postgresql.Driver");
			Connection conn = DriverManager.getConnection(URL);
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			image.compress(Bitmap.CompressFormat.JPEG, 85, baos);
			
			PreparedStatement ps = conn.prepareStatement("INSERT INTO trees (long, lat, date, size, age, name) VALUES (?, ?, ?, ?, ?, ?) RETURNING tid;");
			if(loc != null){
				ps.setDouble(1, loc[1]);
				ps.setDouble(2, loc[0]);
			}
			else{
				ps.setDouble(1, Double.MAX_VALUE);
				ps.setDouble(2, Double.MAX_VALUE);
			}
			Date d = new Date();
			ps.setTimestamp(3, new java.sql.Timestamp(d.getTime()));
			ps.setDouble(4, size);
			ps.setInt(5, age);
			ps.setString(6, name);
			
			ResultSet rs = ps.executeQuery();
			if(rs.next()){
			      tid = rs.getInt(1);
			      success = true;
			}
			ps.close();
			
			int pid = addTreeImage(image, tid, conn, new java.sql.Timestamp(d.getTime()));
			addTreeImageRelation(pid, tid, conn);
			addUserImageRelation(uid,pid,conn);
			
			conn.close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
    	return success;
	}
	
	public boolean updateTree(Tree tree){
		return true;
	}
	
	private int addTreeImage(Bitmap image, int treeID, Connection conn, java.sql.Timestamp date){
		int pid = 0;
		try {
    		Log.i("postgres","Go Postgres!");
			Class.forName("org.postgresql.Driver");
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			image.compress(Bitmap.CompressFormat.JPEG, 85, baos);
			
			PreparedStatement ps = conn.prepareStatement("INSERT INTO images (date, img) VALUES (?, ?) RETURNING pid;");
			ps.setTimestamp(1, date);
			ps.setBytes(2, baos.toByteArray());
			
			ResultSet rs = ps.executeQuery();
			if(rs.next()){
			      pid = rs.getInt(1);
			}
			
			ps.close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		Log.i(this.getClass().getSimpleName(), "Add TreeImage pid:"+pid);
		
		return pid;
	}
	
	private boolean addTreeImageRelation(int pid, int tid, Connection conn){
		try {
    		Log.i("postgres","Go Postgres!");
			Class.forName("org.postgresql.Driver");
			
			
			PreparedStatement ps = conn.prepareStatement("INSERT INTO tree_has_picture (pid, tid) VALUES (?, ?);");
			ps.setInt(1, pid);
			ps.setInt(2, tid);
			
			if(ps.executeUpdate() >= 1){
				Log.i(this.getClass().getSimpleName(), "Add TreeImageRelation pid:"+ pid + " tid:"+tid);
				return true;
			}
			ps.close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		Log.i(this.getClass().getSimpleName(), "Add TreeImageRelation pid:"+ pid + " tid:"+tid);
		
		return false;
	}
	
	private boolean addUserImageRelation(int uid, int pid, Connection conn){
		try {
    		Log.i("postgres","Go Postgres!");
			Class.forName("org.postgresql.Driver");
			
			
			PreparedStatement ps = conn.prepareStatement("INSERT INTO user_took_picture (uid, pid) VALUES (?, ?);");
			ps.setInt(1, uid);
			ps.setInt(2, pid);
			
			if(ps.executeUpdate() >= 1){
				Log.i(this.getClass().getSimpleName(), "Add UserImageRelation pid:"+ pid + " uid:"+uid);
				return true;
			}
			ps.close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		Log.i(this.getClass().getSimpleName(), "Add UserImageRelation pid:"+ pid + " uid:"+uid);
		
		return false;
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
	
	public Tree getTree(java.sql.Timestamp date, String name){
		ResultSet rs;
		
		Tree tree = new Tree();
		int count = 0;
    	
    	try {
    		Log.i("postgres","getTree");
			Class.forName("org.postgresql.Driver");
			Connection conn = DriverManager.getConnection(URL);
			
			PreparedStatement ps = conn.prepareStatement("SELECT tid, long, lat, date, size, age, name FROM trees WHERE (name = ? and date = ?);");
			ps.setString(1, name);
			ps.setTimestamp(2, date);
			rs = ps.executeQuery();
		
			Log.i(this.getClass().getSimpleName(), ps.toString());
			
			while(rs.next()){
				count++;
				tree.ID = rs.getInt(1);
				tree.Location[1] = rs.getDouble(2);
				tree.Location[0] = rs.getDouble(3);
				tree.Date = rs.getTimestamp(4).getTime();
				tree.Size = rs.getInt(5);
				tree.Age = rs.getInt(6);
				tree.Name = rs.getString(7);
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
    	Log.i(this.getClass().getSimpleName(), tree.toString());
    	if(count == 0){
    		Log.e(this.getClass().getSimpleName(), "no Tree in DB found with this name and date");
    		tree = null;
    	}
    	return tree;
	}
	
	//nur nach insert aufrufen!
	public int getCurrval(String seqname, Connection conn){
		ResultSet rs;
		
		int currval = 0;
		int count = 0;
    	
    	try {
    		Log.i("postgres","getTree");
			Class.forName("org.postgresql.Driver");
			
			PreparedStatement ps = conn.prepareStatement("SELECT currval(?);");
			ps.setString(1, seqname);
			rs = ps.executeQuery();
		
			Log.i(this.getClass().getSimpleName(), ps.toString());
			
			while(rs.next()){
				count++;
				rs.getInt(1);
			}
			
		    rs.close();
			ps.close();
			//check, if the statement changes DB entries
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	Log.i(this.getClass().getSimpleName(), "Currval:" + currval);
    	if(count == 0){
    		Log.e(this.getClass().getSimpleName(), "Currval error, vllt insert vergessen?");
    	}
		return currval;
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
	
	public boolean addComment(int tid, Comment comment){
		try {
    		Log.i("postgres","Go Postgres!");
			Connection conn = DriverManager.getConnection(URL);
			
			PreparedStatement ps = conn.prepareStatement("INSERT INTO comments (uid, tid, content, date) VALUES (?, ?, ?, ?);");
			ps.setInt(1, comment.User.ID);
			ps.setInt(2, tid);
			ps.setString(3, comment.Content);
			ps.setTimestamp(4, new java.sql.Timestamp(comment.Date));
			
			if(ps.executeUpdate() >= 1){
				Log.i(this.getClass().getSimpleName(), "Add Comment");
				return true;
			}
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
    	return false;
	}
	
	public ArrayList<Comment> getCommentList(int tid){
		try {
    		Log.i("postgres","Go Postgres!");
			Connection conn = DriverManager.getConnection(URL);
			
			PreparedStatement ps = conn.prepareStatement("SELECT u.uid, u.name, u.avatar, c.content, c.date FROM users u NATURAL JOIN comments c WHERE (c.tid = ?);");
			ps.setInt(1, tid);
			
			Log.i(this.getClass().getSimpleName(), ps.toString());
			
			ResultSet rs = ps.executeQuery();
			
			ArrayList<Comment> commentlist = new ArrayList<Comment>();
					
		    while(rs.next()){
		    	
		    	User u = new User(rs.getInt(1), rs.getString(2), BitmapFactory.decodeByteArray(rs.getBytes(3), 0, rs.getBytes(3).length));
		    	commentlist.add(new Comment(u, rs.getTimestamp(5).getTime(), rs.getString(4)));
		    	Log.i(this.getClass().getSimpleName(), rs.getString(4));
		    }
			rs.close();
			ps.close();
			//check, if the statement changes DB entries
			conn.close();
			return commentlist;
		} catch (SQLException e) {
			e.printStackTrace();
		}    	
		return new ArrayList<Comment>();
	}
}
