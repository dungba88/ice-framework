package model;

import java.util.ArrayList;

import org.ice.db.Table;

public class User extends Table {
	
	public long id;
	public String username;
	public String role;

	public User()	{
		super();
		this.table = "bk_users";
		this.key = "id";
	}
	
	public ArrayList<User> getAdmins() throws Exception	{
		role = "admin";
		return this.select("role = ?role");
	}
	
	public void edit() throws Exception {
		role = "admin";
		username = "hehe";
		this.update("role,username");
	}
}
