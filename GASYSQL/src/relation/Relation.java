package relation;

import java.util.Scanner;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Relation implements Serializable{
    private Map<String, Database> databases;
    private Database currentDatabase;
    Table table = new Table();
    public Relation() {
        databases = new HashMap<>();
    }
    
    public void createDatabase(String name) {
        if (!databases.containsKey(name)) {
            Database db = new Database(name);
            databases.put(name, db);
            System.out.println("Base de données '" + name + "' créée avec succès.");
        } else {
            System.out.println("La base de données '" + name + "' existe déjà.");
        }
    }

    public void useDatabase(String name) {
        if (databases.containsKey(name)) {
            currentDatabase = databases.get(name);
            System.out.println("Utilisation de la base de données '" + name + "'.");
        } else {
            System.out.println("La base de données '" + name + "' n'existe pas.");
        }
    }

    public boolean createTable(String tableName, String columns) {
        if (currentDatabase != null) {
            return currentDatabase.createTable(tableName, columns);
        } else {
            System.out.println("Aucune base de données n'est sélectionnée. Utilisez 'USE nom_de_la_bdd' pour sélectionner une base de données.");
            return false;
        }
    }

    public void showTables() {
        if (currentDatabase != null) {
            currentDatabase.showTables();
        } else {
            System.out.println("Aucune base de données n'est sélectionnée. Utilisez 'USE nom_de_la_bdd' pour sélectionner une base de données.");
        }
    }
    private void displaySelectedData(String columnSelection, List<Map<String, Object>> selectedData) {
        // Affichez les noms des colonnes sélectionnées
        System.out.println(columnSelection);

        List<String> columns = new ArrayList<>(selectedData.get(0).keySet());
        Map<String, Integer> columnWidths = calculateDynamicColumnWidths(columns, selectedData);

        // Affichez la ligne supérieure du tableau
        table.printTableBorder(columns, columnWidths);

        // Affichez les noms de colonnes dans le tableau
        table.printColumnNames(columns, columnWidths);

        // Affichez la ligne médiane du tableau
        table.printTableBorder(columns, columnWidths);

        for (Map<String, Object> rowData : selectedData) {
            printDataRows(columns, columnWidths, rowData);
        }

        // Affichez la ligne inférieure du tableau
        table.printTableBorder(columns, columnWidths);
    }

    private void displayAllData(List<Map<String, Object>> data) {
        if (data.isEmpty()) {
            System.out.println("Aucune donnée à afficher.");
        } else {
            List<String> columns = new ArrayList<>(data.get(0).keySet());
            Map<String, Integer> columnWidths = calculateDynamicColumnWidths(columns, data);

            // Affichez la ligne supérieure du tableau
            table.printTableBorder(columns, columnWidths);

            // Affichez les noms de colonnes dans le tableau
            table.printColumnNames(columns, columnWidths);

            // Affichez la ligne médiane du tableau
            table.printTableBorder(columns, columnWidths);

            for (Map<String, Object> rowData : data) {
                printDataRows(columns, columnWidths, rowData);
            }

            // Affichez la ligne inférieure du tableau
            table.printTableBorder(columns, columnWidths);
        }
    }
    
    private Map<String, Integer> calculateDynamicColumnWidths(List<String> columns, List<Map<String, Object>> data) {
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

    private void printDataRows(List<String> columns, Map<String, Integer> columnWidths, Map<String, Object> rowData) {
        for (String columnName : columns) {
            String value = String.valueOf(rowData.get(columnName));
            System.out.print("| " + formatCell(value, columnWidths.get(columnName)) + " ");
        }
        System.out.println("|");
    }

    private String formatCell(String value, int width) {
        return String.format("%-" + width + "s", value);
    }



    public void executeCommand(String command) {
        if (command.toLowerCase().startsWith("create database ")) {
            handleCreateDatabaseCommand(command);
        } else if (command.toLowerCase().startsWith("use ")) {
            handleUseDatabaseCommand(command);
        } else if (command.toLowerCase().startsWith("create table ")) {
            handleCreateTableCommand(command);
        } else if (command.toLowerCase().startsWith("show tables")) {
            showTables();
        } else if (command.toLowerCase().startsWith("select ")) {
            handleSelectCommand(command);
        } else if (command.toLowerCase().startsWith("commit")) {
            saveDataToFile();
            System.out.println("Données sauvegardées dans le fichier.");
        } else if (command.toLowerCase().startsWith("load")) {
            loadDataFromFile(); 
            System.out.println("Données chargées à partir du fichier.");
        } else if (command.toLowerCase().startsWith("insert into ")) {
            handleInsertIntoCommand(command);
        } else {
            System.out.println("Commande non reconnue : " + command);
        }
    }


    private void handleCreateDatabaseCommand(String command) {
        // Votre logique pour gérer la création de la base de données
        String dbName = command.substring("create database ".length());
        createDatabase(dbName);
    }

    private void handleUseDatabaseCommand(String command) {
        // Votre logique pour gérer l'utilisation de la base de données
        String dbName = command.substring("use ".length());
        useDatabase(dbName);
    }

    private void handleCreateTableCommand(String command) {
        // Votre logique pour gérer la création de la table
        String regex = "create table ([a-zA-Z]+) \\((.+)\\)";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(command);

        if (matcher.matches()) {
            String tableName = matcher.group(1);
            String columnDefinitions = matcher.group(2);
            boolean success = createTable(tableName, columnDefinitions);
            if (success) {
                System.out.println("Table '" + tableName + "' créée dans la base de données '" + currentDatabase.getName() + "'.");
            } else {
                System.out.println("La table '" + tableName + "' existe déjà dans la base de données '" + currentDatabase.getName() + "'.");
            }
        } else {
            System.out.println("Commande non reconnue : " + command);
        }
    }
    
    public List<Map<String, Object>> selectAllFromTable(String tableName) {
        if (currentDatabase != null && currentDatabase.tableExists(tableName)) {
            Table table = currentDatabase.getTable(tableName);
            return table.getData();
        } else {
            System.out.println("La table '" + tableName + "' n'existe pas dans la base de données '" + currentDatabase.getName() + "'.");
            return null;
        }
    }

    
    private void handleSelectCommand(String command) {
        String regex = "SELECT (.+) FROM ([a-zA-Z]+)(?: WHERE (.+))?";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(command);

        if (matcher.matches()) {
            String columnSelection = matcher.group(1).trim();
            String tableName = matcher.group(2).trim();
            String whereClause = matcher.group(3);

            if (whereClause != null && !whereClause.isEmpty()) {
                // Sélectionner des colonnes spécifiques avec une clause WHERE
                String[] selectedColumns = columnSelection.split(",");
                String condition = whereClause.trim();

                // Divisez la clause WHERE pour obtenir les noms des colonnes référencées
                String[] whereParts = condition.split("=");
                String columnInWhere = whereParts[0].trim();

                // Vérifiez si la colonne du WHERE a été sélectionnée dans le SELECT
                boolean columnSelected = false;
                for (String selectedColumn : selectedColumns) {
                    String trimmedSelectedColumn = selectedColumn.trim();
                    if (trimmedSelectedColumn.equals("*") || trimmedSelectedColumn.equals(columnInWhere)) {
                        columnSelected = true;
                        break;
                    }
                }

                if (!columnSelected) {
                    System.out.println("La colonne dans la clause WHERE n'a pas été sélectionnée.");
                    return;
                }

                // Maintenant, vous pouvez appeler handleSelectWithWhereCommand avec la nouvelle sélection
                handleSelectWithWhereCommand(tableName, selectedColumns, condition);
            } else {
                if (columnSelection.equals("*")) {
                    // Sélectionner toutes les colonnes sans clause WHERE
                    List<Map<String, Object>> selectedData = selectAllFromTable(tableName);
                    if (selectedData != null) {
                        displayAllData(selectedData);
                    }
                } else {
                    // Sélectionner des colonnes spécifiques sans clause WHERE
                    List<Map<String, Object>> selectedData = selectFromTable(tableName, columnSelection);
                    if (selectedData != null) {
                        displaySelectedData(columnSelection, selectedData);
                    }
                }
            }
        } else {
            System.out.println("Commande SELECT non valide : " + command);
        }
    }

    private void handleSelectWithWhereCommand(String tableName, String[] selectedColumns, String condition) {
        // Obtenez les données de la table spécifiée (par exemple, "Personne").
        Table table = currentDatabase.getTable(tableName);

        // Analysez la condition (par exemple, "id = 1").
        String[] parts = condition.split("=");
        String columnToFilter = parts[0].trim();
        String valueToMatch = parts[1].trim();

        // Vérifiez que la colonne du WHERE a été sélectionnée dans le SELECT
        boolean columnSelected = false;
        for (String selectedColumn : selectedColumns) {
            String trimmedSelectedColumn = selectedColumn.trim();
            if (trimmedSelectedColumn.equals("*") || trimmedSelectedColumn.equals(columnToFilter)) {
                columnSelected = true;
                break;
            }
        }

        if (!columnSelected) {
            System.out.println("La colonne dans la clause WHERE n'a pas été sélectionnée.");
            return;
        }

        // Créez une liste pour stocker les lignes correspondant à la condition.
        List<Map<String, Object>> filteredData = new ArrayList<>();

        // Parcourez les données de la table et filtrez les lignes.
        for (Map<String, Object> rowData : table.getData()) {
            Object columnValue = rowData.get(columnToFilter);
            if (columnValue != null && columnValue.toString().equals(valueToMatch)) {
                // Cette ligne satisfait la condition, ajoutez-la aux résultats.
                filteredData.add(rowData);
            }
        }

        // Affichez les résultats.
        displaySelectedData(String.join(", ", selectedColumns), filteredData);
    }




        private List<Map<String, Object>> selectFromTable(String tableName, String columnSelection) {
        if (currentDatabase != null && currentDatabase.tableExists(tableName)) {
            Table table = currentDatabase.getTable(tableName);
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
            System.out.println("La table '" + tableName + "' n'existe pas dans la base de données '" + currentDatabase.getName() + "'.");
            return null;
        }
    }


    private void handleInsertIntoCommand(String command) {
        Pattern pattern = Pattern.compile("INSERT INTO (\\w+) \\((.+)\\) VALUES \\((.+)\\)");
        Matcher matcher = pattern.matcher(command);

        if (matcher.matches()) {
            String tableName = matcher.group(1);
            String columnNames = matcher.group(2);
            String values = matcher.group(3);

            if (currentDatabase != null && currentDatabase.tableExists(tableName)) {
                Table table = currentDatabase.getTable(tableName);
                String[] columnNamesArray = columnNames.split(",");
                String[] valuesArray = values.split(",");
                if (columnNamesArray.length == valuesArray.length) {
                    Map<String, Object> rowData = buildRowData(columnNamesArray, valuesArray, table);
                    if (rowData != null) {
                        insertDataIntoTable(table, rowData, tableName);
                    }
                } else {
                    System.out.println("Le nombre de colonnes et de valeurs ne correspond pas.");
                }
            } else {
                System.out.println("La table '" + tableName + "' n'existe pas dans la base de données '" + currentDatabase.getName() + "'.");
            }
        } else {
            System.out.println("Commande non reconnue : " + command);
        }
    }

    private Map<String, Object> buildRowData(String[] columnNamesArray, String[] valuesArray, Table table) {
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
            FileOutputStream fileOutputStream = new FileOutputStream("C:\\Users\\Henintsoa\\Desktop\\Tsinjo\\GASYSQL\\src\\fichier\\file.txt");
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);

            // Écrivez les données de la classe DatabaseManager dans le fichier
            objectOutputStream.writeObject(databases);
            objectOutputStream.writeObject(currentDatabase);

            // Fermez les flux
            objectOutputStream.close();
            fileOutputStream.close();

            System.out.println("Données sauvegardées dans le fichier");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public void loadDataFromFile() {
        try {
            // Créez un flux d'entrée pour lire à partir du fichier
            FileInputStream fileInputStream = new FileInputStream("C:\\Users\\Henintsoa\\Desktop\\Tsinjo\\GASYSQL\\src\\fichier\\file.txt");
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

            // Lisez les données de la classe DatabaseManager à partir du fichier
            databases = (Map<String, Database>) objectInputStream.readObject();
            currentDatabase = (Database) objectInputStream.readObject();

            // Fermez les flux
            objectInputStream.close();
            fileInputStream.close();

            System.out.println("Données chargées à partir du fichier");
        } catch (EOFException e) {
            System.err.println("Le fichier est vide. Aucune donnée n'a été chargée.");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Relation manager = new Relation();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("GASYSQL> ");
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("exit")) {
                break;
            }
            manager.executeCommand(input);
        }

        scanner.close();
    }
}
