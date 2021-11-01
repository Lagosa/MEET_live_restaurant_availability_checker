package blank.meet.server.db;

import blank.meet.server.config.ConfigurationFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class AdDaoImpl implements AdDao {
    private ConnectionManager connectionManager;

    public AdDaoImpl(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @Override
    public String searchAd(int locationId) throws SQLException{
       String sql = "SELECT ad_url FROM blank_ad WHERE location_id = ?";
       String result = "";
       String siteUrl = ConfigurationFactory.getInstance().getSiteUrl();
        try(Connection connection = connectionManager.getConnection();
        PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1,locationId);
            try(ResultSet rs = statement.executeQuery()){
                if(rs.next()){
                    result = siteUrl + rs.getString("ad_url");
                }
            }
       }
        return result;
    }
}
