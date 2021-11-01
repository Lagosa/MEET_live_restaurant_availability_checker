package blank.meet.server.service;

import blank.meet.server.db.DatabaseFactory;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class AdManager {
    private static AdManager ourInstance = new AdManager();

    public static AdManager getInstance() {
        return ourInstance;
    }

    public Object searchPageAd() throws SQLException {
        String searchAd;
        Random random = new Random();
        int randomId = random.nextInt(45);
        Map<String, Map<String,Object>> adUrl = new HashMap<>();
        while(randomId == 0 ){
           randomId = random.nextInt(45);
        }
       searchAd = DatabaseFactory.getAdDao().searchAd(randomId);


        return searchAd;
    }

    public String locationPageAd(int location_id) throws SQLException{
        return (String) DatabaseFactory.getAdDao().searchAd(location_id);
    }

}
