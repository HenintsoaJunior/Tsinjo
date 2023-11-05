package relation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Table implements Serializable{
    private Map<String, String> columns; // Attribut de la classe pour stocker les métadonnées des colonnes (noms et types de données).
    private List<Map<String, Object>> data; //// Attribut de la classe pour stocker les données de la table.
    private static final long serialVersionUID = -6714179368360311624L;


    public Map<String, String> getColumns() {
		return columns;
	}

	public void setColumns(Map<String, String> columns) {
		this.columns = columns;
	}

	public List<Map<String, Object>> getData() {
		return data;
	}

	public void setData(List<Map<String, Object>> data) {
		this.data = data;
	}
	
	public Table() {
		columns = new HashMap<>(); // Initialisation d'une nouvelle structure de colonnes vide.
        data = new ArrayList<>();	// Initialisation d'une nouvelle liste de donnees vide.
	}

	public Table(String columnDefinitions) {
        columns = new HashMap<>(); // Initialisation d'une nouvelle structure de colonnes vide.
        data = new ArrayList<>();	// Initialisation d'une nouvelle liste de donnees vide.
        parseColumnDefinitions(columnDefinitions); // Appel d'une methode pour analyser les définitions de colonnes fournies.
    }
	public void displaySelectedData(String columnSelection, List<Map<String, Object>> selectedData) {
	    if (!selectedData.isEmpty()) {
	        List<String> columns = new ArrayList<>(selectedData.get(0).keySet());
	        Map<String, Integer> columnWidths = calculateColumnWidths(columns);

	        // Affichez les noms des colonnes sélectionnées
	        printColumnNames(columns, columnWidths);

	        // Affichez la ligne supérieure du tableau
	        printTableBorder(columns, columnWidths);

	        for (Map<String, Object> rowData : selectedData) {
	            printDataRows(columns, columnWidths, rowData);
	        }

	        // Affichez la ligne inférieure du tableau
	        printTableBorder(columns, columnWidths);
	    }
	}
	public void displayAllData() {
	    if (data.isEmpty()) {
	        System.out.println("Aucune donnée à afficher.");
	    } else {
	        List<String> columns = new ArrayList<>(data.get(0).keySet());
	        Map<String, Integer> columnWidths = calculateColumnWidths(columns);
	        printTable(columns, columnWidths, data);
	    }
	}



    public Map<String, Integer> calculateColumnWidths(List<String> columns) {
        Map<String, Integer> columnWidths = new HashMap<>();
        for (String columnName : columns) {
            columnWidths.put(columnName, columnName.length());
        }

        for (Map<String, Object> rowData : data) {
            for (String columnName : columns) {
                String value = String.valueOf(rowData.get(columnName));
                int width = value.length();
                if (width > columnWidths.get(columnName)) {
                    columnWidths.put(columnName, width);
                }
            }
        }
        return columnWidths;
    }

    public void printTable(List<String> columns, Map<String, Integer> columnWidths, List<Map<String, Object>> tableData) {
        printTableBorder(columns, columnWidths);
        printColumnNames(columns, columnWidths);
        printTableBorder(columns, columnWidths);
        for (Map<String, Object> rowData : tableData) {
            printDataRows(columns, columnWidths, rowData);
        }
        printTableBorder(columns, columnWidths);
    }




    public void printTableBorder(List<String> columns, Map<String, Integer> columnWidths) {
        for (String columnName : columns) {
            System.out.print("+");
            for (int i = 0; i < columnWidths.get(columnName) + 2; i++) {
                System.out.print("-");
            }
        }
        System.out.println("+");
    }

    public void printColumnNames(List<String> columns, Map<String, Integer> columnWidths) {
        for (String columnName : columns) {
            System.out.print("| " + formatCell(columnName, columnWidths.get(columnName)) + " ");
        }
        System.out.println("|");
    }

    public void printDataRows(List<String> columns, Map<String, Integer> columnWidths, Map<String, Object> rowData) {
        for (String columnName : columns) {
            String value = String.valueOf(rowData.get(columnName));
            System.out.print("| " + formatCell(value, columnWidths.get(columnName)) + " ");
        }
        System.out.println("|");
    }




    public String formatCell(String value, int width) {
        int padding = width - value.length();
        StringBuilder cell = new StringBuilder(value);
        for (int i = 0; i < padding; i++) {
            cell.append(" ");
        }
        return cell.toString();
    }

    public void parseColumnDefinitions(String columnDefinitions) {
        String[] definitions = columnDefinitions.split(",");
        for (String definition : definitions) {
            String[] parts = definition.trim().split(" ");
            if (parts.length == 2) {
                String columnName = parts[0];
                String columnType = parts[1];
                if (columnType.equals("int") || columnType.equals("String")) {
                    columns.put(columnName, columnType);
                } else {
                    System.out.println("Type de colonne non pris en charge : " + columnType);
                }
            } else {
                System.out.println("colonne tsy mety : " + definition);
            }
        }
    }
    
    public List<Map<String, Object>> unionTables(Table table1, Table table2) {
        List<Map<String, Object>> result = new ArrayList<>();

        // Ajoutez les données de la première table à la résultat
        //result.addAll(data);

        // Ajoutez les données de la deuxième table à la résultat
        result.addAll(table1.data);
        result.addAll(table2.data);

        return result;
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
