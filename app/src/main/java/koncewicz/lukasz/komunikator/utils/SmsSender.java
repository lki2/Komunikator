package koncewicz.lukasz.komunikator.utils;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

public class SmsSender {

    private Context context;
    private final static String TAG = SmsSender.class.getName();

    private final static String SMS_SENT = "SMS_SENT";
    private final static String SMS_DELIVERED = "SMS_DELIVERED";
    private final static short SMS_PORT = 80;

    public SmsSender(Context context){
        this.context = context;
    }

    public void send(String phone, String text){
        send(phone, text.getBytes());
    }

    public void send(String phone, byte[] text){
        PendingIntent sentPendingIntent = PendingIntent.getBroadcast(context, 0, new Intent(SMS_SENT), 0);
        PendingIntent deliveredPendingIntent = PendingIntent.getBroadcast(context, 0, new Intent(SMS_DELIVERED), 0);

        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendDataMessage(phone, null, SMS_PORT, text, sentPendingIntent, deliveredPendingIntent);
        Log.d(TAG,"wysylanie sms");
    }

    private void registerReceiver(){

        // For when the SMS has been sent
        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(context, "SMS sent successfully", Toast.LENGTH_SHORT).show();
                        Log.d(TAG,"SMS sent successfully");
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(context, "Generic failure cause", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(context, "Service is currently unavailable", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(context, "No pdu provided", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(context, "Radio was explicitly turned off", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(SMS_SENT));

        // For when the SMS has been delivered
        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(context, "SMS delivered", Toast.LENGTH_SHORT).show();
                        Log.d(TAG,"SMS delivered");
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(context, "SMS not delivered", Toast.LENGTH_SHORT).show();
                        Log.d(TAG,"SMS not delivered");
                        break;
                }
            }
        }, new IntentFilter(SMS_DELIVERED));
    }
}
