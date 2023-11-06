package relation;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Relation {
	private Map<String, Database> databases;
	private Database database;	
	Table table = new Table();
	
	public Relation() {
		databases = new HashMap<String, Database>();
	}
	
	public void CreateDatabase(String name) {
		if(!databases.containsKey(name)) {
			Database db = new Database(name);
			databases.put(name, db);
			
			System.out.println("Base de donnee" +name+ "Creer");
		}
		else {
			System.out.println("La Base de donnee" +name+ "existe deja");
		}
	}
	
	public void useDatabase(String name) {
		if(databases.containsKey(name)) {
			database = databases.get(name);
			
			System.out.println("Utilisation de la bdd " +name);
		}else {
			System.out.println("La bdd" +name+ "N'existe pas");
		}
	}
	
	public boolean CreateTable(String tableName,String columns) {
		if(database != null) {
			return database.CreateTable(tableName, columns);
		}else {
			System.out.println("Aucune bdd n'est selectionner");
			return false;
		}
	}
	
	public void ShowTables() {
		if(database!=null) {
			database.showTables();
		}else {
			System.out.println("Aucune bdd n'est selectionner");
		}
	}
	
    private void displaySelectedData(String columnSelection, List<Map<String, Object>> selectedData) {
    	if (selectedData.isEmpty()) {
            System.out.println("No rows to display.");
            return; // Quittez la méthode car il n'y a rien à afficher
        }
    	// Affichez les noms des colonnes sélectionnées
        System.out.println(columnSelection);

        List<String> columns = new ArrayList<>(selectedData.get(0).keySet());
        Map<String, Integer> columnWidths = table.calculateDynamicColumnWidths(columns, selectedData);

        // Affichez la ligne supérieure du tableau
        table.printTableBorder(columns, columnWidths);

        // Affichez les noms de colonnes dans le tableau
        table.printColumnNames(columns, columnWidths);

        // Affichez la ligne médiane du tableau
        table.printTableBorder(columns, columnWidths);

        for (Map<String, Object> rowData : selectedData) {
            table.printDataRows(columns, columnWidths, rowData);
        }

        // Affichez la ligne inférieure du tableau
        table.printTableBorder(columns, columnWidths);
    }
    

    private void displayAllData(List<Map<String, Object>> data) {
        if (data.isEmpty()) {
            System.out.println("Aucune donnée à afficher.");
        } else {
            List<String> columns = new ArrayList<>(data.get(0).keySet());
            Map<String, Integer> columnWidths = table.calculateDynamicColumnWidths(columns, data);

            // Affichez la ligne supérieure du tableau
            table.printTableBorder(columns, columnWidths);

            // Affichez les noms de colonnes dans le tableau
            table.printColumnNames(columns, columnWidths);

            // Affichez la ligne médiane du tableau
            table.printTableBorder(columns, columnWidths);

            for (Map<String, Object> rowData : data) {
                table.printDataRows(columns, columnWidths, rowData);
            }

            // Affichez la ligne inférieure du tableau
            table.printTableBorder(columns, columnWidths);
        }
    }


    public void executeCommand(String command) {
        if (command.toLowerCase().startsWith("crear database ")) {
        	traitementCreateDatabaseCommand(command);
        } else if (command.toLowerCase().startsWith("usar ")) {
        	traitementUseDatabaseCommand(command);
        } else if (command.toLowerCase().startsWith("crear table ")) {
        	traitementCreateTableCommand(command);
        } else if (command.toLowerCase().startsWith("spettacolo tables")) {
            ShowTables();
        } else if (command.toLowerCase().startsWith("seleccionar ")) {
        	traitementSelectCommand(command);
        } else if (command.toLowerCase().startsWith("commit")) {
            saveDataToFile();
            System.out.println("Données sauvegardées dans le fichier.");
        } else if (command.toLowerCase().startsWith("load")) {
            loadDataFromFile(); 
            System.out.println("Données chargées à partir du fichier.");
        } else if (command.toLowerCase().startsWith("insertar into ")) {
        	traitementInsertIntoCommand(command);
        } else {
            System.out.println("Commande non reconnue : " + command);
        }
    }

    private void traitementCreateDatabaseCommand(String command) {
        // Votre logique pour gérer la création de la base de données
        String dbName = command.substring("crear database ".length());
        CreateDatabase(dbName);
    }

    private void traitementUseDatabaseCommand(String command) {
        // Votre logique pour gérer l'utilisation de la base de données
        String dbName = command.substring("usar ".length());
        useDatabase(dbName);
    }

    private void traitementCreateTableCommand(String command) {
        // Votre logique pour gérer la création de la table
        String regex = "crear table ([a-zA-Z]+) \\((.+)\\)";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(command);

        if (matcher.matches()) {
            String tableName = matcher.group(1);
            String columnDefinitions = matcher.group(2);
            boolean success = CreateTable(tableName, columnDefinitions);
            if (success) {
                System.out.println("Table '" + tableName + "' créée dans la base de données '" + database.getName() + "'.");
            } else {
                System.out.println("La table '" + tableName + "' existe déjà dans la base de données '" + database.getName() + "'.");
            }
        } else {
            System.out.println("Commande non reconnue : " + command);
        }
    }
    
    public List<Map<String, Object>> selectAllFromTable(String tableName) {
        if (database != null && database.tableExists(tableName)) {
            Table table = database.getTable(tableName);
            return table.getData();
        } else {
            System.out.println("La table '" + tableName + "' n'existe pas dans la base de données '" + database.getName() + "'.");
            return null;
        }
    }

    
    private void traitementSelectCommand(String command) {
        String regex = "SELECCIONAR (.+) FROM ([a-zA-Z]+)(?: WHERE (.+))?";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(command);

        if (matcher.matches()) {
            String columnSelection = matcher.group(1).trim();
            String tableName = matcher.group(2).trim();
            String whereClause = matcher.group(3);

            if (whereClause != null && !whereClause.isEmpty()) {
                // Handle SELECT command with WHERE clause
            	traitementSelectWithWhere(tableName, columnSelection, whereClause);
            } else {
                // Handle SELECT command without WHERE clause
            	traitementSelectWithoutWhere(tableName, columnSelection);
            }
        } else {
            System.out.println("Invalid SELECT command: " + command);
        }
    }

    private void traitementSelectWithWhere(String tableName, String columnSelection, String whereClause) {
        List<Map<String, Object>> filteredData = filterDonneeWithWhere(tableName, columnSelection, whereClause);

        if (columnSelection.equals("*")) {
            displaySelectedData("*", filteredData);
        } else {
            String[] selectedColumns = columnSelection.split(",");
            
            // Créez une liste avec uniquement les colonnes sélectionnées
            List<Map<String, Object>> selectedData = new ArrayList<>();
            for (Map<String, Object> rowData : filteredData) {
                Map<String, Object> selectedRow = new HashMap<>();
                for (String column : selectedColumns) {
                    String trimmedColumn = column.trim();
                    selectedRow.put(trimmedColumn, rowData.get(trimmedColumn));
                }
                selectedData.add(selectedRow);
            }

            displaySelectedData(columnSelection, selectedData);
        }
    }
    //traiterCommandeSélectionSansCondition
    private void traitementSelectWithoutWhere(String tableName, String columnSelection) {
        if (columnSelection.equals("*")) {
            List<Map<String, Object>> selectedData = selectAllFromTable(tableName);
            if (selectedData != null) {
                displayAllData(selectedData);
            }
        } else {
            List<Map<String, Object>> selectedData = selectFromTable(tableName, columnSelection);
            if (selectedData != null) {
                displaySelectedData(columnSelection, selectedData);
            }
        }
    }

    private List<Map<String, Object>> filterDonneeWithWhere(String tableName, String columnSelection, String whereClause) {
        try {
            // Get the table from the database
            Table table = database.getTable(tableName);

            // Split the WHERE clause into individual conditions
            String[] orConditions = whereClause.split("(?i)(\\s+OR\\s+)");

            List<Map<String, Object>> filteredData = new ArrayList<>();

            for (Map<String, Object> rowData : table.getData()) {
                boolean anyOrConditionSatisfied = false;

                for (String orCondition : orConditions) {
                    if (evaluateCombinedConditions(rowData, orCondition, columnSelection, "AND")) {
                        anyOrConditionSatisfied = true;
                        break; // If an OR condition is satisfied, no need to check the rest
                    }
                }

                if (anyOrConditionSatisfied) {
                    filteredData.add(rowData);
                }
            }

            if (filteredData.isEmpty()) {
                System.out.println("No rows match the conditions in the WHERE clause.");
            }

            return filteredData;
        } catch (Exception e) {
            // Handle the exception here (print an error message, log, etc.)
            System.err.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>(); // Return an empty list or null in case of an error
        }
    }


    private boolean evaluateSingleCondition(Map<String, Object> rowData, String condition, String columnSelection) {
        String[] parts;
        String operator;

        // Recherche de l'opérateur de condition
        if (condition.contains(">=")) {
            parts = condition.split(">=");
            operator = ">=";
        } else if (condition.contains("<=")) {
            parts = condition.split("<=");
            operator = "<=";
        } else if (condition.contains(">")) {
            parts = condition.split(">");
            operator = ">";
        } else if (condition.contains("<")) {
            parts = condition.split("<");
            operator = "<";
        } else if (condition.contains("=")) {
            parts = condition.split("=");
            operator = "=";
        } else {
            // Opérateur par défaut si aucun n'est spécifié
            parts = condition.split("=");
            operator = "=";
        }

        String columnToFilter = parts[0].trim();
        String valueToMatch = parts[1].trim();

        if (isColumnSelected(columnToFilter, columnSelection.split(","))) {
            Object columnValue = rowData.get(columnToFilter);

            if (columnValue != null) {
                String strColumnValue = columnValue.toString();

                // Comparaison en fonction de l'opérateur
                switch (operator) {
                    case ">":
                        if (strColumnValue.compareTo(valueToMatch) > 0) {
                            return true;
                        }
                        break;
                    case ">=":
                        if (strColumnValue.compareTo(valueToMatch) >= 0) {
                            return true;
                        }
                        break;
                    case "<":
                        if (strColumnValue.compareTo(valueToMatch) < 0) {
                            return true;
                        }
                        break;
                    case "<=":
                        if (strColumnValue.compareTo(valueToMatch) <= 0) {
                            return true;
                        }
                        break;
                    case "=":
                        if (strColumnValue.equals(valueToMatch)) {
                            return true;
                        }
                        break;
                        
                }
            }
        }

        return false;
    }

    private boolean evaluateCombinedConditions(Map<String, Object> rowData, String conditions, String columnSelection, String logicalOperator) {
        String[] conditionsArray;
        
        if (logicalOperator.equalsIgnoreCase("OR")) {
            conditionsArray = conditions.split("(?i)(\\s+OR\\s+)");
        } else {
            conditionsArray = conditions.split("(?i)(\\s+AND\\s+)");
        }

        if (logicalOperator.equalsIgnoreCase("OR")) {
            for (String condition : conditionsArray) {
                if (evaluateSingleCondition(rowData, condition, columnSelection)) {
                    return true; // If any OR condition is satisfied, return true
                }
            }
        } else {
            boolean allAndConditionsSatisfied = true;
            for (String condition : conditionsArray) {
                if (!evaluateSingleCondition(rowData, condition, columnSelection)) {
                    allAndConditionsSatisfied = false; // For "AND", return false if any condition is not satisfied
                    break;
                }
            }
            return allAndConditionsSatisfied;
        }

        return false; // Default return if no conditions are met
    }


    private boolean isColumnSelected(String columnInWhere, String[] selectedColumns) {
        for (String selectedColumn : selectedColumns) {
            String trimmedSelectedColumn = selectedColumn.trim();
            if (trimmedSelectedColumn.equals("*") || trimmedSelectedColumn.equals(columnInWhere)) {
                return true;
            }
        }
        return false;
    }

    
    private List<Map<String, Object>> selectFromTable(String tableName, String columnSelection) {
    	if (database != null && database.tableExists(tableName)) {
    		Table table = database.getTable(tableName);
    		List<Map<String, Object>> selectedData = new ArrayList<>();

	        // Split la sélection de colonnes par des virgules
	        String[] selectedColumns = columnSelection.split(",");
	        for (Map<String, Object> rowData : table.getData()) {
	            Map<String, Object> selectedRow = new HashMap<>();
	            for (String columnName : selectedColumns) {
	                String trimmedColumnName = columnName.trim();
	                if (table.columnExists(trimmedColumnName)) {
	                    selectedRow.put(trimmedColumnName, rowData.get(trimmedColumnName));
	                }
	            }
	            selectedData.add(selectedRow);
	        }
	        return selectedData;
	    } else {
	        System.out.println("La table '" + tableName + "' n'existe pas dans la base de données '" + database.getName() + "'.");
	        return null;
	    }
	}
    
    private void traitementInsertIntoCommand(String command) {
        Pattern pattern = Pattern.compile("INSERTAR INTO (\\w+) \\((.+)\\) VALUES \\((.+)\\)");
        Matcher matcher = pattern.matcher(command);

        if (matcher.matches()) {
            String tableName = matcher.group(1);
            String columnNames = matcher.group(2);
            String values = matcher.group(3);

            if (database != null && database.tableExists(tableName)) {
                Table table = database.getTable(tableName);
                String[] columnNamesArray = columnNames.split(",");
                String[] valuesArray = values.split(",");
                if (columnNamesArray.length == valuesArray.length) {
                    Map<String, Object> rowData = construireLigneDeData(columnNamesArray, valuesArray, table);
                    if (rowData != null) {
                        insertDataIntoTable(table, rowData, tableName);
                    }
                } else {
                    System.out.println("Le nombre de colonnes et de valeurs ne correspond pas.");
                }
            } else {
                System.out.println("La table '" + tableName + "' n'existe pas dans la base de données '" + database.getName() + "'.");
            }
        } else {
            System.out.println("Commande non reconnue : " + command);
        }
    }
    
    private Map<String, Object> construireLigneDeData(String[] columnNamesArray, String[] valuesArray, Table table) {
        Map<String, Object> rowData = new HashMap<>();
        for (int i = 0; i < columnNamesArray.length; i++) {
            String columnName = columnNamesArray[i].trim();
            String value = valuesArray[i].trim();
            if (table.columnExists(columnName)) {
                String columnType = table.getColumnType(columnName);
                Object columnValue = convertValue(value, columnType);
                if (columnValue != null) {
                    rowData.put(columnName, columnValue);
                } else {
                    return null;
                }
            } else {
                System.out.println("La colonne '" + columnName + "' n'existe pas dans la table ");
                return null;
            }
        }
        return rowData;
    }
    
    private Object convertValue(String value, String columnType) {
        if (columnType.equals("int")) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                System.out.println("La valeur doit être de type int.");
                return null;
            }
        } else if (columnType.equals("String")) {
            return value;
        } else if (columnType.equals("date")) {
            // Supprimer les guillemets simples autour de la date
            value = value.replaceAll("'", "");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date date = dateFormat.parse(value);
                return date;
            } catch (ParseException e) {
                System.out.println("La valeur doit être de type date au format 'yyyy-MM-dd'.");
                return null;
            }
        } else {
            System.out.println("Type de colonne non géré : " + columnType);
            return null;
        }
    }


    private void insertDataIntoTable(Table table, Map<String, Object> rowData, String tableName) {
        table.insertData(rowData);
        System.out.println("Données insérées avec succès dans la table '" + tableName + "'.");
    }

    public void saveDataToFile() {
        try {
            // Créez un flux de sortie pour écrire dans le fichier
            FileOutputStream fileOutputStream = new FileOutputStream("C:\\Users\\Henintsoa\\Documents\\github\\Tsinjo\\SQL\\src\\fichier\\file.txt");
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);

            // Écrivez les données de la classe DatabaseManager dans le fichier
            objectOutputStream.writeObject(databases);
            objectOutputStream.writeObject(database);

            // Fermez les flux
            objectOutputStream.close();
            fileOutputStream.close();

            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public void loadDataFromFile() {
        try {
            // Créez un flux d'entrée pour lire à partir du fichier
            FileInputStream fileInputStream = new FileInputStream("C:\\Users\\Henintsoa\\Documents\\github\\Tsinjo\\SQL\\src\\fichier\\file.txt");
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

            // Lisez les données de la classe DatabaseManager à partir du fichier
            databases = (Map<String, Database>) objectInputStream.readObject();
            database = (Database) objectInputStream.readObject();

            // Fermez les flux
            objectInputStream.close();
            fileInputStream.close();

      
        } catch (EOFException e) {
            System.err.println("Le fichier est vide. Aucune donnée n'a été chargée.");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }    
}
