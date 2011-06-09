package dbit.belegii;

public class Query {	
	
	public String query;
	public String result;
	private long last_use;
	private long usage;
	private long duration;
	
	public Query (String query, String result, long duration, long last_use){
		this.usage = 1;
		this.query = query;
		this.result = result;
		this.duration = duration;
		this.last_use = last_use;
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
		return duration;
	}
}
