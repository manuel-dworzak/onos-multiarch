package de.uniba.ktr.crma;

import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.slf4j.LoggerFactory.getLogger;

public class AppConfigurator {
    private static final Logger logger = getLogger(AppConfigurator.class.getName());
    private static Properties properties;
    private static final AppConfigurator instance = new AppConfigurator();

    private AppConfigurator() {
        properties = new Properties();
        try(InputStream in = AppConfigurator.class.getClassLoader().getResourceAsStream("app.properties")) {
            properties.load(in);
        } catch(IOException e){
            logger.error("Error loading configuration file config.properties",e);
        }
    }

    public static AppConfigurator getInstance() {
        return instance;
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }
}
