package gunnarsgrunn.file;

import org.json.JSONObject;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileHandler {

    private static final String FILE_PATH = "bin.json";

    public static void savePasswordToFile(String domain, String password) {
        try {
            JSONObject jsonData = new JSONObject();
            File file = new File(FILE_PATH);

            if (file.exists()) {
                String content = new String(Files.readAllBytes(Paths.get(FILE_PATH)));
                jsonData = new JSONObject(content);
            }

            JSONObject domainData = jsonData.optJSONObject(domain);
            if (domainData == null) {
                domainData = new JSONObject();
            }
            domainData.put("password", password);

            jsonData.put(domain, domainData);

            try (FileWriter fileWriter = new FileWriter(FILE_PATH)) {
                fileWriter.write(jsonData.toString(4));
            }

        } catch (IOException e) {
            System.out.println("An error occurred while saving the password to file.");
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

            String content = new String(Files.readAllBytes(Paths.get(FILE_PATH)));
            JSONObject jsonData = new JSONObject(content);

            JSONObject domainData = jsonData.optJSONObject(domain);
            if (domainData != null) {
                return domainData.optString("password", null);
            } else {
                System.out.println("Domain not found in the password file.");
                return null;
            }

        } catch (IOException e) {
            System.out.println("An error occurred while retrieving the password.");
            e.printStackTrace();
            return null;
        }
    }
}
