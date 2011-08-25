package module;

import model.User;
import net.arnx.jsonic.JSON;

import org.ice.db.Result;
import org.ice.module.HttpModule;

public abstract class BaseAjaxModule extends HttpModule {
	
	protected Result result;
	protected User viewer;
	
	public void init()	{
		viewer = (User) this.getSession("viewer");
		if (viewer == null)	{
			viewer = new User();
			viewer.id = -1;
		}
	}
	
	public void postDispatch()	{
		echo(JSON.encode(result));
	}
}
