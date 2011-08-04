package module;

import net.arnx.jsonic.JSON;

import org.ice.db.Result;
import org.ice.module.HttpModule;

public abstract class BaseAjaxModule extends HttpModule {
	
	protected Result result;
	
	public void postDispatch()	{
		echo(JSON.encode(result));
	}
}
