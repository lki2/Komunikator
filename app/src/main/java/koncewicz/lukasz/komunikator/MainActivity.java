package koncewicz.lukasz.komunikator;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import info.guardianproject.cacheword.CacheWordHandler;
import info.guardianproject.cacheword.ICacheWordSubscriber;
import info.guardianproject.cacheword.PassphraseSecrets;
import koncewicz.lukasz.komunikator.database.DatabaseAdapter;
import koncewicz.lukasz.komunikator.fragments.ContactsFragment;
import koncewicz.lukasz.komunikator.fragments.LoginFragment;
import koncewicz.lukasz.komunikator.fragments.RegisterFragment;
import koncewicz.lukasz.komunikator.utils.RsaUtils;

import net.sqlcipher.database.SQLiteDatabase;

import java.security.KeyPair;

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
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        myToolbar.setVisibility(Toolbar.GONE);
        setSupportActionBar(myToolbar);

        SQLiteDatabase.loadLibs(this);
        mCacheWord = new CacheWordHandler(this);
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
        dbAdapter = DatabaseAdapter.getInstance(MainActivity.this, mCacheWord);
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

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, String.valueOf(mCacheWord.isLocked()));
        mCacheWord.connectToService();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG, String.valueOf(mCacheWord.isLocked()));
        mCacheWord.disconnectFromService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.w(TAG, "onDestroy()");
        if (dbAdapter != null){
            dbAdapter.close();
        }
    }

    private void showFragment(Fragment fragment){
        //fragment.setEnterTransition(new Slide(Gravity.RIGHT));
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