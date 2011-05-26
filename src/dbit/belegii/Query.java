package dbit.belegii;

public class Query {	
	
	public String query;
	public String result;
	
	public Query (String query, String result){
		this.query = query;
		this.result = result;
	}

	public boolean equals(Object o){
		Query q = (Query) o;
		if(!q.query.equals(this.query)) return false;
		if(!q.result.equals(this.result)) return false;
		return true;
	}
}
