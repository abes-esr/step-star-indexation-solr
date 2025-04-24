
package fr.abes.indexationsolr.database;

import com.zaxxer.hikari.HikariDataSource;
import oracle.jdbc.pool.OracleDataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.sql.SQLException;

@Configuration
public class DbConfig {

    @Bean
    @Primary
    public DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @ConfigurationProperties("spring.db.datasource")
    public DataSource dataSourceOracle() throws SQLException {
        return DataSourceBuilder.create().url(dataSourceProperties().getUrl())
                .username(dataSourceProperties().getUsername()).password(dataSourceProperties().getPassword())
                .type(OracleDataSource.class).build();
    }

    @Bean
    public DataSource dataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setJdbcUrl("jdbc:h2:mem:testdb");
        dataSource.setUsername("sa");
        dataSource.setPassword("");
        dataSource.setAutoCommit(true);
        return dataSource;
    }
}

