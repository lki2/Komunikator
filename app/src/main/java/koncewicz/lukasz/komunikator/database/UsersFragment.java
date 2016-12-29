package koncewicz.lukasz.komunikator.database;

import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import net.sqlcipher.Cursor;

import koncewicz.lukasz.komunikator.R;

public class UsersFragment extends Fragment{

    DatabaseAdapter dbAdapter;
    Cursor usersCursor;

    UsersCursorAdapter dataAdapter;
    ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.users_fragment, container, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroy();
        usersCursor.close();
        dbAdapter.close();
    }

    @Override
    public void onStart() {
        super.onStart();

        users();
    }

    private void users() {
        dbAdapter = new DatabaseAdapter(getContext());
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

}
