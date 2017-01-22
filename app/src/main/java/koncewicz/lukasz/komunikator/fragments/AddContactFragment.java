package koncewicz.lukasz.komunikator.fragments;

import android.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import koncewicz.lukasz.komunikator.R;
import koncewicz.lukasz.komunikator.database.DatabaseAdapter;
import koncewicz.lukasz.komunikator.database.KeyPOJO;
import koncewicz.lukasz.komunikator.database.UserPOJO;

public class AddContactFragment extends Fragment implements View.OnClickListener
{
    //private static final String TAG = AddContactFragment.class.getName();

    TextView tvKey;
    EditText etPhone;
    EditText etUsername;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_contact, container, false);
        etPhone = ((EditText)view.findViewById(R.id.et_phone));
        etUsername = ((EditText)view.findViewById(R.id.et_username));
        tvKey = ((TextView)view.findViewById(R.id.tv_key));

        Bundle bundle = this.getArguments();
        String key = bundle.getString(ShowQrFragment.QR_KEY);
        String phone = bundle.getString(ShowQrFragment.QR_PHONE);
        String username = bundle.getString(ShowQrFragment.QR_USERNAME);

        tvKey.setText(key);
        etPhone.setText(phone);
        etUsername.setText(username);

        view.findViewById(R.id.bt_add_contact).setOnClickListener(this);

        return view;
    }

    private void addContact(){
        String key = tvKey.getText().toString();
        String phone = etPhone.getText().toString();
        String username = etUsername.getText().toString();

        DatabaseAdapter dbAdapter = DatabaseAdapter.getInstance(getActivity());
        Long userId = dbAdapter.addContact(new UserPOJO(phone, username));
        if (userId > 0){
            dbAdapter.addKey(new KeyPOJO(key, userId));
        }else{//todo update key
            Toast.makeText(getActivity(), "Kontakt już istnieje", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View v) {
        addContact();
        getActivity().getFragmentManager().popBackStack();
        getActivity().getFragmentManager().popBackStack();

        Toast.makeText(getActivity(), "Dodano kontakt", Toast.LENGTH_LONG).show();
    }
}
