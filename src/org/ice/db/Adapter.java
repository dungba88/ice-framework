package org.ice.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

import org.ice.Config;
import org.ice.logger.Logger;
import org.ice.utils.FieldUtils;

public abstract class Adapter {
	
	private Connection connection;

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	public ResultSet executeSelect(String query, Object data) throws SQLException	{
		ParsedQuery parsed = parseQuery(query);
		if (Config.debugMode)
			debugSql(parsed, data);
		
		PreparedStatement statement = connection.prepareStatement(parsed.query);
		for(int i=0;i<parsed.params.size();i++)	{
			statement.setObject(i+1, FieldUtils.getValue(data, parsed.params.get(i)));
		}
		return statement.executeQuery();
	}
	
	public int executeUpdate(String query, Object data) throws SQLException {
		ParsedQuery parsed = parseQuery(query);
		if (Config.debugMode)
			debugSql(parsed, data);
		
		PreparedStatement statement = connection.prepareStatement(parsed.query);
		for(int i=0;i<parsed.params.size();i++)	{
			statement.setObject(i+1, FieldUtils.getValue(data, parsed.params.get(i)));
		}
		return statement.executeUpdate();
	}
	
	private void debugSql(ParsedQuery parsed, Object data) {
		StringBuilder builder = new StringBuilder(parsed.query);
		builder.append(" (");
		for(String p: parsed.params)	{
			Object obj = FieldUtils.getValue(data, p);
			builder.append("'"+obj+"',");
		}
		builder.append("END)");
		Logger.getLogger().log(builder.toString(), Logger.LEVEL_NOTICE);
	}

	protected Object extendObject(ResultSet rs, Object obj) throws SQLException	{
		ResultSetMetaData rsmd = rs.getMetaData();
		int count = rsmd.getColumnCount();
		for(int i=0; i<count; i++)	{
			try {
				String columnName = rsmd.getColumnName(i);
				FieldUtils.setValue(obj, columnName, rs.getObject(columnName));
			} catch (Exception ex) {
			}
		}
		return obj;
	}
	
	public abstract String getDriverName();

	public abstract boolean load(Table obj) throws Exception;
	
	public abstract ArrayList select(Table obj, String where, String choice, 
			String order, String group, int pageIndex, int pageSize) throws Exception;
	
	public abstract int update(Table obj, String fields, String where) throws Exception;
	
	public abstract String getConnectionString(String host, String port, String dbName);
	
	protected abstract ParsedQuery parseQuery(String query);
	
	class ParsedQuery {
		public String query;
		public ArrayList<String> params;
		
		public ParsedQuery(String query, ArrayList<String> params)	{
			this.query = query;
			this.params = params;
		}
	}
}
