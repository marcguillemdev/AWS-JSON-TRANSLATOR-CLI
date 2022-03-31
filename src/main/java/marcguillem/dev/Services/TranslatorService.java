package marcguillem.dev.Services;

import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.SystemPropertiesCredentialsProvider;
import com.amazonaws.services.translate.AmazonTranslate;
import com.amazonaws.services.translate.AmazonTranslateClient;
import com.amazonaws.services.translate.model.*;
import marcguillem.dev.Models.Configuration;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.naming.NameAlreadyBoundException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Scanner;

public class TranslatorService {

    private final AmazonTranslate awsTranslator;
    private String sourceLanguage;
    private String targetLanguage;
    private String formality;
    private boolean profanity;
    private int initialCharCount = 0;
    private final Scanner scanner = new Scanner(System.in);

    public TranslatorService(String sourceLanguage, String targetLanguage, String formality, Boolean profanity) throws Exception {
        this.sourceLanguage = sourceLanguage;
        this.targetLanguage = targetLanguage;
        this.formality = formality;
        this.profanity = profanity;
        Configuration configuration = ConfigurationService.loadConfiguration();
        this.setAWSCredentials(configuration);
        AWSCredentialsProvider awsCreds = new SystemPropertiesCredentialsProvider();
        awsTranslator = AmazonTranslateClient.builder()
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds.getCredentials()))
                .withRegion(configuration.getRegion())
                .build();
    }

    // Check if given string equals ignore case with "informal" or "formal"
    private boolean isFormalityValid(String formality) {
        if(formality == null || formality.isEmpty()) {
            return false;
        }
        return formality.equalsIgnoreCase("informal") || formality.equalsIgnoreCase("formal");
    }

    private void setAWSCredentials(Configuration configuration) {
        System.setProperty("aws.accessKeyId", configuration.getAwsAccessKeyId());
        System.setProperty("aws.secretKey", configuration.getAwsSecretAccessKey());
    }

    public Integer translateJSON(String filename, boolean nonInteractive) throws IOException {
        File file;
        String jsonString;
        String responseFromUser;
        try {
             if(!isFormalityValid(this.formality)) {
                 MessageService.displayYellowMessage("Formality not found or not valid. Using default value: formal", true);
                 this.formality = "formal";
             }
             file = new File(filename);
             if(file.exists()) {
                 jsonString = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
                 MessageService.displayYellowMessage("Counting characters before proceeding", true);
                 this.countCharacters(jsonString);
                 if(!nonInteractive) {
                     MessageService.displayYellowMessage("The file contains ", false);
                     MessageService.displayRedMessage(String.valueOf(this.initialCharCount), false);
                     MessageService.displayYellowMessage(" characters. ", false);
                     MessageService.displayYellowMessage("Do you want to proceed? Y / N", true);
                     responseFromUser = scanner.nextLine();
                     if(responseFromUser.equalsIgnoreCase("Y") || responseFromUser.equalsIgnoreCase("yes")) {
                         return this.doTranslate(jsonString);
                     } else {
                         MessageService.displayRedMessage("Aborting translate operation. Bye :)", true);
                         return 0;
                     }
                 } else {
                     MessageService.displayYellowMessage("The file contains ", false);
                     MessageService.displayRedMessage(String.valueOf(this.initialCharCount), false);
                     MessageService.displayYellowMessage(" characters. Proceeding with translate operation.", false);
                     return this.doTranslate(jsonString);
                 }
             } else {
                 MessageService.displayRedMessage("File not found. Aborting translate operation.", true);
                 MessageService.displayRedMessage("Bye :)", true);
                 return 1;
             }
         } finally {
             file = null;
             jsonString = null;
             responseFromUser = null;
         }
    }

    private Integer doTranslate(String jsonString) throws IOException {
        if(this.isJSONArray(jsonString)) {
            return this.doJsonArrayTranslate(new JSONArray(jsonString));
        } else {
            return this.doJsonObjectTranslate(new JSONObject(jsonString));
        }
    }

    private void countCharacters(String jsonString) {
        if(this.isJSONArray(jsonString)) {
            this.countCharactersInJSONArray(new JSONArray(jsonString));
        } else {
            this.countCharactersInJSONOBject(new JSONObject(jsonString));
        }
    }

    //Check if given string is a JSONArray or JSONObject
    private boolean isJSONArray(String jsonString) {
        return jsonString.startsWith("[");
    }

    private Integer doJsonArrayTranslate(JSONArray jsonToTranslate) throws IOException {
        this.displayTranslatingStringsMessage();
        JSONArray translatedJson = this.translateJSONArray(jsonToTranslate);
        return this.saveAndExit(translatedJson);
    }

    private Integer doJsonObjectTranslate(JSONObject jsonToTranslate) throws IOException {
        this.displayTranslatingStringsMessage();
        JSONObject translatedJson = this.translateJsonObject(jsonToTranslate);
        return this.saveAndExit(translatedJson);
    }

    private void displayTranslatingStringsMessage() {
        MessageService.displayGreenMessage("Translating each string value... ", false);
        MessageService.displayMessage("(this operation may take some time)", true);
    }

    private Integer saveAndExit(Object jsonResult) throws IOException {
        MessageService.displayGreenMessage("Translation done successfully.", true);
        this.saveJsonToFile(jsonResult);
        MessageService.displayGreenMessage("Saved file to output folder.", true);
        return 0;
    }

    private JSONObject translateJsonObject(JSONObject json) {
        json.keySet().forEach(keyStr ->
        {
            String translationTempString;
            JSONArray translationTempArray;
            JSONObject translationTempJSONObject;
            Object keyValue = json.get(keyStr);

            //for nested objects iteration if required
            if (keyValue instanceof JSONObject) {
                translationTempJSONObject = this.translateJsonObject((JSONObject)keyValue);
                json.put(keyStr, translationTempJSONObject);
            } else {
                if(keyValue instanceof JSONArray) {
                    translationTempArray = this.translateJSONArray((JSONArray) keyValue);
                    json.put(keyStr, translationTempArray);
                } else {
                    if(keyValue instanceof String) {
                        translationTempString = this.translateText((String)keyValue);
                        json.put(keyStr, translationTempString);
                    } else {
                        MessageService.displayYellowMessage("Non string value found, skipping...", true);
                    }
                }
            }
        });
        return json;
    }

    private JSONArray translateJSONArray(JSONArray jsonArray) {
        for(int i = 0; i < jsonArray.length(); i ++) {
            if(jsonArray.get(i) instanceof JSONObject) {
                jsonArray.put(i, this.translateJsonObject((JSONObject) jsonArray.get(i)));
            } else {
                if(jsonArray.get(i)  instanceof JSONArray) {
                    jsonArray.put(i, this.translateJSONArray((JSONArray) jsonArray.get(i)));
                } else {
                    jsonArray.put(i, this.translateText((String)jsonArray.get(i)));
                }
            }
        }
        return jsonArray;
    }

    private void saveJsonToFile(Object json) throws IOException {
        File outputFolder = new File("./output/");
        if(!outputFolder.exists()) {
            outputFolder.mkdir();
        }
        File file = new File("./output/" + this.targetLanguage + ".json");
        FileUtils.writeStringToFile(file, json.toString(), StandardCharsets.UTF_8);
    }

    private String translateText(String text) {
        if(text != null) {
            if(text.length() > 0) {
                TranslateTextRequest request = prepareTranslateRequest(text);
                try {
                    TranslateTextResult result = awsTranslator.translateText(request);
                    return result.getTranslatedText();
                } catch (Exception amazonTranslateException) {
                    if(amazonTranslateException instanceof AmazonTranslateException) {
                        if(((AmazonTranslateException) amazonTranslateException).getErrorCode().equalsIgnoreCase("UnrecognizedClientException")) {
                            MessageService.displayRedMessage("Invalid credentials, please configure your credentials with ", false);
                            MessageService.displayGreenMessage("set-configuration ", false);
                            MessageService.displayRedMessage("command.", false);
                            System.exit(1);
                        }
                        MessageService.displayRedMessage(((AmazonTranslateException)amazonTranslateException).getErrorMessage(), true);
                        System.exit(1);
                    }
                    if(amazonTranslateException instanceof SdkClientException) {
                        MessageService.displayRedMessage("Error occurred sending request to AWS Translate service. Maybe the AWS region is not configured correctly.", true);
                        System.exit(1);
                    }
                }
            }
        }
        MessageService.displayYellowMessage("Empty string found, ignoring...", true);
        return "";
    }

    private TranslateTextRequest prepareTranslateRequest(String text) {
        TranslateTextRequest request = new TranslateTextRequest();
        TranslationSettings translationSettings = new TranslationSettings();
        translationSettings.setFormality(this.formality.toUpperCase(Locale.ROOT));
        if(this.profanity) {
            translationSettings.setProfanity(String.valueOf(Profanity.MASK));
        }
        request.setText(text);
        request.setSettings(translationSettings);
        request.setSourceLanguageCode(this.sourceLanguage);
        request.setTargetLanguageCode(this.targetLanguage);
        return request;
    }

    private void countCharactersInJSONOBject(JSONObject json) {

        json.keySet().forEach(keyStr ->
        {
            Object keyValue = json.get(keyStr);
            if (keyValue instanceof JSONObject) {
                this.countCharactersInJSONOBject((JSONObject)keyValue);
            } else {
                if(keyValue instanceof JSONArray) {
                    this.countCharactersInJSONArray((JSONArray) keyValue);
                } else {
                    if(keyValue instanceof String) {
                        this.initialCharCount = this.initialCharCount + keyValue.toString().trim().length();
                    }
                }
            }
        });
    }

    private void countCharactersInJSONArray(JSONArray jsonArray) {
        for(int i = 0; i < jsonArray.length(); i ++) {
            if(jsonArray.get(i) instanceof JSONObject) {
                this.countCharactersInJSONOBject((JSONObject) jsonArray.get(i));
            } else {
                if(jsonArray.get(i)  instanceof JSONArray) {
                    this.countCharactersInJSONArray((JSONArray) jsonArray.get(i));
                } else {
                    if(jsonArray.get(i) instanceof String) {
                        this.initialCharCount = this.initialCharCount + jsonArray.get(i).toString().trim().length();
                    }
                }
            }
        }
    }

    public String getSourceLanguage() {
        return sourceLanguage;
    }

    public void setSourceLanguage(String sourceLanguage) {
        this.sourceLanguage = sourceLanguage;
    }

    public String getTargetLanguage() {
        return targetLanguage;
    }

    public void setTargetLanguage(String targetLanguage) {
        this.targetLanguage = targetLanguage;
    }

    public String getFormality() {
        return formality;
    }

    public void setFormality(String formality) {
        this.formality = formality;
    }

    public boolean isProfanity() {
        return profanity;
    }

    public void setProfanity(boolean profanity) {
        this.profanity = profanity;
    }
}
