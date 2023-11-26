package relation;

import java.util.Stack;

public class TextBlockReader {
    private String text;
    private int start;
    private int cursor;
    private boolean read;
    private Stack<Character> parenthesesStack = new Stack<>();

    public TextBlockReader(String text) {
        this.text = text;
        this.start = 0;
        this.cursor = 0;
        this.read = true;
    }

    public TextBlockReader() {}

    // Avance au prochain bloc de texte
    public boolean next() {
        if (cursor >= text.length()) {
            return (read = false);
        }
        skipSpaces();
        readBlockByBlock();
        return (read = cursor > start);
    }

    // Ignore les espacesSS
    private void skipSpaces() {
        while (cursor < text.length() && text.charAt(cursor) == ' ') {
            cursor++;
        }
        start = cursor;
    }

    // Lit le texte bloc par bloc en tenant compte des parenthèses ouvertes et fermées
    private void readBlockByBlock() {
        parenthesesStack.clear();
        while (cursor < text.length()) {
            char currentChar = text.charAt(cursor);
            if (currentChar == '(') {
                parenthesesStack.push(currentChar); 
            } else if (currentChar == ')') {
                if (!parenthesesStack.isEmpty()) {
                    parenthesesStack.pop(); 
                }
            }
            if (parenthesesStack.isEmpty() && currentChar == ' ') {
                cursor++;
                break; 
            }
            cursor++;
        }
    }

    // Récupère le bloc de texte actuel
    public String getCurrentBlock() {
        return text.substring(start, cursor);
    }
}
