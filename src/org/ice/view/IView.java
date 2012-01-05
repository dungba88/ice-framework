package org.ice.view;

import org.ice.http.HttpRequest;
import org.ice.http.HttpResponse;

public interface View {

	public abstract void render(HttpRequest request, HttpResponse response);
}
