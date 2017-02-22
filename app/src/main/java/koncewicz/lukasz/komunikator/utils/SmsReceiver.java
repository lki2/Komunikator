package koncewicz.lukasz.komunikator.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Base64;
import android.util.Log;
import java.util.Calendar;

import koncewicz.lukasz.komunikator.database.MessagePOJO;

public class SmsReceiver extends BroadcastReceiver
{
    private final static String TAG = SmsReceiver.class.getName();
    public static final String BROADCAST_SEND_CODE = "koncewicz.lukasz.komunikator.utils.SEND_CODE";

    Context context;

    //todo animacje przejscia miedzy fragmentami
    //todo orientacja pozioma
    //todo pusta lista kontaktow/wiadomosci
    //todo edycja kontaktu
    //todo usuwanie wiadomosci
    //todo status wiadomosci

    @Override
    public void onReceive(Context context, Intent intent)
    {
        this.context = context;
        Log.d(TAG, "odebrano sms");

        Bundle bundle = intent.getExtras();
        SmsMessage[] msgs = null;

        if(null != bundle)
        {
            String info = "Binary SMS from ";
            Object[] pdus = (Object[]) bundle.get("pdus");
            msgs = new SmsMessage[pdus.length];
            byte[] data;

            for (int i=0; i < msgs.length; i++){
                msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                info += msgs[i].getOriginatingAddress();
                info += "\n*****BINARY MESSAGE*****\n";

                data = msgs[i].getUserData();

                String content = "";
                for (byte aData : data) {
                    content += Character.toString((char) aData);
                }

                info += content;

                String phone = msgs[i].getOriginatingAddress();
                addMsgToBuffer(phone, data);
                refreshView(phone);

            }   //todo multi msg
        }
    }

    private void addMsgToBuffer(String phone, byte[] data){
        MessagesBuffer buffer = new MessagesBuffer(context);
        buffer.putMessage(new MessagePOJO(phone, Base64.encodeToString(data, Base64.DEFAULT), java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime())));
    }

    private void refreshView(String phone){
        Intent updatePosIntent = new Intent(BROADCAST_SEND_CODE);
        updatePosIntent.putExtra("PHONE", phone);
        context.sendBroadcast(updatePosIntent);
    }
}
