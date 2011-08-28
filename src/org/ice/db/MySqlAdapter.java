package org.ice.db;

import java.sql.ResultSet;
import java.util.ArrayList;

public class MySqlAdapter extends Adapter {

	@Override
	public String getDriverName() {
		return "com.mysql.jdbc.Driver";
	}

	@Override
	public boolean load(Table obj) throws Exception {
		String query = "SELECT * FROM `"+obj.table+"` WHERE `"+obj.key+"` = ?"+obj.key;
		ResultSet rs = this.executeSelect(query, obj);
		if (rs.first())	{
			extendObject(rs, obj);
			return true;
		}
		return false;
	}
	
	@Override
	public int update(Table obj, String fields, String where) throws Exception {
		String[] fieldArr = fields.split(",");
		StringBuilder builder = new StringBuilder("UPDATE `"+obj.table+"` SET ");
		for(int i=0;i<fieldArr.length;i++)	{
			String f = fieldArr[i];
			builder.append(f+" = ?"+f);
			if (i<fieldArr.length-1)	{
				builder.append(" , ");
			}
		}
		
		if (where == null || where.isEmpty())	{
			where = obj.key + " = ?"+obj.key;
		}
		
		builder.append(" WHERE "+where);
		return this.executeUpdate(builder.toString(), obj);
	}
	
	public ArrayList select(Table obj, String where, String choice, 
			String order, String group, int pageIndex, int pageSize) throws Exception	{
		ArrayList list = new ArrayList();
		if (choice == null || choice.isEmpty()) 
			choice = "*";
		String query = "SELECT "+choice+" FROM `"+obj.table+"`";
		if (where != null && !where.isEmpty())
			query += " WHERE "+where;
		if (group != null && !group.isEmpty())
			query += " GROUP BY "+group;
		if (order != null && !order.isEmpty())
			query += " ORDER BY "+order;
		if (pageIndex >= 0 && pageSize > 0)
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
	public boolean insert(Table obj, String fields) throws Exception{
        String f = "(";
        String v = "(";
        String[] option = fields.split(",");
        for(int i = 0; i < option.length; i++){
            option[i] = option[i].trim();
            f += "`" + option[i] + "`";
            v += "?";
            if(i < (option.length - 1)){
                f += ",";
                v += ",";
            }
            else{
                f += ")";
                v += ")";
            }
        }
        return this.executeInsert("INSERT INTO `" + obj.table + "`" + f + " VALUES" + v, obj);
    }
	public int delete(Table obj, String where) throws Exception{
        ArrayList<Object> param = new ArrayList<Object>();
        if(where != null && !where.equals("")){
            if(where.indexOf("?") != -1){
                String[] params = where.split(" ");
                where = "WHERE ";
                for(int i = 0; i < params.length; i++){
                    if(params[i].charAt(0) == '?'){
                    	where += "? ";
                        try{
                            param.add(this.getClass().getField(params[i].substring(1)).get(this));
                        }
                        catch(Exception ex){}
                    }
                    else{
                    	where += params[i] + " ";
                    }
                }
            }
            else{
            	where = "WHERE " + where;
            }
        }
        else{
        	where = "";
        }
        return this.executeUpdate("DELETE FROM `" + obj.table + "` " + where, obj);
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
