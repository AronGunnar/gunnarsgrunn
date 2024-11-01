package gunnarsgrunn.file;

import gunnarsgrunn.security.EncryptionHandler;
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
    private static String PASSWORD = "your_password"; // TODO: Prompt user for password during runtime instead

    private static boolean isFileEncrypted(String filePath) throws IOException {
        byte[] fileContent = Files.readAllBytes(Paths.get(filePath));
        String header = new String(fileContent, 0, EncryptionHandler.getFlag().length(), StandardCharsets.UTF_8);
        return header.equals(EncryptionHandler.getFlag());
    }

    public static void savePasswordToFile(String domain, String password) {
        try {
            JSONObject jsonData = new JSONObject();
            File file = new File(FILE_PATH);

            // Decrypt the file content if it's encrypted
            if (file.exists() && isFileEncrypted(FILE_PATH)) {
                EncryptionHandler.decryptFile(FILE_PATH, PASSWORD);
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
            EncryptionHandler.encryptFile(FILE_PATH, PASSWORD);

        } catch (IOException e) {
            System.out.println("An error occurred while saving the password to file.");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("An error occurred during encryption.");
            e.printStackTrace();
        }
    }

    public static String getPasswordByDomain(String domain) {
        try {
            File file = new File(FILE_PATH);

            if (!file.exists()) {
                System.out.println("The password file does not exist.");
                return null;
            }

            // Decrypt the file content if it's encrypted
            if (isFileEncrypted(FILE_PATH)) {
                EncryptionHandler.decryptFile(FILE_PATH, PASSWORD);
            }

            String content = new String(Files.readAllBytes(Paths.get(FILE_PATH)), StandardCharsets.UTF_8);
            JSONObject jsonData = new JSONObject(content);

            // Retrieve the password
            JSONObject domainData = jsonData.optJSONObject(domain);
            String retrievedPassword = domainData != null ? domainData.optString("password", null) : null;

            // Re-encrypt the file after reading
            EncryptionHandler.encryptFile(FILE_PATH, PASSWORD);
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
        FileHandler.PASSWORD = arg;
    }
}