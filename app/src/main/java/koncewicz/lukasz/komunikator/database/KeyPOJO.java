package koncewicz.lukasz.komunikator.database;

public class KeyPOJO {
    private String key;
    private Long contactId;

    public KeyPOJO(String key, Long contactId){
        this.key = key;
        this.contactId = contactId;
    }

    public String getKey() {
        return key;
    }

    public Long getContactId(){
        return contactId;
    }
}
