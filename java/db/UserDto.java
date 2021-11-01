package blank.meet.server.db;

import java.io.Serializable;
import java.util.Map;

public class UserDto implements Serializable {
    private Map<String, Object> dbData;

    public UserDto(Map<String, Object> dbData) {
        this.dbData = dbData;
    }

    public int getId() {
        return (Integer) dbData.get("id");
    }

    public String getFirstName() {
        return (String) dbData.get("first_name");
    }

    public String getLastName() {
        return (String) dbData.get("last_name");
    }

    public int getBirthYear() {
        return (Integer) dbData.get("birth_year");
    }

    public int getBirthMonth() {
        return (Integer) dbData.get("birth_month");
    }

    public int getBirthDay() {
        return (Integer) dbData.get("birth_day");
    }

    public String getEmail() {
        return (String) dbData.get("email");
    }

    public String getToken() {
        return (String) dbData.get("token");
    }

    public String getStatus() {
        return (String) dbData.get("status");
    }
    public boolean isActive() {
        return "ACTIVE".equals(getStatus());
    }

    @Override
    public String toString() {
        return "UserDto: " + dbData;
    }
}
