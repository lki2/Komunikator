package koncewicz.lukasz.komunikator.fragments;


import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Handler;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.zxing.Result;

import org.json.JSONException;
import org.json.JSONObject;

import koncewicz.lukasz.komunikator.R;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class QrScannerFragment extends Fragment implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mScannerView = new ZXingScannerView(getActivity());
        return mScannerView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void handleResult(Result rawResult) {

        try {
            Bundle bundle = parseQr(rawResult.getText());
            showAddUserFragment(bundle);
        } catch (JSONException e) {
            Toast.makeText(getActivity(), "Nieprawidłowy kod", Toast.LENGTH_LONG).show();
            resumePreview();
        }
    }

    private Bundle parseQr(String qrContent) throws JSONException {
        Bundle bundle = new Bundle();
        JSONObject reader = new JSONObject(qrContent);
        String key = reader.getString(ShowQrFragment.QR_KEY);
        String phone = reader.getString(ShowQrFragment.QR_PHONE);
        String username = reader.getString(ShowQrFragment.QR_USERNAME);

        bundle.putString(ShowQrFragment.QR_KEY, key);
        bundle.putString(ShowQrFragment.QR_PHONE, phone);
        bundle.putString(ShowQrFragment.QR_USERNAME, username);

        return bundle;
    }

    private void showAddUserFragment(Bundle bundle){
        AddContactFragment firstFragment = new AddContactFragment();

        firstFragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, firstFragment, "dds");
        fragmentTransaction.addToBackStack("dds");
        fragmentTransaction.commitAllowingStateLoss();
    }

    private void resumePreview(){
        // Note:
        // * Wait 2 seconds to resume the preview.
        // * On older devices continuously stopping and resuming camera preview can result in freezing the app.
        // * I don't know why this is the case but I don't have the time to figure out.
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mScannerView.resumeCameraPreview(QrScannerFragment.this);
            }
        }, 2000);
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }
}