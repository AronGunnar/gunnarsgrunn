package gunnarsgrunn.file;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;

/**
 * This class handles encryption and decryption of files using AES.
 */
public class EncryptionHandler {
    private static final int SALT_LENGTH = 16; // 16 bytes = 128 bits
    private static final int IV_LENGTH = 16; // 16 bytes for AES IV
    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String ENCRYPTED_HEADER = "ENCRYPTED:";

    /**
     * Generates a secret key based on the provided password and salt.
     * 
     * @param password Password used for key generation
     * @param salt     Salt used for key generation
     * @return SecretKey
     * @throws Exception If an error occurs during key generation
     */
    private static SecretKey getSecretKey(String password, byte[] salt) throws Exception {
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
    }

    /**
     * Encrypts a file using the specified password.
     * 
     * @param filePath Path to the file to encrypt
     * @param password Password used for encryption
     * @throws Exception If an error occurs during encryption
     */
    public static void encryptFile(String filePath, String password) throws Exception {
        String content = new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);

        // Generate a new salt and IV
        byte[] salt = new byte[SALT_LENGTH];
        byte[] iv = new byte[IV_LENGTH];
        SecureRandom random = new SecureRandom();
        random.nextBytes(salt);
        random.nextBytes(iv);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(password, salt), ivSpec);
        byte[] encryptedBytes = cipher.doFinal(content.getBytes(StandardCharsets.UTF_8));

        // Write header, salt, IV, and encrypted data to file
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(ENCRYPTED_HEADER.getBytes(StandardCharsets.UTF_8));
            fos.write(salt); // Write the salt
            fos.write(iv); // Write the IV
            fos.write(encryptedBytes); // Write encrypted data
        }
    }

    /**
     * Decrypts a file using the specified password.
     * 
     * @param filePath Path to the file to decrypt
     * @param password Password used for decryption
     * @throws Exception If an error occurs during decryption
     */
    public static void decryptFile(String filePath, String password) throws Exception {
        byte[] fileContent = Files.readAllBytes(Paths.get(filePath));

        // Verify header and extract salt, IV, and encrypted data
        String header = new String(fileContent, 0, ENCRYPTED_HEADER.length(), StandardCharsets.UTF_8);
        if (!header.equals(ENCRYPTED_HEADER)) {
            throw new IllegalArgumentException("File is not encrypted or has an invalid format.");
        }

        // Extract salt
        byte[] salt = new byte[SALT_LENGTH];
        System.arraycopy(fileContent, ENCRYPTED_HEADER.length(), salt, 0, SALT_LENGTH);

        // Extract IV
        byte[] iv = new byte[IV_LENGTH];
        System.arraycopy(fileContent, ENCRYPTED_HEADER.length() + SALT_LENGTH, iv, 0, IV_LENGTH);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        // Extract encrypted content
        byte[] encryptedBytes = new byte[fileContent.length - ENCRYPTED_HEADER.length() - SALT_LENGTH - IV_LENGTH];
        System.arraycopy(fileContent, ENCRYPTED_HEADER.length() + SALT_LENGTH + IV_LENGTH, encryptedBytes, 0,
                encryptedBytes.length);

        // Decrypt the content
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, getSecretKey(password, salt), ivSpec);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

        // Write decrypted content back to file
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(decryptedBytes);
        }
    }

    /**
     * Get the flag used to identify encrypted files.
     * 
     * @return String
     */
    public static String getFlag() {
        return ENCRYPTED_HEADER;
    }
}
