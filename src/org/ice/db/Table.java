package org.ice.db;

import java.util.ArrayList;

public class Table {

	protected String table;
	
	protected String key;
	
	protected Adapter adapter;
	
	public Table()	{
		setupAdapter();
	}
	
	protected void setupAdapter()	{
		adapter = AdapterFactory.getAdapter();
	}
	
	public Object view(String fields)	{
		return new Viewer(this, fields);
	}
	
	public ArrayList<Object> view(ArrayList<? extends Table> list, String fields)	{
		ArrayList<Object> result = new ArrayList<Object>();
		for(Object obj: list)	{
			result.add(new Viewer(obj, fields).serialize());
		}
		return result;
	}
	
	public boolean load() throws Exception	{
		return adapter.load(this);
	}
	
	public ArrayList select(String where) throws Exception	{
		return adapter.select(this, where, null, null, null, -1, -1);
	}
	
	public ArrayList select(String where, String choice, String order, String group) throws Exception	{
		return adapter.select(this, where, choice, order, group, -1, -1);
	}
	
	public ArrayList select(String where, String choice, String order, String group, int pageIndex, int pageSize) throws Exception	{
		return adapter.select(this, where, choice, order, group, pageIndex, pageSize);
	}
	
	public int update(String fields) throws Exception {
		return adapter.update(this, fields, null);
	}
	
	public int update(String fields, String where) throws Exception {
		return adapter.update(this, fields, where);
	}
}
