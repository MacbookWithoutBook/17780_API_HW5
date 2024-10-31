package Collation;

/**
 * The purposes that locales serve are grouped into categories, so that a user or a program
 * can choose the locale for each category independently. The following is all the available categories;
 * each name is both an environment variable that a user can set, and a macro name that you can use as
 * the first argument to {@link Collation}{@code .setLocale}.
 */
public enum LocaleCategory {
    LC_COLLATE, // This category applies to collation of strings
    LC_CTYPE, // This category applies to classification and conversion of characters
    LC_MONETARY, // This category applies to formatting monetary values
    LC_NUMERIC, // This category applies to formatting numeric values that are not monetary
    LC_TIME, // This category applies to formatting date and time values
    LC_MESSAGES, // This category applies to selecting the language used in the user interface for message translation and contains regular expressions for affirmative and negative responses.
    LC_ALL, // This is not a category; it is only a macro that you can use with setlocale to set a single locale for all purposes. Setting this environment variable overwrites all selections by the other LC_* variables or LANG.
    LANG // If this environment variable is defined, its value specifies the locale to use for all purposes except as overridden by the variables above.
}