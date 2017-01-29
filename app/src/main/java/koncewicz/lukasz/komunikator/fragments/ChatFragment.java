package koncewicz.lukasz.komunikator.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import net.sqlcipher.Cursor;

import java.security.PrivateKey;
import java.security.PublicKey;

import koncewicz.lukasz.komunikator.MainActivity;
import koncewicz.lukasz.komunikator.database.ChatCursorAdapter;
import koncewicz.lukasz.komunikator.utils.RsaUtils;
import koncewicz.lukasz.komunikator.utils.SmsReceiver;
import koncewicz.lukasz.komunikator.R;
import koncewicz.lukasz.komunikator.utils.SmsSender;
import koncewicz.lukasz.komunikator.database.DatabaseAdapter;
import koncewicz.lukasz.komunikator.database.MessagePOJO;

public class ChatFragment extends Fragment{

    public static final String TAG = ChatFragment.class.getName();

    private final static String CONTACT_ID = "contact_id";
    private final static String PHONE = "phone";
    private final static String NAME = "name";

    private DatabaseAdapter dbAdapter;

    private ChatCursorAdapter dataAdapter;
    private ListView messageList;
    private EditText etMsgContent;

    private long contactId; // ID kontaktu.
    private String phone; // Numer telefonu kontaktu.
    private String name;

    /**
     * Przygotowuję instancję {@code ChatFragment} z podanymi parametrami.
     * @param contactId ID kontaktu.
     * @param phone numer telefonu.
     * @param name nazwa kontaktu.
     * @return referencja do obiektu {@code ChatFragment}.
     */
    public static ChatFragment newInstance(long contactId, String phone, String name) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putLong(CONTACT_ID, contactId);
        args.putString(PHONE, phone);
        args.putString(NAME, name);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        Bundle bundle = getArguments();
        contactId = bundle.getLong(CONTACT_ID, -1);
        phone = bundle.getString(PHONE);
        name = bundle.getString(NAME);
        etMsgContent = (EditText)view.findViewById(R.id.et_message);
        messageList = (ListView)view.findViewById(R.id.chat_list);

        view.findViewById(R.id.btn_send).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String msgContent = etMsgContent.getText().toString();
                etMsgContent.setText(null);
                if (msgContent.length() > 0){
                    MessagePOJO msg = new MessagePOJO(contactId, msgContent, MessagePOJO.Status.SENT);
                    scrollListToBottom();
                    encryptMsgAndSend(msg);
                    dbAdapter.addMsg(msg);
                    refreshList();
                }else {
                    Toast.makeText(getActivity(),"Wpisz treść wiadomości",Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart()");
        showToolbar();
        showChat();
        getActivity().registerReceiver(broadcastReceiver,
                new IntentFilter(SmsReceiver.BROADCAST_SEND_CODE));
    }

    private void showToolbar(){
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle(name);
        actionBar.setSubtitle(phone);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop()");

        try {
            getActivity().unregisterReceiver(broadcastReceiver);
        }catch (Exception e){
            Log.d(TAG, "Receiver not unregistred");
        }
        Cursor oldCursor = (Cursor) dataAdapter.swapCursor(null);
        if (oldCursor != null){
            oldCursor.close();
        }
    }

    private void showChat() throws NullPointerException{
        if (dbAdapter == null || !dbAdapter.isOpen()){
            dbAdapter = ((MainActivity)getActivity()).getOpenDatabase();
        }
        Cursor chatCursor = dbAdapter.fetchChat(contactId);
        dataAdapter = new ChatCursorAdapter(getActivity(), chatCursor);

        messageList.setStackFromBottom(true);
        messageList.setAdapter(dataAdapter);
        messageList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> listView, View view,
                                    int position, long id) {
                // Get the cursor, positioned to the corresponding row in the result set
                Cursor cursor = (Cursor) listView.getItemAtPosition(position);

                // Get the state's capital from this row in the database.
                String countryCode =
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseAdapter.COLUMN_CONTENT));
                Toast.makeText(getActivity(), countryCode, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void encryptMsgAndSend(MessagePOJO msg){
        String contactKey = dbAdapter.getContactKey(contactId);
        if (contactKey != null) {
            try {
                PublicKey pubK = RsaUtils.publicKeyFromBase64(contactKey);
                PrivateKey privK = RsaUtils.privateKeyFromBase64(dbAdapter.getPrivateKey());

                byte[] encryptedContent;
                encryptedContent = RsaUtils.RSAEncrypt(msg.getContent().getBytes(), pubK);
                encryptedContent = RsaUtils.RSAEncrypt(encryptedContent, privK);

                sendEncryptedMsg(encryptedContent);
            } catch (Exception e) {
                e.printStackTrace(); //todo kontakt bez klucza
            }
        }
        else {
            Toast.makeText(getActivity(), "Kontakt nie jest powiązany", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendEncryptedMsg(byte[] msg){
        new SmsSender(getActivity()).send(phone, msg);
    }

    /**
     * Odbiera powiadomienia o dodaniu nowych wiadomości do bazy danych. Odświeża widok
     * listy i przewija go do ostatniej wiadomości.
     */
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent bufferIntent) {
            refreshList();
            scrollListToBottom();
        }
    };

    /**
     * Odświeża widok listy. Ponownie odczytuje wiadomości z bazy danych i podmienia
     * kursor w {@code dataAdapter}.
     */
    private void refreshList(){
        if (dbAdapter == null || !dbAdapter.isOpen()){
            dbAdapter = ((MainActivity)getActivity()).getOpenDatabase();
        }
        Cursor newCursor = dbAdapter.fetchChat(contactId);
        Cursor oldCursor = (Cursor) dataAdapter.swapCursor(newCursor);
        if (oldCursor != null){
            oldCursor.close();
        }
    }

    /**
     * Przewija listę {@code messageList} do ostatniego elementu.
     */
    private void scrollListToBottom() {
        messageList.post(new Runnable() {
            @Override
            public void run() {
                messageList.setSelection(dataAdapter.getCount() - 1);
            }
        });
    }
}