package module;

import java.util.ArrayList;
import java.util.Date;

import model.Article;
import model.User;
import net.arnx.jsonic.JSON;
import net.arnx.jsonic.JSONException;

import org.ice.exception.AccessDeniedException;
import org.ice.module.HttpModule;

public class TutorialModule extends HttpModule {
	
	/**
	 * Test db
	 * @throws Exception
	 */
	public void getUserTask() throws Exception {
		User user = new User();
		user.id = 45;
		user.load();
		echo("Hello "+user.username);
	}
	
	/**
	 * Test access control
	 * @throws Exception
	 */
	public void permissionTask() throws Exception	{
		throw new AccessDeniedException("You are not allowed to take this action");
	}
	
	/**
	 * Test error handler
	 * @throws Exception
	 */
	public void exceptionTask() throws Exception	{
		throw new Exception("Exception rồi này");
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
		String time = (String)getRequest().getSession("time");
		if (time == null)	{
			time = new Date().getTime() + "";
			getRequest().setSession("time", time);
			echo("The last timestamp is unspecified");
		} else {
			echo("The last timestamp is "+time);
		}
	}
	
	/**
	 * Test session - destroy
	 */
	public void logoutTask()	{
		getRequest().destroySession();
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
	
	public void joinTask() throws Exception	{
		Article article = new Article();
		echo(JSON.encode(article.fetchLatest()));
	}
	
	/**
	 * Test post dispatch
	 */
	public void postDispatch()	{
//		echo("<br />This string is appended in all tasks: <br />Base URL: "+getBaseUrl());
	}
}
