package blank.meet.server.db;

import blank.meet.server.config.ConfigurationFactory;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocationDaoImpl implements LocationDao {

    private ConnectionManager connectionManager;

    public LocationDaoImpl(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @Override
    public List<Map<String, Object>> findByName(String location_name, String sort, int offset) throws SQLException {
        List<Map<String, Object>> result = new ArrayList<>();
        String orderBy = "";
        if("nameasc".equals(sort)) {
            orderBy = "ORDER BY bl.location_name ASC";
        }else if("namedesc".equals(sort)){
            orderBy = "ORDER BY bl.location_name DESC";
        }
        String sql = "SELECT * FROM blank_location bl LEFT JOIN blank_location_rating_view blr ON bl.location_id = blr.location_id " +
                "WHERE bl.name_without_accents ILIKE ? AND status='active' "+ orderBy +" LIMIT 30 OFFSET ?";
        // for getting the fullness we're retrieve the last record for given location_id
        // // infact, it will be  the first record ordered desc based on id
        String sql_fulness = "SELECT * FROM blank_location_fulness WHERE location_id = ? AND last_update > current_timestamp - interval '1 day' ORDER BY id desc LIMIT 1";
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             PreparedStatement statement_fulness = connection.prepareStatement(sql_fulness)) {
            statement.setString(1, "%" + location_name + "%");
            statement.setInt(2, offset);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> oneResult = readLocationFromResultSet(rs, false);
                    statement_fulness.setInt(1, (Integer) oneResult.get("location_id"));
                    try (ResultSet rs_fulness = statement_fulness.executeQuery()) {
                        if (rs_fulness.next()) {
                            oneResult.put("progress", rs_fulness.getInt("rating"));
                        } else {
                            oneResult.put("progress", 0);
                        }
                    }
                    result.add(oneResult);
                }
            }
        }
        return result;
    }

    @Override
    public int rating(int location_id, int rating, int user_id) throws SQLException {
        String update = "UPDATE blank_location_rating SET rating = ?, last_update = now() "+
                "WHERE user_id = ? AND location_id = ? ";
        String insert= "INSERT INTO blank_location_rating (user_id, location_id, rating, last_update) VALUES (?, ?, ?, now())";
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement1 = connection.prepareStatement(update);
             PreparedStatement statement2 = connection.prepareStatement(insert)) {
            statement1.setInt(1, rating);
            statement1.setInt(2, user_id);
            statement1.setInt(3, location_id);
            int modifiedRowsNumber=statement1.executeUpdate();
            if (modifiedRowsNumber==0) {
                statement2.setInt(1, user_id);
                statement2.setInt(2, location_id);
                statement2.setInt(3, rating);
                statement2.executeUpdate();
            }
        }
        return 0;
    }

    @Override
    public boolean setFulness(int location_id, int rating, int userId) throws SQLException{
        boolean result = false;
        String update = "UPDATE blank_location_fulness SET user_id = ?, rating = ?, last_update = now()" +
                "WHERE location_id = ?";
        String insert = "INSERT INTO blank_location_fulness (location_id, user_id, rating, last_update) VALUES (?,?,?,now())";
        try(Connection connection = connectionManager.getConnection();
        PreparedStatement statementUpdate = connection.prepareStatement(update);
        PreparedStatement statementInsert = connection.prepareStatement(insert)){
            statementUpdate.setInt(1,userId);
            statementUpdate.setInt(2,rating);
            statementUpdate.setInt(3,location_id);
            int modifiedRowsNumber = statementUpdate.executeUpdate();
            if(modifiedRowsNumber == 0){
                statementInsert.setInt(1,location_id);
                statementInsert.setInt(2,userId);
                statementInsert.setInt(3,rating);
                statementInsert.executeUpdate();
            }
            result = true;
        }

        return result;
    }

    @Override
    public Map<String, Object> getLocationDetails(int location_id) throws SQLException{
        String sql = "SELECT * FROM blank_location bl LEFT JOIN blank_location_rating_view blr ON bl.location_id = blr.location_id " +
                "WHERE bl.location_id = ?";
        String sql_fulness = "SELECT *, extract(epoch from now() - last_update)::bigint as post_time FROM blank_location_fulness WHERE location_id = ? ORDER BY location_id desc LIMIT 1";
        Map<String,Object> result = new HashMap<>();
        try(Connection connection = connectionManager.getConnection();
        PreparedStatement statement = connection.prepareStatement(sql);
        PreparedStatement statement_fulness = connection.prepareStatement(sql_fulness)
            ){
            statement.setInt(1,location_id);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    result = readLocationFromResultSet(rs, true);
                    statement_fulness.setInt(1, (Integer) result.get("location_id"));
                    try (ResultSet rs_fulness = statement_fulness.executeQuery()) {
                        if (rs_fulness.next()) {
                            result.put("progress", rs_fulness.getInt("rating"));
                            result.put("post_time", rs_fulness.getLong("post_time"));
                        } else {
                            result.put("progress", 0);
                        }
                    }
                }
            }
            return result;
        }
    }

    private Map<String, Object> readLocationFromResultSet(ResultSet rs, boolean allData) throws SQLException {
        Map<String, Object> result = new HashMap<>();
        String url = ConfigurationFactory.getInstance().getSiteUrl();
        result.put("location_id", rs.getInt("location_id"));
        result.put("location_name", rs.getString("location_name"));
        result.put("logo_url", url + rs.getString("logo_url"));
        result.put("phone_number", rs.getString("phone_number"));
        result.put("rating", rs.getDouble("avg"));
        result.put("address", rs.getString("address"));
        if (allData) {
            result.put("email", rs.getString("email"));
            result.put("url", rs.getString("url"));
            result.put("latitude", rs.getString("latitude"));
            result.put("ordering_phone", rs.getString("ordering_phone"));
            result.put("ordering_email", rs.getString("ordering_email"));
            result.put("small_imagine_url", url + rs.getString("small_imagine_url"));
            result.put("longitude", rs.getString("longitude"));
        }
        return result;
    }
}
