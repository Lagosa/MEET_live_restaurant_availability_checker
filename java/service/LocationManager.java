package blank.meet.server.service;

import blank.meet.server.db.DatabaseFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocationManager {

    private static LocationManager ourInstance = new LocationManager();

    public static LocationManager getInstance() {
        return ourInstance;
    }

    public List<Map<String, Object>> search (String location_name, String sort, int offset) throws SQLException {
        List<Map<String,Object>> result = DatabaseFactory.getLocationDao().findByName(location_name, sort, offset);
        Map<String, Object> locationData;
        List<Map<String,Object>> resultWithAds = new ArrayList<>();
        for(int i=0;i<result.size();i++){
            locationData = result.get(i);
            locationData.put("ad", AdManager.getInstance().searchPageAd());
            resultWithAds.add(locationData);
        }

        return resultWithAds;
    }

    public int rating (int location_id, int rating, int user_id) throws SQLException {
        return DatabaseFactory.getLocationDao().rating(location_id, rating, user_id);
    }

    public boolean rate_fulness(int location_id, int rating, int userId) throws SQLException {
        return DatabaseFactory.getLocationDao().setFulness(location_id, rating, userId);
    }

    public Map<String, Object> getLocationDetails(int location_id) throws SQLException{
        return DatabaseFactory.getLocationDao().getLocationDetails(location_id);
    }
}
