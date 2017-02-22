package koncewicz.lukasz.komunikator;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import net.sqlcipher.database.SQLiteDatabase;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

import info.guardianproject.cacheword.CacheWordHandler;
import info.guardianproject.cacheword.ICacheWordSubscriber;
import koncewicz.lukasz.komunikator.database.Contact;
import koncewicz.lukasz.komunikator.database.DatabaseAdapter;
import koncewicz.lukasz.komunikator.database.Message;
import koncewicz.lukasz.komunikator.fragments.ContactsFragment;
import koncewicz.lukasz.komunikator.fragments.LoginFragment;
import koncewicz.lukasz.komunikator.fragments.RegisterFragment;
import koncewicz.lukasz.komunikator.utils.MessagesBuffer;
import koncewicz.lukasz.komunikator.utils.RsaUtils;

public class MainActivity extends AppCompatActivity implements ICacheWordSubscriber {
    private static final String TAG = "MainActivity";
    private DatabaseAdapter dbAdapter;
    private CacheWordHandler mCacheWord;

    public CacheWordHandler getCacheWord() {
        return mCacheWord;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.w(TAG, "onCreate()");
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        myToolbar.setVisibility(Toolbar.GONE);
        setSupportActionBar(myToolbar);

        SQLiteDatabase.loadLibs(this);
        mCacheWord = new CacheWordHandler(this, 5000);
        mCacheWord.connectToService();
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

    public DatabaseAdapter getOpenDatabase(){
        if (dbAdapter == null) {
            dbAdapter = new DatabaseAdapter(MainActivity.this, mCacheWord);
        }
        if (!dbAdapter.isOpen()) {
            dbAdapter.open();
        }
        return dbAdapter;
    }

    @Override
    public void onCacheWordUninitialized() {
        showFragment(new RegisterFragment());
    }

    @Override
    public void onCacheWordLocked() {
        if (mCacheWord.isLocked()) {
            showFragment(new LoginFragment());
        }
    }

    @Override
    public void onCacheWordOpened() {
        if (!mCacheWord.isLocked()){
            prepareDatabase();
            showFragment(new ContactsFragment());
        }
    }

    public void saveReceivedMsgs(){
        MessagesBuffer buffer = new MessagesBuffer(this);
        Message[] msgs = buffer.popMessages();
        for (Message encryptedMsg: msgs) {
            long userId = dbAdapter.getContactId(encryptedMsg.getSenderNumber());
            if(userId == -1){
                dbAdapter.addContact(new Contact(encryptedMsg.getSenderNumber(), getString(R.string.new_contact_name)));
                dbAdapter.addMsg(encryptedMsg);
            }else {
                try {
                    PublicKey contactPubK = RsaUtils.publicKeyFromBase64(dbAdapter.getContactKey(userId));
                    PrivateKey privK = RsaUtils.privateKeyFromBase64(dbAdapter.getPrivateKey());

                    byte[] bytes = Base64.decode(encryptedMsg.getContent(), Base64.DEFAULT);
                    bytes = RsaUtils.RSADecrypt(bytes, contactPubK);
                    bytes = RsaUtils.RSADecrypt(bytes, privK);
                    String decryptedContent = new String(bytes);

                    Message decryptedMsg = new Message(userId, decryptedContent, Message.Status.RECEIVED);
                    dbAdapter.addMsg(decryptedMsg);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.w(TAG, "onDestroy()");
        mCacheWord.disconnectFromService();
        if (dbAdapter != null){
            dbAdapter.close();
        }
    }

    private void showFragment(Fragment fragment){
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_container, fragment);
        ft.commit();
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
}