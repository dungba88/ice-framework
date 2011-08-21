package org.ice.module.router;

import org.ice.exception.IceException;
import org.ice.http.HttpRequest;
import org.ice.module.IModule;

public interface IRouter {
	public IModule route(HttpRequest request) throws IceException;
}
