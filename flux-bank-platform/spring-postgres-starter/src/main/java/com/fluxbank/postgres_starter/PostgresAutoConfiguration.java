package com.fluxbank.postgres_starter;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.util.Assert;

import javax.sql.DataSource;


@AutoConfiguration
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
@ConditionalOnClass(DataSource.class)
@EnableConfigurationProperties(PostgresProperties.class)
public class PostgresAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public DataSource dataSource(
            PostgresProperties props,
            ObjectProvider<PostgresProvisioningAutoConfiguration.PostgresProvisioner> provisionerProvider) {

        Assert.hasText(props.getDatabase(),
                "fluxbank.postgres.database should be defined by the service");
        Assert.hasText(props.getUsername(),
                "fluxbank.postgres.username should be defined");

        provisionerProvider.ifAvailable(provisioner -> {  });

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(buildJdbcUrl(props));
        config.setUsername(props.getUsername());
        config.setPassword(props.getPassword());
        config.setDriverClassName("org.postgresql.Driver");
        config.setMaximumPoolSize(props.getMaxPoolSize());
        config.setMinimumIdle(props.getMinIdle());
        config.setConnectionTimeout(props.getConnectionTimeoutMs());
        config.setPoolName(props.getDatabase() + "-pool");

        if (props.getSchema() != null && !props.getSchema().isBlank()) {
            config.setConnectionInitSql("SET search_path TO \"" + props.getSchema() + "\"");
        }

        return new HikariDataSource(config);
    }

    private String buildJdbcUrl(PostgresProperties props) {
        return "jdbc:postgresql://%s:%d/%s".formatted(
                props.getHost(), props.getPort(), props.getDatabase());
    }
}