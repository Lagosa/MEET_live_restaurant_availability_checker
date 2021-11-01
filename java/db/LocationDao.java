package blank.meet.server.db;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface LocationDao {

    /**
     * Search for a given location based on name.
     *
     * @param location_name the token of the user
     * @return the location if found, null if not found
     * @throws java.sql.SQLException in case of DB error
    */
    List<Map<String, Object>> findByName(String location_name, String sort, int offset)throws SQLException;

    int rating (int location_id, int rating, int user_id) throws SQLException;

    boolean setFulness(int location_id, int rating, int userId) throws SQLException;

    Map<String, Object> getLocationDetails(int location_id) throws SQLException;
}
