package marcguillem.dev.Services;

import marcguillem.dev.Models.Configuration;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class ConfigurationService {

    public static Configuration loadConfiguration() throws Exception {
        File file = new File("./config.json");
        if (file.exists()) {
            if (isValidJson(Files.readString(file.toPath(), StandardCharsets.UTF_8))) {
                JSONObject conf = new JSONObject(Files.readString(file.toPath(), StandardCharsets.US_ASCII));
                String awsRegionKey = (String) conf.get("region");
                String awsSecretKey = (String) conf.get("awsSecretAccessKey");
                String awsAccessKey = (String) conf.get("awsAccessKeyId");

                if (awsAccessKey != null && awsSecretKey != null && awsRegionKey != null) {
                    if (awsAccessKey.length() > 0 && awsSecretKey.length() > 0 && awsRegionKey.length() > 0) {
                        return new Configuration(awsAccessKey, awsSecretKey, awsRegionKey);
                    }
                }
            }
        }
        MessageService.displayRedMessage("Configuration not found or not valid. Please configure it with the command: ", false);
        MessageService.displayGreenMessage("set-configuration", false);
        System.exit(1);
        return null;
    }

    //Method that save awsSecretAccessKey to config.json taking existing config.json as a base
    public static void saveConfiguration(String awsAccessKey, String awsSecretKey, String awsRegionKey) throws Exception {
        File file = new File("./config.json");
        if (file.exists()) {
            String jsonString = Files.readString(file.toPath(), StandardCharsets.UTF_8);
            if (isValidJson(jsonString)) {
                JSONObject conf = new JSONObject(jsonString);
                if (awsAccessKey != null) {
                    conf.put("awsAccessKeyId", awsAccessKey);
                }
                if (awsSecretKey != null) {
                    conf.put("awsSecretAccessKey", awsSecretKey);
                }
                if (awsRegionKey != null) {
                    conf.put("region", awsRegionKey);
                }
                Files.write(new File("./config.json").toPath(), conf.toString().getBytes());
                return;
            } else {
                file.delete();
            }
        }
        writeEmptyConfigurationObjectToJson();
        saveConfiguration(awsAccessKey, awsSecretKey, awsRegionKey);
    }

    private static void writeEmptyConfigurationObjectToJson() throws Exception {
        File file = new File("./config.json");
        if (!file.exists()) {
            file.createNewFile();
        }
        JSONObject conf = new JSONObject();
        conf.put("awsAccessKeyId", "");
        conf.put("awsSecretAccessKey", "");
        conf.put("region", "");
        Files.write(new File("./config.json").toPath(), conf.toString().getBytes());
    }


    // Check if given string is a valid json
    private static boolean isValidJson(String json) {
        try {
            new JSONObject(json);
        } catch (JSONException ex) {
            try {
                new JSONArray(json);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }


}
