package koncewicz.lukasz.komunikator.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
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

import koncewicz.lukasz.komunikator.MainActivity;
import koncewicz.lukasz.komunikator.database.Contact;
import koncewicz.lukasz.komunikator.database.ContactsCursorAdapter;
import koncewicz.lukasz.komunikator.utils.SmsReceiver;
import koncewicz.lukasz.komunikator.R;
import koncewicz.lukasz.komunikator.database.DatabaseAdapter;

public class ContactsFragment extends Fragment implements View.OnClickListener{

    public static final String TAG = ContactsFragment.class.getName();

    DatabaseAdapter dbAdapter;

    ContactsCursorAdapter contactsAdapter;
    ListView listView;

    private BroadcastReceiver broadcastBufferReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            refreshList();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView()");
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);
        view.findViewById(R.id.add_contact_fab).setOnClickListener(this);
        listView = (ListView) view.findViewById(R.id.contacts_list);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.w(TAG, "onStart()");
        dbAdapter = ((MainActivity) getActivity()).getOpenDatabase();
        showToolbar();
        showContacts();
        getActivity().registerReceiver(broadcastBufferReceiver,
                new IntentFilter(SmsReceiver.BROADCAST_SEND_CODE));
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop()");
        getActivity().unregisterReceiver(broadcastBufferReceiver);
        Cursor oldCursor = (Cursor) contactsAdapter.swapCursor(null);
        if (oldCursor != null){
            oldCursor.close();
        }
    }

    private void showContacts() {
        Cursor contactsCursor = dbAdapter.fetchContacts();
        contactsAdapter = new ContactsCursorAdapter(getActivity(), contactsCursor);

        listView.setAdapter(contactsAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> listView, View view,
                                    int position, long id) {
                Cursor cursor = (Cursor) listView.getItemAtPosition(position);
                Contact contact = DatabaseAdapter.getContact(cursor);
                cursor.close();
                openChat(contact);
            }
        });
    }

    private void openChat(Contact contact){
        ChatFragment chatFragment = ChatFragment.newInstance(contact);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, chatFragment, "dd");
        fragmentTransaction.addToBackStack("dd");
        fragmentTransaction.commitAllowingStateLoss();
    }

    private void addContact(){
        QrScannerFragment firstFragment = new QrScannerFragment();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, firstFragment, "d");
        fragmentTransaction.addToBackStack("d");
        fragmentTransaction.commitAllowingStateLoss();
    }

    private void showProfile(){
        ProfileFragment firstFragment = new ProfileFragment();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, firstFragment, "dds");
        fragmentTransaction.addToBackStack("dds");
        fragmentTransaction.commitAllowingStateLoss();
    }

    private void refreshList(){
        ((MainActivity)getActivity()).saveReceivedMsgs();
        Cursor newCursor = dbAdapter.fetchContacts();
        Cursor oldCursor = (Cursor) contactsAdapter.swapCursor(newCursor);
        if (oldCursor != null){
            oldCursor.close();
        }
    }

    private void showToolbar(){
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle(R.string.fragment_contacts_title);
        actionBar.setSubtitle("");
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.showProfile:
                showProfile();
                return true;
            case R.id.addContact:
                addContact();
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.add_contact_fab){
            addContact();
        }
    }
}
