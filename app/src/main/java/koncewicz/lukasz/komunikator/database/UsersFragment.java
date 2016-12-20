package koncewicz.lukasz.komunikator.database;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import net.sqlcipher.Cursor;

import koncewicz.lukasz.komunikator.R;

public class UsersFragment extends Fragment{

    DatabaseAdapter dbAdapter;
    UsersCursorAdapter dataAdapter;
    ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.users_fragment, container, false);
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

        Cursor cursor = dbAdapter.fetchUsers(); //todo

        dataAdapter = new UsersCursorAdapter(getContext(), cursor);

        listView = (ListView) getView().findViewById(R.id.usersList);
        listView.setAdapter(dataAdapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dbAdapter.close();
    }
}
