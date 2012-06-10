package tree.database.data;

public class Comment {

	public String Content;
	public long Date;
	public User User;
	
	public Comment(){
		Date = 0;
		User = new User();
		User.ID = -1;
	}
}
