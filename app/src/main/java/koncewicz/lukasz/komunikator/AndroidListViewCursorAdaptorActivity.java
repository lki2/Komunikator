package koncewicz.lukasz.komunikator;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import koncewicz.lukasz.komunikator.database.ChatsCursorAdapter;
import koncewicz.lukasz.komunikator.database.DatabaseAdapter;
import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

public class AndroidListViewCursorAdaptorActivity extends Activity {

    private DatabaseAdapter dbAdapter;
    private ChatsCursorAdapter dataAdapter;
    ListView listView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);

        SQLiteDatabase.loadLibs(this);

        chat();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbAdapter.close();
    }

    private void chat() {

        dbAdapter = new DatabaseAdapter(this);
        dbAdapter.open("123");
        dbAdapter.testChat();

        Cursor cursor = dbAdapter.fetchChat(2); //todo

        dataAdapter = new ChatsCursorAdapter(this, cursor);

        listView = (ListView) findViewById(R.id.listView1);
        listView.setAdapter(dataAdapter);

        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> listView, View view,
                                    int position, long id) {
                // Get the cursor, positioned to the corresponding row in the result set
                Cursor cursor = (Cursor) listView.getItemAtPosition(position);

                // Get the state's capital from this row in the database.
                String countryCode =
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseAdapter.COLUMN_CONTENT));
                Toast.makeText(getApplicationContext(),
                        countryCode, Toast.LENGTH_SHORT).show();
            }
        });

        scrollMyListViewToBottom();
    }

    private void scrollMyListViewToBottom() {
        listView.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                listView.setSelection(dataAdapter.getCount() - 1);
            }
        });
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