import java.io.*;
import java.util.*;

/**
 * Parser for INI files.
 * This class allows loading, modifying, and querying configuration settings stored in INI format.
 * 
 * <p>
 * INI files store configuration data in a structured format with sections and key-value pairs.
 * This API supports retrieving values as strings, integers, doubles, and booleans, as well as
 * setting or unsetting entries and accessing sections directly.
 * 
 * <p>
 * This API is translated from the iniparser API found here:
 * <a href="https://github.com/ndevilla/iniparser/tree/master/src">iniparser GitHub</a>
 * 
 * <p>
 * <strong>Thread Safety:</strong> The {@code INIParser} class is not thread-safe. Access to the
 * dictionary (i.e., loading, modifying, and querying) should be synchronized externally if used
 * in a concurrent environment.
 * 
 * <h3>Example Usage:</h3>
 * <pre>{@code
 * INIParser parser = new INIParser();
 * 
 * // Load INI file
 * try {
 *     parser.load("config.ini");
 * } catch (IOException e) {
 *     System.err.println("Failed to load INI file.");
 * }
 * 
 * // Retrieve values
 * String value = parser.getString("section:key", "default");
 * int intValue = parser.getInt("section:key", -1);
 * boolean boolValue = parser.getBoolean("section:key", false);
 * 
 * // Set and remove values
 * parser.setEntry("section:key", "newValue");
 * parser.unsetEntry("section:key");
 * 
 * // Dump contents to System.out
 * parser.dump(System.out);
 * }</pre>
 */
public class INIParser {

    private final Map<String, String> dictionary;
    private static PrintStream errorCallback;

    /**
     * Constructs an empty INIParser instance with an empty configuration dictionary.
     */
    public INIParser() {
        this.dictionary = new HashMap<>();
    }

    /**
     * Sets a custom error output stream for reporting syntax errors or missing entries.
     *
     * @param errCallback a PrintStream for error output, such as {@code System.err}
     *                    or a custom logging stream. If null, defaults to {@code System.err}.
     */
    public static void setErrorCallback(PrintStream errCallback) {
        errorCallback = (errCallback != null) ? errCallback : System.err;
    }

    /**
     * Loads the contents of an INI file, populating the dictionary with parsed entries.
     *
     * @param fileName the name of the INI file to parse
     * @return the populated dictionary of parsed entries
     * @throws IOException if the file cannot be read
     * 
     * <h3>Example Usage:</h3>
     * <pre>{@code
     * INIParser parser = new INIParser();
     * try {
     *     parser.load("config.ini");
     * } catch (IOException e) {
     *     e.printStackTrace();
     * }
     * }</pre>
     */
    public Map<String, String> load(String fileName) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            return loadFromReader(reader);
        }
    }

    /**
     * Loads the contents of an INI file from a BufferedReader, populating the dictionary.
     *
     * @param reader BufferedReader providing the INI file contents
     * @return the populated dictionary of parsed entries
     * @throws IOException if an error occurs while reading
     */
    public Map<String, String> loadFromReader(BufferedReader reader) throws IOException {
        String line, section = "";
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#") || line.startsWith(";")) continue;

            if (line.startsWith("[")) {
                section = parseSection(line);
            } else {
                parseKeyValue(line, section);
            }
        }
        return dictionary;
    }

    // Private methods for loadFromReader()
    private String parseSection(String line) {
        int end = line.indexOf(']');
        if (end == -1) {
            errorCallback.println("Syntax error: malformed section header.");
            return "";
        }
        return line.substring(1, end).trim().toLowerCase();
    }

    private void parseKeyValue(String line, String section) {
        String[] keyValue = line.split("=", 2);
        if (keyValue.length < 2) {
            errorCallback.println("Syntax error: malformed key-value pair.");
            return;
        }
        String key = section + ":" + keyValue[0].trim().toLowerCase();
        String value = parseValue(keyValue[1]);
        dictionary.put(key, value);
    }

    private String parseValue(String valuePart) {
        valuePart = valuePart.trim();
        if ((valuePart.startsWith("\"") && valuePart.endsWith("\"")) || 
            (valuePart.startsWith("'") && valuePart.endsWith("'"))) {
            valuePart = valuePart.substring(1, valuePart.length() - 1);
        }
        return valuePart;
    }

    /**
     * Retrieves the string value associated with the specified key.
     *
     * @param key          the key to retrieve the value for
     * @param defaultValue the default value if the key is not found
     * @return the value associated with the key, or {@code defaultValue} if the key is absent
     */
    public String getString(String key, String defaultValue) {
        return dictionary.getOrDefault(key, defaultValue);
    }

    /**
     * Retrieves the integer value associated with the specified key.
     *
     * @param key          the key to retrieve the value for
     * @param defaultValue the default integer value if the key is not found or cannot be parsed as an integer
     * @return the integer value, or {@code defaultValue} if the key is absent or invalid
     */
    public int getInt(String key, int defaultValue) {
        String value = dictionary.get(key);
        if (value == null) return defaultValue;
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Retrieves the boolean value associated with the specified key.
     *
     * @param key          the key to retrieve the value for
     * @param defaultValue the default boolean value if the key is not found or cannot be parsed as a boolean
     * @return the boolean value, or {@code defaultValue} if the key is absent or invalid
     */
    public boolean getBoolean(String key, boolean defaultValue) {
        String value = dictionary.get(key);
        if (value == null) return defaultValue;
        switch (value.toLowerCase()) {
            case "1": case "y": case "yes": case "true": return true;
            case "0": case "n": case "no": case "false": return false;
            default: return defaultValue;
        }
    }

    /**
     * Counts the number of unique sections within the dictionary.
     *
     * @return the total number of sections found in the dictionary
     */
    public int getSectionCount() {
        Set<String> sections = new HashSet<>();
        for (String key : dictionary.keySet()) {
            String section = key.split(":")[0];
            sections.add(section);
        }
        return sections.size();
    }

    /**
     * Retrieves the name of a section at a specified index.
     *
     * @param index the index of the section name to retrieve
     * @return the name of the section at the specified index, or {@code null} if the index is out of bounds
     */
    public String getSectionName(int index) {
        Set<String> sections = new LinkedHashSet<>();
        for (String key : dictionary.keySet()) {
            String section = key.split(":")[0];
            sections.add(section);
        }
        return index < sections.size() ? new ArrayList<>(sections).get(index) : null;
    }

    /**
     * Checks if a specific entry is present within the dictionary.
     *
     * @param entry the entry to search for
     * @return {@code true} if the entry exists, {@code false} otherwise
     */
    public boolean findEntry(String entry) {
        return dictionary.containsKey(entry.toLowerCase());
    }

    /**
     * Sets or updates an entry in the dictionary with the specified value.
     *
     * @param entry the entry key
     * @param value the value to associate with the entry
     * @return 0 if set successfully, or -1 if the entry is null or empty
     */
    public int setEntry(String entry, String value) {
        if (entry == null || entry.isEmpty()) {
            return -1;
        }
        dictionary.put(entry.toLowerCase(), value);
        return 0;
    }

    /**
     * Removes an entry from the dictionary if it exists.
     *
     * @param entry the entry to remove
     */
    public void unsetEntry(String entry) {
        dictionary.remove(entry.toLowerCase());
    }

    /**
     * Dumps all entries in the dictionary to the specified PrintStream.
     *
     * @param out the PrintStream to write dictionary contents to, e.g., {@code System.out}
     */
    public void dump(PrintStream out) {
        for (Map.Entry<String, String> entry : dictionary.entrySet()) {
            out.println("[" + entry.getKey() + "]=" + entry.getValue());
        }
    }
}
