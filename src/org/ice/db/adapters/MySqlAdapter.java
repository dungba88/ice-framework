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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import org.ice.Config;
import org.ice.logger.Logger;
import org.ice.utils.FieldUtils;

public class MySqlAdapter extends AbstractAdapter {

	public String getDriverName() {
		return "com.mysql.jdbc.Driver";
	}

	public boolean load(Table obj) throws Exception {
		String query = "SELECT * FROM `"+obj.table+"` WHERE `"+obj.key+"` = ?"+obj.key;
		ResultSet rs = this.executeSelect(query, obj);
		if (rs.first())	{
			extendObject(rs, obj);
			rs.close();
			return true;
		}
		return false;
	}
	
	public ArrayList query(Table obj, String query) throws Exception {
		ResultSet rs = this.executeSelect(query, obj);
		rs.beforeFirst();
		Class<?> c = obj.getClass();
		ArrayList list = new ArrayList();
		while(rs.next())	{
			Object newObj = c.newInstance();
			extendObject(rs, newObj);
			list.add(newObj);
		}
		rs.close();
		return list;
	}
	
	public int update(Table obj, String fields, String where) throws Exception {
		String[] fieldArr = fields.split(",");
		StringBuilder builder = new StringBuilder("UPDATE `"+obj.table+"` SET ");
		for(int i=0;i<fieldArr.length;i++)	{
			String f = fieldArr[i].trim();
			if (!f.contains(" "))
				f += " = ?"+f;
			builder.append(f);
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
		rs.close();
		return list;
	}
	
	public boolean insert(Table obj, String fields) throws Exception{
        String f = "(";
        String v = "( ";
        String[] option = fields.split(",");
        for(int i = 0; i < option.length; i++){
            option[i] = option[i].trim();
            f += "`" + option[i] + "`";
            v += "?" + option[i];
            if(i < (option.length - 1)){
                f += ",";
                v += " , ";
            }
            else{
                f += ")";
                v += " )";
            }
        }
        return this.executeInsert("INSERT INTO `" + obj.table + "` " + f + " VALUES " + v, obj);
    }
	
	public int delete(Table obj, String where) throws Exception	{
		if (where == null || where.isEmpty())	{
			where = obj.key + " = ?"+obj.key;
		}
        return this.executeUpdate("DELETE FROM `" + obj.table + "` WHERE " + where, obj);
    }
	
	public String getConnectionString(String host, String port, String dbName) {
		String connectionString = null;
		if (port == null || port.isEmpty())
			connectionString = "jdbc:mysql://"+host+"/"+dbName+"?characterEncoding=UTF8";
		else
			connectionString = "jdbc:mysql://"+host+":"+port+"/"+dbName+"?characterEncoding=UTF8";
		return connectionString;
	}
	
	/**
	 * Select fields by joining 2 tables with constraint: N - 1
	 * @return default: list of object of returnClass. Should add extra field of "choices" into returnClass if needed.
	 * @param primaryObj: object of Class with table of N (has the primary key).
	 * @param foreignObj: object of Class with table of 1 (has the foreign key or reference key).
	 * @param where: extra where except the Join-where (primary key = reference key).
	 * @param primaryChoice: what you want to select from primary table. Just give the name of field, WITHOUT name of table, WITH "as" for alias if needed.
	 * @param foreignChoice: like primaryChoice.
	 * @param order: pass the name of field to be ordered, WITH name of table if needed, then "ASC or DESC". Or just NULL for this.
	 * @param group: pass the name of field to be grouped, WITH name of table if needed.
	 * @param returnClass: class of object you want to return.
	 * */
	public ArrayList selectJoin(Table primaryObj, Table foreignObj, String foreignKey, String where,
			String primaryChoice, String foreignChoice, String order, String group, int pageIndex,
			int pageSize, Class<? extends Table> returnClass) throws Exception {
		ArrayList list = new ArrayList();
		
		if (primaryChoice == null) {
			primaryChoice = primaryObj.table + ".*";
		}
		else if(!primaryChoice.isEmpty()) {
			String[] option = primaryChoice.split(",");
			primaryChoice = "";
	        for(int i = 0; i < option.length; i++){
	            primaryChoice += option[i].trim();
	            if(i < (option.length - 1)){
	            	primaryChoice += ",";
	            }
	        }
		}
		if (foreignChoice == null) {
			foreignChoice = foreignObj.table + ".*";
		} else if(!foreignChoice.isEmpty()) {
			String[] option = foreignChoice.split(",");
			foreignChoice = "";
	        for(int i = 0; i < option.length; i++){
	            foreignChoice += option[i].trim();
	            if(i < (option.length - 1)){
	            	foreignChoice += ",";
	            }
	        }
		}
		
		if(!primaryChoice.isEmpty() && !foreignChoice.isEmpty()) foreignChoice = "," + foreignChoice;
		String query = "SELECT "+primaryChoice + foreignChoice+" FROM `"+ primaryObj.table+"`, `" + foreignObj.table + "` ";
		if (foreignKey == null || foreignKey.isEmpty())
			query += "WHERE 1";
		else
			query += "WHERE " + primaryObj.table + "." + primaryObj.key + " = " + foreignObj.table + "." + foreignKey;
		
		ArrayList<Object> param = new ArrayList<Object>();
		if (where != null && !where.isEmpty())	{
			where = where.trim();
			boolean usingAND = !where.startsWith("OR");
			if(where.indexOf("?") != -1){
	            String[] params = where.split(" ");
	            where = "";
	            for(int i = 0; i < params.length; i++){
	                if(params[i].charAt(0) == '?'){
	                	where += "? ";
	                	if(params[i].indexOf(".") != -1){
	                		String []arr = params[i].split("\\.");
	                		if(primaryObj.table.equals(arr[0].substring(1))){
	                			param.add(FieldUtils.getValue(primaryObj, arr[1]));
	                		} else{
	                			param.add(FieldUtils.getValue(foreignObj, arr[1]));
	                		}
	                	}
	                }
	                else{
	                	where += params[i] + " ";
	                }
	            }
	        }
			if (usingAND)
				query += " AND (" + where + ") ";
			else
				query += " "+where;
		}
		
		if (group != null && !group.isEmpty())
			query += " GROUP BY "+group;
		if (order != null && !order.isEmpty())
			query += " ORDER BY "+order;
		if (pageIndex >= 0 && pageSize > 0)
			query += " LIMIT "+pageIndex*pageSize+","+pageSize;
		
		if (Config.debugMode){
			StringBuilder builder = new StringBuilder(query);
			
			if (!param.isEmpty())	{
				builder.append(" (");
				for(Object p: param)	{
					builder.append("'"+p+"',");
				}
				if (builder.charAt(builder.length()-1) == ',')	{
					builder.setCharAt(builder.length()-1, ')');
				}
			}
			Logger.getLogger().log(builder.toString(), Logger.LEVEL_DEBUG);
		}
		
		PreparedStatement statement = connection.prepareStatement(query);
		for(int i=0;i<param.size();i++)	{
			statement.setObject(i+1, param.get(i));
		}
		ResultSet rs = statement.executeQuery();
		rs.beforeFirst();
		if(returnClass == null) returnClass = primaryObj.getClass();
		while(rs.next())	{
			Object newObj = returnClass.newInstance();
			extendObject(rs, newObj);
			list.add(newObj);
		}
		rs.close();
		return list;
	}
}
