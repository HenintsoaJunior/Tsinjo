package relation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import file.File;

public class Relation {
	private Map<String, Database> databases;
	private Database database;	
	private Table table = new Table();
	private Tableau tableau = new Tableau();
	
	public Relation() {
		databases = new HashMap<String, Database>();
	}
	
	/*************************************************COMMMANDE*************************************************/
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
            File.saveDataToFile(databases,database);
            System.out.println("Données sauvegardées dans le fichier.");
        } else if (command.toLowerCase().startsWith("load")) {
            File.loadDataFromFile(databases,database); 
            System.out.println("Données chargées à partir du fichier.");
        } else if (command.toLowerCase().startsWith("insertar into ")) {
            traitementInsertIntoCommand(command);
        } else if (command.toLowerCase().startsWith("natural ")) {
            traitementJointureCommand(command);
        } else if (command.toLowerCase().startsWith("cartesian ")) {
            traitementJointureCommand(command);
        }else if (command.toLowerCase().startsWith("theta ")) {
            traitementJointureCommand(command);
        }  
        else {
            System.out.println("Commande non reconnue : " + command);
        }
    }


/******************************************JOINTURE***********************************************/
    
    private void traitementJointureCommand(String command) {
        if (isNaturalJoinCommand(command)) {
            String[] parts = command.split(" ");
            String tableName1 = parts[2];
            String tableName2 = parts[4];
            processNaturalJoin(tableName1, tableName2);
        } else if (isCartesianJoinCommand(command)) {
            String[] parts = command.split(" ");
            String tableName1 = parts[2];
            String tableName2 = parts[4];
            cartesianProduct(tableName1, tableName2);
        } else if (isThetaJoinCommand(command)) {
            String[] parts = command.split(" ");
            String tableName1 = parts[2];
            String tableName2 = parts[4];
            String conditionColumn = parts[6]; // Supposons que la condition est spécifiée après le WITH
            thetaJoin(tableName1, tableName2, conditionColumn);
        } else {
            System.out.println("Commande de jointure non valide : " + command);
        }
    }

    private boolean isThetaJoinCommand(String command) {
        String[] parts = command.split(" ");
        return (parts.length >= 8 && parts[0].equalsIgnoreCase("theta")
                && parts[1].equalsIgnoreCase("JOIN") && parts[3].equalsIgnoreCase("WITH")
                && parts[5].equalsIgnoreCase("ON"));
    }
    

    private List<Map<String, Object>> performThetaJoin(Table table1, Table table2, String conditionColumn) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> row1 : table1.getData()) {
            for (Map<String, Object> row2 : table2.getData()) {
                if ((int)row1.get(conditionColumn) == (int)row2.get(conditionColumn)) {
                    Map<String, Object> resultRow = new HashMap<>();
                    resultRow.putAll(row1);
                    resultRow.putAll(row2);
                    result.add(resultRow);
                }
            }
        }
        displayAllData(result);
        return result;
    }
    
    private List<Map<String, Object>> thetaJoin(String tableName1, String tableName2, String conditionColumn) {
        if (database != null && database.tableExists(tableName1) && database.tableExists(tableName2)) {
            Table table1 = database.getTable(tableName1);
            Table table2 = database.getTable(tableName2);
            return performThetaJoin(table1, table2, conditionColumn);
        } else {
            System.out.println("Une ou plusieurs tables n'existent pas dans la base de données.");
            return null;
        }
    }

	public Table cartesianProduct(Table table1, Table table2) {
	    if (table1 == null || table2 == null) {
	        // Vérifiez que les tables ne sont pas nulles
	        System.out.println("Les tables ne doivent pas être nulles.");
	        return null;
	    }

	    Table resultTable = new Table();

	    // Créez le schéma de la table résultante
	    Map<String, String> schema = new HashMap<>(table1.getColumns());
	    schema.putAll(table2.getColumns());
	    resultTable.setColumns(schema);

	    // Effectuez le produit cartésien
	    List<Map<String, Object>> resultData = new ArrayList<>();
	    for (Map<String, Object> row1 : table1.getData()) {
	        for (Map<String, Object> row2 : table2.getData()) {
	            Map<String, Object> combinedRow = new HashMap<>(row1);
	            combinedRow.putAll(row2);
	            resultData.add(combinedRow);
	        }
	    }

	    resultTable.setData(resultData);
	    return resultTable;
	}
	
	private void cartesianProduct(String tableName1, String tableName2) {
	    Table table1 = database.getTable(tableName1);
	    Table table2 = database.getTable(tableName2);

	    if (table1 != null && table2 != null) {
	        Table resultTable = cartesianProduct(table1, table2);
	        displayAllData(resultTable.getData());
	    } else {
	        System.out.println("Les tables spécifiées n'existent pas dans la base de données.");
	    }
	}
	
	
	private boolean isCartesianJoinCommand(String command) {
	    String[] parts = command.split(" ");

	    return (parts.length == 5 && parts[0].equalsIgnoreCase("cartesian") && parts[1].equalsIgnoreCase("JOIN")
	            && parts[3].equalsIgnoreCase("WITH"));
	}
	
	
	private boolean isNaturalJoinCommand(String command) {
	    String[] parts = command.split(" ");

	    return (parts.length == 5 && parts[0].equalsIgnoreCase("natural") && parts[1].equalsIgnoreCase("JOIN")
	            && parts[3].equalsIgnoreCase("WITH"));
	}
	
	
	private void processNaturalJoin(String tableName1, String tableName2) {
	    if (database != null && database.tableExists(tableName1) && database.tableExists(tableName2)) {
	        List<Map<String, Object>> result = naturalJoin(tableName1, tableName2);

	        if (result != null) {
	            displayAllData(result);
	        }
	    } else {
	        System.out.println("Les tables spécifiées n'existent pas dans la base de données.");
	    }
	}
	
	
	private List<String> getCommonAttributes(Table table1, Table table2) {
	    List<String> commonAttributes = new ArrayList<>();
	    for (String attr1 : table1.getColumns().keySet()) {
	        if (table2.getColumns().containsKey(attr1)) {
	            commonAttributes.add(attr1);
	        }
	    }
	    return commonAttributes;
	}

	
	private List<Map<String, Object>> performNaturalJoin(Table table1, Table table2, List<String> commonAttributes) {
	    List<Map<String, Object>> result = new ArrayList<>();
	    for (Map<String, Object> row1 : table1.getData()) {
	        for (Map<String, Object> row2 : table2.getData()) {
	            boolean match = true;
	            for (String attr : commonAttributes) {
	                if (!row1.get(attr).equals(row2.get(attr))) {
	                    match = false;
	                    break;
	                }
	            }
	            if (match) {
	                Map<String, Object> resultRow = new HashMap<>();
	                resultRow.putAll(row1);
	                resultRow.putAll(row2);
	                result.add(resultRow);
	            }
	        }
	    }
	    return result;
	}

	
	private List<Map<String, Object>> naturalJoin(String tableName1, String tableName2) {
	    if (database != null && database.tableExists(tableName1) && database.tableExists(tableName2)) {
	        Table table1 = database.getTable(tableName1);
	        Table table2 = database.getTable(tableName2);
	        List<String> commonAttributes = getCommonAttributes(table1, table2);
	        if (!commonAttributes.isEmpty()) {
	            return performNaturalJoin(table1, table2, commonAttributes);
	        } else {
	            System.out.println("Les tables ne partagent aucune colonne en commun.");
	            return null;
	        }
	    } else {
	        System.out.println("Une ou plusieurs tables n'existent pas dans la base de données.");
	        return null;
	    }
	}
	
	
/**************************************************Pour la database************************************************/

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
	
	
/******************************************Create relation*******************************************************/
	
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
	
/******************************SELECT******************************************************/
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
        List<Map<String, Object>> filteredData = Predicat(tableName, columnSelection, whereClause);

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

    private List<Map<String, Object>> Predicat(String tableName, String columnSelection, String whereClause) {
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

    private String findOperator(String condition) {
        if (condition.contains(">=")) {
            return ">=";
        } else if (condition.contains("<=")) {
            return "<=";
        } else if (condition.contains(">")) {
            return ">";
        } else if (condition.contains("<")) {
            return "<";
        } else if (condition.contains("=")) {
            return "=";
        } else {
            // Opérateur par défaut si aucun n'est spécifié
            return "=";
        }
    }
    
    private String[] splitCondition(String condition) {
        return condition.split(findOperator(condition));
    }

    private String[] getColumnAndValue(String condition) {
        String[] parts = splitCondition(condition);
        return new String[]{parts[0].trim(), parts[1].trim()};
    }

    private boolean isConditionSatisfied(String strColumnValue, String valueToMatch, String operator) {
        switch (operator) {
            case ">":
                return strColumnValue.compareTo(valueToMatch) > 0;
            case ">=":
                return strColumnValue.compareTo(valueToMatch) >= 0;
            case "<":
                return strColumnValue.compareTo(valueToMatch) < 0;
            case "<=":
                return strColumnValue.compareTo(valueToMatch) <= 0;
            case "=":
                return strColumnValue.equals(valueToMatch);
            default:
                return false;
        }
    }
    
    private boolean evaluateSingleCondition(Map<String, Object> rowData, String condition, String columnSelection) {
        String operator = findOperator(condition);
        String[] columnAndValue = getColumnAndValue(condition);
        String columnToFilter = columnAndValue[0];
        String valueToMatch = columnAndValue[1];

        if (isColumnSelected(columnToFilter, columnSelection.split(","))) {
            Object columnValue = rowData.get(columnToFilter);

            if (columnValue != null) {
                String strColumnValue = columnValue.toString();
                return isConditionSatisfied(strColumnValue, valueToMatch, operator);
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

	
/******************************************Affichage******************************************************************/	
    private void displaySelectedData(String columnSelection, List<Map<String, Object>> selectedData) {
    	if (selectedData.isEmpty()) {
            System.out.println("No rows to display.");
            return; // Quittez la méthode car il n'y a rien à afficher
        }
    	// Affichez les noms des colonnes sélectionnées
        System.out.println(columnSelection);

        List<String> columns = new ArrayList<>(selectedData.get(0).keySet());
        Map<String, Integer> columnWidths = tableau.calculateDynamicColumnWidths(columns, selectedData);

        // Affichez la ligne supérieure du tableau
        tableau.printTableBorder(columns, columnWidths);

        // Affichez les noms de colonnes dans le tableau
        tableau.printColumnNames(columns, columnWidths);

        // Affichez la ligne médiane du tableau
        tableau.printTableBorder(columns, columnWidths);

        for (Map<String, Object> rowData : selectedData) {
            tableau.printDataRows(columns, columnWidths, rowData);
        }

        // Affichez la ligne inférieure du tableau
        tableau.printTableBorder(columns, columnWidths);
    }
    
    private void displayAllData(List<Map<String, Object>> data) {
        if (data.isEmpty()) {
            System.out.println("Aucune donnée à afficher.");
        } else {
            List<String> columns = new ArrayList<>(data.get(0).keySet());
            Map<String, Integer> columnWidths = tableau.calculateDynamicColumnWidths(columns, data);

            // Affichez la ligne supérieure du tableau
            tableau.printTableBorder(columns, columnWidths);

            // Affichez les noms de colonnes dans le tableau
            tableau.printColumnNames(columns, columnWidths);

            // Affichez la ligne médiane du tableau
            tableau.printTableBorder(columns, columnWidths);

            for (Map<String, Object> rowData : data) {
            	tableau.printDataRows(columns, columnWidths, rowData);
            }

            // Affichez la ligne inférieure du tableau
            tableau.printTableBorder(columns, columnWidths);
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

/******************************************INSERT**************************************************************/
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
  }
