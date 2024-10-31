package gunnarsgrunn.generation;

import java.security.SecureRandom;

public class PasswordGenerator {
    private static final String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final String NUMBERS = "0123456789";
    private static final String SYMBOLS = "!@#$%^&*";
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generatePassword(int length, boolean includeSymbols) {
        StringBuilder password = new StringBuilder(length);

        // Ensure at least one symbol is included
        if (includeSymbols) {
            password.append(SYMBOLS.charAt(RANDOM.nextInt(SYMBOLS.length())));
            length--;
        }

        // Ensure at least one number is included
        password.append(NUMBERS.charAt(RANDOM.nextInt(NUMBERS.length())));
        length--;

        String characterSet = includeSymbols ? LETTERS + NUMBERS + SYMBOLS : LETTERS + NUMBERS;

        for (int i = 0; i < length; i++) {
            password.append(characterSet.charAt(RANDOM.nextInt(characterSet.length())));
        }

        // Shuffle password for randomness
        return shuffleString(password.toString());
    }

    // Helper method to shuffle the generated password for randomness
    private static String shuffleString(String input) {
        char[] characters = input.toCharArray();
        for (int i = 0; i < characters.length; i++) {
            int randomIndex = RANDOM.nextInt(characters.length);
            // Swap characters
            char temp = characters[i];
            characters[i] = characters[randomIndex];
            characters[randomIndex] = temp;
        }
        return new String(characters);
    }
}
