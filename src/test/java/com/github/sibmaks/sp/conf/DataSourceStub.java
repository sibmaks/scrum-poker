package com.github.sibmaks.sp.conf;

/**
 * @author drobyshev-ma
 * Created at 30-12-2021
 */

import com.github.sibmaks.sp.fixture.DbInitializer;
import com.github.sibmaks.sp.fixture.DbPreparer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * @author maksim.drobyshev
 * Created on 12.10.2018
 */
@TestConfiguration
public class DataSourceStub {

    @Bean
    public DbPreparer dbPreparer() {
        return new DbPreparer();
    }

    @Bean
    public DbInitializer dbInitializer(DbPreparer dbPreparer) {
        return new DbInitializer(dbPreparer);
    }

    @Bean
    public DataSource dataSource(DbInitializer dbInitializer) throws SQLException {
        return dbInitializer.createDataSource();
    }
}