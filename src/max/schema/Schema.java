package max.schema;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import max.data.Dictionary;
import max.data.TransactionResponse;
import max.net.Connector;

@SuppressWarnings("serial")
public class Schema extends HashMap<String, SchemaProperty> {
	
	private String tableName = "";
	public String getTableName() {return tableName;}
	private String dbName = "";
	public String getDbName() {return dbName;}
	public Schema(String tableName, String dbName) {
		this.tableName = tableName;
		this.dbName = dbName;
	}
	
	public ReferenceInfo ref(String propertyName) {
		if(get(propertyName) != null) {
			SchemaProperty p = (SchemaProperty) get(propertyName);
			String columnName = p.name;
			return new ReferenceInfo(tableName, columnName, dbName);
		}
		return null;
	}

	
	
	public void setProperties(SchemaProperty... properties) {
		for(SchemaProperty property : properties) {
			put(property.name, property);
		}
	}
	
	public SchemaValidationResult validate(Dictionary data) {
	    for (Map.Entry<String, SchemaProperty> entry : entrySet()) {
	        String key = entry.getKey();
	        SchemaProperty sp = entry.getValue();

	        // Ver si existe
	        if (data.$(key) != null) {
	            // El dato existe.
	            SchemaValidationResult _r = sp.validate(data.$(key), key);

	            if (!_r.status) {
	                return _r;
	            }
	        } else if (sp.required) {
	            // El dato no existe y es requerido.
	            return new SchemaValidationResult(key, false, "The required property '" + key + "' is missing.");
	        }
	    }

	    // Si no se encontró ningún resultado en 'false', devolvemos un resultado en 'true'.
	    return new SchemaValidationResult("", true, "All properties passed validation.");
	}
	private class QueryAndParameters {
		public String query;
		public Dictionary params;
	}
	private QueryAndParameters create__generateQuery(Dictionary data) {
		Dictionary parameters = new Dictionary();
		StringBuilder _query = new StringBuilder();
		_query
		.append("INSERT INTO ")
		.append(tableName)
		.append(" (");
		// Por cada parámetro:
		for(Map.Entry<String, SchemaProperty> prop : entrySet()) {
			String key = prop.getKey();
			_query.append(key).append(", ");
		}
		_query.setLength(_query.length() - 2);
		_query.append(") SELECT ");
		for(Map.Entry<String, SchemaProperty> prop : entrySet()) {
			String key = prop.getKey();
			_query.append("@").append(key).append(", ");
			Object valueToAdd = data.$(key) == null ? prop.getValue().defaultValue : data.$(key);
			parameters.put(key, valueToAdd);
		}
		_query.setLength(_query.length() - 2);
		System.out.println(_query.toString());
		System.out.println(parameters.toString());
		return new QueryAndParameters() {{
			query = _query.toString();
			params = parameters;
		}};
	}
	private QueryAndParameters modify__generateQuery(Dictionary data, Dictionary where) {
		Dictionary parameters = new Dictionary();
		StringBuilder _query = new StringBuilder();
		_query
		.append("UPDATE ")
		.append(tableName)
		.append(" SET ");
		// Por cada parámetro:
		for(Map.Entry<String, Object> prop : data.entrySet()) {
			String key = prop.getKey();
			if(containsKey(key) && data.$(key) != null) {
				_query.append(key).append(" = @").append(key + "m").append(", ");
				parameters.put(key + "m", data.$(key));
			}
		}
		_query.setLength(_query.length() - 2);
		
		
		_query.append(" WHERE ");
		for(Map.Entry<String, Object> prop : where.entrySet()) {
			String key = prop.getKey();
			if(containsKey(key) && where.$(key) != null) {
				_query.append(key).append(" = @").append(key + "w").append(", ");
				parameters.put(key + "w", where.$(key));
			}
		}
		_query.setLength(_query.length() - 2);
		System.out.println(_query.toString());
		System.out.println(parameters.toString());
		return new QueryAndParameters() {{
			query = _query.toString();
			params = parameters;
		}};
	}
	
	private QueryAndParameters delete__generateQuery(Dictionary where) {
		Dictionary parameters = new Dictionary();
		StringBuilder _query = new StringBuilder();
		_query
		.append("DELETE FROM ")
		.append(tableName)
		.append(" WHERE ");
		for(Map.Entry<String, Object> prop : where.entrySet()) {
			String key = prop.getKey();
			if(containsKey(key) && where.$(key) != null) {
				_query.append(key).append(" = @").append(key).append(", ");
				parameters.put(key, where.$(key));
			}
		}
		_query.setLength(_query.length() - 2);
		System.out.println(_query.toString());
		System.out.println(parameters.toString());
		return new QueryAndParameters() {{
			query = _query.toString();
			params = parameters;
		}};
	}
	
	// Método CREATE (Único registro + Múltiples registros)
	public TransactionResponse<?> create(Dictionary data) throws SQLException, Exception {
		SchemaValidationResult svr = validate(data);
		TransactionResponse<?> res = TransactionResponse.create();
		if(svr.status) {
			QueryAndParameters q = create__generateQuery(data);
			res = new Connector(dbName).transact(q.query, q.params);
			
		} else {
			res.status = false;
			throw new Exception(svr.message);
		}
		return res;
		
    }
	public TransactionResponse<?> create(Dictionary... data) throws SQLException, Exception {
		TransactionResponse<?> f = TransactionResponse.create();
		f.status = true;
		for(Dictionary dic : data) {
			TransactionResponse<?> e = create(dic);
			f.status = f.status && e.status;
			f.rowsAffected += e.rowsAffected;
		}
		return f;
	}

	// Método MODIFY (Único registro sólamente)
	public TransactionResponse<?> modify(Dictionary newValues, Dictionary where) throws Exception {
		SchemaValidationResult svr = validate(newValues);
		TransactionResponse<?> res = TransactionResponse.create();
		if(svr.status) {
			QueryAndParameters q = modify__generateQuery(newValues, where);
			res = new Connector(dbName).transact(q.query, q.params);
			
		} else {
			res.status = false;
			throw new Exception(svr.message);
		}
		return res;
	}
	
	// Método DELETE (Único registro sólamente)
	public TransactionResponse<?> delete(Dictionary where) throws Exception {
		TransactionResponse<?> res = TransactionResponse.create();
		QueryAndParameters q = delete__generateQuery(where);
		res = new Connector(dbName).transact(q.query, q.params);
		return res;
	}
	
	public void compile() {
		Connector c = new Connector();
		try {
			c.transact("CREATE DATABASE IF NOT EXISTS " + this.dbName + ";");
			StringBuilder t = new StringBuilder();
			t.append("CREATE TABLE IF NOT EXISTS ");
			t.append(this.tableName);
			t.append("( ");
			for(SchemaProperty prop : this.values()) {
				t
				 .append(prop.name)
				 .append(" " + prop.getSQLTypeName(prop.type))
				 .append(prop.maxlength < Integer.MAX_VALUE ? "(" + prop.maxlength + ")" : "")
				 .append(prop.autoIncrement ? " AUTO_INCREMENT" : "")
				 .append(prop.required ? " NOT NULL" : "")
				 .append(prop.primary ? " PRIMARY KEY" : (prop.unique ? " UNIQUE" : ""))
				 .append(prop.ref != null ? " REFERENCES " + 
						 (prop.ref.getDbName() == this.dbName ? "" : prop.ref.getDbName() + ".") 
						 + prop.ref.getTableName() + "(" + prop.ref.getColumnName() + ")" : "")
				 .append(", ");
			}
			t.setLength(t.length() - 2);
			t.append(" );");
			
			new Connector(this.dbName).transact(t.toString());
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
