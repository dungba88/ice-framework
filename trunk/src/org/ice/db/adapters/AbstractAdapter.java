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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;

import org.ice.Config;
import org.ice.utils.FieldUtils;
import org.ice.utils.LogUtils;

public abstract class AbstractAdapter implements IAdapter {

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

	protected ResultSet executeSelect(String query, Object data)
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

	protected int executeUpdate(String query, Object data) throws SQLException {
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
	
	protected int doExecuteUpdate(PreparedStatement statement) throws SQLException {
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
				LogUtils.log(Level.SEVERE, "Transaction is being rollback. Error: "
						+ ex.toString());
			}
		} finally {
			if (statement != null)
				statement.close();
		}
		return rs;
	}

	protected int executeInsert(String query, Table data) throws SQLException {
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

	protected int doExecuteInsert(PreparedStatement statement, Table data) throws SQLException {
		ResultSet rs = null;
		int result = -1;
		try {
			lastError = null;
			result = statement.executeUpdate();
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
				LogUtils.log(Level.SEVERE, "Transaction is being rollback. Error: "
						+ ex.toString());
			} else {
				throw ex;
			}
		} finally {
			if (statement != null)
				statement.close();
			if (rs != null)
				rs.close();
		}
		return result;
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
		LogUtils.log(Level.INFO, builder.toString());
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
			LogUtils.log(Level.WARNING, "Cannot close connection: " + ex.toString());
		}
	}

	public void startBatch() throws SQLException {
		connection.setAutoCommit(isAutoCommit());
		batchStmt = connection.createStatement();
	}
	
	public void addBatch(String sql) throws SQLException {
		batchStmt.addBatch(sql);
	}
	
	public void clearBatch() throws SQLException {
		batchStmt.clearBatch();
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