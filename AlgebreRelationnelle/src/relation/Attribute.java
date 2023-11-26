package relation;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Attribute implements Serializable {
    private Class<?> type;
    private String name;
    private List<Element> elements;

    // Constructeurs
    public Attribute() {
        elements = new ArrayList<>();
    }

    public Attribute(String name, Class<?> type) {
        this.name = name;
        this.type = type;
        elements = new ArrayList<>();
    }

    // Accesseurs pour le type
    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    // Accesseurs pour le nom
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Accesseurs pour les éléments
    public List<Element> getElements() {
        return elements;
    }

    public void setElements(List<Element> elements) {
        this.elements = elements;
    }
}
