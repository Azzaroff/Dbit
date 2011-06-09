package dbit.belegii;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import android.os.Parcel;
import android.util.Log;

public class LRU extends Buffer{

	private Queue<Query> data = new LinkedList<Query>();
	
	public LRU(int size) {
		super(size, 1);
	}
	
	@SuppressWarnings("unchecked")
	public LRU(Parcel in) {
		super(in.readInt(), in.readInt());
		in.readList((List<Query>)this.data, null);
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

	@Override
	public void clear() {
		data.clear();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(size);
		dest.writeInt(bufferTypeID);
		dest.writeList((List<Query>)data);		
	}
	
    public static final Creator<LRU> CREATOR = new Creator<LRU>() {
        public LRU createFromParcel(Parcel in) {
            return new LRU(in);
        }

        public LRU[] newArray(int size) {
            return new LRU[size];
        }
    };
}
