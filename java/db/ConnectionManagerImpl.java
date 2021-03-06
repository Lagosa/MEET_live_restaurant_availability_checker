package blank.meet.server.db;

import blank.meet.server.config.Configuration;
import blank.meet.server.config.ConfigurationFactory;
import org.apache.commons.dbcp.BasicDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * This is the implementation for {@link ConnectionManager}.
 *
 */
class ConnectionManagerImpl implements ConnectionManager {

    private DataSource usedDataSource;

    ConnectionManagerImpl() {
        initDataSource();
    }

    @Override
    public Connection getConnection() throws SQLException {
        initDataSource();
        return usedDataSource.getConnection();
    }

    @Override
    public DataSource getDataSource() {
        return usedDataSource;
    }

    private synchronized void initDataSource() {
        if (usedDataSource == null) {
            Configuration config = ConfigurationFactory.getInstance();
            BasicDataSource ds = new BasicDataSource();
            ds.setUrl(config.getJdbcUrl());
            ds.setUsername(config.getJdbcUsername());
            ds.setPassword(config.getJdbcPassword());
            ds.setDriverClassName("org.postgresql.Driver");

            usedDataSource = ds;
        }
    }
}
