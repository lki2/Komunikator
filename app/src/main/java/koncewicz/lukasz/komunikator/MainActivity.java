package koncewicz.lukasz.komunikator;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import info.guardianproject.cacheword.CacheWordHandler;
import info.guardianproject.cacheword.ICacheWordSubscriber;
import info.guardianproject.cacheword.PassphraseSecrets;
import koncewicz.lukasz.komunikator.database.DatabaseAdapter;
import koncewicz.lukasz.komunikator.fragments.ContactsFragment;
import koncewicz.lukasz.komunikator.utils.RsaUtils;

import net.sqlcipher.database.SQLiteDatabase;

import java.security.GeneralSecurityException;
import java.security.KeyPair;


public class MainActivity extends AppCompatActivity implements ICacheWordSubscriber {
    private static final String TAG = "MainActivity";

    CacheWordHandler mCacheWord;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.w(TAG, "onCreate()");
        SQLiteDatabase.loadLibs(this);
        mCacheWord = new CacheWordHandler(this);
        mCacheWord.connectToService();
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

    @Override
    public void onCacheWordUninitialized() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Create a Passphrase");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT
                | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String passphrase = input.getText().toString();

                mCacheWord.setCachedSecrets(PassphraseSecrets.initializeSecrets(
                        MainActivity.this, passphrase.toCharArray()));
            }
        });
        builder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        builder.show();
    }

    @Override
    public void onCacheWordLocked() {
        if (mCacheWord.isLocked()) {
            // lets unlock!
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Enter your passphrase");
            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_TEXT
                    | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            builder.setView(input);

            builder.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String passphrase = input.getText().toString();

                            // verify the passphrase with CacheWord

                            char[] passwd = passphrase.toCharArray();
                            PassphraseSecrets secrets;
                            try {
                                secrets = PassphraseSecrets.fetchSecrets(
                                        MainActivity.this, passwd);
                                mCacheWord.setCachedSecrets(secrets);
                            } catch (GeneralSecurityException e) {
                                // Invalid password or the secret key has been
                                // tampered with
                                // TODO(abel) handle bad password in sample app
                                Log.e(TAG, "invalid password or secrets has been tampered with");
                                Log.e(TAG, e.getClass().getName() + " : " + e.getMessage());
                                e.printStackTrace();
                            }

                        }
                    });
            builder.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

            builder.show();
        }
    }

    @Override
    public void onCacheWordOpened() {
        prepareDatabase();
        setToolbar();
        showContactsFragment();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG, String.valueOf(mCacheWord.isLocked()));
        mCacheWord.disconnectFromService();
}

    @Override
    protected void onResume() {
        super.onResume();
        mCacheWord.connectToService();
        Log.e(TAG, String.valueOf(mCacheWord.isLocked()));
    }

}