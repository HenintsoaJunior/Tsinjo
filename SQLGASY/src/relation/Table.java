package relation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Table implements Serializable {
    private static final long serialVersionUID = 7071817217418961870L;
    private List<Map<String, Object>> data; // pour stocker les données de la relation
    private Map<String, String> columns; // stocke les noms de colonnes de la relation

    
    public Table() {
        data = new ArrayList<>(); // Initialisation d'une nouvelle structure de colonnes vide.
        columns = new HashMap<>(); // Initialisation d'une nouvelle liste de données vide.
    }

    public Table(String columnDef) {
        data = new ArrayList<>(); // Initialisation d'une nouvelle structure de colonnes vide.
        columns = new HashMap<>(); // Initialisation d'une nouvelle liste de données vide.
        parseColumn(columnDef);
    }
    public List<Map<String, Object>> getData() {
        return data;
    }

    public void setData(List<Map<String, Object>> data) {
        this.data = data;
    }

    public Map<String, String> getColumns() {
        return columns;
    }

    public void setColumns(Map<String, String> columns) {
        this.columns = columns;
    }

    public void parseColumn(String columnDef) {
        String[] definitions = columnDef.split(",");
        for (String definition : definitions) {
            String[] decoupage = definition.trim().split(" ");
            if (decoupage.length == 2) { // vérification de deux parties distinctes
                String columnName = decoupage[0];
                String columnType = decoupage[1];

                if (columnType.equals("int") || columnType.equals("String") || columnType.equals("date")) {
                    columns.put(columnName, columnType);
                } else {
                    System.out.println("Type de colonne non valide");
                }
            }
        }
    }

    public void insertData(Map<String, Object> rowData) {
        data.add(rowData);
    }

    public boolean columnExists(String columnName) {
        return columns.containsKey(columnName);
    }

    public String getColumnType(String columnName) {
        return columns.get(columnName);
    }
}
