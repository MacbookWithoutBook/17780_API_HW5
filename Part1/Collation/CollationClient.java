package Collation;

// Client code for using collation functions
public class CollationClient {
    public static void main(String[] args) {
        Collation collation = new Collation();

        // Set the locale to "zh_CN.GB2312" for all categories
        boolean success = collation.setLocale(LocaleCategory.LC_ALL, "zh_CN.GB2312");
        
        if (success) {

            // Compare two strings lexicographically based on the collation rules of the current locale
            int comparisonResult = collation.compareStringWithCollation("你好", "你好世界");
            if (comparisonResult < 0) {
                System.out.println("\"你好\" is lexicographically smaller than \"你好世界\"");
            } else if (comparisonResult > 0) {
                System.out.println("\"你好\" is lexicographically greater than \"你好世界\"");
            } else {
                System.out.println("\"你好\" is lexicographically equal to \"你好世界\"");
            }

            // Transform the first 2 characters of "你好世界" based on the current locale settings
            try {
                String transformed = collation.transformStringWithCollation("你好世界", 2);
                System.out.println("Transformed string: " + transformed);
            } catch (IllegalArgumentException e) {
                System.err.println("Error in transforming string: " + e.getMessage());
            }
        } else {
            System.err.println("Failed to set the locale. Double check the locale name!");
        }
    }
}
