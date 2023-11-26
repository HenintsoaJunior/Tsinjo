package relation;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Element implements Serializable {
    private Object value;

    // Constructeurs
    public Element() {
        // Constructeur par défaut
    }

    public Element(Object value) {
        this.value = value;
    }

    // Accesseurs pour la valeur
    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    // Méthode pour convertir une chaîne en type approprié de manière récursive
    public static Object recursiveCast(String stringValue) {
        Object result;

        // Essaie de convertir en entier
        try {
            result = Integer.parseInt(stringValue);
        } catch (NumberFormatException e) {
            // Si la conversion en entier échoue, essaie en double
            try {
                result = Double.parseDouble(stringValue);
            } catch (NumberFormatException ee) {
                // Si la conversion en double échoue, essaie en date
                try {
                    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                    result = formatter.parse(stringValue);
                } catch (ParseException eee) {
                    // Si aucune conversion ne fonctionne, retourne la chaîne originale
                    result = stringValue;
                }
            }
        }

        return result;
    }
}
