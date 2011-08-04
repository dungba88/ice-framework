package org.ice.module.router;

import org.ice.exception.IceException;
import org.ice.http.HttpRequest;
import org.ice.module.IModule;
import org.ice.utils.StringUtils;

public class DefaultRouter {

	public IModule route(HttpRequest request) throws IceException	{
		String moduleName = request.getModuleName();
		moduleName = new StringBuilder("module.").append(StringUtils.ucwords(moduleName, '-', true).replaceAll("-", "")).append("Module").toString();
		try {
			Class<?> c = Class.forName(moduleName);
			Object obj = c.newInstance();
			if (obj instanceof IModule)
				return (IModule) obj;
		} catch (Exception ex)	{
			
		}
		throw new IceException("Module not found: "+moduleName, 404);
	}
}