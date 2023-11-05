package relation;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Database implements Serializable{
    private String name;
    private Map<String, Table> tables;
    private static final long serialVersionUID = 8670155608876539662L;
    
    
    public Database() {
		super();
	}

	public Database(String name) {
        this.name = name;
        tables = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public boolean createTable(String tableName, String columnDefinitions) {
        if (!tables.containsKey(tableName)) { // Si la table n'existe pas 
            Table table = new Table(columnDefinitions);
            tables.put(tableName, table);
            return true;
        } else {
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
        System.out.println("Ireto avy n solaitra anaty base '" + name + "':");
        for (String tableName : tables.keySet()) {//renvoie un ensemble de toutes les clés (noms de tables)
            System.out.println(tableName);
        }
    }
}

