package marcguillem.dev;

import marcguillem.dev.Commands.MainCommand;
import picocli.CommandLine;

public class AwsJsonTranslatorCLI {
    public static void main(String[] args) {
        System.exit(new CommandLine(new MainCommand()).execute(args));
    }

    private static void doConfigure() {
        String[] array = new String[4];
        array[0] = "set-configuration";
        array[1] = "--awsAccessKey=EXAMPLE";
        array[2] = "--awsSecretKey=EXAMPLE";
        array[3] = "--awsRegion=eu-west-3";
        System.exit(new CommandLine(new MainCommand()).execute(array));
    }

    private static void doTranslate() {
        String[] array = new String[7];
        array[0] = "translate-json";
        array[1] = "--sourceLanguage=es";
        array[2] = "--targetLanguage=en";
        array[3] = "--sourceFile=test.json";
        array[4] = "--formality=formal";
        array[5] = "--profanityFilter";
        array[6] = "--non-interactive";
        System.exit(new CommandLine(new MainCommand()).execute(array));
    }

}

