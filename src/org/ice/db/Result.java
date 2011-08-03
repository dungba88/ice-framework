package org.ice.db;

public class Result {

	public Object data;
	
	public String msg;
	
	public boolean status;
	
	public Result(boolean status, String msg, Object data)	{
		this.status = status;
		this.msg = msg;
		this.data = data;
	}
}
