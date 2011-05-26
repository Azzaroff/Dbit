package dbit.belegii;

import java.util.LinkedList;
import java.util.Queue;

import android.util.Log;

public class LRU extends Buffer{

	private Queue<Query> data = new LinkedList<Query>();
	
	public LRU(int size) {
		super(size);
	}

	@Override
	public void add(Query query) {
		if(contains(query)){
			Log.i(this.getClass().getSimpleName(), "nothing to add, size: "+data.size());
			return;
		}		
		if(data.size() == size){
			data.poll();
			Log.i(this.getClass().getSimpleName(), "poll");
		}
		
		data.offer(query);
		Log.i(this.getClass().getSimpleName(), "add, new size: "+data.size());
	}

	@Override
	public Query get(String query) {
		for(Query result : data){
			if(result.query.equals(query)){
				Log.i(this.getClass().getSimpleName(), "result found in cache");
				data.remove(result);
				data.offer(result);
				return result;
			}
		}
		Log.i(this.getClass().getSimpleName(), "no result found in cache");
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
