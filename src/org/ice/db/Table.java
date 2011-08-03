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
	
	public boolean load() throws Exception	{
		return adapter.load(table, key, this);
	}
	
	public ArrayList select(String where) throws Exception	{
		return adapter.select(table, this, where, null, null, null, -1, -1);
	}
	
	public ArrayList select(String where, String choice, String order, String group) throws Exception	{
		return adapter.select(table, this, where, choice, order, group, -1, -1);
	}
	
	public ArrayList select(String where, String choice, String order, String group, int pageIndex, int pageSize) throws Exception	{
		return adapter.select(table, this, where, choice, order, group, pageIndex, pageSize);
	}
}
