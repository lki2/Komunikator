package koncewicz.lukasz.komunikator;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import koncewicz.lukasz.komunikator.database.DatabaseAdapter;
import koncewicz.lukasz.komunikator.fragments.ContactsFragment;
import koncewicz.lukasz.komunikator.utils.RsaUtils;

import net.sqlcipher.database.SQLiteDatabase;

import java.security.KeyPair;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.w(TAG, "onCreate()");

        prepareDatabase();
        setToolbar();
        showContactsFragment();
    }

    private void setToolbar(){
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getFragmentManager().popBackStack();
                return true;
        }
        return false;
    }

    private void prepareDatabase(){
        SQLiteDatabase.loadLibs(this);
        DatabaseAdapter dbAdapter = getOpenDatabase();
        if (dbAdapter.getPublicKey() == null){
            Log.i(TAG,"Inicjalizacja pary kluczy");
            try {
                KeyPair keyPair = RsaUtils.generateKeyPair();
                String publicKey = RsaUtils.keyToBase64(keyPair.getPublic());
                String privateKey = RsaUtils.keyToBase64(keyPair.getPrivate());
                dbAdapter.addOrUpdateOwnKeyPair(publicKey, privateKey);
            }catch (Exception e){
                Log.e(TAG,"Niepowodzenie inicjalizacji pary kluczy");
            }
        }
    }

    private void showContactsFragment(){
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ContactsFragment contactsFragment = new ContactsFragment();
        ft.replace(R.id.fragment_container, contactsFragment);
        ft.commit();
    }

    public DatabaseAdapter getOpenDatabase(){
        DatabaseAdapter dbAdapter = DatabaseAdapter.getInstance(MainActivity.this);
        if (!dbAdapter.isOpen()) {
            dbAdapter.open("123"); // todo pass
        }
        return dbAdapter;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DatabaseAdapter.getInstance(MainActivity.this).close();
        Log.w(TAG, "onDestroy()");
    }
}