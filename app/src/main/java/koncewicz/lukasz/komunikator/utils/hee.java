package koncewicz.lukasz.komunikator.utils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import java.security.PrivateKey;
import java.security.PublicKey;

import koncewicz.lukasz.komunikator.database.DatabaseAdapter;

public class hee extends Activity {
    private static byte [] result;
    private static String ans;
    static RsaHelper rsaHelper;

    public static void tere(Context xs){
        try {
            String wiadomosc = "tajna wiadomość do zaszyfrowania!";

            DatabaseAdapter dbAdapter = DatabaseAdapter.getInstance(xs);
            PublicKey pubK = rsaHelper.publicKeyFromBase64(dbAdapter.getKey(11L));
            PrivateKey privK = rsaHelper.privateKeyFromBase64(dbAdapter.getKey(12L));

            result = rsaHelper.RSAEncrypt(wiadomosc, pubK);
            Log.w("dd", "Result:" + result);

            ans = rsaHelper.RSADecrypt(result, privK);
            System.out.println("Result is" + ans);
        }
        catch(Exception e) {
            e.printStackTrace();
        }

    }

}
