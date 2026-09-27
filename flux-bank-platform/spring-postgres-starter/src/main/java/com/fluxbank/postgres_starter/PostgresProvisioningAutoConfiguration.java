package com.fluxbank.postgres_starter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.util.Assert;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;


@AutoConfiguration
@AutoConfigureBefore(PostgresAutoConfiguration.class)
@ConditionalOnProperty(prefix = "fluxbank.postgres.provisioning", name = "enabled", havingValue = "true")
@EnableConfigurationProperties(PostgresProperties.class)
public class PostgresProvisioningAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(PostgresProvisioningAutoConfiguration.class);

    @Bean
    @ConditionalOnMissingBean
    public PostgresProvisioner postgresProvisioner(PostgresProperties props) {
        PostgresProvisioner provisioner = new PostgresProvisioner(props);
        provisioner.provision();
        return provisioner;
    }

    public static class PostgresProvisioner {

        private final PostgresProperties props;

        PostgresProvisioner(PostgresProperties props) {
            this.props = props;
        }

        void provision() {
            PostgresProperties.Provisioning provisioning = props.getProvisioning();
            Assert.hasText(provisioning.getBootstrapUser(),
                    "fluxbank.postgres.provisioning.bootstrap-user must be setted when provisioning its on");
            Assert.hasText(provisioning.getBootstrapPassword(),
                    "fluxbank.postgres.provisioning.bootstrap-password must be setted when provisioning its on");
            Assert.hasText(props.getDatabase(), "fluxbank.postgres.database must be setted.");
            Assert.hasText(props.getUsername(), "fluxbank.postgres.username must be setted.");

            String bootstrapUrl = "jdbc:postgresql://%s:%d/%s".formatted(
                    props.getHost(), props.getPort(), provisioning.getBootstrapDatabase());

            log.info("Verificando provisionamento do database '{}' (self-provisioning habilitado)",
                    props.getDatabase());

            try (Connection conn = DriverManager.getConnection(
                    bootstrapUrl, provisioning.getBootstrapUser(), provisioning.getBootstrapPassword())) {

                conn.setAutoCommit(true);

                createRoleIfNotExists(conn, props.getUsername(), props.getPassword());
                createDatabaseIfNotExists(conn, props.getDatabase(), props.getUsername());

                if (props.getSchema() != null && !props.getSchema().isBlank()) {
                    createSchemaIfNotExists(props, props.getSchema());
                }

                log.info("Database '{}' e role '{}' prontos.", props.getDatabase(), props.getUsername());
            } catch (SQLException e) {
                throw new IllegalStateException(
                        "Falha ao self-provisionar database/role Postgres para '%s'"
                                .formatted(props.getDatabase()), e);
            }
        }

        private void createRoleIfNotExists(Connection conn, String user, String password) throws SQLException {
            if (roleExists(conn, user)) {
                return;
            }

            assertSafeIdentifier(user);
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("CREATE ROLE \"%s\" WITH LOGIN PASSWORD '%s'"
                        .formatted(user, escapeLiteral(password)));
            }
            log.info("Role '{}' criada.", user);
        }

        private boolean roleExists(Connection conn, String user) throws SQLException {
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT 1 FROM pg_roles WHERE rolname = ?")) {
                ps.setString(1, user);
                return ps.executeQuery().next();
            }
        }

        private void createDatabaseIfNotExists(Connection conn, String database, String owner) throws SQLException {
            if (databaseExists(conn, database)) {
                return;
            }
            assertSafeIdentifier(database);
            assertSafeIdentifier(owner);
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("CREATE DATABASE \"%s\" OWNER \"%s\"".formatted(database, owner));
            }
            log.info("Database '{}' criado com owner '{}'.", database, owner);
        }

        private boolean databaseExists(Connection conn, String database) throws SQLException {
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT 1 FROM pg_database WHERE datname = ?")) {
                ps.setString(1, database);
                return ps.executeQuery().next();
            }
        }

        private void createSchemaIfNotExists(PostgresProperties props, String schema) throws SQLException {
            String appUrl = "jdbc:postgresql://%s:%d/%s".formatted(
                    props.getHost(), props.getPort(), props.getDatabase());

            try (Connection conn = DriverManager.getConnection(
                    appUrl, props.getProvisioning().getBootstrapUser(), props.getProvisioning().getBootstrapPassword())) {
                assertSafeIdentifier(schema);
                assertSafeIdentifier(props.getUsername());
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute("CREATE SCHEMA IF NOT EXISTS \"%s\" AUTHORIZATION \"%s\""
                            .formatted(schema, props.getUsername()));
                }
                log.info("Schema '{}' pronto no database '{}'.", schema, props.getDatabase());
            }
        }

        private void assertSafeIdentifier(String identifier) {
            Assert.isTrue(identifier.matches("^[a-zA-Z_][a-zA-Z0-9_]*$"),
                    "Identificador invalido/nao seguro: " + identifier);
        }

        private String escapeLiteral(String value) {
            return value.replace("'", "''");
        }
    }
}