package marcguillem.dev.Commands;

import marcguillem.dev.Services.ConfigurationService;
import marcguillem.dev.Services.MessageService;
import org.json.JSONArray;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.concurrent.Callable;

@Command(
        name = "set-configuration",
        description = "Set the access and secret AWS keys. For more information use set-configuration --help"
)
public class SetConfigurationCommand implements Callable<Integer> {

    private static final String[] regions = {"us-east-1", "us-east-2", "us-west-1", "us-west-2", "eu-west-1", "eu-west-2", "eu-west-3", "eu-central-1", "ap-northeast-1", "ap-northeast-2", "ap-southeast-1", "ap-southeast-2", "ap-south-1", "ca-central-1", "sa-east-1"};

    @Option(
            names = "--awsAccessKey",
            description = "AWS Access Key."
    )
    private String awsAccessKey;

    @Option(
            names = "--awsSecretKey",
            description = "AWS Secret Key."
    )
    private String awsSecretKey;

    @Option(
            names = "--awsRegion",
            description = "AWS Region."
    )
    private String awsRegion;

    @Option(
            names = {"-h", "--help"},
            description = "Display help message",
            usageHelp = true
    )
    private boolean usageHelp;

    @Override
    public Integer call() throws Exception {
        try {
            if (awsAccessKey != null) {
                ConfigurationService.saveConfiguration(awsAccessKey, null, null);
                MessageService.displayGreenMessage("AWS Access Key saved!", true);
            }
            if (awsSecretKey != null) {
                ConfigurationService.saveConfiguration(null, awsSecretKey, null);
                MessageService.displayGreenMessage("AWS Secret Key saved!", true);
            }

            if (awsRegion != null) {
                if (!isValidRegion(awsRegion)) {
                    MessageService.displayRedMessage("AWS Region " + awsRegion + " is not present in the list of valid regions. Please check the AWS documentation. ", true);
                    MessageService.displayYellowMessage("Valid regions are: ", false);
                    MessageService.displayGreenMessage(new JSONArray(regions).toString(), true);
                    MessageService.displayYellowMessage("Saving the AWS Region anyway...", true);
                }
                ConfigurationService.saveConfiguration(null, null, awsRegion);
                MessageService.displayGreenMessage("AWS Region saved!", true);
            }

            if (awsSecretKey == null && awsAccessKey == null && awsRegion == null) {
                MessageService.displayYellowMessage("No options specified, exiting with no changes...", true);
            }
            return 0;
        } catch (Exception error) {
            MessageService.displayRedMessage(error.getMessage(), true);
            return 1;
        }
    }

    // Check if given string is a valid AWS Region
    private static boolean isValidRegion(String region) {
        for (String r : regions) {
            if (r.equalsIgnoreCase(region)) {
                return true;
            }
        }
        return false;
    }

}
