package relation;

import java.util.Scanner;

public class Main {
    
    public static void main(String[] args) {
        Relation manager = new Relation();
        Scanner scanner = new Scanner(System.in);

        while (true) {
        	
            System.out.print("GASYSQL> ");
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("exit")) {
                break;
            }
            manager.executeCommand(input);
        }

        scanner.close();
    }
}
