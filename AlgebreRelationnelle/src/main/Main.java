package main;

import relation.Database;

public class Main{
    public static void main(String[] args){
        try{
     

            Database b = new Database();
            b.runConsole();

    

        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}