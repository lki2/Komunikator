package koncewicz.lukasz.komunikator.database;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import net.sqlcipher.Cursor;

import koncewicz.lukasz.komunikator.BinarySMSReceiver;
import koncewicz.lukasz.komunikator.R;
import koncewicz.lukasz.komunikator.SmsHelper;

public class ChatFragment extends Fragment{

    private final static String USER_ID = "userId";
    private final static String PHONE = "phone";
    private final static String USERNAME = "username";

    DatabaseAdapter dbAdapter;
    Cursor chatCursor;

    ChatCursorAdapter dataAdapter;
    ListView listView;

    long userId;
    String phone;
    String username;

    private BroadcastReceiver broadcastBufferReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent bufferIntent) {
            refreshList();
        }
    };

    public static ChatFragment newInstance(long userId, String phone, String username) {
        ChatFragment f = new ChatFragment();
        Bundle args = new Bundle();
        args.putLong(USER_ID, userId);
        args.putString(PHONE, phone);
        args.putString(USERNAME, username);
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.chat_fragment, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        userId = getArguments().getLong(USER_ID, -1);
        phone = getArguments().getString(PHONE);
        username = getArguments().getString(USERNAME);

        ((TextView)getView().findViewById(R.id.chatName)).setText(username + " (" + phone + ")");

        setListView();
        setListener();
    }

    @Override
    public void onResume() {
        super.onResume();
        getContext().registerReceiver(broadcastBufferReceiver,
                new IntentFilter(BinarySMSReceiver.BROADCAST_BUFFER_SEND_CODE));
    }

    @Override
    public void onPause() {
        super.onPause();
        getContext().unregisterReceiver(broadcastBufferReceiver);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        chatCursor.close();
        dbAdapter.close();
    }

    private void setListView() {
        dbAdapter = new DatabaseAdapter(getContext());
        dbAdapter.open("123");
        //dbAdapter.addChats();

        chatCursor = dbAdapter.fetchChat(userId);
        dataAdapter = new ChatCursorAdapter(getContext(), chatCursor);

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

    void setListener(){
        Button btSend = (Button) getView().findViewById(R.id.btSend);
        btSend.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                EditText etMsgContent = (EditText)getView().findViewById(R.id.etMsgContent);
                String msgContent = etMsgContent.getText().toString();
                etMsgContent.setText(null);
                MessagePOJO msg = new MessagePOJO(userId, msgContent, MessagePOJO.Status.SENT);

                addMsgToDb(msg);
                sendMsg(msg);
            }
        });
    }

    private void addMsgToDb(MessagePOJO msg){
        dbAdapter.addMsg(msg);
        refreshList();
    }
    private void refreshList(){
        chatCursor.close();
        chatCursor = dbAdapter.fetchChat(userId);
        dataAdapter.swapCursor(chatCursor);
        scrollMyListViewToBottom();
    }


    private void sendMsg(MessagePOJO msg){
        new SmsHelper(getContext()).sendBinary(phone, msg.getContent());
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

}