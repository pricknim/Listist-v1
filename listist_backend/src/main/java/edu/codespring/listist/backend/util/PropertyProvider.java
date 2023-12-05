package edu.codespring.listist.backend.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyProvider {

    private static final Logger LOG = LoggerFactory.getLogger(PropertyProvider.class);

    private static final Properties properties;

    static {
        properties = new Properties();
        try (InputStream is = PropertyProvider.class.getResourceAsStream("/listist.properties")) {
            if (is != null) {
                properties.load(is);
            }
        } catch (IOException e) {
            LOG.error("Failed to access resource listist.", e);
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

}
