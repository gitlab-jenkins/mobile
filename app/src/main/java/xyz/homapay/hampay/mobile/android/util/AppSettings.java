package xyz.homapay.hampay.mobile.android.util;

import android.content.Context;
import android.content.SharedPreferences.Editor;

import java.util.HashMap;

public class AppSettings {
    // Constants
    public static final String LANGUAGE = "language";
    private final static String tag = AppSettings.class.getSimpleName();
    // Boolean Values
    public static HashMap<String, Boolean> booleanValues;
    // Float Values
    public static HashMap<String, Float> floatValues;
    // Long Values
    public static HashMap<String, Long> longValues;
    // String Values
    public static HashMap<String, String> stringValues;
    // Integer Values
    public static HashMap<String, Integer> intValues;

    /**
     * Get values from shared preferences
     *
     * @param context Context  of application
     * @param key     A string that will be your key to retrieve data from shared preferences
     * @return
     */
    public static Boolean getBoolean(Context context, String key) {
        return getBoolean(context, key, null);
    }

    /**
     * Get values from shared preferences with default value
     *
     * @param context      Context  of application
     * @param key          A string that will be your key to retrieve data from shared preferences
     * @param defaultValue Default value when key not found
     * @return
     */
    public static Boolean getBoolean(Context context, String key,
                                     Boolean defaultValue) {
        if (booleanValues == null)
            booleanValues = new HashMap<>();
        if (!booleanValues.containsKey(key))
            booleanValues.put(key, context.getSharedPreferences(Constants.APP_PREFERENCE_NAME, Context.MODE_PRIVATE).getBoolean(key, defaultValue));

        return booleanValues.get(key);
    }

    /**
     * Set values in shared preferences
     *
     * @param context Context  of application
     * @param key     A string that will be your key to save data in shared preferences
     * @param value   Value to save
     */
    public static void setValue(Context context, String key, Boolean value) {

        if (booleanValues == null)
            booleanValues = new HashMap<>();

        Editor editor = context.getSharedPreferences(Constants.APP_PREFERENCE_NAME, Context.MODE_PRIVATE)
                .edit();
        editor.putBoolean(key, value);
        editor.commit();

        booleanValues.put(key, value);
    }

    /**
     * Get values from shared preferences
     *
     * @param context Context  of application
     * @param key     A string that will be your key to retrieve data from shared preferences
     * @return
     */
    public static Float getFloat(Context context, String key) {
        return getFloat(context, key, null);
    }

    /**
     * Get values from shared preferences with default value
     *
     * @param context      Context  of application
     * @param key          A string that will be your key to retrieve data from shared preferences
     * @param defaultValue Default value when key not found
     * @return
     */
    public static Float getFloat(Context context, String key, Float defaultValue) {

        if (floatValues == null)
            floatValues = new HashMap<>();

        if (!floatValues.containsKey(key))
            floatValues.put(key,
                    context.getSharedPreferences(Constants.APP_PREFERENCE_NAME, Context.MODE_PRIVATE)
                            .getFloat(key, defaultValue));

        return floatValues.get(key);
    }

    /**
     * Set values in shared preferences
     *
     * @param context Context  of application
     * @param key     A string that will be your key to save data in shared preferences
     * @param value   Value to save
     */
    public static void setValue(Context context, String key, Float value) {

        if (floatValues == null)
            floatValues = new HashMap<>();

        Editor editor = context.getSharedPreferences(Constants.APP_PREFERENCE_NAME, Context.MODE_PRIVATE)
                .edit();
        editor.putFloat(key, value);
        editor.commit();

        floatValues.put(key, value);
    }

    /**
     * Get values from shared preferences
     *
     * @param context Context  of application
     * @param key     A string that will be your key to retrieve data from shared preferences
     * @return
     */
    public static Long getLong(Context context, String key) {
        return getLong(context, key, null);
    }

    /**
     * Get values from shared preferences with default value
     *
     * @param context      Context  of application
     * @param key          A string that will be your key to retrieve data from shared preferences
     * @param defaultValue Default value when key not found
     * @return
     */
    public static Long getLong(Context context, String key, Long defaultValue) {

        if (longValues == null)
            longValues = new HashMap<>();

        if (!longValues.containsKey(key))
            longValues.put(key,
                    context.getSharedPreferences(Constants.APP_PREFERENCE_NAME, Context.MODE_PRIVATE)
                            .getLong(key, defaultValue));

        return longValues.get(key);
    }

    /**
     * Set values in shared preferences
     *
     * @param context Context  of application
     * @param key     A string that will be your key to save data in shared preferences
     * @param value   Value to save
     */
    public static void setValue(Context context, String key, Long value) {

        if (longValues == null)
            longValues = new HashMap<>();

        Editor editor = context.getSharedPreferences(Constants.APP_PREFERENCE_NAME, Context.MODE_PRIVATE)
                .edit();
        editor.putLong(key, value);
        editor.commit();

        longValues.put(key, value);
    }

    /**
     * Get values from shared preferences
     *
     * @param context Context  of application
     * @param key     A string that will be your key to retrieve data from shared preferences
     * @return
     */
    public static String getString(Context context, String key) {
        return getString(context, key, null);
    }

    /**
     * Get values from shared preferences with default value
     *
     * @param context      Context  of application
     * @param key          A string that will be your key to retrieve data from shared preferences
     * @param defaultValue Default value when key not found
     * @return
     */
    public static String getString(Context context, String key,
                                   String defaultValue) {

        if (stringValues == null)
            stringValues = new HashMap<>();

        if (!stringValues.containsKey(key))
            stringValues.put(key,
                    context.getSharedPreferences(Constants.APP_PREFERENCE_NAME, Context.MODE_PRIVATE)
                            .getString(key, defaultValue));

        return stringValues.get(key);
    }

    /**
     * Set values in shared preferences
     *
     * @param context Context  of application
     * @param key     A string that will be your key to save data in shared preferences
     * @param value   Value to save
     */
    public static void setValue(Context context, String key, String value) {

        if (stringValues == null)
            stringValues = new HashMap<>();

        Editor editor = context.getSharedPreferences(Constants.APP_PREFERENCE_NAME, Context.MODE_PRIVATE)
                .edit();
        editor.putString(key, value);
        editor.commit();

        stringValues.put(key, value);
    }

    /**
     * Get values from shared preferences
     *
     * @param context Context  of application
     * @param key     A string that will be your key to retrieve data from shared preferences
     * @return
     */
    public static Integer getInt(Context context, String key) {
        return getInt(context, key, null);
    }

    /**
     * Get values from shared preferences with default value
     *
     * @param context      Context  of application
     * @param key          A string that will be your key to retrieve data from shared preferences
     * @param defaultValue Default value when key not found
     * @return
     */
    public static Integer getInt(Context context, String key,
                                 Integer defaultValue) {

        if (intValues == null)
            intValues = new HashMap<>();

        if (!intValues.containsKey(key))
            intValues.put(key,
                    context.getSharedPreferences(Constants.APP_PREFERENCE_NAME, Context.MODE_PRIVATE)
                            .getInt(key, defaultValue));

        return intValues.get(key);
    }

    /**
     * Set values in shared preferences
     *
     * @param context Context  of application
     * @param key     A string that will be your key to save data in shared preferences
     * @param value   Value to save
     */
    public static void setValue(Context context, String key, Integer value) {

        if (intValues == null)
            intValues = new HashMap<>();

        Editor editor = context.getSharedPreferences(Constants.APP_PREFERENCE_NAME, Context.MODE_PRIVATE)
                .edit();
        editor.putInt(key, value);
        editor.commit();

        intValues.put(key, value);
    }

}
