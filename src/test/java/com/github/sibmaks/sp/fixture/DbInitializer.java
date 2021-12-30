package com.github.sibmaks.sp.fixture;

import com.opentable.db.postgres.embedded.PreparedDbProvider;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * @author drobyshev-ma
 * Created at 30-12-2021
 */
public class DbInitializer {
    private final DbPreparer dbPreparer;

    private PreparedDbProvider provider;

    public DbInitializer(DbPreparer dbPreparer) {
        this.dbPreparer = dbPreparer;
    }

    @PostConstruct
    public void init() {
        provider = PreparedDbProvider.forPreparer(dbPreparer);
    }

    public DataSource createDataSource() throws SQLException {
        return provider.createDataSource();
    }
}