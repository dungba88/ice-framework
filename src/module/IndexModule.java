package module;

import java.util.ArrayList;
import java.util.Date;

import model.User;

import org.ice.module.HttpModule;

public class IndexModule extends HttpModule {
	
	public void indexTask() throws Exception	{
		User user = new User();
		user.id = 1;
		user.load();
		echo ("Hello "+user.username);
	}
	
	public void getAdminsTask() throws Exception	{
		User user = new User();
		ArrayList<User> users = user.getAdmins();
		for(User u: users)	{
			echo ("Hello "+u.username+"<br />");
		}
	}

	public void sessionTask()	{
		String time = (String)getSession("time");
		if (time == null)	{
			time = new Date().getTime() + "";
			setSession("time", time);
			echo("The last timestamp is unspecified");
		} else {
			echo("The last timestamp is "+time);
		}
	}
	
	public void logoutTask()	{
		destroySession();
		echo("Session destroyed");
	}
	
	public void anotherDayTask()	{
		echo("Hello this is the another-day task");
	}
	
	public void postDispatch()	{
//		echo("<br />This string is appended in all tasks: <br />Base URL: "+getBaseUrl());
	}
}
