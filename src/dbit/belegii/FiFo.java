package dbit.belegii;

import java.util.LinkedList;
import java.util.Queue;

import android.util.Log;

public class FiFo extends Buffer{

	public FiFo(int size) {
		super(size);
	}

	private Queue<Query> data = new LinkedList<Query>();
	
	@Override
	public void add(Query query) {
		if(contains(query)){
			Log.i(this.getClass().getSimpleName(), "FiFo: nothing to add, size: "+data.size());
			return;
		}		
		if(data.size() == size){
			data.remove();
			Log.i(this.getClass().getSimpleName(), "FiFo: remove");
		}
		
		data.offer(query);
		Log.i(this.getClass().getSimpleName(), "FiFo: add, new size: "+data.size());
	}

	@Override
	public Query get(String query) {
		for(Query result : data){
			if(result.query.equals(query)) return result;
		}		
		return null;
	}
	
	private boolean contains(Query query){
		boolean contains = false;
		for(Query queuequery : data){
			if(queuequery.equals(query)) contains = true;
		}
		return contains;
	}

}
