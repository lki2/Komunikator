package koncewicz.lukasz.komunikator.fragments;

import android.app.Fragment;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import koncewicz.lukasz.komunikator.MainActivity;
import koncewicz.lukasz.komunikator.R;
import koncewicz.lukasz.komunikator.database.DatabaseAdapter;
import koncewicz.lukasz.komunikator.database.Key;
import koncewicz.lukasz.komunikator.database.Contact;

public class AddContactFragment extends Fragment {
    //private static final String TAG = AddContactFragment.class.getName();

    TextView tvKey;
    EditText etPhone;
    EditText etUsername;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        showToolbar();
        View view = inflater.inflate(R.layout.fragment_add_contact, container, false);
        etPhone = ((EditText)view.findViewById(R.id.et_phone));
        etUsername = ((EditText)view.findViewById(R.id.et_name));
        tvKey = ((TextView)view.findViewById(R.id.tv_key));

        Bundle bundle = this.getArguments();
        String key = bundle.getString(ProfileFragment.QR_KEY);
        String phone = bundle.getString(ProfileFragment.QR_PHONE);
        String username = bundle.getString(ProfileFragment.QR_NAME);

        tvKey.setText(key);
        etPhone.setText(phone);
        etUsername.setText(username);

        view.findViewById(R.id.bt_add_contact).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addContact();
                getActivity().getFragmentManager().popBackStack();
                getActivity().getFragmentManager().popBackStack();
            }
        });
        return view;
    }

    private void showToolbar(){
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle(R.string.fragment_add_contact_title);
        actionBar.setSubtitle("");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.show();
    }

    private void addContact(){
        String key = tvKey.getText().toString();
        String phone = etPhone.getText().toString();
        String username = etUsername.getText().toString();

        DatabaseAdapter dbAdapter = ((MainActivity)getActivity()).getOpenDatabase();
        Long contactId = dbAdapter.getContactId(phone);
        if(contactId == -1){
            contactId = dbAdapter.addContact(new Contact(phone, username));
            dbAdapter.addOrUpdateContactKey(new Key(key, contactId));
            Toast.makeText(getActivity(), R.string.toast_contact_added, Toast.LENGTH_SHORT).show();
        }else{
            dbAdapter.addOrUpdateContactKey(new Key(key, contactId));
            Toast.makeText(getActivity(), R.string.toast_contact_exists, Toast.LENGTH_LONG).show();
        }

    }
}
