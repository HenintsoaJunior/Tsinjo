package relation;



import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import file.File;

public class Database implements Serializable {
    List<Relation> relations;

    // Constructeur
    public Database() {
        relations = new ArrayList<>();
    }

    // Getter pour les relations
    public List<Relation> getRelations() {
        return relations;
    }

    // Setter pour les relations
    public void setRelations(List<Relation> relations) {
        this.relations = relations;
    }

    // Vérifie si une relation est dans la base
    public boolean isRelationInDatabase(Relation relation) {
        for (Relation test : this.relations) {
            if ((relation.getName()).equalsIgnoreCase(test.getName())) {
                return true;
            }
        }
        return false;
    }

    // Exécute une requête sur la base de données
    public void executeRequest(String query) {
        Relation relation = new Relation();
        boolean isSuccessful = false;
        Interpreteur interpreter = new Interpreteur();
        QueryScanner scanner = new QueryScanner();
        interpreter.setDatabase(this);

        try {
            if (scanner.isCreateQueryValid(query)) {
                this.relations.add(interpreter.interpretCreateQuery(query));
                System.out.println("Relation created");
                isSuccessful = true;
            } else if (scanner.isInsertQueryValid(query)) {
                interpreter.interpretInsertQuery(query);
                System.out.println("Insert successful");
                isSuccessful = true;
            } else if (scanner.isSelectQueryValid(query)) {
                Relation.selectPrinter(interpreter.interpretSelectQuery(query));
                isSuccessful = true;
            }

            if (!isSuccessful) {
                System.out.println("Invalid query");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    // Exécute les commandes de la console de la base de données
    public void runConsole() throws Exception {
        java.util.Scanner scanner = new java.util.Scanner(System.in);
        String command;
        int i = 0;
        Database database = new Database();
        File fileHandler = new File(database.relations);

        while (true) {
            fileHandler.load(database);

            if (i == 0) {
                System.out.println("                WELCOME TO 'myBase'");
                String text2 = "Call 'help' for assistance";
                for (int o = 0; o < text2.length(); o++) {
                    System.out.print(text2.charAt(o));
                    try {
                        Thread.sleep(10);
                    } catch (Exception ex) {
                    }
                }
            }
            i = 1;

            System.out.println();
            System.out.print("#myBase>");
            command = scanner.nextLine();

            if ("exit".equalsIgnoreCase(command)) {
                break;
            } else if ("help".equalsIgnoreCase(command)) {
                fileHandler.loadRules();
            } else if ("save".equalsIgnoreCase(command)) {
                fileHandler.save();
            } else if ("clear all".equalsIgnoreCase(command)) {
                this.relations.clear();
                fileHandler.clearFileContent();
            } else {
                this.executeRequest(command);
            }
        }
    }
}
