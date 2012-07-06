package tree.database.data;

import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class User implements Parcelable {
	
	public static final int INITIAL_RIGHT = 0; 
	public static final int SHOW_PICTURES = 0;
	public static final int READ_COMMENTS = 1;
	public static final int READ_WRITE_COMMENTS = 2;
	public static final int CREATE_GROUP = 3;
	
	public String Name;
	public int ID = 0;
	public String Password;
	public int Rights;
	public Bitmap Avatar;
	
	public User(){};
	
	//minimal c#tor for comment list
	public User(int ID, String Name, Bitmap Avatar){
		this.ID = ID;
		this.Name = Name;
		this.Avatar = Avatar;
	}
	
	public User(String Name, String Password, int Rights, Bitmap Avatar){
		this.Name = Name;
		this.Password = sha256(Password);
		this.Rights = Rights;
		this.Avatar = Avatar;
		System.out.println(toString());
	}
	
	public User(Parcel in){
		readFromParcel(in);
	}
	
	public String toString(){
		return "ID: "+ID+" Name: "+Name+" Password: "+Password+" Rights: "+Rights;
	}
	
	public boolean testPassword(String Password){
		Log.i(this.getClass().getSimpleName(), "test password");
		return this.Password.equals(sha256(Password));
	}
	
	public String setNewPassword(String Password){
		this.Password = sha256(Password);
		return this.Password;
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
	
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		public User createFromParcel(Parcel in) {
            return new User(in);
        }

		public User[] newArray(int size) {
			// TODO Auto-generated method stub
			return new User[size];
		}
 
    };

	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(ID);
		dest.writeString(Name);
		dest.writeString(Password);
		dest.writeInt(Rights);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		if(Avatar != null){
			Avatar.compress(Bitmap.CompressFormat.JPEG, 85, baos);
			dest.writeInt(baos.toByteArray().length);
			dest.writeByteArray(baos.toByteArray());
		}else{
			dest.writeInt(-1);
		}
		
	}
	
	public void readFromParcel(Parcel in){
		ID = in.readInt();
		Name = in.readString();
		Password = in.readString();
		Rights = in.readInt();
		int length = in.readInt();
		if(length >= 0){
			byte[] bytes = new byte[length];
			in.readByteArray(bytes);
			Avatar = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
		}else{
			Avatar = null;
		}
	}
	

	public Boolean eqals(Object o){
		User tempuser = (User)o;
		return ID == (tempuser).ID 
				&& Name.equals(tempuser.Name) 
				&& Rights == (tempuser.Rights);
	}
	
}
