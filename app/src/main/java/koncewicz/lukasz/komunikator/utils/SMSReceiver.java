package koncewicz.lukasz.komunikator.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import net.sqlcipher.database.SQLiteDatabase;

import java.security.PrivateKey;
import java.security.PublicKey;

import koncewicz.lukasz.komunikator.database.DatabaseAdapter;
import koncewicz.lukasz.komunikator.database.MessagePOJO;
import koncewicz.lukasz.komunikator.database.ContactPOJO;

public class SmsReceiver extends BroadcastReceiver
{
    private final static String TAG = SmsReceiver.class.getName();
    public static final String BROADCAST_SEND_CODE = "koncewicz.lukasz.komunikator.utils.SEND_CODE";

    Context context;

    //todo szyfrowanie ++
    //todo exceptions in RSA cipher
    //todo dodawanie kontaktu ++
    //todo edycja kontaktu
    //todo usuwanie wiadomosci
    //todo status wiadomosci
    //todo odswiezanie listy uzytkownikow ++
    //todo wprawadzanie hasla do bazy danych CacheWord

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
            byte[] data = null;

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
                addMsgToDb(phone, data);
                refreshView(phone);
            }   //todo multi msg

            Toast.makeText(context, info, Toast.LENGTH_SHORT).show(); //todo delete
        }
    }

    private void addMsgToDb(String phone, byte[] data){
        DatabaseAdapter dbAdapter = DatabaseAdapter.getInstance(context);

        if (!dbAdapter.isOpen()) {
            SQLiteDatabase.loadLibs(context);
            dbAdapter.open("123"); //todo
        }

        long userId = dbAdapter.getContact(phone);
        if(userId == -1){
            ContactPOJO contact = new ContactPOJO(phone, "nowy kontakt");
            MessagePOJO msg = new MessagePOJO(userId, data.toString(), MessagePOJO.Status.RECEIVED);
            dbAdapter.addMsg(msg);

        }else {
            try {
                PublicKey pubK = RsaUtils.publicKeyFromBase64(dbAdapter.getContactKey(userId));
                PrivateKey privK = RsaUtils.privateKeyFromBase64(dbAdapter.getPrivateKey());

                String content = new String(RsaUtils.RSADecrypt(RsaUtils.RSADecrypt(data, pubK), privK));
                MessagePOJO msg = new MessagePOJO(userId, content, MessagePOJO.Status.RECEIVED);
                dbAdapter.addMsg(msg);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        dbAdapter.close();
    }

    private void refreshView(String phone){
        Intent updatePosIntent = new Intent(BROADCAST_SEND_CODE);
        updatePosIntent.putExtra("PHONE", phone);
        context.sendBroadcast(updatePosIntent);
    }
}