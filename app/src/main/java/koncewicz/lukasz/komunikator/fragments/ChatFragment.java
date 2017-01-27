package koncewicz.lukasz.komunikator.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.app.Fragment;
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

import java.security.PrivateKey;
import java.security.PublicKey;

import koncewicz.lukasz.komunikator.database.ChatCursorAdapter;
import koncewicz.lukasz.komunikator.utils.RsaUtils;
import koncewicz.lukasz.komunikator.utils.SmsReceiver;
import koncewicz.lukasz.komunikator.R;
import koncewicz.lukasz.komunikator.utils.SmsSender;
import koncewicz.lukasz.komunikator.database.DatabaseAdapter;
import koncewicz.lukasz.komunikator.database.MessagePOJO;

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
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            Bundle bundle = getArguments();
            userId = bundle.getLong(USER_ID, -1);
            phone = bundle.getString(PHONE);
            username = bundle.getString(USERNAME);

            TextView tvChatName = (TextView)getView().findViewById(R.id.chatName);
            tvChatName.setText(username + " (" + phone + ")");

            setListView();
            setListener();
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(broadcastBufferReceiver,
                new IntentFilter(SmsReceiver.BROADCAST_BUFFER_SEND_CODE));
    }

    @Override
    public void onPause() {
        super.onPause();
        chatCursor.close();
        getActivity().unregisterReceiver(broadcastBufferReceiver);
    }

    private void setListView() throws NullPointerException{
        dbAdapter = DatabaseAdapter.getInstance(getActivity());
        dbAdapter.open("123");


        chatCursor = dbAdapter.fetchChat(userId);
        dataAdapter = new ChatCursorAdapter(getActivity(), chatCursor);

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
                Toast.makeText(getActivity(),
                        countryCode, Toast.LENGTH_SHORT).show();
            }
        });

        scrollMyListViewToBottom();
    }

    void setListener() throws NullPointerException{
        Button btSend = (Button) getView().findViewById(R.id.btSend);
        btSend.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                EditText etMsgContent = (EditText)getView().findViewById(R.id.etMsgContent);
                String msgContent = etMsgContent.getText().toString();
                etMsgContent.setText(null);
                MessagePOJO msg = new MessagePOJO(userId, msgContent, MessagePOJO.Status.SENT);

                addMsgToDb(msg);
                encryptMsgAndSend(msg);
                //sendMsg(msg.getContent());
            }
        });
    }

    private void addMsgToDb(MessagePOJO msg){
        dbAdapter.addMsg(msg);
        refreshList();
    }

    private void refreshList(){
        chatCursor.close();
        if (!dbAdapter.isOpen()) dbAdapter.open("123");//todo
        chatCursor = dbAdapter.fetchChat(userId);
        dataAdapter.swapCursor(chatCursor);
        scrollMyListViewToBottom();
    }

    private void encryptMsgAndSend(MessagePOJO msg){
        try{
            MessagePOJO encryptedMsg = msg;
            byte[] encryptedContent;

            PublicKey pubK = RsaUtils.publicKeyFromBase64(dbAdapter.getContactKey(userId));
            PrivateKey privK = RsaUtils.privateKeyFromBase64(dbAdapter.getPrivateKey());

            encryptedContent = RsaUtils.RSAEncrypt(msg.getContent(), pubK);
            encryptedContent = RsaUtils.RSAEncrypt(encryptedContent, privK);

            sendMsg(encryptedContent);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void sendMsg(byte[] msg){
        new SmsSender(getActivity()).send(phone, msg);
    }

    private void sendMsg(String msg){
        new SmsSender(getActivity()).send(phone, msg);
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