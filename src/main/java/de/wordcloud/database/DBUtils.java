package de.wordcloud.database;

import java.util.Properties;

public class DBUtils {
    public static Properties getConnectionProperties() {
        Properties connectionProperties = new Properties();
        connectionProperties.put("user", "root");
        connectionProperties.put("password", "root");
        return connectionProperties;
    }
}
