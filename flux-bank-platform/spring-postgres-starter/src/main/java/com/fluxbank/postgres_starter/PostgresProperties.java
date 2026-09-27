package com.fluxbank.postgres_starter;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties(prefix = "fluxbank.postgres")
public class PostgresProperties {

    private String host = "localhost";

    private int port = 5432;

    private String database;

    private String schema;

    private String username;

    private String password;

    private int maxPoolSize = 10;

    private int minIdle = 2;

    private long connectionTimeoutMs = 30_000L;

    @NestedConfigurationProperty
    private Provisioning provisioning = new Provisioning();

    @NestedConfigurationProperty
    private Flyway flyway = new Flyway();

    public static class Provisioning {
        private boolean enabled = false;
        private String bootstrapUser;

        private String bootstrapPassword;

        private String bootstrapDatabase = "postgres";

        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }

        public String getBootstrapUser() { return bootstrapUser; }
        public void setBootstrapUser(String bootstrapUser) { this.bootstrapUser = bootstrapUser; }

        public String getBootstrapPassword() { return bootstrapPassword; }
        public void setBootstrapPassword(String bootstrapPassword) { this.bootstrapPassword = bootstrapPassword; }

        public String getBootstrapDatabase() { return bootstrapDatabase; }
        public void setBootstrapDatabase(String bootstrapDatabase) { this.bootstrapDatabase = bootstrapDatabase; }
    }

    public static class Flyway {
        private boolean enabled = true;

        private boolean createSchemas = true;

        private String locations = "classpath:db/migration";

        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }

        public boolean isCreateSchemas() { return createSchemas; }
        public void setCreateSchemas(boolean createSchemas) { this.createSchemas = createSchemas; }

        public String getLocations() { return locations; }
        public void setLocations(String locations) { this.locations = locations; }
    }

    public String getHost() { return host; }
    public void setHost(String host) { this.host = host; }

    public int getPort() { return port; }
    public void setPort(int port) { this.port = port; }

    public String getDatabase() { return database; }
    public void setDatabase(String database) { this.database = database; }

    public String getSchema() { return schema; }
    public void setSchema(String schema) { this.schema = schema; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public int getMaxPoolSize() { return maxPoolSize; }
    public void setMaxPoolSize(int maxPoolSize) { this.maxPoolSize = maxPoolSize; }

    public int getMinIdle() { return minIdle; }
    public void setMinIdle(int minIdle) { this.minIdle = minIdle; }

    public long getConnectionTimeoutMs() { return connectionTimeoutMs; }
    public void setConnectionTimeoutMs(long connectionTimeoutMs) { this.connectionTimeoutMs = connectionTimeoutMs; }

    public Provisioning getProvisioning() { return provisioning; }
    public void setProvisioning(Provisioning provisioning) { this.provisioning = provisioning; }

    public Flyway getFlyway() { return flyway; }
    public void setFlyway(Flyway flyway) { this.flyway = flyway; }
}
