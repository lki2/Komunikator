package koncewicz.lukasz.komunikator.utils;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import koncewicz.lukasz.komunikator.database.Message;

public class MessagesBuffer {

    private static final String PREFS_MSGS = "MSGS";
    private static final String MSGS_COUNT = "COUNT";
    private static final String MSGS_TAG = "MSG";

private SharedPreferences prefs;

    public MessagesBuffer(Context ctx){
        prefs = ctx.getSharedPreferences(PREFS_MSGS, Context.MODE_PRIVATE);
    }

    public void putMessage(Message msg) {
        int count = prefs.getInt(MSGS_COUNT, 0);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(MSGS_TAG + count, msg.getJSON().toString());
        editor.putInt(MSGS_COUNT, ++count);
        editor.apply();
    }

    public Message[] popMessages(){
        int count = prefs.getInt(MSGS_COUNT, 0);
        if (count == 0){
            return new Message[0];
        }

        Message[] list = new Message[count];
        SharedPreferences.Editor editor = prefs.edit();
        for (int i = 0; i < count; i++){
            try {
                JSONObject json = new JSONObject(prefs.getString(MSGS_TAG + i, null));
                list[i] = new Message(json);
                editor.remove(MSGS_TAG + i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        editor.putInt(MSGS_COUNT, 0);
        editor.apply();
        return list;
    }
}
