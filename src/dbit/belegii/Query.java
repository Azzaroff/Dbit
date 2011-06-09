package dbit.belegii;

import android.os.Parcel;
import android.os.Parcelable;

public class Query implements Parcelable{	
	
	public String query;
	public String result;
	private long query_duration;
	
	public Query (String query, String result, long query_duration){
		this.query = query;
		this.result = result;
		this.query_duration = query_duration;
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
	
	public long getDuration(){
		return this.query_duration;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
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
