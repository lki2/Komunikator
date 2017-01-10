package koncewicz.lukasz.komunikator;

import android.os.Bundle;

import info.guardianproject.cacheword.CacheWordHandler;
import koncewicz.lukasz.komunikator.database.DatabaseAdapter;
import koncewicz.lukasz.komunikator.database.KeyPOJO;
import koncewicz.lukasz.komunikator.fragments.UsersFragment;
import koncewicz.lukasz.komunikator.utils.RsaHelper;
import koncewicz.lukasz.komunikator.utils.hee;

import net.sqlcipher.database.SQLiteDatabase;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import java.security.KeyPair;

public class MainActivity extends FragmentActivity {
    private static final String TAG = "MainActivity";
    // our handler does all the work in talking to the CacheWordService
    private CacheWordHandler mCacheWord;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.w(TAG, "onCreate()");

        openDatabase();
        initializeKeyPair();

        setContentView(R.layout.activity_main);
        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) return;

            UsersFragment firstFragment = new UsersFragment();

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            firstFragment.setArguments(getIntent().getExtras());

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, firstFragment).commit();
        }
        hee.tere(getBaseContext());
    }

    private void openDatabase(){
        SQLiteDatabase.loadLibs(this);
        DatabaseAdapter.getInstance(this).open("123");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DatabaseAdapter.getInstance(this).close();
        Log.w(TAG, "onDestroy()");
    }

    private void initializeKeyPair(){
        KeyPair kp = RsaHelper.generateKeyPair();

        String publicKey = RsaHelper.keyToBase64(kp.getPublic());
        String privateKey = RsaHelper.keyToBase64(kp.getPrivate());

        DatabaseAdapter dbAdapter = DatabaseAdapter.getInstance(this);
        dbAdapter.addKey(new KeyPOJO(publicKey, 11L));
        dbAdapter.addKey(new KeyPOJO(privateKey, 12L));
    }

    /*
   @Override
   protected void onResume() {
       super.onResume();
       Log.d(TAG, "onResume()");

       connectToCacheWord();
       if (mCacheWord.isLocked())
           Log.d(TAG, "locked");
       else
           Log.d(TAG, "unlocked");
   }

   private void connectToCacheWord()
   {
       if (mCacheWord == null) {
           mCacheWord = new CacheWordHandler(this, (ICacheWordSubscriber) this);
           mCacheWord.setNotification(buildNotification(this));
           mCacheWord.connectToService();
       }
   }

   @Override
   protected void onPause() {
       super.onPause();
       mCacheWord.disconnectFromService();
   }



    @Override
    public void onCacheWordUninitialized() {
        Log.d(TAG, "onCacheWordUninitialized()");
    }

    @Override
    public void onCacheWordLocked() {
        Log.d(TAG, "onCacheWordLocked()");
    }

    @Override
    public void onCacheWordOpened() {
        Log.d(TAG, "onCacheWordOpened()");
    }


    private void countries() {

        EditText myFilter = (EditText) findViewById(R.id.myFilter);
        myFilter.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                dataAdapter.getFilter().filter(s.toString());
            }
        });

        dataAdapter.setFilterQueryProvider(new FilterQueryProvider() {
            public android.database.Cursor runQuery(CharSequence constraint) {
                return db1Helper.fetchCountriesByName(constraint.toString());
            }
        });
    }

    private Notification buildNotification(Context c) {
        NotificationCompat.Builder b = new NotificationCompat.Builder(c);
        b.setSmallIcon(R.mipmap.ic_launcher);
        b.setContentTitle("title");
        b.setContentText("message");
        b.setTicker("notification");
        b.setDefaults(Notification.DEFAULT_VIBRATE);
        b.setWhen(System.currentTimeMillis());
        b.setOngoing(true);
        b.setContentIntent(CacheWordHandler.getPasswordLockPendingIntent(c));
        return b.build();
    }*/
}