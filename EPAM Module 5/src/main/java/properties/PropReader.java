package properties;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static java.util.ResourceBundle.Control.FORMAT_PROPERTIES;

/**
 * Universal reader for .properties files
 */

/*public TimeZoneNames(@NotNull String lang) {
        currentLang = lang.toLowerCase();
        if (!supportedTimeZones.containsKey(currentLang)) {                     // This language wasn't requested before and wasn't read as a fallback
        Locale langLocale = new Locale(currentLang);
        ResourceBundle R = ResourceBundle.getBundle("timezones", langLocale);
        String resourceLang = R.getLocale().getLanguage();                  // Detect locale fallbacks, use the same Properties instead of creating a new one
        if (supportedTimeZones.containsKey(resourceLang)) supportedTimeZones.put(currentLang, supportedTimeZones.get(resourceLang));
        else {
        Properties langProps = new Properties();                       // Add new language to cache, load K-V pairs from file into cache map
        R.keySet().forEach(s -> langProps.put(s, R.getString(s)));
        supportedTimeZones.put(currentLang, langProps);                // lang and resourceLang are equivalent, but not guaranteed to be equal
        if (!currentLang.equals(resourceLang)) supportedTimeZones.put(resourceLang, langProps);*/

public class PropReader {
    private class LP {
        final Locale fallback;
        final Properties prop;

        public LP(@Nullable Locale fallback, @NotNull Properties prop) {
            this.fallback = fallback;
            this.prop = prop;
        }
    }

    private final Map<Locale, LP> propMap = new HashMap<>();        // Locale cache. For each locale, fallback is stored if exists
    private final String bundleName;

    public PropReader(String bundleName) throws BundleNotFound {
        try {
            ResourceBundle.getBundle(bundleName);
        } catch (NullPointerException | MissingResourceException e) {
            throw new BundleNotFound(e);
        }
        this.bundleName = bundleName;
    }

    /**
     * Creates a property reader, checks if bundle exists. Returns null if bundle not found.
     *
     * @param bundleName Bundle to read from
     * @return Property reader
     */
    public static @Nullable PropReader createReader(@Nullable String bundleName) {
        try {
            return new PropReader(bundleName);
        } catch (BundleNotFound e) {
            return null;
        }
    }

    private void loadLocaleIntoCache(Locale locale, ResourceBundle R) {
        Properties p = new Properties();
        R.keySet().forEach(key -> p.put(key, R.getString(key)));
        Locale fb = ResourceBundle.Control.getControl(FORMAT_PROPERTIES).getFallbackLocale(R.getBaseBundleName(), locale);
        propMap.put(locale, new LP(fb, p));
        if (!(fb == null || propMap.containsKey(fb)))
            loadLocaleIntoCache(fb, ResourceBundle.getBundle(R.getBaseBundleName(), fb));                                      // Recursively load the entire fallback chain
    }

    /**
     * Tries to read property value for specified locale, returns null if key is null or not found.
     * Bundles for all requested locales are cached.
     *
     * @param property Property key to read
     * @param locale   Locale to read the parameter for
     * @return Property value, null if not found
     */
    public @Nullable String readProperty(@Nullable String property, @NotNull Locale locale) {
        try {
            if (!propMap.containsKey(locale)) {                                                         // This locale is not present in the cache
                ResourceBundle R = ResourceBundle.getBundle(bundleName, locale);
                Locale autoFallback = R.getLocale();
                if (!propMap.containsKey(autoFallback))
                    loadLocaleIntoCache(autoFallback, R);           // If this locale ALWAYS defaults to fallback, it must be cached, otherwise, the locale itself must be cached
                if (!autoFallback.equals(locale))
                    propMap.put(locale, propMap.get(autoFallback));       // In case this locale is ALWAYS read from a fallback, which is now guaranteed to be cached
            }

            String val = (String) propMap.get(locale).prop.get(property);
            Locale possibleFallback = propMap.get(locale).fallback;
            return (val != null) ? val : (possibleFallback != null) ? readProperty(property, possibleFallback) : null;
        } catch (ClassCastException | NullPointerException e) {
            return null;
        }
    }

    /**
     * Tries to read property value for default locale, returns null if key is null or not found.
     * Default bundle is cached on the first call.
     *
     * @param property Property key to read
     * @return Property value, null if not found
     */
    public @Nullable String readProperty(@Nullable String property) {
        return readProperty(property, Locale.getDefault());
    }
}
