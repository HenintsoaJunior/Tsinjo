package relation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Table implements Serializable{
	private List<Map<String,Object>> data; //pour stoquer les donnee de la relation
	private Map<String, String> columns; //stoque les nom de columns de la relation
	
	
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
	
	public Table() {
		data = new ArrayList<Map<String,Object>>();// Initialisation d'une nouvelle structure de colonnes vide.
		columns = new HashMap<String, String>();// Initialisation d'une nouvelle liste de donnees vide.
	}
	
	public Table(String columnDef) {
		data = new ArrayList<Map<String,Object>>();// Initialisation d'une nouvelle structure de colonnes vide.
		columns = new HashMap<String, String>();// Initialisation d'une nouvelle liste de donnees vide.
		ParseColumn(columnDef);
	}
	//ajoute moi une type date sur ca 
	public void ParseColumn(String columnDef) {
		String[] definitions = columnDef.split(",");
		for(String definition : definitions) {
			String[] decoupage = definition.trim().split(" ");
			if(decoupage.length == 2){ //verification de deux partie distinct
				String columnName = decoupage[0];
				String columnType = decoupage[1];
				
				if(columnType.equals("int") || columnType.equals("String") || columnType.equals("date")) {
					columns.put(columnName, columnType);
				}
				else {
					System.out.println("Type de column non valide");
				}
			}
		}
	}
	
	public void insertData(Map<String, Object> rowdata) {
		data.add(rowdata);
	}
	
	public boolean columnExists(String columnName) {
		return columns.containsKey(columnName);
	}
	
	public String getColumnType(String columnName) {
		return columns.get(columnName);
	}
	

    public Map<String, Integer> calculateDynamicColumnWidths(List<String> columns, List<Map<String, Object>> data) {
        Map<String, Integer> columnWidths = new HashMap<>();
        
        // Initialisez les largeurs de colonnes avec la longueur des noms de colonnes
        for (String columnName : columns) {
            columnWidths.put(columnName, columnName.length());
        }

        // Parcourez les données pour trouver les valeurs les plus longues pour chaque colonne
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
        return String.format("%-" + width + "s", value);
    }

	
}
