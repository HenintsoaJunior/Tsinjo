package relation;


import java.util.List;

public class Interpreteur {
    Database database = new Database();

    // Méthode pour définir la base de données
    public void setDatabase(Database db) {
        this.database = db;
    }

    // Interpréter une requête de création
    public Relation interpretCreateQuery(String query) throws Exception {
        QueryScanner scanner = new QueryScanner();
        List<String> blocks = scanner.correspondance(query, "create");
        String[] typeValue = blocks.get(1).split(",");
        String[] types = new String[typeValue.length];
        String[] attributeNames = new String[typeValue.length];

        for (int i = 0; i < typeValue.length; i++) {
            String[] temp = typeValue[i].split(" ");
            types[i] = temp[0];
            attributeNames[i] = temp[1];
        }

        for (Relation rel : database.getRelations()) {
            if (blocks.get(0).equalsIgnoreCase(rel.getName())) {
                throw new Exception("Relation already exists");
            }
        }
        return Relation.createRelation(blocks.get(0), Relation.creerAttributsDepuisChaines(attributeNames, types));
    }

    // Interpréter une requête d'insertion
    public void interpretInsertQuery(String query) throws Exception {
        QueryScanner scanner = new QueryScanner();
        List<String> blocks = scanner.correspondance(query, "insert");
        Element[] elements = Relation.creerElementsDepuisChaines(blocks.get(1).split(","));
        boolean isSuccessful = false;

        for (Relation rel : database.getRelations()) {
            if (blocks.get(0).equalsIgnoreCase(rel.getName())) {
                rel.insererElements(elements);
                isSuccessful = true;
                break;
            }
        }
        if (!isSuccessful) {
            throw new Exception("Relation doesn't exist");
        }
    }

    // Interpréter une requête de sélection
    public Relation interpretSelectQuery(String query) throws Exception {
        QueryScanner scanner = new QueryScanner();
        boolean isMixed = false;
        List<String> blocks = null;

        if (scanner.ismixteSelect(query)) {
            isMixed = true;
            blocks = scanner.correspondance(query, "selectMixte");
        } else {
            blocks = scanner.correspondance(query, "select");
        }

        String[] columnNames = blocks.get(0).split(",");
        String relationName = blocks.get(1);

        boolean relationFound = false;
        Relation selectedPredicate = null;
        Relation selectedColumns = null;

        for (Relation rel : database.getRelations()) {
            if (rel.getName().equalsIgnoreCase(relationName)) {
                relationFound = true;
                selectedPredicate = rel;
                if (blocks.get(0).equalsIgnoreCase("daholo")) {
                    selectedColumns = rel.copieAvecToutesLesColonnes();
                } else {
                    selectedColumns = rel.copieAvecColonnesSpecifiques(columnNames);
                }
                break;
            }
        }

        if (!relationFound) {
            throw new Exception("Relation not found: " + relationName);
        }

        else {
            return selectedColumns;
        }
    }
}
