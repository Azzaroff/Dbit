package tree.database;

import android.os.Parcelable;

public abstract class Buffer implements Parcelable{
	
    protected int size;
    public final int bufferTypeID; // 0..fifo, 1..lru, 2..lfu
    
    public Buffer(int size, int bufferTypeID){
    	this.size = size;
    	this.bufferTypeID = bufferTypeID;
    }
	
	abstract public void add(Query query);
	abstract public Query get(String query);
	abstract public void clear();
	abstract public int getNumberofElements();
	/**
	 * Entfernt alle Elemente aus dem Buffer, die eine der Tabellen tables enthält.
	 * @param tables
	 */
	abstract public void cleanBuffer(String tables);
}


