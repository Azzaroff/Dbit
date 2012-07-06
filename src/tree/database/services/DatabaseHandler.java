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
import java.util.TreeMap;

import tree.database.R;
import tree.database.SettingsActivity;
import tree.database.data.Comment;
import tree.database.data.Group;
import tree.database.data.Tree;
import tree.database.data.User;
import tree.database.misc.Connectivity;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;

public class DatabaseHandler{

	private Connection connection;
	private static String URL = "jdbc:postgresql://seclab10.medien.uni-weimar.de/baumdb?user=worker&password=mindless&ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";
	
	public ArrayList<Tree> getTreeList(float[] userlocation, float radius, User user, Activity activity, int time_since_last_usage){
		ResultSet rs;
		
    	try {
    		Log.i("postgres","Go Postgres!");
			Class.forName("org.postgresql.Driver");
			Connection conn = DriverManager.getConnection(URL);
			
			//calculate radius
			float distance_in_minutes = (float) (radius / 1.852);
			
			PreparedStatement ps;
			
			ps = conn.prepareStatement("SELECT tid, long, lat, date, size, age, name FROM trees;");
			
			//for radius treelist
			if(radius > 0.0f && radius < Float.MAX_VALUE){
				ps = conn.prepareStatement("SELECT tid, long, lat, date, size, age, name FROM trees WHERE (@(? - long) < ?) AND (@(? - lat) < ?);");
				ps.setFloat(1, userlocation[1]);
				ps.setFloat(2, distance_in_minutes);
				ps.setFloat(3, userlocation[0]);
				ps.setFloat(4, distance_in_minutes);
			}
			
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
			if(user.Rights >= User.SHOW_PICTURES && tempmap.size() > 0){
				Log.i(this.getClass().getSimpleName(), "get pictures from database");
				ps = conn.prepareStatement("SELECT tree_has_picture.tid, images.img_thumb FROM tree_has_picture INNER JOIN images ON tree_has_picture.pid = images.pid;");
				rs = ps.executeQuery();
				Log.i(this.getClass().getSimpleName(), ps.toString());
				Tree temptree;
				while(rs.next()){
					byte[] bytes = rs.getBytes(2);
					temptree = tempmap.get(rs.getInt(1));
					if(temptree != null) temptree.Images.add(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
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
	
	public ArrayList<Tree> getTreeListWithRadius(float[] userlocation, float radius, User user, Activity activity, int time_since_last_usage){
		ResultSet rs;
		
    	try {
    		Log.i("postgres","Go Postgres!");
			Class.forName("org.postgresql.Driver");
			Connection conn = DriverManager.getConnection(URL);
			
			//calculate radius
			float distance_in_minutes = (float) (radius / 1.852);
			
			if(userlocation[0] == 0.0f){
				distance_in_minutes = Float.MAX_VALUE;
			}
			
			PreparedStatement ps;
			
			ps = conn.prepareStatement("SELECT tid, long, lat, date, size, age, name FROM trees WHERE (@(? - long) < ?) AND (@(? - lat) < ?);");
			ps.setFloat(1, userlocation[1]);
			ps.setFloat(2, distance_in_minutes);
			ps.setFloat(3, userlocation[0]);
			ps.setFloat(4, distance_in_minutes);
			
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
			if(user.Rights >= User.SHOW_PICTURES && tempmap.size() > 0){
				Log.i(this.getClass().getSimpleName(), "get pictures from database");
				ps = conn.prepareStatement("SELECT tree_has_picture.tid, images.img_thumb FROM tree_has_picture INNER JOIN images ON tree_has_picture.pid = images.pid;");
				rs = ps.executeQuery();
				Log.i(this.getClass().getSimpleName(), ps.toString());
				Tree temptree;
				while(rs.next()){
					byte[] bytes = rs.getBytes(2);
					temptree = tempmap.get(rs.getInt(1));
					if(temptree != null) temptree.Images.add(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
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
	
	public ArrayList<Tree> getUsersTreeList( User user, Activity activity){
		ResultSet rs;
		
    	try {
    		Log.i("postgres","Go Postgres!");
			Class.forName("org.postgresql.Driver");
			Connection conn = DriverManager.getConnection(URL);
			
			PreparedStatement ps;
			
			ps = conn.prepareStatement("SELECT tid, long, lat, date, size, age, treename FROM users_trees WHERE uid = ?;");
			ps.setInt(1, user.ID);
			
			rs = ps.executeQuery();
			
			Log.i(this.getClass().getSimpleName(), ps.toString());
			
			HashMap<Integer, Tree> tempmap = new HashMap<Integer, Tree>();
			int count = 0;
		    while(rs.next()){
		    	Double[] location = {rs.getDouble(3), rs.getDouble(2)};
		    	Tree t = new Tree(rs.getInt(1), rs.getString(7), rs.getInt(6), rs.getDouble(5), location, rs.getTimestamp(4));
		    	tempmap.put(t.ID, t);
		    	++count;
    		}
		    Log.i(this.getClass().getSimpleName(), "Number of trees in DB: "+(count));
			rs.close();
			ps.close();
			//get pictures if the rights are enought
			
			Log.i(this.getClass().getSimpleName(), "get pictures from database");
			ps = conn.prepareStatement("SELECT tid, img_thumb FROM users_trees_images WHERE uid = ?;");
			ps.setInt(1, user.ID);
			rs = ps.executeQuery();
			Log.i(this.getClass().getSimpleName(), ps.toString());
			while(rs.next()){
				byte[] bytes = rs.getBytes(2);
				tempmap.get(rs.getInt(1)).Images.add(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
			}
			rs.close();
			ps.close();
			
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
	
	public boolean addTreeImage(int uid, int tid, Bitmap image){
		try {
    		Log.i("postgres","Go Postgres!");
			Class.forName("org.postgresql.Driver");
			Connection conn = DriverManager.getConnection(URL);
			Date d = new Date();
			int pid = addTreeImage(image, tid, conn, new java.sql.Timestamp(d.getTime()));
			addTreeImageRelation(pid, tid, conn);
			addUserImageRelation(uid,pid,conn);
			
			conn.close();
			return true;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	private int addTreeImage(Bitmap image, int treeID, Connection conn, java.sql.Timestamp date){
		int pid = 0;
		try {
    		Log.i("postgres","Go Postgres!");
			Class.forName("org.postgresql.Driver");
			
			//prepare and scale image
			Matrix matrix = new Matrix();
			matrix.preRotate(90);
	        Bitmap resizedThumb = Bitmap.createBitmap(Bitmap.createScaledBitmap(image, 160, 120, true), 0, 0, 160, 120, matrix, true);
	        Bitmap resized240 = Bitmap.createBitmap(Bitmap.createScaledBitmap(image, 320, 240, true), 0, 0, 320, 240, matrix, true);
	        Bitmap resized480 = Bitmap.createBitmap(Bitmap.createScaledBitmap(image, 640, 480, true), 0, 0, 640, 480, matrix, true);
	        Bitmap resized960 = Bitmap.createBitmap(Bitmap.createScaledBitmap(image, 1280, 960, true), 0, 0, 1280, 960, matrix, true);
			
			ByteArrayOutputStream baosThumb = new ByteArrayOutputStream();
			ByteArrayOutputStream baos240 = new ByteArrayOutputStream();
			ByteArrayOutputStream baos480 = new ByteArrayOutputStream();
			ByteArrayOutputStream baos960 = new ByteArrayOutputStream();
			resizedThumb.compress(Bitmap.CompressFormat.JPEG, 85, baosThumb);
			resized240.compress(Bitmap.CompressFormat.JPEG, 85, baos240);
			resized480.compress(Bitmap.CompressFormat.JPEG, 85, baos480);
			resized960.compress(Bitmap.CompressFormat.JPEG, 85, baos960);
			
			PreparedStatement ps = conn.prepareStatement("INSERT INTO images (date, img_thumb, img_240, img_480, img_960) VALUES (?, ?, ?, ?, ?) RETURNING pid;");
			ps.setTimestamp(1, date);
			ps.setBytes(2, baosThumb.toByteArray());
			ps.setBytes(3, baos240.toByteArray());
			ps.setBytes(4, baos480.toByteArray());
			ps.setBytes(5, baos960.toByteArray());
			
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
		Log.i(this.getClass().getSimpleName(), "Failure adding TreeImageRelation pid:"+ pid + " tid:"+tid);
		
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
	
	public Bitmap getHighResPicture(int tid, int picture_number, int connectionspeed) {
		// TODO Auto-generated method stub
		if(connectionspeed == Connectivity.NETWORK_CONNECTION_SPEED_UNKNOWN || connectionspeed == Connectivity.NETWORK_CONNECTION_SPEED_VERY_LOW){
			return null;
		}else{
			try {
	    		Log.i("postgres","Go Postgres!");
				Class.forName("org.postgresql.Driver");
				Connection conn = DriverManager.getConnection(URL);
				
				PreparedStatement ps;
				ResultSet rs;
				Bitmap picture = null;
				String[] column = {"img_240", "img_480", "img_960"};
				
				ps = conn.prepareStatement("SELECT ? images WHERE pid = ?;");
				ps.setString(1, column[connectionspeed-1]);
				ps.setInt(2, tid);
				
				rs = ps.executeQuery();
				while(rs.next()){
					picture_number --;
					if(picture_number == 0) break;
				}
				byte[] bytes = rs.getBytes(0);
			    picture = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
				ps.close();
				rs.close();
				return picture;
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public ArrayList<Group> getGroupList(int userID){
		ResultSet rs;
		
		try {
    		Log.i("postgres","Go Postgres!");
			Class.forName("org.postgresql.Driver");
			Connection conn = DriverManager.getConnection(URL);
			
			PreparedStatement ps = conn.prepareStatement("SELECT gid, name, pw FROM groups;");
			rs = ps.executeQuery();
		
			Log.i(this.getClass().getSimpleName(), ps.toString());
			HashMap<Integer, Group> groupmap = new HashMap<Integer, Group>();
			
			while(rs.next()){
				Group group = new Group();
				group.ID = rs.getInt(1);
				group.Name = rs.getString(2);
				group.Password = rs.getString(3);
				group.MemberList = new ArrayList<Integer>();
				groupmap.put(group.ID, group);
			}
			rs.close();
			ps.close();
			//check, if the statement changes DB entries
			ps = conn.prepareStatement("SELECT g.gid FROM member_of_group mog INNER JOIN groups g ON mog.gid = g.gid WHERE mog.uid = ?;");
			ps.setInt(1, userID);
			rs = ps.executeQuery();
			
			while(rs.next()){
				if(groupmap.containsKey(rs.getInt(1))){
					groupmap.get(rs.getInt(1)).MemberList.add(userID);
				}
			}
			rs.close();
			ps.close();
			conn.close();
			return new ArrayList<Group>(groupmap.values());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	return new ArrayList<Group>();
	}
	
	public boolean addGroup(int uid, String name, String password){
		try {
    		Log.i("postgres","Go Postgres!");
			Class.forName("org.postgresql.Driver");
			Connection conn = DriverManager.getConnection(URL);
			
			PreparedStatement ps = conn.prepareStatement("INSERT INTO groups (name, pw) VALUES (?, ?) RETURNING gid;");
			ps.setString(1, name);
			ps.setString(2, password);
			
			ResultSet rs = ps.executeQuery();
			
			if(rs.next()){
				Log.i(this.getClass().getSimpleName(), "Add Group");
				int gid = rs.getInt(1);
				ps.close();
				ps = conn.prepareStatement("INSERT INTO member_of_group (uid, gid) VALUES (?, ?);");
				ps.setInt(1, uid);
				ps.setInt(2, gid);
				if(ps.executeUpdate() >= 1){
					ps.close();
					Log.i(this.getClass().getSimpleName(), "User inserted to group.");
					return true;
				}
			}
			ps.close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
    	return false;
	}
	
	public boolean addUserToGroup(int groupID, int userID){
		try {
			Class.forName("org.postgresql.Driver");
			Connection conn = DriverManager.getConnection(URL);
			
			PreparedStatement ps = conn.prepareStatement("INSERT INTO member_of_group (uid, gid) VALUES (?, ?);");
			ps.setInt(1, userID);
			ps.setInt(2, groupID);
			
			if(ps.executeUpdate() >= 1){
				Log.i(this.getClass().getSimpleName(), "Add User To Group");
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
	
	public boolean removeUserFromGroup(int groupID, int userID){
		try {
			Class.forName("org.postgresql.Driver");
			Connection conn = DriverManager.getConnection(URL);
			
			PreparedStatement ps = conn.prepareStatement("DELETE FROM member_of_group WHERE uid = ? AND gid = ?;");
			ps.setInt(1, userID);
			ps.setInt(2, groupID);
			
			if(ps.executeUpdate() >= 1){
				Log.i(this.getClass().getSimpleName(), "Remove user from group: success");
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
				Log.i(this.getClass().getSimpleName(), "Add User");
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
	
	public boolean deleteUser(int userID){
		try {
			Class.forName("org.postgresql.Driver");
			Connection conn = DriverManager.getConnection(URL);
			
			PreparedStatement ps = conn.prepareStatement("BEGIN; " +
														 "DELETE FROM users WHERE uid = ?; " +
														 "DELETE FROM images WHERE pid NOT IN (select pid from user_took_picture); " +
														 "DELETE FROM trees WHERE tid NOT IN (select tid from tree_has_picture); " +
														 "COMMIT;");
			ps.setInt(1, userID);
			
			//gibt immer 0 zurück
			Log.i(this.getClass().getSimpleName(), "Execute Update Delete User: "+ps.executeUpdate());
			Log.i(this.getClass().getSimpleName(), "Remove user Transaction: success");
			
			ps.close();
			return true;	
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	return false;
	}
	
	private Connection connectionHandler(Context context) throws SQLException{
		if(Connectivity.isConnected(context)){
			return DriverManager.getConnection(URL);
		}
		return null;
	}
}