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
		return new Viewer(this, fields).serialize();
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
	
	public boolean insert(String fields) throws Exception {
		return adapter.insert(this, fields);
	}
	
	public int delete() throws Exception {
		return adapter.delete(this, null);
	}
	
	public int delete(String where) throws Exception {
		return adapter.delete(this, where);
	}

	/**
	 * Select fields by joining 2 tables with constraint: N - 1
	 * @return default: list of object of returnClass. Should add extra field of "choices" into returnClass if needed.
	 * @param foreignObj: object of Class with table of 1 (has the foreign key or reference key).
	 * @param where: extra where except the Join-where (primary key = reference key).
	 * @param primaryChoice: what you want to select from primary table. Just give the name of field, WITHOUT name of table, WITH "as" for alias if needed.
	 * @param foreignChoice: like primaryChoice.
	 * @param order: pass the name of field to be ordered, WITH name of table if needed, then "ASC or DESC". Or just NULL for this.
	 * @param group: pass the name of field to be grouped, WITH name of table if needed.
	 * @param returnClass: class of object you want to return.
	 * */
	public ArrayList join(Table foreignObj, String foreignKey, String where, String primaryChoice,
			String foreignChoice, String order, String group, int pageIndex, int pageSize, Class<? extends Table> returnClass) throws Exception {
		return adapter.selectJoin(this, foreignObj, foreignKey, where, primaryChoice, foreignChoice, order, group, pageIndex, pageSize, returnClass);
	}
	
	public ArrayList primaryJoin(Table foreignObj, String foreignKey, String where, String primaryChoice,
			String foreignChoice, String order, String group, int pageIndex, int pageSize) throws Exception {
		return adapter.selectJoin(this, foreignObj, foreignKey, where, primaryChoice, foreignChoice, order, group, pageIndex, pageSize, this.getClass());
	}
	
	public ArrayList foreignJoin(Table primaryObj, String foreignKey, String where, String primaryChoice,
			String foreignChoice, String order, String group, int pageIndex, int pageSize) throws Exception {
		return adapter.selectJoin(primaryObj, this, foreignKey, where, primaryChoice, foreignChoice, order, group, pageIndex, pageSize, this.getClass());
	}
	
	public ArrayList join(Table primaryObj, String foreignKey, String primaryChoice,
			String foreignChoice, int pageIndex, int pageSize) throws Exception {
		return adapter.selectJoin(primaryObj, this, foreignKey, null, primaryChoice, foreignChoice, primaryObj.key+" DESC", null, pageIndex, pageSize, this.getClass());
	}
}
