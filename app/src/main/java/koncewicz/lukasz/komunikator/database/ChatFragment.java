package koncewicz.lukasz.komunikator.database;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import net.sqlcipher.Cursor;

import koncewicz.lukasz.komunikator.R;

public class ChatFragment extends Fragment{

    DatabaseAdapter dbAdapter;
    ChatCursorAdapter dataAdapter;
    ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.chat_fragment, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        chat();
    }

    private void chat() {

        dbAdapter = new DatabaseAdapter(getContext());
        dbAdapter.open("123");
        //dbAdapter.testChat();

        Cursor cursor = dbAdapter.fetchChat(1); //todo

        dataAdapter = new ChatCursorAdapter(getContext(), cursor);

        listView = (ListView) getView().findViewById(R.id.chatList);
        listView.setAdapter(dataAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> listView, View view,
                                    int position, long id) {
                // Get the cursor, positioned to the corresponding row in the result set
                Cursor cursor = (Cursor) listView.getItemAtPosition(position);

                // Get the state's capital from this row in the database.
                String countryCode =
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseAdapter.COLUMN_CONTENT));
                Toast.makeText(getContext(),
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        dbAdapter.close();
    }
}