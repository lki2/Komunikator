package koncewicz.lukasz.komunikator.database;

public class UserPOJO {
    
    private String phone;
    private String username;
    
    public UserPOJO(String phone, String username){
        this.phone = phone;
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public String getUsername() {
        return username;
    }
}
