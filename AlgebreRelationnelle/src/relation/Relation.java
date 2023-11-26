package relation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Relation implements Serializable {
    private List<Attribute> attributs;
    private String name;

    public Relation() {
        attributs = new ArrayList<>();
    }

    public Relation(List<Attribute> attributs, String name) {
        this.attributs = attributs;
        this.name = name;
    }

    public void setAttributs(List<Attribute> attributs) {
        this.attributs = attributs;
    }

    public List<Attribute> getAttributs() {
        return attributs;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    
    public static Relation createRelation(String name, List<Attribute> attributs) {
        Relation result = new Relation();
        result.setAttributs(attributs);
        result.setName(name);
        return result;
    }
    
    public static List<Attribute> creerAttributsDepuisChaines(String[] nomsAttributs, String[] types) throws Exception {
        List<Attribute> attributs = new ArrayList<>();
        for (int i = 0; i < nomsAttributs.length; i++) {
            Attribute attribut = new Attribute();
            attribut.setName(nomsAttributs[i]);

            switch (types[i]) {
                case "int":
                    attribut.setType(Integer.class);
                    break;
                case "double":
                    attribut.setType(Double.class);
                    break;
                case "String":
                    attribut.setType(String.class);
                    break;
                case "Date":
                    attribut.setType(java.util.Date.class);
                    break;
                default:
                    throw new Exception("Type inconnu");
            }
            
            attributs.add(attribut);
        }
        return attributs;
    }

    public void insererElements(Element[] elements) throws Exception {
        if (this.attributs.size() == elements.length) {
            for (int i = 0; i < this.attributs.size(); i++) {
                Object elmnt = elements[i].getValue();
                if (!((Attribute) this.attributs.get(i)).getType().isInstance(elmnt)) {
                    throw new Exception("Type incompatible");
                }
                this.attributs.get(i).getElements().add(elements[i]);
            }
        } else {
            throw new Exception("Valeur incomplète");
        }
    }
    
///convertir un tableau de chaînes de valeurs en un tableau    
    public static Element[] creerElementsDepuisChaines(String[] chainesValeurs) throws Exception {
        Element[] resultat = new Element[chainesValeurs.length];
        for (int i = 0; i < chainesValeurs.length; i++) {
            resultat[i] = new Element();
            resultat[i].setValue(Element.recursiveCast(chainesValeurs[i]));
        }
        return resultat;
    }
    
 // Calcul de la longueur maximale des noms d'attributs dans la relation
    public int longueurMaxNomAttribut() {
        
        int longueurMax = 0;
        for (Attribute attribut : this.attributs) {
            int longueurNom = attribut.getName().length();
            if (longueurNom > longueurMax) {
                longueurMax = longueurNom;
            }
        }
        return longueurMax;
    }
 
 // Calcul de la longueur maximale des valeurs des éléments dans la relation
    public int longueurMaxValeurElement() {
        
        int longueurMax = 0;
        for (Attribute attribut : this.attributs) {
            for (Element element : attribut.getElements()) {
                int longueur = element.getValue().toString().length();
                if (longueur > longueurMax) {
                    longueurMax = longueur;
                }
            }
        }
        return longueurMax;
    }
    
    
    public static void selectPrinter(Relation relation) {
        int max;
        String mess = "";
        if (relation.longueurMaxValeurElement() == 0) {
            mess = "No row selected";
        }

        if (mess.isEmpty()) {
            int maxLength = Math.max(relation.longueurMaxNomAttribut(), relation.longueurMaxValeurElement());
            afficherEnTete(relation, maxLength);
            afficherSeparateur(relation, maxLength);
            afficherElements(relation, maxLength);
        } else {
            System.out.println(mess);
        }
    }

    private static void afficherEnTete(Relation relation, int maxLength) {
        System.out.print("| ");
        for (Attribute attribut : relation.getAttributs()) {
            System.out.printf("%-" + maxLength + "s | ", attribut.getName());
        }
        System.out.println();
    }

    
    private static void afficherSeparateur(Relation relation,int maxLength) {
        System.out.print("| ");
        for (int i = 0; i < relation.getAttributs().size(); i++) {
            System.out.print("-".repeat(maxLength) + " | ");
        }
        System.out.println();
    }

    private static void afficherElements(Relation relation, int maxLength) {
        for (int j = 0; j < relation.getAttributs().get(0).getElements().size(); j++) {
            System.out.print("|");
            for (Attribute attribut : relation.getAttributs()) {
                Element element = attribut.getElements().get(j);
                System.out.printf(" %-" + maxLength + "s |", element.getValue().toString());
            }
            System.out.print("\n| ");
            for (int i = 0; i < relation.getAttributs().size(); i++) {
                System.out.print("-".repeat(maxLength) + " | ");
            }
            System.out.print("\n");
        }
    }

///permet de dupliquer une relation en créant une nouvelle relation
    public Relation copieAvecToutesLesColonnes() {
        Relation nouvelleRelation = new Relation();
        for (Attribute attribut : this.attributs) {
            nouvelleRelation.getAttributs().add(new Attribute(attribut.getName(), attribut.getType()));
        }
        return nouvelleRelation;
    }

///Crée une nouvelle relation avec les colonnes spécifiées
    public Relation copieAvecColonnesSpecifiques(String[] nomsColonnes) {
        Relation nouvelleRelation = new Relation();
        
        for (Attribute attribut : this.attributs) {
            for (String nomColonne : nomsColonnes) {
                if (attribut.getName().equalsIgnoreCase(nomColonne)) {
                    nouvelleRelation.getAttributs().add(new Attribute(attribut.getName(), attribut.getType()));
                }
            }
        }
        
        return nouvelleRelation;
    }
///Insère une ligne spécifique depuis une source dans cette relation
    public void insererLigne(Relation source, int ligne) {
        
        for (int i = 0; i < source.getAttributs().size(); i++) {
            Element element = source.getAttributs().get(i).getElements().get(ligne);
            this.getAttributs().get(i).getElements().add(element);
        }
    }

    private Relation initialiserNouvelleRelation() {
        Relation result = new Relation();
        result.setName(this.getName()); // Copie le nom de cette relation
        return result;
    }

    private void construireIntersection(Relation result, Relation rel1) {
        boolean intersectionTrouvee = false;

        for (Attribute attributThis : this.getAttributs()) {
            for (Attribute attributRel1 : rel1.getAttributs()) {
                if (attributThis.getName().equalsIgnoreCase(attributRel1.getName())) {
                    // Si les noms d'attributs correspondent, crée un nouvel attribut dans la relation résultante
                    result.getAttributs().add(new Attribute(attributThis.getName(), attributThis.getType()));

                    for (Element elementThis : attributThis.getElements()) {
                        // Vérifie si l'élément de cette relation est présent dans rel1
                        if (attributRel1.getElements().contains(elementThis)) {
                            // Ajoute l'élément à l'attribut correspondant dans la relation résultante
                            result.getAttributs().get(result.getAttributs().size() - 1).getElements().add(elementThis);
                            intersectionTrouvee = true;
                        }
                    }
                }
            }
        }

        if (!intersectionTrouvee) {
            System.out.println("Aucune intersection trouvée entre " + rel1.getName() + " et " + this.getName());
        }
    }

    public Relation intersection(Relation rel1) throws Exception {
        Relation result = initialiserNouvelleRelation(); // Initialise la nouvelle relation avec le nom

        construireIntersection(result, rel1); // Recherche les attributs communs et construit la relation intersection

        return result;
    }

    
    
    private void ajouterAttributsDeCetteRelation(Relation result) {
        for (Attribute attribut : this.getAttributs()) {
            result.getAttributs().add(new Attribute(attribut.getName(), attribut.getType()));
            result.getAttributs().get(result.getAttributs().size() - 1).getElements().addAll(attribut.getElements());
        }
    }
    
    public Relation union(Relation rel1) throws Exception {
        Relation result = new Relation();
        result.setName(this.getName());
        
        this.ajouterAttributsDeCetteRelation(result); // Ajoute les attributs de cette relation à result
        
        for (Attribute rel1Attribut : rel1.getAttributs()) {
            boolean attributExiste = false;
            int indiceAttribut = -1;
            
            for (int i = 0; i < result.getAttributs().size(); i++) {
                if (result.getAttributs().get(i).getName().equalsIgnoreCase(rel1Attribut.getName())) {
                    attributExiste = true;
                    indiceAttribut = i;
                    break;
                }
            }
            
            if (attributExiste) {
                this.fusionnerAttributs(result, indiceAttribut, rel1Attribut);
            } else {
                result.getAttributs().add(new Attribute(rel1Attribut.getName(), rel1Attribut.getType()));
                result.getAttributs().get(result.getAttributs().size() - 1).getElements().addAll(rel1Attribut.getElements());
            }
        }
        
        return result;
    }

    private void fusionnerAttributs(Relation result, int indiceAttribut, Attribute rel1Attribut) {
        for (Element element : rel1Attribut.getElements()) {
            if (!result.getAttributs().get(indiceAttribut).getElements().contains(element)) {
                result.getAttributs().get(indiceAttribut).getElements().add(element);
            }
        }
    }

}
