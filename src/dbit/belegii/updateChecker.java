package dbit.belegii;

public final class updateChecker {
	
	//returns tables that need to be removed from the buffer as String e.g. String("a,b,c")
	//return null if no match was found
	public String checkUpdate(String sql){
		return null;
	}
	
	//return true if table was found in sql query
	//otherwise returns false
	public boolean checkTable(String table,String sql){
		return false;
	}
}
