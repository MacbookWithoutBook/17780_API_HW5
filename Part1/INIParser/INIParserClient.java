import java.io.*;
import java.util.Map;

public class INIParserClient {

    public static void main(String[] args) {
        int status;

        if (args.length < 1) {
            createExampleIniFile();
            status = parseIniFile("example.ini");
        } else {
            status = parseIniFile(args[0]);
        }

        System.exit(status);
    }

    /**
     * Creates an example INI file with sample sections and key-value pairs.
     * 
     * Thanks to the iniparser API(https://github.com/ndevilla/iniparser/tree/master/src) for the example data.
     */
    private static void createExampleIniFile() {
        try (PrintWriter writer = new PrintWriter("example.ini")) {
            writer.println("# This is an example of an ini file");
            writer.println();
            writer.println("[Pizza]");
            writer.println("Ham       = yes ;");
            writer.println("Mushrooms = TRUE ;");
            writer.println("Capres    = 0 ;");
            writer.println("Cheese    = Non ;");
            writer.println();
            writer.println("[Wine]");
            writer.println("Grape     = Cabernet Sauvignon ;");
            writer.println("Year      = 1989 ;");
            writer.println("Country   = Spain ;");
            writer.println("Alcohol   = 12.5 ;");
        } catch (IOException e) {
            System.err.println("INIParserClient: Cannot create example.ini file");
        }
    }

    /**
     * Parses an INI file, displays its contents, and retrieves specific entries.
     *
     * @param iniName The name of the INI file to parse.
     * @return 0 on success, -1 on failure.
     */
    private static int parseIniFile(String iniName) {
        INIParser parser = new INIParser();
    
        try {
            Map<String, String> iniData = parser.load(iniName);
            parser.dump(System.err);
    
            // Get Pizza attributes
            System.out.println("Pizza:");
            boolean ham = parser.getBoolean("pizza:ham", false);
            System.out.println("Ham:       [" + (ham ? 1 : 0) + "]");
            boolean mushrooms = parser.getBoolean("pizza:mushrooms", false);
            System.out.println("Mushrooms: [" + (mushrooms ? 1 : 0) + "]");
            boolean capres = parser.getBoolean("pizza:capres", false);
            System.out.println("Capres:    [" + (capres ? 1 : 0) + "]");
            boolean cheese = parser.getBoolean("pizza:cheese", false);
            System.out.println("Cheese:    [" + (cheese ? 1 : 0) + "]");
    
            // Get Wine attributes
            System.out.println("Wine:");
            String grape = parser.getString("wine:grape", "UNDEF");
            System.out.println("Grape:     [" + grape + "]");
            int year = parser.getInt("wine:year", -1);
            System.out.println("Year:      [" + year + "]");
            String country = parser.getString("wine:country", "UNDEF");
            System.out.println("Country:   [" + country + "]");
            String alcoholStr = parser.getString("wine:alcohol", "-1.0");
            double alcohol = Double.parseDouble(alcoholStr);
            System.out.println("Alcohol:   [" + alcohol + "]");
    
            return 0;
    
        } catch (IOException e) {
            System.err.println("INIParserClient: Cannot parse file: " + iniName);
            return -1;
        }
    }

    /**
     * Writes example entries to an INI file, creating sections and key-value pairs.
     *
     * @param iniName The name of the INI file to write to.
     * @return 0 on success, -1 on failure.
     */
    private static int writeToIniFile(String iniName) {
        INIParser parser = new INIParser();

        try {
            parser.load(iniName);

            // Set a section and key/value pair
            parser.setEntry("Pizza", null);
            parser.setEntry("Pizza:Cheese", "TRUE");

            try (PrintStream out = new PrintStream(new FileOutputStream(iniName))) {
                parser.dump(out);
            } catch (IOException e) {
                System.err.println("INIParserClient: Cannot write to file: " + iniName);
                return -1;
            }

            return 0;

        } catch (IOException e) {
            System.err.println("INIParserClient: Cannot parse file: " + iniName);
            return -1;
        }
    }

    /**
     * Creates an empty INI file for testing purposes.
     */
    private static void createEmptyIniFile() {
        try (PrintWriter writer = new PrintWriter("example.ini")) {
            // Create an empty file
        } catch (IOException e) {
            System.err.println("INIParserClient: Cannot create empty example.ini file");
        }
    }
}
