package module;

import java.util.ArrayList;
import java.util.Date;

import model.User;

import org.ice.exception.AccessDeniedException;
import org.ice.module.HttpModule;


public class IndexModule extends HttpModule {
	
	public void init()	{
		setContentType("text/html");
	}
	
	/**
	 * Test database - load a row using primary key
	 * @throws Exception
	 */
	public void indexTask() throws Exception	{
		this.view.setParam("baseUrl", this.getBaseUrl());
		this.view.setParam("resourceUrl", this.getResourceUrl());
		setTemplate("/index.fall.copy.html");
	}
	
	public void permissionTask() throws Exception	{
		throw new AccessDeniedException("You are not allowed to take this action");
	}
	
	/**
	 * Test error handler
	 * @throws Exception
	 */
	public void exceptionTask() throws Exception	{
		throw new AccessDeniedException("You are allowed to take this action");
	}
	
	/**
	 * Test database - select
	 * @throws Exception
	 */
	public void getAdminsTask() throws Exception	{
		User user = new User();
		ArrayList<User> users = user.getAdmins();
		for(User u: users)	{
			echo ("Hello "+u.username+"<br />");
		}
	}

	/**
	 * Test session - set/get
	 */
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
	
	/**
	 * Test session - destroy
	 */
	public void logoutTask()	{
		destroySession();
		echo("Session destroyed");
	}

	/**
	 * Test multi-word task
	 */
	public void anotherDayTask()	{
		echo("Hello this is the another-day task");
	}
	
	/**
	 * Test view
	 */
	public void viewTask()	{
		setTemplate("/index.htm");
		view.setParam("title", "hello");
		view.setParam("username", "griever");
	}
	
	/**
	 * Test post dispatch
	 */
	public void postDispatch()	{
//		echo("<br />This string is appended in all tasks: <br />Base URL: "+getBaseUrl());
	}
}
