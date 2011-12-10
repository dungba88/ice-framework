package org.ice.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.ice.Config;
import org.ice.logger.Logger;
import org.ice.utils.FieldUtils;

public abstract class Adapter {

	protected Connection connection;
	protected boolean autoCommit;
	protected Exception lastError = null;
	protected Statement batchStmt;
	
	public Exception getLastError() {
		return lastError;
	}

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	public void setAutoCommit(boolean autoCommit) {
		this.autoCommit = autoCommit;
	}

	public boolean isAutoCommit() {
		return autoCommit;
	}

	public ResultSet executeSelect(String query, Object data)
			throws SQLException {
		ParsedQuery parsed = parseQuery(query);
		if (Config.debugMode)
			debugSql(parsed, data);

		PreparedStatement statement = connection.prepareStatement(parsed.query);
		for (int i = 0; i < parsed.params.size(); i++) {
			statement.setObject(i + 1,
					FieldUtils.getValue(data, parsed.params.get(i)));
		}
		return statement.executeQuery();
	}

	public int executeUpdate(String query, Object data) throws SQLException {
		ParsedQuery parsed = parseQuery(query);
		if (Config.debugMode)
			debugSql(parsed, data);

		PreparedStatement statement = connection.prepareStatement(parsed.query);
		for (int i = 0; i < parsed.params.size(); i++) {
			statement.setObject(i + 1,
					FieldUtils.getValue(data, parsed.params.get(i)));
		}
		return this.doExecuteUpdate(statement);
	}
	
	public int doExecuteUpdate(PreparedStatement statement) throws SQLException {
		connection.setAutoCommit(isAutoCommit());
		int rs = -1;
		try {
			lastError = null;
			rs = statement.executeUpdate();
			if (!isAutoCommit())
				connection.commit();
		} catch (Exception ex) {
			if (!isAutoCommit()) {
				lastError = ex;
				connection.rollback();
				Logger.getLogger().log("Transaction is being rollback. Error: "
						+ ex.toString(), Logger.LEVEL_ERROR);
			}
		} finally {
			if (statement != null)
				statement.close();
		}
		return rs;
	}

	public boolean executeInsert(String query, Table data) throws SQLException {
		ParsedQuery parsed = parseQuery(query);
		if ((Boolean) Config.get("debugMode"))
			debugSql(parsed, data);

		PreparedStatement statement = connection.prepareStatement(parsed.query,
				Statement.RETURN_GENERATED_KEYS);
		for (int i = 0; i < parsed.params.size(); i++) {
			statement.setObject(i + 1,
					FieldUtils.getValue(data, parsed.params.get(i)));
		}
		return this.doExecuteInsert(statement, data);
	}

	public boolean doExecuteInsert(PreparedStatement statement, Table data) throws SQLException {
		ResultSet rs = null;
		try {
			lastError = null;
			statement.executeUpdate();
			if (!isAutoCommit())
				connection.commit();
			rs = statement.getGeneratedKeys();
			while (rs.next()) {
				FieldUtils.setValue(data, data.key, rs.getObject(1));
			}
		} catch (SQLException ex) {
			if (!isAutoCommit()) {
				lastError = ex;
				connection.rollback();
				Logger.getLogger().log("Transaction is being rollback. Error: "
						+ ex.toString(), Logger.LEVEL_ERROR);
			} else {
				throw ex;
			}
		} finally {
			if (statement != null)
				statement.close();
			if (rs != null)
				rs.close();
		}
		return true;
	}

	private void debugSql(ParsedQuery parsed, Object data) {
		StringBuilder builder = new StringBuilder(parsed.query);

		if (!parsed.params.isEmpty()) {
			builder.append(" (");
			for (String p : parsed.params) {
				Object obj = FieldUtils.getValue(data, p);
				builder.append("'" + obj + "',");
			}
			if (builder.charAt(builder.length() - 1) == ',') {
				builder.setCharAt(builder.length() - 1, ')');
			}
		}
		Logger.getLogger().log(builder.toString(), Logger.LEVEL_DEBUG);
	}

	protected Object extendObject(ResultSet rs, Object obj) throws SQLException {
		ResultSetMetaData rsmd = rs.getMetaData();
		int count = rsmd.getColumnCount();
		for (int i = 1; i <= count; i++) {
			try {
				String columnName = rsmd.getColumnLabel(i);
				FieldUtils.setValue(obj, columnName, rs.getObject(columnName));
			} catch (Exception ex) {
			}
		}
		return obj;
	}

	public abstract String getDriverName();

	public abstract boolean load(Table obj) throws Exception;

	public abstract ArrayList query(Table obj, String query) throws Exception;

	public abstract ArrayList select(Table obj, String where, String choice,
			String order, String group, int pageIndex, int pageSize)
			throws Exception;

	public abstract int update(Table obj, String fields, String where)
			throws Exception;

	public abstract boolean insert(Table obj, String fields) throws Exception;

	public abstract int delete(Table obj, String where) throws Exception;

	public abstract ArrayList selectJoin(Table primaryObj, Table foreignObj,
			String foreignKey, String where, String primaryChoice,
			String foreignChoice, String order, String group, int pageIndex,
			int pageSize, Class<? extends Table> returnClass) throws Exception;

	public abstract String getConnectionString(String host, String port,
			String dbName);

	protected ParsedQuery parseQuery(String query) {
		String parsedQuery = "";
		ArrayList<String> params = new ArrayList<String>();

		String[] frag = query.split(" ");
		for (String f : frag) {
			f = f.trim();
			if (f.isEmpty())
				continue;
			if (f.charAt(0) == '?') {
				params.add(f.substring(1));
				f = "?";
			}
			parsedQuery += f + " ";
		}

		return new ParsedQuery(parsedQuery, params);
	}

	class ParsedQuery {
		public String query;
		public ArrayList<String> params;

		public ParsedQuery(String query, ArrayList<String> params) {
			this.query = query;
			this.params = params;
		}
	}

	public void close() {
		try {
			if (connection != null && !connection.isClosed()) {
				connection.close();
			}
		} catch (Exception ex) {
			Logger.getLogger().log("Cannot close connection: " + ex.toString(),
					Logger.LEVEL_WARNING);
		}
	}

	public void startBatch() throws SQLException {
		connection.setAutoCommit(isAutoCommit());
		batchStmt = connection.createStatement();
	}
	
	public void addBatch(String sql) throws SQLException {
		batchStmt.addBatch(sql);
	}
	
	public void batchUpdate() throws SQLException {
		try {
			batchStmt.executeBatch();
			if (isAutoCommit()) connection.commit();
		} catch (SQLException ex) {
			if (isAutoCommit()) connection.rollback();
			throw ex;
		} finally {
			try {
				batchStmt.close();
				batchStmt = null;
			} catch (Exception ex) {}
		}
	}
}
