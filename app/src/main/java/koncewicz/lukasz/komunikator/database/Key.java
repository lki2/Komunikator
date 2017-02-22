package koncewicz.lukasz.komunikator.database;

public class Key {
    private String key;
    private Long contactId;

    public Key(String key, Long contactId){
        this.key = key;
        this.contactId = contactId;
    }

    String getKey() {
        return key;
    }

    Long getContactId(){
        return contactId;
    }
}
