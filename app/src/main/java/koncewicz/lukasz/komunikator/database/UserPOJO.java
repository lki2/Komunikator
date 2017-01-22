package koncewicz.lukasz.komunikator.database;

import koncewicz.lukasz.komunikator.utils.PhoneNumberUtils;

public class UserPOJO {
    
    private String phone;
    private String username;
    
    public UserPOJO(String phone, String username){
        this.phone = PhoneNumberUtils.normalizeNumber(phone);
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public String getUsername() {
        return username;
    }
}
