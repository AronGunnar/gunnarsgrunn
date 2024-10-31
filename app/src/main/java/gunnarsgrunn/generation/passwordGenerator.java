package gunnarsgrunn.generation;

import java.security.SecureRandom;

/**
 * This class generates a random password.
 */
public class PasswordGenerator {
    private static final String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final String NUMBERS = "0123456789";
    private static final String SYMBOLS = "!@#$%^&*";
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Generates a random password with the specified length.
     * 
     * @param length         the length of the password
     * @param includeSymbols whether to include symbols in the password
     * @return a randomly generated password
     */
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

    /**
     * Shuffles the characters of a string.
     * 
     * @param input the input string
     * @return the shuffled string
     */
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
