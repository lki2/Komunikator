package koncewicz.lukasz.komunikator;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

import info.guardianproject.cacheword.CacheWordHandler;
import koncewicz.lukasz.komunikator.database.DatabaseAdapter;
import koncewicz.lukasz.komunikator.database.KeyPOJO;
import koncewicz.lukasz.komunikator.fragments.ContactsFragment;
import koncewicz.lukasz.komunikator.utils.RsaUtils;

import net.sqlcipher.database.SQLiteDatabase;

import android.util.Log;

import java.security.KeyPair;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    // our handler does all the work in talking to the CacheWordService
    private CacheWordHandler mCacheWord;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.w(TAG, "onCreate()");

        SQLiteDatabase.loadLibs(this);
        //initializeKeyPair();//todo

        setContentView(R.layout.activity_main);

        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        ContactsFragment contactsFragment = new ContactsFragment();
        ft.replace(R.id.fragment_container, contactsFragment);
        ft.commit();
    }

    private void initializeKeyPair(){
        KeyPair kp = RsaUtils.generateKeyPair();

        String publicKey = RsaUtils.keyToBase64(kp.getPublic());
        String privateKey = RsaUtils.keyToBase64(kp.getPrivate());

        DatabaseAdapter dbAdapter = DatabaseAdapter.getInstance(this);
        dbAdapter.open("123"); //todo
        dbAdapter.addOrUpdateOwnKeyPair(publicKey, privateKey);
        dbAdapter.close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DatabaseAdapter.getInstance(this).close();
        Log.w(TAG, "onDestroy()");
    }
}