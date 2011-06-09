package dbit.belegii;

import java.util.PriorityQueue;

import android.os.SystemClock;
import android.util.Log;

public class LRU extends Buffer{

	private PriorityQueue<Query> data;
	private QueryComparatorLast_use comparator = new QueryComparatorLast_use();
	
	public LRU(int size) {
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
			Log.i(this.getClass().getSimpleName(), "polled: "+erased.query+" with "+erased.getLast_use()+" ms");
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
				result.setLast_use(SystemClock.elapsedRealtime());
				data.offer(result);
				Log.i(this.getClass().getSimpleName(), "Query was used at: "+result.getLast_use()+" ms");
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
