package tree.database;

import android.os.Parcel;
import android.os.Parcelable;

public class Query implements Parcelable{	
	
	public String query;
	public String result;
	private long last_use;
	private long usage;
	private long query_duration;
	
	public Query (String query, String result, long duration, long last_use){
		this.usage = 1;
		this.query = query;
		this.result = result;
		this.query_duration = duration;
		this.last_use = last_use;
	}

	public Query(Parcel in) {
		query = in.readString();
		result = in.readString();
		query_duration = in.readLong();
	}

	public boolean equals(Object o){
		Query q = (Query) o;
		if(!q.query.equals(this.query)) return false;
		if(!q.result.equals(this.result)) return false;
		return true;
	}
	
	
	public long incUsage(){
		return usage++;
	}
	
	public long getUsage(){
		return usage;
	}

	public void setLast_use(long last_use) {
		this.last_use = last_use;
	}

	public long getLast_use() {
		return last_use;
	}

	public long getDuration() {
		return query_duration;
	}

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(query);
		dest.writeString(result);
		dest.writeLong(query_duration);		
	}
	
    public static final Creator<Query> CREATOR = new Creator<Query>() {
        public Query createFromParcel(Parcel in) {
            return new Query(in);
        }

        public Query[] newArray(int size) {
            return new Query[size];
        }
    };
}
