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

import koncewicz.lukasz.komunikator.MainActivity;
import koncewicz.lukasz.komunikator.R;
import koncewicz.lukasz.komunikator.database.DatabaseAdapter;
import koncewicz.lukasz.komunikator.utils.PhoneNumberUtils;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;

public class ProfileFragment extends Fragment {
    //private static final String TAG = ProfileFragment.class.getName();

    private static final int WIDTH = 1024;
    private static final int HEIGHT = 1024;

    private static final String PREFS_PROFILE = "PROFILE";
    private static final String PROFILE_NAME = "NAME";
    private static final String PROFILE_PHONE = "PHONE";
    private static final String PROFILE_KEY = "KEY";

    public static final String QR_NAME = "name";
    public static final String QR_PHONE = "phone";
    public static final String QR_KEY = "key";

    private ImageView imageView;
    private EditText etName;
    private EditText etPhone;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        etName = (EditText) view.findViewById(R.id.et_name);
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
        SharedPreferences prefs = getActivity().getSharedPreferences(PREFS_PROFILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROFILE_NAME, name);
        editor.putString(PROFILE_PHONE, phone);
        editor.putString(PROFILE_KEY, key);
        editor.apply();
    }

    private void getFromPrefs(){
        SharedPreferences prefs = getActivity().getSharedPreferences(PREFS_PROFILE, Context.MODE_PRIVATE);
        String name = prefs.getString(PROFILE_NAME, null);
        String phone = prefs.getString(PROFILE_PHONE, null);
        String publicKey = prefs.getString(PROFILE_KEY, null);

        if (phone != null && name != null && publicKey != null) {
            etPhone.setText(phone);
            etName.setText(name);
            showQr(name, phone, publicKey);
        }
    }

    private void showToolbar(){
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle(R.string.fragment_profile_title);
        actionBar.setSubtitle("");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
    }

    private void doJob(){
        String username = etName.getText().toString();
        String phone = etPhone.getText().toString();

        if(!PhoneNumberUtils.isValidNumber(phone)){
            etPhone.setError(getString(R.string.invalid_phone));
            return;
        }

        DatabaseAdapter dbAdapter = ((MainActivity)getActivity()).getOpenDatabase();
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
