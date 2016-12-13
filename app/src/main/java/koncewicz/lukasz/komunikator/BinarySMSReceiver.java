package koncewicz.lukasz.komunikator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

public class BinarySMSReceiver extends BroadcastReceiver
{
    private final static String TAG = "camera";

    @Override
    public void onReceive(Context context, Intent intent)
    {
        Log.d(TAG, "SMS received");

        Bundle bundle = intent.getExtras();
        SmsMessage[] msgs = null;

        if(null != bundle)
        {
            String info = "Binary SMS from ";
            Object[] pdus = (Object[]) bundle.get("pdus");
            msgs = new SmsMessage[pdus.length];
            byte[] data = null;

            for (int i=0; i<msgs.length; i++){
                msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                info += msgs[i].getOriginatingAddress();
                info += "\n*****BINARY MESSAGE*****\n";

                data = msgs[i].getUserData();

                for(int index=0; index<data.length; ++index)
                {
                    info += Character.toString((char)data[index]);
                }
            }

            Toast.makeText(context, info, Toast.LENGTH_SHORT).show();

        }
    }
}