package gunnarsgrunn.file;

import org.json.JSONObject;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;

/**
 * This class handles file operations for saving and retrieving passwords with
 * encryption.
 */
public class FileHandler {

    private static final String FILE_PATH = "bin.json";
    private static String ENCRYPTION_KEY = "TBD"; // This is set at runtime

    /**
     * Checks if the file is encrypted based on the header.
     * 
     * @param filePath The path to the file
     * @return True if the file is encrypted, false otherwise
     * @throws IOException If an I/O error occurs
     */
    private static boolean isFileEncrypted(String filePath) throws IOException {
        byte[] fileContent = Files.readAllBytes(Paths.get(filePath));
        String header = new String(fileContent, 0, EncryptionHandler.getFlag().length(), StandardCharsets.UTF_8);
        return header.equals(EncryptionHandler.getFlag());
    }

    /**
     * Saves a password to a file for a specific domain.
     * 
     * @param domain   The domain for which the password is saved
     * @param password The password to save
     */
    public static void savePasswordToFile(String domain, String password) {
        try {
            JSONObject jsonData = new JSONObject();
            File file = new File(FILE_PATH);

            // Decrypt the file content if it's encrypted
            if (file.exists() && isFileEncrypted(FILE_PATH)) {
                EncryptionHandler.decryptFile(FILE_PATH, ENCRYPTION_KEY);
                String content = new String(Files.readAllBytes(Paths.get(FILE_PATH)), StandardCharsets.UTF_8);
                jsonData = new JSONObject(content);
            }

            // Update the JSON data
            JSONObject domainData = jsonData.optJSONObject(domain);
            if (domainData == null) {
                domainData = new JSONObject();
            }
            domainData.put("password", password);
            jsonData.put(domain, domainData);

            // Write updated JSON back and encrypt
            try (FileWriter fileWriter = new FileWriter(FILE_PATH)) {
                fileWriter.write(jsonData.toString(4));
            }
            EncryptionHandler.encryptFile(FILE_PATH, ENCRYPTION_KEY);

        } catch (IOException e) {
            System.out.println("An error occurred while saving the password to file.");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("An error occurred during encryption.");
            e.printStackTrace();
        }
    }

    /**
     * Retrieves a password for a specific domain from the file.
     * 
     * @param domain The domain for which to retrieve the password
     * @return The password, or null if not found
     */
    public static String getPasswordByDomain(String domain) {
        try {
            File file = new File(FILE_PATH);

            if (!file.exists()) {
                System.out.println("The password file does not exist.");
                return null;
            }

            // Decrypt the file content if it's encrypted
            if (isFileEncrypted(FILE_PATH)) {
                EncryptionHandler.decryptFile(FILE_PATH, ENCRYPTION_KEY);
            }

            String content = new String(Files.readAllBytes(Paths.get(FILE_PATH)), StandardCharsets.UTF_8);
            JSONObject jsonData = new JSONObject(content);

            // Retrieve the password
            JSONObject domainData = jsonData.optJSONObject(domain);
            String retrievedPassword = domainData != null ? domainData.optString("password", null) : null;

            // Re-encrypt the file after reading
            EncryptionHandler.encryptFile(FILE_PATH, ENCRYPTION_KEY);
            return retrievedPassword;

        } catch (IOException e) {
            System.out.println("An error occurred while retrieving the password.");
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            System.out.println("An error occurred during decryption.");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Set the password for encryption and decryption.
     * 
     * @param arg The password to set.
     */
    public static void setKey(String arg) {
        FileHandler.ENCRYPTION_KEY = arg;
    }
}