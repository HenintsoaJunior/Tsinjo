package relation;

import java.util.ArrayList;
import java.util.List;

public class QueryScanner {
    private TextBlockReader textBlockReader;

    // Modèles de requête
    private final String select = "SELECT $columnName FROM $RelName";
    private final String selectMixte = "SELECT $columnName FROM $RelName $condition";
    private final String insert = "INSERT INTO $RelName VALUES $values";
    private final String create = "CREATE TABLE $RelName $typeValue";

    // Constructeur
    public QueryScanner() {
        textBlockReader = new TextBlockReader();
    }

    // Récupère les blocs de requête avec des variables pour un type de requête donné
    public List<String> blocksQueryWithVariable(String typeQuery) {
        List<String> result = new ArrayList<>();
        String lower = typeQuery.toLowerCase();
        TextBlockReader test = null;

        switch (lower) {
            case "selectmixte":
                test = new TextBlockReader(selectMixte);
                break;
            case "select":
                test = new TextBlockReader(select);
                break;
            case "insert":
                test = new TextBlockReader(insert);
                break;
            case "create":
                test = new TextBlockReader(create);
                break;
        }

        while (test.next()) {
            result.add(test.getCurrentBlock());
        }
        return result;
    }

    // Récupère les blocs de requête
    public List<String> blocksQuery() {
        List<String> result = new ArrayList<>();
        while (textBlockReader.next()) {
            result.add(textBlockReader.getCurrentBlock());
        }
        return result;
    }

    // Récupère les correspondances dans une requête donnée pour un type de requête spécifique
    public List<String> correspondance(String query, String typeQuery) throws Exception {
        List<String> result = new ArrayList<>();
        textBlockReader = new TextBlockReader(query);
        List<String> variable = blocksQueryWithVariable(typeQuery);
        List<String> test = blocksQuery();
        String trim = null;

        for (int i = 0; i < variable.size(); i++) {
            if (variable.get(i).startsWith("$")) {
                trim = test.get(i).trim();
                if (trim.startsWith("(") && trim.endsWith(")")) {
                    if (!variable.get(i).equalsIgnoreCase("$condition")) {
                        result.add(((trim.replace('(', ' ')).replace(')', ' ')).trim());
                    } else {
                        result.add(trim);
                    }
                } else {
                    throw new Exception("Parenthese issues");
                }
            }
        }
        return result;
    }

    // Vérifie si une requête est une requête mixte (avec plusieurs paramètres)
    public boolean ismixteSelect(String query) {
        String lower = query.toLowerCase();
        textBlockReader = new TextBlockReader(query);
        List<String> test = blocksQuery();
        return test.size() != 4;
    }

    // Vérifie si une requête SELECT est valide
    public boolean isSelectQueryValid(String query) {
        String lower = query.toLowerCase();
        textBlockReader = new TextBlockReader(query);
        List<String> test = blocksQuery();
        if (lower.startsWith("ameza")) {
            if (test.size() == 4) {
                List<String> blocks = blocksQueryWithVariable("select");
                return checkQueryBlocks(query, blocks);
            } else {
                List<String> blocks = blocksQueryWithVariable("selectMixte");
                return checkQueryBlocks(query, blocks);
            }
        } else {
            return false;
        }
    }

    // Vérifie si une requête INSERT est valide
    public boolean isInsertQueryValid(String query) {
        String lower = query.toLowerCase();
        if (lower.startsWith("atsofoy")) {
            List<String> blocks = blocksQueryWithVariable("insert");
            return checkQueryBlocks(query, blocks);
        } else {
            return false;
        }
    }

    // Vérifie si une requête CREATE est valide
    public boolean isCreateQueryValid(String query) {
        String lower = query.toLowerCase();
        if (lower.startsWith("mamorona")) {
            List<String> blocks = blocksQueryWithVariable("create");
            return checkQueryBlocks(query, blocks);
        } else {
            return false;
        }
    }

    // Vérifie les blocs de la requête
    private boolean checkQueryBlocks(String query, List<String> blocks) {
        textBlockReader = new TextBlockReader(query);
        List<String> parts = blocksQuery();
        for (int i = 0; i < parts.size(); i++) {
            String test = parts.get(i).trim();
            if ((blocks.get(i).trim()).startsWith("$")) {
                if (!(test.startsWith("(") && test.endsWith(")"))) {
                    return false;
                }
            } else {
                if (!test.equals((blocks.get(i).trim()))) {
                    return false;
                }
            }
        }
        return true;
    }
}
