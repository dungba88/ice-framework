/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package org.ice.db.adapters;

import java.sql.ResultSet;
import java.util.ArrayList;

import org.ice.db.AdapterFactory;
import org.ice.db.Viewer;

public class Table {

	protected String table;
	
	protected String key;
	
	protected IAdapter adapter;
	
	public Table()	{
		setupAdapter();
	}
	
	protected void setupAdapter()	{
		adapter = AdapterFactory.getAdapter();
	}
	
	public String key() {
		return key;
	}
	
	public String table() {
		return table;
	}
	
	public Exception getLastError() {
		return adapter.getLastError();
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
	
	public ArrayList query(String query) throws Exception {
		return adapter.query(this, query);
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
	
	public int insert(String fields) throws Exception {
		return adapter.insert(this, fields);
	}
	
	public int delete() throws Exception {
		return adapter.delete(this, null);
	}
	
	public int delete(String where) throws Exception {
		return adapter.delete(this, where);
	}
	
	public ResultSet selectQuery(String query) throws Exception {
		if (adapter instanceof AbstractAdapter) {
			return ((AbstractAdapter) adapter).executeSelect(query, this);
		}
		throw new UnsupportedOperationException();
	}
	
	public int updateQuery(String query, boolean raw) throws Exception {
		if (adapter instanceof AbstractAdapter) {
			if (raw)
				return ((AbstractAdapter) adapter).doExecuteUpdate(adapter.getConnection().prepareStatement(query));
			return ((AbstractAdapter) adapter).executeUpdate(query, this);
		}
		throw new UnsupportedOperationException();
	}
	
	public int insertQuery(String query, boolean raw) throws Exception {
		if (adapter instanceof AbstractAdapter) {
			if (raw)
				return ((AbstractAdapter) adapter).doExecuteInsert(adapter.getConnection().prepareStatement(query), this);
			return ((AbstractAdapter) adapter).executeInsert(query, this);
		}
		throw new UnsupportedOperationException();
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
	
	public void startBatch() throws Exception {
		adapter.startBatch();
	}
	
	public void addBatch(String sql) throws Exception {
		adapter.addBatch(sql);
	}
	
	public void batchUpdate() throws Exception {
		adapter.batchUpdate();
	}
}
