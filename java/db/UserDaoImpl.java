package blank.meet.server.db;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

class UserDaoImpl implements UserDao {
    private ConnectionManager connectionManager;

    public UserDaoImpl(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @Override
    public Map<String, Object> findById(int id) throws SQLException {
        Map<String, Object> result = null;
        String sql = "SELECT * FROM blank_user WHERE id = ?";
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    result = readUserFromResultSet(rs);
                }
            }
        }
        return result;
    }
    @Override
    public Map<String, Object> findByToken(String token) throws SQLException {
        Map<String, Object> result = null;
        String sql = "SELECT * FROM blank_user WHERE token = ?";
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, token);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    result = readUserFromResultSet(rs);
                }
            }
        }
        return result;
    }

    @Override
    public Map<String, Object> findByEmailAndPassword(String email, String password) throws SQLException {
        Map<String, Object> result = null;
        String sql = "SELECT * FROM blank_user WHERE email = ? AND password = ?";
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            statement.setString(2, password);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    result = readUserFromResultSet(rs);
                }
            }
        }
        return result;
    }
    @Override
    public Map<String, Object> registration(String first_name, String last_name, Integer birth_year, Integer birth_month,
                                            Integer birth_day, String username, String password, String email, String user_recommend,
                                            String language, String token) throws SQLException {
        Map<String, Object> result = new HashMap<>();
        if(isRegistered(username, "username")){
            result.put("username", "3z5s9d5sa3dw1a35ds1a3sdaw5wqe564we984u");
            return result;
        }else if(isRegistered(email, "email")) {
            result.put("email", "3z5s9d5sa3dw1a35ds1a3sdaw5wqe564we984e");
            return result;
        }else {
            String sql = "INSERT INTO blank_user (username, password, first_name, last_name, email, dob, status, token, user_recommend)" +
                    " VALUES (?, ?, ?, ?, ?, ?, 'NEW', ?, ?)";
            try (Connection connection = connectionManager.getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, username);
                statement.setString(2, password);
                statement.setString(3, first_name);
                statement.setString(4, last_name);
                statement.setString(5, email);
                statement.setDate(6, new Date(birth_year - 1900, birth_month - 1, birth_day));
                statement.setString(7, token);
                statement.setString(8, user_recommend);

                statement.executeUpdate();

                try (ResultSet rs = statement.getGeneratedKeys()) {
                    if (rs.next()) {
                        int id = rs.getInt("id");
                        result = findById(id);
                    }
                }
            }
            return result;
        }
    }
    @Override
    public Boolean change_password(String new_password, String old_password, int id)throws SQLException{
        Boolean result = false;
        String sql = "UPDATE blank_user SET password = ? WHERE id = ? AND password = ?";
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, new_password);
            statement.setInt(2, id);
            statement.setString(3, old_password);

            Integer rs = statement.executeUpdate();
            if(rs == 1){
                result = true;
            }

        }
        return result;
    }
    @Override
    public Map<String,  Object> update(String first_name, String last_name, Integer birth_year, Integer birth_month,
                                       Integer birth_day, String username, String email, String token, int id,
                                       String password, String status) throws SQLException{
        Map<String, Object> result =  new HashMap<>();
        if(isRegistered(username, "username")){
            result.put("username", "3z5s9d5sa3dw1a35ds1a3sdaw5wqe564we984u");
            return result;
        }else if(isRegistered(email, "email")) {
            result.put("email", "3z5s9d5sa3dw1a35ds1a3sdaw5wqe564we984e");
            return result;
        }else {
            String sql = "UPDATE blank_user SET";
            if (!isNullOrBlank(first_name)) {
                sql += " first_name = ?,";
            }
            if (!isNullOrBlank(last_name)) {
                sql += " last_name = ?,";
            }
            if (birth_day != null && birth_month != null && birth_year != null) {
                sql += " dob = ?,";
            }
            if (!isNullOrBlank(username)) {
                sql += " username = ?,";
            }
            if (!isNullOrBlank(email)) {
                sql += " email = ?,";
            }
            if (!isNullOrBlank(token)) {
                sql += " token = ?,";
            }
            if (!isNullOrBlank(password)) {
                sql += " password = ?,";
            }
            if (!isNullOrBlank(status)) {
                sql += " status = ?,";
            }
            //Ha van a kifejezes vegen ,(vesszo) akkor kitorli
            if (sql.endsWith(",")) {
                sql = sql.substring(0, sql.length() - 1);
            }
            sql += " WHERE id = ?";
            try (Connection connection = connectionManager.getConnection()) {
                PreparedStatement statement = connection.prepareStatement(sql);
                int db = 0;
                if (!isNullOrBlank(first_name)) {
                    db++;
                    statement.setString(db, first_name);
                }
                if (!isNullOrBlank(last_name)) {
                    db++;
                    statement.setString(db, last_name);
                }
                if (birth_day != null && birth_month != null && birth_year != null) {
                    db++;
                    statement.setDate(db, new Date(birth_year - 1900, birth_month - 1, birth_day));
                }
                if (!isNullOrBlank(username)) {
                    db++;
                    statement.setString(db, username);
                }
                if (!isNullOrBlank(email)) {
                    db++;
                    statement.setString(db, email);
                }
                if (!isNullOrBlank(token)) {
                    db++;
                    statement.setString(db, token);
                }
                if (!isNullOrBlank(password)) {
                    db++;
                    statement.setString(db, password);
                }
                if (!isNullOrBlank(status)) {
                    db++;
                    statement.setString(db, status);
                }
                db++;
                statement.setInt(db, id);

                Integer rs = statement.executeUpdate();
                if (rs == 1) {
                    result = findById(id);
                }
            }
            return result;
        }
    }

    @Override
    public Map<String, Object> findByEmail(String email) throws SQLException{
        String sql = "SELECT * FROM blank_user WHERE email = ?";
        Map<String, Object> result = null;
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    result = readUserFromResultSet(rs);
                }
            }
        }
        return result;
    }

    private static boolean isNullOrBlank(String text){
        return text == null || text.trim().length() == 0;
    }

    private boolean isRegistered(String userData, String userDataType) throws SQLException{
        String sql = "SELECT * FROM blank_user WHERE ";
        sql += ""+userDataType+" = ?";
        try (Connection connection = connectionManager.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, userData);
            try(ResultSet rs = statement.executeQuery()){
                if(rs.next()){
                    return true;
                }else {
                    return false;
                }
            }
        }
    }

    private Map<String, Object> readUserFromResultSet(ResultSet rs) throws SQLException {
            Map<String, Object> result = new HashMap<>();
            result.put("id", rs.getInt("id"));
            result.put("first_name", rs.getString("first_name"));
            result.put("last_name", rs.getString("last_name"));
            result.put("username", rs.getString("username"));
            result.put("email", rs.getString("email"));
            result.put("token", rs.getString("token"));
            result.put("status", rs.getString("status"));
            result.put("user_recommend", rs.getString("user_recommend"));
            Date dob = rs.getDate("dob");
            result.put("dob", dob);
            result.put("birth_year", dob.getYear() + 1900);
            result.put("birth_month", dob.getMonth() + 1);
            result.put("birth_day", dob.getDate());
            return result;
    }
}
