package koncewicz.lukasz.komunikator.database;

public class MessagePOJO {

    public int getUserId(){
        return userId;
    };

    public String getContent() {
        return content;
    }

    public Status getStatus() {
        return status;
    }

    public enum Status {
        FAILURE(0),
        RECEIVED(1),
        SENT(2);

        private int _value;

        Status(int Value) {
            this._value = Value;
        }

        public int getValue() {
            return _value;
        }

        public static Status fromInt(int i) {
            for (Status b : Status.values()) {
                if (b.getValue() == i) { return b; }
            }
            return null;
        }
    }

    private int userId;
    private String content;
    private Status status;
    private String datetime;

    public MessagePOJO(int userId ,String content, Status status){
        this.userId = userId;
        this.content = content;
        this.status = status;
    }
}
