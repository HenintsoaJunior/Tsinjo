package file;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;

import relation.Database;

public class File {

/*********************************************************SAVET ET LOAD*********************************************************/
    public static void saveDataToFile(Map<String, Database> databases,Database database) {
        try {
            // Créez un flux de sortie pour écrire dans le fichier
            FileOutputStream fileOutputStream = new FileOutputStream("C:\\Users\\Henintsoa\\Documents\\github\\Tsinjo\\SQLGASY\\src\\fichier\\file.txt");
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
    public static void loadDataFromFile(Map<String, Database> databases,Database database) {
        try {
            // Créez un flux d'entrée pour lire à partir du fichier
            FileInputStream fileInputStream = new FileInputStream("C:\\Users\\Henintsoa\\Documents\\github\\Tsinjo\\SQLGASY\\src\\fichier\\file.txt");
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