package koncewicz.lukasz.komunikator.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import info.guardianproject.cacheword.PassphraseSecrets;
import koncewicz.lukasz.komunikator.MainActivity;
import koncewicz.lukasz.komunikator.R;

public class RegisterFragment extends Fragment implements View.OnClickListener{
    TextView etPassword, etConfirm;
    Button btRegister;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        etPassword = (TextView) view.findViewById(R.id.et_password);
        etConfirm = (TextView) view.findViewById(R.id.et_confirm_password);
        btRegister = (Button) view.findViewById(R.id.bt_register);
        btRegister.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        InputMethodManager inputManager = (InputMethodManager)
                getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
        String passphrase = etPassword.getText().toString();

        if (passphrase.length() < 1){
            etPassword.setError(getString(R.string.empty_password));
            return;
        }
        if (!TextUtils.equals(etPassword.getText(), etConfirm.getText())){
            etConfirm.setError(getString(R.string.different_passwords));
            return;
        }

        ((MainActivity)getActivity()).getCacheWord().setCachedSecrets(
                PassphraseSecrets.initializeSecrets(getActivity(), passphrase.toCharArray()));
    }
}
