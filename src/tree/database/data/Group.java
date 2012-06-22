package tree.database.data;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import android.util.Log;

public class Group {

	public String Name;
	public ArrayList<Integer> MemberList;
	public String Password;
	public int ID;
	
	public Group(){
		this.Name = "";
		this.Password = sha256("");
		this.ID = -1;
		this.MemberList = new ArrayList<Integer>();
	}
	
	public Group(String Name, String Password){
		this.Name = Name;
		this.Password = sha256(Password);
//		Log.i(this.getClass().getSimpleName(), "Password: "+Password+" Hash: "+this.Password);
	}
	
	public boolean equals(Object o) {  
		return ID == ((Group)o).ID && Name.equals(((Group)o).Name);
	}  
	   
	public int hashCode() {  
		return Name.hashCode();  
	}
	
	public boolean testPassword(String Password){
//		Log.i(this.getClass().getSimpleName(), "test password");
//		Log.i(this.getClass().getSimpleName(), "pwd: "+(sha256(Password)));
//		Log.i(this.getClass().getSimpleName(), "test password "+this.Password);
		return this.Password.equals(sha256(Password));
	}
	
	private String sha256(String s) {
	    try {
	        // Create MD5 Hash
	        MessageDigest digest = java.security.MessageDigest.getInstance("SHA256");
	        digest.update(s.getBytes());
	        byte messageDigest[] = digest.digest();
	        
	        // Create Hex String
	        StringBuffer hexString = new StringBuffer();
	        for (int i=0; i<messageDigest.length; i++)
	            hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
	        return hexString.toString();
	        
	    } catch (NoSuchAlgorithmException e) {
	        e.printStackTrace();
	    }
	    return "";
	}
}
