package dbit.belegii;

public class Query {	
	
	public String query;
	public String result;
	private long query_duration;
	
	public Query (String query, String result, long query_duration){
		this.query = query;
		this.result = result;
		this.query_duration = query_duration;
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
}
