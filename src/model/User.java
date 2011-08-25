package model;

import java.util.ArrayList;

public class User extends Base {
	
	public long id;
	public String username;
	public String role;
	public String expertField;
	public String name;

	public User()	{
		super();
		this.table = "users";
		this.key = "id";
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<User> getAdmins() throws Exception	{
		role = "admin";
		return this.select("role = ?role");
	}
	
	public void edit() throws Exception {
		role = "admin";
		username = "hehe";
		this.update("role,username");
	}

	public Object getInfo() {
		return this.view("username, id");
	}

	public Object getExperts() throws Exception {
		ArrayList<User> user = this.select("isExpert = 1", "id, name, expertField, avatar", "score DESC", null);
		return this.view(user, "id, name, expertField, avatar");
	}
}
