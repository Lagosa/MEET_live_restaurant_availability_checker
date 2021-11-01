package blank.meet.server.ws;

import blank.meet.server.db.UserDto;
import blank.meet.server.service.UserManager;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

@Path("/users")
public class UsersWS {
    @POST
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    public String authenticate(@FormParam("email") String email, @FormParam("password") String password,
                               @FormParam("lang") String language, @Context HttpServletRequest request) throws ClientException {
        try {
            Map<String, Object> userData = UserManager.getInstance().authenticate(email, password);

            if (userData == null) {
                ResourceBundle myBoundle = getBoundle(language);
                throw new AuthenticationException(myBoundle.getString("authenticationException"));
            } else if (userData.isEmpty()) {
                ResourceBundle myBoundle = getBoundle(language);
                throw new UserNotActiveException(myBoundle.getString("notActiveUser"));
            }
            return new JSONObject(userData).toString();
        } catch (ClientException e) {
            throw e;
        } catch (Exception e) {
            ResourceBundle myBundle = getBoundle(language);
            throw new ClientException(myBundle.getString("databaseError"));
        }
    }

    @POST
    @Path("/password_change")
    public String password_change(@Context HttpServletRequest request, @FormParam("new_password") String new_password,
                                  @FormParam("old_password") String old_password,
                                  @FormParam("lang") String language) throws ClientException {
        if(language == null || language.isEmpty()){
            language = "en";
        }
        try {
            UserDto user = findUserInRequest(request);
            Boolean rs = UserManager.getInstance().change_password(new_password, old_password, user.getId());
            if (!rs) {
                ResourceBundle myBoundle = getBoundle(language);
                throw new ClientException(myBoundle.getString("invalidOldPassword"));
            }
            return "{\"status\":\"sucess\"}";
        } catch (ClientException e){
            throw e;
        } catch (Exception e){
            ResourceBundle myBoundle = getBoundle(language);
            throw  new ClientException(myBoundle.getString("databaseError"));
        }



    }

    @POST
    @Path("/register")
    @Produces(MediaType.APPLICATION_JSON)
    public String register(@FormParam("first_name") String first_name, @FormParam("last_name") String last_name,
                           @FormParam("birth_year") Integer birth_year, @FormParam("birth_month") Integer birth_month,
                           @FormParam("birth_day") Integer birth_day, @FormParam("username") String username,
                           @FormParam("password") String password, @FormParam("email") String email,
                           @FormParam("recommended_by") String user_recommend, @FormParam("lang") String language) throws ClientException {
        try {
            Map<String, Object> registrationData = UserManager.getInstance().register(first_name, last_name, birth_year, birth_month,
                    birth_day, username, password, email, user_recommend, language);

            if (registrationData == null || registrationData.isEmpty()) {

                ResourceBundle myBoundle = getBoundle(language);
                throw  new ClientException(myBoundle.getString("databaseError"));
            }

            if(registrationData.get("username") == "3z5s9d5sa3dw1a35ds1a3sdaw5wqe564we984u"){
                ResourceBundle myBoundle = getBoundle(language);
                throw  new RegistrationException(myBoundle.getString("registrationUsernameOccupied"));
            }else if(registrationData.get("email") == "3z5s9d5sa3dw1a35ds1a3sdaw5wqe564we984e"){
                ResourceBundle myBoundle = getBoundle(language);
                throw  new RegistrationException(myBoundle.getString("registrationEmailOccupied"));
            }

            return new JSONObject(registrationData).toString();
        } catch (ClientException e) {
            throw e;
        } catch (Exception e) {
            ResourceBundle myBoundle = getBoundle(language);
            throw  new ClientException(myBoundle.getString("databaseError"));
        }
    }

    @POST
    @Path("/update")
    @Produces(MediaType.APPLICATION_JSON)
    public String update(@Context HttpServletRequest request,
                         @FormParam("first_name") String first_name, @FormParam("last_name") String last_name,
                         @FormParam("birth_year") Integer birth_year, @FormParam("birth_month") Integer birth_month,
                         @FormParam("birth_day") Integer birth_day, @FormParam("username") String username,
                         @FormParam("email") String email, @FormParam("lang") String language) throws ClientException {
        try {
            UserDto userDto = findUserInRequest(request);
            Map<String, Object> updatedData = UserManager.getInstance().update(first_name, last_name, birth_year, birth_month,
                    birth_day, username, email, "", userDto.getId(),"","");
            if (updatedData == null || updatedData.isEmpty()) {
                ResourceBundle myBoundle = getBoundle(language);
                throw new ClientException(myBoundle.getString("databaseError"));
            }
            if(updatedData.get("email") == "3z5s9d5sa3dw1a35ds1a3sdaw5wqe564we984e"){
                ResourceBundle myBoundle = getBoundle(language);
                throw new UpdateExeption(myBoundle.getString("updateEmailOccupied"));
            }
            if(updatedData.get("username") == "3z5s9d5sa3dw1a35ds1a3sdaw5wqe564we984u"){
                ResourceBundle myBoundle = getBoundle(language);
                throw new UpdateExeption(myBoundle.getString("updateUsernameOccupied"));
            }
            return new JSONObject(updatedData).toString();
        } catch (ClientException e) {
            throw e;
        } catch (Exception e) {
            ResourceBundle myBoundle = getBoundle(language);
            throw  new ClientException(myBoundle.getString("databaseError"));
        }
    }
    private UserDto findUserInRequest(HttpServletRequest request) throws AuthenticationException {
        return (UserDto) request.getAttribute("user");
    }

    private ResourceBundle getBoundle(String language){
        if(language == null || language.isEmpty()){
            language = "en";
        }
        Locale locale = new Locale(language);
        return ResourceBundle.getBundle("exception", locale);
    }

    @POST
    @Path("/reset_password")
    public String reset_password(@FormParam("email") String email, @FormParam("language") String language) throws ClientException{
        if(language == null || language.isEmpty()){
            language = "en";
        }
        try{
            String result = "DENIED";
            if(UserManager.getInstance().resetPassword(email)){
                result = "ACCEPTED";
            }
            return result;
        }catch (Exception e){
            ResourceBundle myBoundle = ResourceBundle.getBundle("exception");
            throw  new ClientException(myBoundle.getString("databaseError"));
        }
    }
    @GET
    @Path("/activateAccount")
    @Produces(MediaType.TEXT_HTML)
    public String activateAccount(@Context HttpServletRequest request) throws ClientException{
        try{
            UserDto userDto = findUserInRequest(request);
            Boolean result = UserManager.getInstance().activateAccount(userDto.getId());
            if(result) {
                return "<div style=\"color:green;\"><h1><b>Account activated </b></h1></div>";
            }else {
                return "<div style=\"color:red;\"><h1><b>Account activation failed</b></h1></div>";
            }
        }catch (Exception e){
            ResourceBundle myBoundle = ResourceBundle.getBundle("exception");
            throw  new ClientException(myBoundle.getString("databaseError"));
        }
    }
}
