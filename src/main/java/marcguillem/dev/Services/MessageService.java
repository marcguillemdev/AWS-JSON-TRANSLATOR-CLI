package marcguillem.dev.Services;

public class MessageService {

    // Static method that display a message with Yellow color and Bolder text
    public static void displayYellowMessage(String message, boolean newLine) {
        if(newLine) {
            System.out.println("\033[1;33m" + message + "\033[0m");
        } else {
            System.out.print("\033[1;33m" + message + "\033[0m");
        }

    }

    // Static method that display a red message with bold text
    public static void displayRedMessage(String message, boolean newLine) {
        if (newLine) {
            System.out.println("\033[1;31m" + message + "\033[0m");
        } else {
            System.out.print("\033[1;31m" + message + "\033[0m");
        }
    }

    // Static method that display a green message with bold text
    public static void displayGreenMessage(String message, boolean newLine) {
        if(newLine) {
            System.out.println("\033[1;32m" + message + "\033[0m");
        } else {
            System.out.print("\033[1;32m" + message + "\033[0m");
        }
    }

    // Static method that display a message with bold text
    public static void displayMessage(String message, boolean newLine) {
        if (newLine) {
            System.out.println("\033[1m" + message + "\033[0m");
        } else {
            System.out.print("\033[1m" + message + "\033[0m");
        }
    }

}
