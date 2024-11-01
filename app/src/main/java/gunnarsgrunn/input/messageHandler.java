package gunnarsgrunn.input;

import java.util.Scanner;
import gunnarsgrunn.generation.PasswordGenerator;
import gunnarsgrunn.file.FileHandler;

/**
 * This class handles user input and displays messages.
 */
public class MessageHandler {

    private static final Scanner scanner = new Scanner(System.in);

    /**
     * Handles the user input for selecting an operation.
     */
    public static void handleTypeSelect() {

        System.out.print("\n---===::: : Password Manager : :::===---\n");
        System.out.println("Please enter your encryption key (str): "); // "your_password" is used in development
        FileHandler.setKey(scanner.next());

        System.out.print("\nSelect operation: (1) Fetch Password, (2) Create Password, (3) Help\n> ");
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
    }

    /**
     * Fetches a password by domain.
     */
    private static void fetchPass() {
        System.out.print("\n---===::: : Fishing for password : :::===---\n");

        // Domain
        System.out.print("Domain lookup (str): ");
        String domain = scanner.next();

        // Get Password
        String password = FileHandler.getPasswordByDomain(domain);
        System.out
                .println("\nPassword for '" + domain + "' is: " + (password != null ? password : "No password found."));
    }

    /**
     * Creates a password for a domain.
     */
    private static void createPass() {
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
    }

    /**
     * Displays the help message.
     */
    private static void help() {
        System.out.print("\n---===::: : How to use : :::===---\n");
        System.out.println("1. Fetch Password: Retrieve a password by domain.");
        System.out.println("2. Create Password: Generate and save a password for a domain.");
        System.out.println("3. Help: Display this help message.\n");

        System.out.println("- Domains are case-sensitive, use only lowercase.");
        System.out.println("- Wrong encryption key will NOT decrypt passwords.");
        System.out.println("- Encryption key is not stored, make sure to remember it.");
        System.out.println("- If the same domain as an already stored password is used, it will be overwritten.\n");

        System.out.println("NOTE: Passwords are saved in 'bin.json', make sure to backup this file.");
        System.out.println("      Passwords are generated using a cryptographically secure random number generator.");
        System.out.println("      Encryption uses AES-256 with a key derived from user-provided input.\n");

        System.out.println("- Aron Gunnar, 2024\n");
    }
}
