package koncewicz.lukasz.komunikator.database;

public class KeyPOJO {
    private String key;
    private Long userId;

    public KeyPOJO(String key, Long userId){
        this.key = key;
        this.userId = userId;
    }

    public String getKey() {
        return key;
    }

    public Long getUserId(){
        return userId;
    }
}
