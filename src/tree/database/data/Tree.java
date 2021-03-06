package tree.database.data;

import java.util.ArrayList;
import java.util.Comparator;

import android.graphics.Bitmap;

public class Tree  implements Comparable<Tree>{

	public int ID;
	public String Name;
	public int Age;
	public double Size;
	/**
	 * [lat, long]
	 */
	public Double[] Location = new Double[2];
	public ArrayList<Comment> Comments;
	public long Date;
	public ArrayList<Bitmap> Images;
	
	public Tree(){
		this.ID = -1;
		this.Name = "Fnord";
		this.Age = -1;
		this.Size = -1;
		this.Date = 0;
		this.Location[0] = Double.MAX_VALUE;
		this.Location[1] = Double.MAX_VALUE;
		this.Images = new ArrayList<Bitmap>();
	}
	
	public Tree(int ID, String Name, int Age, double Size, Double[] Location, java.sql.Timestamp Date){
		this.ID = ID;
		this.Name = Name;
		this.Age = Age;
		this.Size = Size;
		this.Date = Date.getTime();
		this.Location[0] = Location[0];
		this.Location[1] = Location[1];
		this.Images = new ArrayList<Bitmap>();
	}
	
	public String toString(){
		return "ID: "+ID+" Name: "+Name+" Age: "+Age+" Size: "+Size+
				" Date: "+Date+" Lat: "+Location[0]+" Long: "+Location[1];
	}
	
	public int compareTo(Tree another) {
		return this.Name.compareTo(another.Name);
	}
}
