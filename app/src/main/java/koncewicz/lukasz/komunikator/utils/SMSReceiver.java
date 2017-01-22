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
import koncewicz.lukasz.komunikator.database.UserPOJO;

public class SmsReceiver extends BroadcastReceiver
{
    private final static String TAG = SmsReceiver.class.getName();
    public static final String BROADCAST_BUFFER_SEND_CODE = "koncewicz.lukasz.komunikator.utils.SEND_CODE";

    Context context;

    //todo szyfrowanie ++
    //todo exceptions in RSA cipher
    //todo dodawanie kontaktu
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
                for(int index=0; index<data.length; ++index)
                {
                    content += Character.toString((char)data[index]);
                }

                info += content;

                String phone = msgs[i].getOriginatingAddress();
                addMsgToDb(phone, content, data);
                refreshView(phone);
            }

            Toast.makeText(context, info, Toast.LENGTH_SHORT).show();
        }
    }

    private void addMsgToDb(String phone, String content, byte[] data){
        DatabaseAdapter dbAdapter = DatabaseAdapter.getInstance(context);

        if (!dbAdapter.isOpen()) {
            SQLiteDatabase.loadLibs(context);
            dbAdapter.open("123"); //todo
        }

        long userId = dbAdapter.findContact(phone);
        if(userId == -1){
            UserPOJO user = new UserPOJO(phone, "nowy kontakt");
            userId = dbAdapter.addContact(user);
        }

        try {
            PublicKey pubK = RsaUtils.publicKeyFromBase64(dbAdapter.getKey(userId));
            PrivateKey privK = RsaUtils.privateKeyFromBase64(dbAdapter.getKey(12L)); //todo

            content = RsaUtils.RSADecrypt(data, pubK);
            content = RsaUtils.RSADecrypt(content.getBytes(), privK);

            MessagePOJO msg = new MessagePOJO(userId, content, MessagePOJO.Status.RECEIVED);
            dbAdapter.addMsg(msg);

        }catch (Exception e){

        }

        dbAdapter.close();

    }

    private void refreshView(String phone){
        Intent updatePosIntent = new Intent(BROADCAST_BUFFER_SEND_CODE);
        updatePosIntent.putExtra("PHONE", phone);
        context.sendBroadcast(updatePosIntent);
    }
}