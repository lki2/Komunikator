package koncewicz.lukasz.komunikator.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberFormattingTextWatcher;
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
import koncewicz.lukasz.komunikator.utils.PhoneNumberUtils;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;

public class ProfileFragment extends Fragment {
    //private static final String TAG = ProfileFragment.class.getName();

    private static final int WIDTH = 1024;
    private static final int HEIGHT = 1024;

    private static final String PREFS = "koncewicz.lukasz.komunikator";
    private static final String PREFS_NAME = "NAME";
    private static final String PREFS_PHONE = "PHONE";
    private static final String PREFS_KEY = "PHONE";

    public static final String QR_NAME = "name";
    public static final String QR_PHONE = "phone";
    public static final String QR_KEY = "key";

    private ImageView imageView;
    private EditText etUsername;
    private EditText etPhone;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        etUsername = (EditText) view.findViewById(R.id.et_name);
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
                doJob();
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        showToolbar();
        getFromPrefs();
    }

    private void putToPrefs(String name, String phone, String key){
        SharedPreferences prefs = getActivity().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        prefs.edit().putString(PREFS_NAME, name).apply();
        prefs.edit().putString(PREFS_PHONE, phone).apply();
        prefs.edit().putString(PREFS_KEY, key).apply();
    }

    private void getFromPrefs(){
        SharedPreferences prefs = getActivity().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        String name = prefs.getString(PREFS_NAME, null);
        String phone = prefs.getString(PREFS_PHONE, null);
        String publicKey = prefs.getString(PREFS_KEY, null);

        if (phone != null && name != null && publicKey != null) {
            etPhone.setText(phone);
            etUsername.setText(name);
            showQr(name, phone, publicKey);
        }
    }

    private void showToolbar(){
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle(R.string.my_profile);
        actionBar.setSubtitle("");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
    }

    private void doJob(){
        String username = etUsername.getText().toString();
        String phone = etPhone.getText().toString();

        if(!PhoneNumberUtils.isValidNumber(phone)){
            etPhone.setError(getString(R.string.invalid_phone));
            return;
        }

        DatabaseAdapter dbAdapter = DatabaseAdapter.getInstance(getActivity());
        String publicKey = dbAdapter.getPublicKey();
        putToPrefs(username, phone, publicKey);
        showQr(username, phone, publicKey);
    }

    private void showQr(String username, String phone, String key){
        try {
            JSONObject json = new JSONObject();
            json.put(QR_KEY, key);
            json.put(QR_PHONE, phone);
            json.put(QR_NAME, username);

            Bitmap bitmap = encodeAsBitmap(json.toString());
            imageView.setImageBitmap(bitmap);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
