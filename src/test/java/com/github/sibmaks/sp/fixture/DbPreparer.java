package com.github.sibmaks.sp.fixture;

import com.opentable.db.postgres.embedded.DatabasePreparer;
import com.opentable.db.postgres.embedded.FlywayPreparer;
import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.beans.factory.annotation.Value;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * @author drobyshev-ma
 * Created at 30-12-2021
 */
public class DbPreparer implements DatabasePreparer {
    private static final String CLEAN_FORMAT = "DROP SCHEMA IF EXISTS %s CASCADE; CREATE SCHEMA %s;";
    private static final String CLEAN_PUBLIC = "DROP SCHEMA IF EXISTS public CASCADE; CREATE SCHEMA public;";
    private static final String SELECT_FORMAT = "ALTER USER %s SET search_path to %s;";

    @Value("${spring.jpa.properties.hibernate.default_schema}")
    private String schema;

    @Override
    public void prepare(DataSource ds) throws SQLException {
        var simpleDataSource = (PGSimpleDataSource) ds;
        try(var connection = ds.getConnection()) {
            connection.prepareStatement(CLEAN_PUBLIC)
                    .execute();
            connection.prepareStatement(String.format(CLEAN_FORMAT, schema, schema))
                    .execute();
            connection.prepareStatement(String.format(SELECT_FORMAT, simpleDataSource.getUser(), schema))
                    .execute();
        }
        FlywayPreparer.forClasspathLocation("db/migration")
                .prepare(ds);
    }
}