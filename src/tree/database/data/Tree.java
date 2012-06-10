package tree.database.data;

import java.sql.Date;
import java.util.ArrayList;

import android.graphics.Bitmap;

public class Tree {

	public int ID;
	public String Name;
	public int Age;
	public double Size;
	/**
	 * [lat, long]
	 */
	public float[] Location = new float[2];
	public ArrayList<Comment> Comments;
	public long Date;
	public ArrayList<Bitmap> Images;
	
	public Tree(int ID, String Name, int Age, double Size, float[] Location, Date Date){
		this.ID = ID;
		this.Name = Name;
		this.Age = Age;
		this.Size = Size;
		this.Date = Date.getTime();
		this.Location[0] = Location[0];
		this.Location[1] = Location[1];
	}
}
