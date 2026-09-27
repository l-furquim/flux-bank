package com.fluxbank.postgres_starter;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;


@AutoConfiguration
@AutoConfigureAfter(PostgresAutoConfiguration.class)
@ConditionalOnClass(Flyway.class)
@ConditionalOnProperty(prefix = "fluxbank.postgres.flyway", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(PostgresProperties.class)
public class PostgresFlywayAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public Flyway flyway(DataSource dataSource, PostgresProperties props) {
        FluentConfiguration config = Flyway.configure()
                .dataSource(dataSource)
                .locations(props.getFlyway().getLocations())
                .baselineOnMigrate(true);

        String schema = props.getSchema();
        if (schema != null && !schema.isBlank()) {
            config.schemas(schema)
                  .defaultSchema(schema)
                  .createSchemas(props.getFlyway().isCreateSchemas());
        }

        Flyway flyway = config.load();
        flyway.migrate();
        return flyway;
    }
}
