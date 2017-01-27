package koncewicz.lukasz.komunikator.database;

import koncewicz.lukasz.komunikator.utils.PhoneNumberUtils;

public class ContactPOJO {
    
    private String phone;
    private String name;
    
    public ContactPOJO(String phone, String name){
        this.phone = PhoneNumberUtils.normalizeNumber(phone);
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public String getName() {
        return name;
    }
}
