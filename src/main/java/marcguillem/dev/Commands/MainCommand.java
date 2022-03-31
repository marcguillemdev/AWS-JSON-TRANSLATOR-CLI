package marcguillem.dev.Commands;

import marcguillem.dev.Services.MessageService;
import picocli.CommandLine.*;

@Command(
        subcommands = {TranslateJsonCommand.class, SetConfigurationCommand.class},
        name = "AWS-JSON-TRANSLATOR",
        description = "AWS-JSON-TRANSLATOR is a command line tool that translates deep and light JSON to other languages without altering structure."
)
public class MainCommand implements Runnable {

    @Option(
            names = {"-h", "--help"},
            description = "Display help message",
            usageHelp = true
    )
    private boolean usageHelp;

    @Override
    public void run() {
        MessageService.displayYellowMessage("No action specified, exiting.", true);
        System.exit(0);
    }

}
