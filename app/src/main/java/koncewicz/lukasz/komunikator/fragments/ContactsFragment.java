package koncewicz.lukasz.komunikator.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import net.sqlcipher.Cursor;

import koncewicz.lukasz.komunikator.database.ContactsCursorAdapter;
import koncewicz.lukasz.komunikator.utils.SmsReceiver;
import koncewicz.lukasz.komunikator.R;
import koncewicz.lukasz.komunikator.database.DatabaseAdapter;

public class ContactsFragment extends Fragment implements View.OnClickListener{

    public static final String TAG = ContactsFragment.class.getName();

    DatabaseAdapter dbAdapter;
    Cursor contactsCursor;

    ContactsCursorAdapter contactsAdapter;
    ListView listView;

    private BroadcastReceiver broadcastBufferReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent bufferIntent) {
            refreshList();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView()");
        return inflater.inflate(R.layout.fragment_contacts, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.w(TAG, "onStart()");
        getView().findViewById(R.id.add_contact_fab).setOnClickListener(this);
        showContacts();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.w(TAG, "onResume()");
        getActivity().registerReceiver(broadcastBufferReceiver,
                new IntentFilter(SmsReceiver.BROADCAST_BUFFER_SEND_CODE));
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause()");
        getActivity().unregisterReceiver(broadcastBufferReceiver);
        contactsCursor.close();
    }

    private void showContacts() {
        dbAdapter = DatabaseAdapter.getInstance(getActivity().getBaseContext());
        dbAdapter.open("123");

        contactsCursor = dbAdapter.fetchContacts();
        contactsAdapter = new ContactsCursorAdapter(getActivity(), contactsCursor);

        listView = (ListView) getView().findViewById(R.id.usersList);
        listView.setAdapter(contactsAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> listView, View view,
                                    int position, long id) {
                Cursor cursor = (Cursor) listView.getItemAtPosition(position);
                long userId = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseAdapter.COLUMN_ID));
                String phone = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseAdapter.COLUMN_PHONE));
                String username = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseAdapter.COLUMN_NAME));
                openChat(userId, phone, username);
            }
        });
    }

    private void openChat(long userId, String phone, String username){
        ChatFragment chatFragment = ChatFragment.newInstance(userId, phone , username);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, chatFragment, "dd");
        fragmentTransaction.addToBackStack("dd");
        fragmentTransaction.commitAllowingStateLoss();
        contactsCursor.close();
    }

    private void addUser(){
        QrScannerFragment firstFragment = new QrScannerFragment();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, firstFragment, "d");
        fragmentTransaction.addToBackStack("d");
        fragmentTransaction.commitAllowingStateLoss();
        contactsCursor.close();
    }

    private void showQr(){
        ShowQrFragment firstFragment = new ShowQrFragment();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, firstFragment, "dds");
        fragmentTransaction.addToBackStack("dds");
        fragmentTransaction.commitAllowingStateLoss();
        contactsCursor.close();
    }

    private void refreshList(){
        contactsCursor.close();
        if (!dbAdapter.isOpen()) dbAdapter.open("123");//todo
        contactsCursor = dbAdapter.fetchContacts();
        contactsAdapter.swapCursor(contactsCursor);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.showQr:
                showQr();
                break;

            case R.id.refresh:
                break;
        }
        return true;

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.add_contact_fab){
            addUser();
        }
    }
}
