package tree.database.data;

import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
	public String Name;
	public int ID = 0;
	public String Password;
	public int Rights;
	public Bitmap Avatar;
	
	public User(){};
	
	public User(String Name, String Password, int Rights, Bitmap Avatar){
		this.Name = Name;
		this.Password = sha256(Password);
		this.Rights = Rights;
		this.Avatar = Avatar;
		System.out.println(toString());
	}
	
	public String toString(){
		return "ID: "+ID+" Name: "+Name+" Password: "+Password+" Rights: "+Rights;
	}
	
	public boolean testPassword(String Password){
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

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(ID);
		dest.writeString(Name);
		dest.writeString(Password);
		dest.writeInt(Rights);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		if(Avatar != null)Avatar.compress(Bitmap.CompressFormat.JPEG, 85, baos);
		dest.writeInt(baos.toByteArray().length);
		dest.writeByteArray(baos.toByteArray());
		
	}
	
	public void readFromParcel(Parcel source){
		ID = source.readInt();
		Name = source.readString();
		Password = source.readString();
		Rights = source.readInt();
		byte[] bytes = new byte[source.readInt()];
		source.readByteArray(bytes);
		Avatar = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
	}
}
