package koncewicz.lukasz.komunikator.database;

import android.support.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

public class Message {
    private static final String SENDER = "SENDER";
    private static final String CONTENT = "CONTENT";
    private static final String DATETIME = "DATETIME";

    public long getContactId(){
        return contactId;
    }

    public String getContent() {
        return content;
    }

    public Status getStatus() {
        return status;
    }

    public String getSenderNumber() {
        return senderNumber;
    }

    public String getDateTime() {
        return dateTime;
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

        @Nullable
        public static Status fromInt(int i) {
            for (Status b : Status.values()) {
                if (b.getValue() == i) { return b; }
            }
            return null;
        }
    }

    private long contactId = -1;
    private String senderNumber;
    private String content;
    private Status status = Status.RECEIVED;
    private String dateTime;


    public Message(JSONObject json){
        try {
            senderNumber = json.getString(SENDER);
            content = json.getString(CONTENT);
            dateTime = json.getString(DATETIME);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Message(String sender, String content, String dateTime){
        this.senderNumber = sender;
        this.content = content;
        this.dateTime = dateTime;
    }

    public Message(long contactId, String content, Status status){
        this.contactId = contactId;
        this.content = content;
        this.status = status;
    }

    public JSONObject getJSON(){
        try {
            JSONObject json = new JSONObject();
            json.put(SENDER, senderNumber);
            json.put(CONTENT, content);
            json.put(DATETIME, dateTime);
            return json;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
