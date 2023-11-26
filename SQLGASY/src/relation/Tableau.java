package relation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tableau {
    private Map<String, Integer> columnWidths;

    public Tableau() {
        columnWidths = new HashMap<>();
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
