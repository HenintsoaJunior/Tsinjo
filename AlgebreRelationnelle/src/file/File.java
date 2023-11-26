package file;

import java.util.*;

import relation.Database;
import relation.Relation;

import java.io.*;
import java.nio.file.*;


public class File {

    List<Relation> relations;

    public File(List<Relation> rels) {
        this.relations = rels;
    }

    public void save() {
        try {
            FileOutputStream fileOut = new FileOutputStream("C:\\Users\\Henintsoa\\Documents\\github\\Tsinjo\\AlgebreRelationnelle\\src\\fichier\\file.txt");
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(relations);
            objectOut.close();
            fileOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void load(Database b) {
        try {
            FileInputStream fileIn = new FileInputStream("C:\\Users\\Henintsoa\\Documents\\github\\Tsinjo\\AlgebreRelationnelle\\src\\fichier\\file.txt");
            ObjectInputStream objectIn = new ObjectInputStream(fileIn);
            b.setRelations((List<Relation>) objectIn.readObject());
            objectIn.close();
            fileIn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadRules() throws Exception {
        System.out.println(Files.readString(Paths.get("C:\\Users\\Henintsoa\\Documents\\github\\Tsinjo\\AlgebreRelationnelle\\src\\fichier\\MyRules.MD")));
    }

    public void clearFileContent() throws Exception {
        Path filePath = Paths.get("C:\\Users\\Henintsoa\\Documents\\github\\Tsinjo\\AlgebreRelationnelle\\src\\fichier\\file.txt");
        try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardOpenOption.TRUNCATE_EXISTING)) {
        }
        System.out.println("drop database success");
    }
}
