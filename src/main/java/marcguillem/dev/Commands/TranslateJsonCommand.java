package marcguillem.dev.Commands;

import marcguillem.dev.Services.MessageService;
import marcguillem.dev.Services.TranslatorService;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.List;
import java.util.concurrent.Callable;

@Command(
        name = "translate-json",
        description = "Translate JSON to desired language. For more information use 'translate-json --help'"
)
public class TranslateJsonCommand implements Callable<Integer> {

    @Option(
            names = {"--non-interactive"},
            description = "Non interactive mode. Do not ask for input."
    )
    private boolean nonInteractive;

    @Option(
            names = {"--formality"},
            description = "Formality level. Possible values: formal, informal. Default: formal"
    )
    private String formality;

    @Option(
            names = {"--enableProfanityFilter"},
            description = "Profanity filter."
    )
    private boolean profanity;

    @Option(
            names = {"-ct", "--customTerminology"},
            description = "Name or names of custom terminologies created in Amazon Translate separated by comma. To create your custom terminology visit https://eu-west-3.console.aws.amazon.com/translate/home?region=eu-west-3#terminology"
    )
    private String customTerminologies;

    @Option(
            names = {"-sl", "--sourceLanguage"},
            description = "Source language of JSON in ISO 3166 alpha-2 code. See the table in https://docs.aws.amazon.com/translate/latest/dg/what-is.html#what-is-languages for more information.",
            required = true
    )
    private String sourceLanguage;

    @Option(
            names = {"-tl", "--targetLanguage"},
            description = "Language you want to translate to in ISO 3166 alpha-2 code. See the table in https://docs.aws.amazon.com/translate/latest/dg/what-is.html#what-is-languages for more information.",
            required = true
    )
    private String targetLanguage;

    @Option(
            names = {"-sf", "--sourceFile"},
            description = "Source file containing JSON to translate.",
            required = true
    )
    private String sourceFile;

    @Option(
            names = {"-h", "--help"},
            description = "Display help message",
            usageHelp = true
    )
    private boolean usageHelp;

    // Method named displayTranslateInfo that displays using MessageService the values of all fields in this class
    public void displayInfo() {
        MessageService.displayMessage("Source language: ", false);
        MessageService.displayGreenMessage(this.sourceLanguage, true);

        MessageService.displayMessage("Target language: ", false);
        MessageService.displayGreenMessage(this.targetLanguage, true);

        MessageService.displayMessage("Source file: ", false);
        MessageService.displayGreenMessage(this.sourceFile, true);

        MessageService.displayMessage("Formality: ", false);
        MessageService.displayGreenMessage(this.formality, true);

        MessageService.displayMessage("Profanity filter: ", false);
        MessageService.displayGreenMessage(String.valueOf(this.profanity), true);

        MessageService.displayMessage("Custom terminologies: ", false);
        if(this.customTerminologies != null && this.customTerminologies.length() > 0) {
            MessageService.displayGreenMessage(this.customTerminologies, true);
        } else {
            MessageService.displayYellowMessage("No custom terminologies provided.", true);
        }
        MessageService.displayMessage("", true);
    }

    @Override
    public Integer call() throws Exception {
        if (this.formality == null) {
            this.formality = "formal";
        }
        TranslatorService translatorService = new TranslatorService(this.sourceLanguage, this.targetLanguage, formality, profanity, customTerminologies);
        this.displayInfo();
        return translatorService.translateJSON(this.sourceFile, nonInteractive);
    }
}
