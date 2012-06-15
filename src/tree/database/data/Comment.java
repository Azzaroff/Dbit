package tree.database.data;

public class Comment {

	public String Content;
	public long Date;
	public User User;
	
	public Comment(){
		Date = 0;
		this.User = new User();
		User.ID = -1;
	}
	public Comment(User User, long Date, String Content){
		this.User= User;
		this.Date = Date;
		this.Content = Content;
	}
}
