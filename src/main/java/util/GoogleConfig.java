package util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class GoogleConfig {
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = GoogleConfig.class.getClassLoader()
                .getResourceAsStream("google.properties")) {
            if (input == null) {
                throw new RuntimeException("Unable to find google.properties");
            }
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Error loading google.properties", e);
        }
    }

    public static String getClientId() {
        return properties.getProperty("google.client.id");
    }

    public static String getClientSecret() {
        return properties.getProperty("google.client.secret");
    }

    public static String getRedirectUri() {
        return properties.getProperty("google.redirect.uri");
    }
}
