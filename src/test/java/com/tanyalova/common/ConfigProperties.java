package com.tanyalova.common;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class ConfigProperties {
    public static final Logger LOGGER = LoggerFactory.getLogger(ConfigProperties.class);

    private static Properties properties = new Properties();

    public enum Env {
        LOCAL,
        PROD
    }

    private static Env env;

    static {
        String fileName = "config.properties";
        try {
            properties.load(ConfigProperties.class.getClassLoader().getResourceAsStream(fileName));
        } catch (Exception e) {
            LOGGER.error("An error occurred while loading properties from file: " + fileName);
        }
        String envSystem = System.getProperty("env");
        String envString = StringUtils.isEmpty(envSystem) ? properties.getProperty("env") : envSystem;
        if (envString == null || envString.isEmpty()) {
            throw new IllegalArgumentException("Couldn't find env value in config file");
        }
        env = Env.valueOf(envString);
        LOGGER.info("Env: " + env);
    }

    private ConfigProperties() {
    }

    public static String getEnvProperty(String name) {
        return properties.getProperty(env + "." + name);
    }

    public static Env getEnv() {
        return env;
    }

}
