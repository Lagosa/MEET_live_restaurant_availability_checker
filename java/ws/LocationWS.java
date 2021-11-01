package blank.meet.server.ws;

import blank.meet.server.db.UserDto;
import blank.meet.server.service.AdManager;
import blank.meet.server.service.LocationManager;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.search.SearchException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.*;


@Path("/locations")
public class LocationWS {
	private static final Logger LOG = LoggerFactory.getLogger(LocationWS.class);
	
    @POST
    @Path("/search")
    @Produces(MediaType.APPLICATION_JSON)
    public String search(@FormParam("location_name") String location_name, @FormParam("sort") String sort,
                         @FormParam("lang") String language, @DefaultValue("0") @FormParam("offset") int offset) throws LocationException {
		LOG.info("Search for name = {}, sort = {}, lang = {}, offset = {}", location_name, sort, language, offset);
        ResourceBundle myBoundle = getBoundle(language);
        try {
            List<Map<String, Object>> locationData = LocationManager.getInstance().search(location_name, sort, offset);
            String result = new JSONArray(locationData).toString();
			LOG.info("Search Result for name = {}, sort = {}, lang = {}, offset = {}: found results = {}, return string size = {}", 
						location_name, sort, language, offset, locationData.size(), result.length());
			return result;
        } catch (Exception e) {
			LOG.error("Error in search for name = {}, sort = {}, lang = {}, offset = {}", location_name, sort, language, offset, e);
            throw new LocationException(myBoundle.getString("databaseError"), e);
        }
    }

    @POST
    @Path("/rate")
    public void rate(@FormParam("location_id") int location_id, @FormParam("rating") int rating,
                     @FormParam("lang") String language, @Context HttpServletRequest request) throws LocationException {
        ResourceBundle myBoundle = getBoundle(language);
        if (rating < 0 || rating > 5) throw new LocationException(myBoundle.getString("wrongRating"));
        try {
            UserDto user_id = findUserInRequest(request);
            LocationManager.getInstance().rating(location_id, rating, user_id.getId());
        } catch (Exception e) {
            throw new LocationException(myBoundle.getString("databaseError"), e);
        }
    }

    @POST
    @Path("/fulness_rating")
    public void fulness_rating(@FormParam("location_id") int location_id, @FormParam("rate") int rating,
                        @Context HttpServletRequest request, @FormParam("lang") String language)throws LocationException {
        try{
            UserDto userDto = findUserInRequest(request);
            boolean isPassed = LocationManager.getInstance().rate_fulness(location_id, rating, userDto.getId());
                if (!isPassed) {
                    ResourceBundle myBoundle = getBoundle(language);
                    throw new LocationException(myBoundle.getString("databaseError"));
                }
        }catch(Exception e){
            ResourceBundle myBoundle = getBoundle(language);
            throw new LocationException(myBoundle.getString("databaseError"),e);
        }
    }

    @Produces(MediaType.APPLICATION_JSON)
    @POST
    @Path("/location_details")
    public String location_details(@FormParam("location_id") int location_id, @FormParam("lang") String language) throws LocationException{
        try{
            Map<String, Object> details = LocationManager.getInstance().getLocationDetails(location_id);
            details.put("ad",AdManager.getInstance().locationPageAd(location_id));
            return new JSONObject(details).toString();
        }catch (Exception e){
            ResourceBundle myBoundle = getBoundle(language);
            throw new LocationException(myBoundle.getString("databaseError"),e);
        }
    }

    private UserDto findUserInRequest(HttpServletRequest request) throws AuthenticationException {
        return (UserDto) request.getAttribute("user");
    }

    private ResourceBundle getBoundle(String language) {
        if (language == null || language.isEmpty()) {
            language = "en";
        }
        Locale locale = new Locale(language);
        return ResourceBundle.getBundle("exception", locale);
    }
}



