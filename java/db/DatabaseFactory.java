package blank.meet.server.db;

/**
 * Class used to return the DB level implementations.
 */
public class DatabaseFactory {
    private static ConnectionManager connManager;
    private static UserDao userDao;
    private static LocationDao LocationDao;
    private static AdDao adDao;

    public synchronized static ConnectionManager getConnectionManager() {
        if (connManager == null) {
            connManager = new ConnectionManagerImpl();
        }
        return connManager;
    }

    public synchronized static UserDao getUserDao() {
        if (userDao == null) {
            userDao = new UserDaoImpl(getConnectionManager());
        }
        return userDao;
    }

    public synchronized static LocationDao getLocationDao() {
        if (LocationDao == null) {
            LocationDao = new LocationDaoImpl(getConnectionManager());
        }
        return LocationDao;
    }

    public synchronized static AdDao getAdDao() {
        if (adDao == null) {
            adDao = new AdDaoImpl(getConnectionManager());
        }
        return adDao;
    }
}
