package koncewicz.lukasz.komunikator.database;

import koncewicz.lukasz.komunikator.utils.PhoneNumberUtils;

public class ContactPOJO {

    private long mId;
    private String mPhone;
    private String mName;

    public ContactPOJO(long id, String phone, String name){
        mId = id;
        mPhone = PhoneNumberUtils.normalizeNumber(phone);
        mName = name;
    }

    public ContactPOJO(String phone, String name){
        mPhone = PhoneNumberUtils.normalizeNumber(phone);
        mName = name;
    }

    public String getPhone() {
        return mPhone;
    }

    public String getName() {
        return mName;
    }

    public long getId() {
        return mId;
    }
}
