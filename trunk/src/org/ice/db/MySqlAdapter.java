package org.ice.db;

import java.sql.ResultSet;
import java.util.ArrayList;

public class MySqlAdapter extends Adapter {

	@Override
	public String getDriverName() {
		return "com.mysql.jdbc.Driver";
	}

	@Override
	public boolean load(String table, String key, Object obj) throws Exception {
		String query = "SELECT * FROM `"+table+"` WHERE `"+key+"` = ?"+key;
		ResultSet rs = this.executeSelect(query, obj);
		if (rs.first())	{
			extendObject(rs, obj);
			return true;
		}
		return false;
	}
	
	public ArrayList select(String table, Object obj, String where, String choice, 
			String order, String group, int pageIndex, int pageSize) throws Exception	{
		ArrayList list = new ArrayList();
		if (choice == null || choice.isEmpty()) 
			choice = "*";
		String query = "SELECT "+choice+" FROM `"+table+"`";
		if (where != null && !where.isEmpty())
			query += " WHERE "+where;
		if (order != null && !order.isEmpty())
			query += " ORDER BY "+order;
		if (group != null && !group.isEmpty())
			query += " GROUP BY "+group;
		if (pageIndex > 0 && pageSize > 0)
			query += " LIMIT "+pageIndex*pageSize+","+pageSize;
		
		ResultSet rs = this.executeSelect(query, obj);
		rs.beforeFirst();
		Class<?> c = obj.getClass();
		while(rs.next())	{
			Object newObj = c.newInstance();
			extendObject(rs, newObj);
			list.add(newObj);
		}
		
		return list;
	}

	@Override
	public String getConnectionString(String host, String port, String dbName) {
		return "jdbc:mysql://"+host+":"+port+"/"+dbName;
	}
	
	@Override
	protected ParsedQuery parseQuery(String query) {
		String parsedQuery = "";
		ArrayList<String> params = new ArrayList<String>();
		
		String[] frag = query.split(" ");
		for(String f: frag)	{
			f = f.trim();
			if (f.isEmpty())
				continue;
			if (f.charAt(0) == '?')	{
				params.add(f.substring(1));
				f = "?";
			}
			parsedQuery += f + " ";
		}
		
		return new ParsedQuery(parsedQuery, params);
	}
}
