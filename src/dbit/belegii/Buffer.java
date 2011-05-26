package dbit.belegii;

public abstract class Buffer {
	
    protected int size;
    
    public Buffer(int size){
    	this.size = size;
    }
	
	abstract public void add(Query query);
	abstract public Query get(String query);
}


