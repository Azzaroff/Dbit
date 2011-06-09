package dbit.belegii;


import java.util.PriorityQueue;

import android.util.Log;

public class LFU extends Buffer{

	private PriorityQueue<Query> data;
	private QueryComparatorUsage comparator = new QueryComparatorUsage();
	
	public LFU(int size) {
		super(size);
		data = new PriorityQueue<Query>(size, comparator);
	}
	
	@Override
	public void add(Query query) {
		if(contains(query)){
			Log.i(this.getClass().getSimpleName(), "nothing to add, size: "+data.size());
            return;
		}		
		if(data.size() >= this.size){
			Query erased = data.poll();
			Log.i(this.getClass().getSimpleName(), "polled: "+erased.query+" with "+erased.getUsage()+" uses");
		}
		
		data.offer(query);
		Log.i(this.getClass().getSimpleName(), "add, new size: "+data.size());
	}

	@Override
	public Query get(String query) {
		for(Query result : data){
			if(result.query.equals(query)){
				Log.i(this.getClass().getSimpleName(), "result found in cache");
				result.incUsage();
				data.remove(result);
				data.offer(result);
				Log.i(this.getClass().getSimpleName(), "Query was used: "+result.getUsage()+" times");
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

	@Override
	public void clear() {
		data.clear();		
	}

}
