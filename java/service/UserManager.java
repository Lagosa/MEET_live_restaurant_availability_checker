package blank.meet.server.service;

import blank.meet.server.config.ConfigurationFactory;
import blank.meet.server.db.DatabaseFactory;
import blank.meet.server.db.UserDto;
import blank.meet.server.email.EmailSender;
import org.apache.commons.codec.digest.DigestUtils;

import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

public class UserManager {
    private static UserManager ourInstance = new UserManager();

    public static UserManager getInstance() {
        return ourInstance;
    }

    private UserManager() {
    }

    /**
     * Authenticate the user with given token.
     *
     * @param token the token of the user
     * @return the authenticated user
     */
    public UserDto authenticate(String token) throws SQLException {
        Map<String, Object> data = DatabaseFactory.getUserDao().findByToken(token);
        return (data == null) ? null : new UserDto(data);
    }

    /**
     * Authenticate the user with given e-mail and giver password.
     *
     * @param email    the e-mail address of the user
     * @param password the user password
     * @return the authenticated user
     */
    public Map<String, Object> authenticate(String email, String password) throws SQLException {
        String encryptedPassword = encryptPassword(password);
        Map<String, Object> user = DatabaseFactory.getUserDao().findByEmailAndPassword(email, encryptedPassword);
        if (user != null) {
            UserDto userDto = new UserDto(user);
            if (!userDto.isActive()) {
                user.clear();
            }
        }
        return user;
    }

    public Map<String, Object> register(String first_name, String last_name, Integer birth_year, Integer birth_month,
                                        Integer birth_day, String username, String password, String email, String user_recommend, String language) throws SQLException{
        String encryptedPassword = encryptPassword(password);
        String token = generateToken();

        Map<String , Object> userData = DatabaseFactory.getUserDao().registration(first_name, last_name, birth_year, birth_month, birth_day, username, encryptedPassword,
                email, user_recommend, language,token);
        if(userData != null && !userData.isEmpty()&& userData.get("username") != "3z5s9d5sa3dw1a35ds1a3sdaw5wqe564we984u" && userData.get("email") != "3z5s9d5sa3dw1a35ds1a3sdaw5wqe564we984e") {
            String link = generateActivationLink(token);
            String emailSubject = "Confirm your registration on Meet!";
            String emailContent = "Dear " + last_name + " " + first_name + ", <br><br>" +
                    "You just registered on Meet. " +
                    "<br>For the registration to be complete, and to make sure that you are our new friend you need to click on the link below." +
                    "<i><br> If you didn't registered to Meet, than disregard this message!</i><br>" +
                    "<br>" +
                    "<b>For the confirmation access the folowing link:</b><br>" +
                    link +
                    "<br><br><br>" +
                    "<i>Sincerely,<br>" +
                    "Meet team</i>";
            EmailSender.getInstance().sendEmail(email, emailSubject, emailContent);
        }
        return userData;

    }

    public Boolean change_password(String new_password, String old_password, int id) throws SQLException{
        String new_encryptedPassword = encryptPassword(new_password);
        String old_encryptedPassword = encryptPassword(old_password);

        return DatabaseFactory.getUserDao().change_password(new_encryptedPassword, old_encryptedPassword, id);
    }

    public Map<String, Object> update(String first_name, String last_name, Integer birth_year, Integer birth_month,
                                      Integer birth_day, String  username, String  email, String token, int id,
                                      String password, String status)throws SQLException{

        return DatabaseFactory.getUserDao().update(first_name,last_name,birth_year,birth_month,birth_day,username,email, token, id, password, status);
    }

    public Boolean resetPassword(String email) throws SQLException{
        boolean result = false;
        String generatedPassword = Long.toString(System.currentTimeMillis(),36).toLowerCase();
        String encryptedGeneratedPassword = encryptPassword(generatedPassword);
        Map<String, Object> userData  = DatabaseFactory.getUserDao().findByEmail(email);
        int id = (int) userData.get("id");
        update("","",null,null,null,"","","", id, encryptedGeneratedPassword,"");

        String emailSubject = "Recover your Meet account!";
        String emailContent = "Dear "+userData.get("last_name")+" "+userData.get("first_name")+", <br><br>" +
                "Here is your brand new password: " +
                "<b>"+generatedPassword+"</b><br>" +
                "Don't forget, you can change it anytime! Just access the change password option in your profile.<br><br><br>" +
                "<i>Sincerely,<br>" +
                "Meet team</i>";
        EmailSender.getInstance().sendEmail(email,emailSubject,emailContent);

        result = true;

        return result;
    }
    public Boolean activateAccount(int id) throws SQLException{
        Map<String, Object> userData = DatabaseFactory.getUserDao().findById(id);
        String status = userData.get("status").toString();
        if(status.equals("NEW")){
            status = "ACTIVE";
            update("","",null,null,null,"","","",id,
                    "",status);
            return true;
        }else {
            return false;
        }

    }
    /**
     * Encrypt the password with SHA256
     *
     * @param password the clear text password
     * @return the encrypted password
     */
    static String encryptPassword(String password) {
        return DigestUtils.sha256Hex("blank[" + password + "]password");
    }

    static String generateToken(){return UUID.randomUUID().toString();}

    static String generateActivationLink(String token){
        return ConfigurationFactory.getInstance().getSiteUrl() + "ws/users/activateAccount?token="+token;
    }
}
