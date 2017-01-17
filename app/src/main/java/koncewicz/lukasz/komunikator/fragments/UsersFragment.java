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

import koncewicz.lukasz.komunikator.add_user.AddUserFragment;
import koncewicz.lukasz.komunikator.add_user.ShowQrFragment;
import koncewicz.lukasz.komunikator.utils.SmsReceiver;
import koncewicz.lukasz.komunikator.R;
import koncewicz.lukasz.komunikator.database.DatabaseAdapter;

public class UsersFragment extends Fragment{

    private static final String TAG = UsersFragment.class.getName();

    DatabaseAdapter dbAdapter;
    Cursor usersCursor;

    UsersCursorAdapter dataAdapter;
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
        return inflater.inflate(R.layout.fragment_users, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.w(TAG, "onStart()");
        getView().findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, dbAdapter.getKey(11L));
                addUser();
            }
        });
        users();
        //dbAdapter.addUsers();
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
        usersCursor.close();
        getActivity().unregisterReceiver(broadcastBufferReceiver);
    }

    private void users() {
        dbAdapter = DatabaseAdapter.getInstance(getActivity());
        dbAdapter.open("123");
        //dbAdapter.addUsers();

        usersCursor = dbAdapter.fetchUsers();
        dataAdapter = new UsersCursorAdapter(getActivity(), usersCursor);

        listView = (ListView) getView().findViewById(R.id.usersList);
        listView.setAdapter(dataAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> listView, View view,
                                    int position, long id) {

                Cursor cursor = (Cursor) listView.getItemAtPosition(position);

                long userId = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseAdapter.COLUMN_ID));
                String phone = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseAdapter.COLUMN_PHONE));
                String username = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseAdapter.COLUMN_USERNAME));

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
    }

    private void addUser(){
        AddUserFragment firstFragment = new AddUserFragment();

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, firstFragment, "d");
        fragmentTransaction.addToBackStack("d");
        fragmentTransaction.commitAllowingStateLoss();
    }

    private void showQr(){
        ShowQrFragment firstFragment = new ShowQrFragment();

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, firstFragment, "dds");
        fragmentTransaction.addToBackStack("dds");
        fragmentTransaction.commitAllowingStateLoss();
    }

    private void refreshList(){
        usersCursor.close();
        usersCursor = dbAdapter.fetchUsers();
        dataAdapter.swapCursor(usersCursor);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        // TODO Add your menu entries here
        inflater.inflate(R.menu.menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
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
}
