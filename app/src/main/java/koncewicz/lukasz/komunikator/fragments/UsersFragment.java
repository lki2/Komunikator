package koncewicz.lukasz.komunikator.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import net.sqlcipher.Cursor;

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
        return inflater.inflate(R.layout.users_fragment, container, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroy();
        Log.d(TAG, "onDestroyView()");
        usersCursor.close();
        //dbAdapter.close();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.w(TAG, "onStart()");
        users();
        //dbAdapter.addUsers();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.w(TAG, "onResume()");
        getContext().registerReceiver(broadcastBufferReceiver,
                new IntentFilter(SmsReceiver.BROADCAST_BUFFER_SEND_CODE));
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause()");
        getContext().unregisterReceiver(broadcastBufferReceiver);
    }

    private void users() {
        dbAdapter = DatabaseAdapter.getInstance(getContext());
        dbAdapter.open("123");
        //dbAdapter.addUsers();

        usersCursor = dbAdapter.fetchUsers();
        dataAdapter = new UsersCursorAdapter(getContext(), usersCursor);

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
        ChatFragment firstFragment = ChatFragment.newInstance(userId, phone , username);

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, firstFragment, "dd");
        fragmentTransaction.addToBackStack("dd");
        fragmentTransaction.commitAllowingStateLoss();
    }

    private void refreshList(){
        usersCursor.close();
        usersCursor = dbAdapter.fetchUsers();
        dataAdapter.swapCursor(usersCursor);
    }
}
