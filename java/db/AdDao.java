package blank.meet.server.db;


import java.sql.SQLException;
import java.util.Map;

public interface AdDao {
    String searchAd(int randomId) throws SQLException;
}
