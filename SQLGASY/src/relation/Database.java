package relation;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Database implements Serializable{
	private String name;
	private Map<String, Table> tables;
	
	public Database() {
		
	}
	
	public Database(String name) {
		this.name = name;
		tables = new HashMap<String, Table>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<String, Table> getTables() {
		return tables;
	}

	public void setTables(Map<String, Table> tables) {
		this.tables = tables;
	}
	
	public boolean CreateTable(String TableName,String column) {
		if(!tables.containsKey(TableName)) {
			Table table = new Table(column);
			tables.put(TableName, table);
			return true;
		}
		else {
			return false;
		}
	}
	
	public boolean tableExists(String tableName) {
		return tables.containsKey(tableName);
	}
	
	public Table getTable(String tableName) {
		return tables.get(tableName);
	}
	
	public void showTables() {
		for(String TableName : tables.keySet()) { //renvoie un ensemble de toutes les clés (noms de tables)
			System.out.println(TableName);
		}
	}
}
