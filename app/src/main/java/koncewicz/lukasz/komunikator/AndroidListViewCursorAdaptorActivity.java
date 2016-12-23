package koncewicz.lukasz.komunikator;

import android.os.Bundle;
import android.widget.ListView;

import koncewicz.lukasz.komunikator.database.ChatCursorAdapter;
import koncewicz.lukasz.komunikator.database.DatabaseAdapter;
import koncewicz.lukasz.komunikator.database.UsersFragment;

import net.sqlcipher.database.SQLiteDatabase;
import android.support.v4.app.FragmentActivity;

public class AndroidListViewCursorAdaptorActivity extends FragmentActivity {
    DatabaseAdapter dbAdapter;
    ChatCursorAdapter dataAdapter;
    ListView listView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SQLiteDatabase.loadLibs(this);

        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) return;

            UsersFragment firstFragment = new UsersFragment();

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            firstFragment.setArguments(getIntent().getExtras());

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, firstFragment).commit();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /*
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

    }*/
}