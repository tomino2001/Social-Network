package com.example.socialnetwork.config;

import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

public class Config {

    public static String CONFIG_LOCATION = Objects.requireNonNull(Config.class.getClassLoader()
            .getResource("com/example/socialnetwork/config.properties")).getFile();

    public static Properties getProperties() {
        Properties properties = new Properties();
        try {
            properties.load(new FileReader(CONFIG_LOCATION));
            return properties;
        } catch (IOException e) {
            throw new RuntimeException("Cannot load config properties");
        }
    }
}