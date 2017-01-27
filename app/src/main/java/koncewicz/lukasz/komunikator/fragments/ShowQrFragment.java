package koncewicz.lukasz.komunikator.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import org.json.JSONException;
import org.json.JSONObject;

import koncewicz.lukasz.komunikator.R;
import koncewicz.lukasz.komunikator.database.DatabaseAdapter;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;

public class ShowQrFragment extends Fragment {
    private static final String TAG = ShowQrFragment.class.getName();

    private static final int WIDTH = 1024;
    private static final int HEIGHT = 1024;

    private static final String PREFS = "koncewicz.lukasz.komunikator";
    private static final String PREFS_USERNAME = "USERNAME";
    private static final String PREFS_PHONE = "PHONE";

    public static final String QR_USERNAME = "username";
    public static final String QR_PHONE = "phone";
    public static final String QR_KEY = "key";

    private ImageView imageView;
    private EditText etUsername;
    private EditText etPhone;

    DatabaseAdapter dbAdapter;
    SharedPreferences prefs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_show_qr, container, false);
        etUsername = (EditText) view.findViewById(R.id.et_username);
        etPhone = (EditText) view.findViewById(R.id.et_phone);
        etPhone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        imageView = (ImageView) view.findViewById(R.id.img_result_qr);

        etPhone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus && etPhone.getText().toString().trim().length() == 0){
                    etPhone.setText(R.string.country_code);
                }
            }
        });

        view.findViewById(R.id.bt_generate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showQr();
            }
        });
        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        dbAdapter = DatabaseAdapter.getInstance(getActivity());
        prefs = getActivity().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        retrieveFromPrefs();
    }

    private void saveToPrefs(String username, String phone){
        if (prefs != null) {
            prefs.edit().putString(PREFS_USERNAME, username).apply();
            prefs.edit().putString(PREFS_PHONE, phone).apply();
        }else{
            Log.e(TAG, "failed saveToPrefs: prefs == null");
        }
    }

    private void retrieveFromPrefs(){
        if (prefs == null) {
            Log.e(TAG, "failed retrieveFromPrefs: prefs == null");
            return;
        }
        String username = prefs.getString(PREFS_USERNAME, null);
        String phone = prefs.getString(PREFS_PHONE, null);

        if (phone == null || username == null) {
           return;
        }
        etPhone.setText(phone);
        etUsername.setText(username);
        String publicKey = dbAdapter.getPublicKey();
        setQr(username, phone, publicKey);
    }

    private void showQr(){
        String username = etUsername.getText().toString();
        String phone = etPhone.getText().toString();

        if(!isValidPhone(phone)){
            etPhone.setError(getString(R.string.invalid_phone));
            return;
        }

        String publicKey = dbAdapter.getPublicKey();
        saveToPrefs(username, phone);
        setQr(username, phone, publicKey);
    }

    private void setQr(String username, String phone, String key){
        try {
            JSONObject json = new JSONObject();
            json.put(QR_KEY, key);
            json.put(QR_PHONE, phone);
            json.put(QR_USERNAME, username);

            Bitmap bitmap = encodeAsBitmap(json.toString());
            imageView.setImageBitmap(bitmap);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean isValidPhone(String phone) {
        return android.util.Patterns.PHONE.matcher(phone).matches();
    }

    Bitmap encodeAsBitmap(String str) {
        BitMatrix result;
        try {
            result = new MultiFormatWriter().encode(str,
                    BarcodeFormat.QR_CODE, WIDTH, HEIGHT, null);
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace(); // Unsupported format
            return null;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
        int w = result.getWidth();
        int h = result.getHeight();
        int[] pixels = new int[w * h];
        for (int y = 0; y < h; y++) {
            int offset = y * w;
            for (int x = 0; x < w; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, WIDTH, 0, 0, w, h);
        return bitmap;
    }
}
