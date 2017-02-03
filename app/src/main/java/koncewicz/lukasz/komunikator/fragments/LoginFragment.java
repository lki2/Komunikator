package koncewicz.lukasz.komunikator.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import java.security.GeneralSecurityException;

import info.guardianproject.cacheword.PassphraseSecrets;
import koncewicz.lukasz.komunikator.MainActivity;
import koncewicz.lukasz.komunikator.R;

public class LoginFragment extends Fragment implements View.OnClickListener{
    TextView etPassword;
    Button btLogin;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        etPassword = (TextView) view.findViewById(R.id.et_password);
        btLogin = (Button) view.findViewById(R.id.bt_login);
        btLogin.setOnClickListener(this);
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
        
        char[] passwd = passphrase.toCharArray();
        PassphraseSecrets secrets;
        try {
            MainActivity mActivity = (MainActivity) getActivity();
            secrets = PassphraseSecrets.fetchSecrets(mActivity, passwd);
            mActivity.getCacheWord().setCachedSecrets(secrets);
            getFragmentManager().popBackStack();
        } catch (GeneralSecurityException e) {
            etPassword.setError(getString(R.string.invalid_password));
        }
    }
}
