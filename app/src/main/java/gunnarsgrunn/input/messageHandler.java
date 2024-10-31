package gunnarsgrunn.input;

import java.util.Scanner;

import gunnarsgrunn.generation.PasswordGenerator;
import gunnarsgrunn.file.FileHandler;

public class MessageHandler {

    public static void handleTypeSelect() {
        Scanner scanner = new Scanner(System.in);
        System.out
                .print("Select operation: (1) Fetch Password, (2) Create Password, (3) Help\n> ");
        int choice = scanner.nextInt();
        if (choice == 1) {
            fetchPass();
        } else if (choice == 2) {
            createPass();
        } else if (choice == 3) {
            help();
        } else {
            System.out.println("Invalid choice. Please select 1, 2, or 3.");
        }
        scanner.close();
    }

    private static void fetchPass() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("\n---===::: : Fishing for password : :::===---\n");

        // Domain
        System.out.print("Domain lookup (str): ");
        String domain = scanner.next();

        String password = FileHandler.getPasswordByDomain(domain);

        System.out.println("\nPassword for '" + domain + "' is: " + password);
        scanner.close();
    }

    private static void createPass() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("\n---===::: : Forging a password : :::===---\n");

        // Domain
        System.out.print("Domain? (str): ");
        String domain = scanner.next();

        // Password Length
        System.out.print("Length? (int): ");
        int length = scanner.nextInt();

        // Include Symbols
        System.out.print("Symbols? (y/n): ");
        String symbolsInput = scanner.next();
        boolean includeSymbols = symbolsInput.equalsIgnoreCase("y");

        while (true) {
            // Generate Password
            String password = PasswordGenerator.generatePassword(length, includeSymbols);
            System.out.println("\nPassword: " + password);

            // Save Password
            System.out.print("Retry? (y/n): ");
            String response = scanner.next();
            if (response.equalsIgnoreCase("n")) {
                FileHandler.savePasswordToFile(domain, password);
                System.out.println("\nPassword saved!");
                break;
            }
        }
        scanner.close();
    }

    private static void help() {
        System.out.print("\n---===::: : How to use : :::===---\n");
        System.out.println("1. Fetch Password: Retrieve a password by domain.");
        System.out.println("2. Create Password: Generate and save a password for a domain.");
        System.out.println("3. Help: Display this help message.\n");

        System.out.println("NOTE: Passwords are saved in 'bin.json', make sure to backup this file.");
        System.out.println("      Passwords are generated using a cryptographically secure random number generator.");
        System.out.println("      If the same domain as an already stored password is used, it will be overwritten.\n");

        System.out.println("- Aron Gunnar, 2024\n");
    }
}
