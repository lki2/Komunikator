package koncewicz.lukasz.komunikator.database;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import koncewicz.lukasz.komunikator.R;

public class ContactsCursorAdapter extends CursorAdapter {

    public ContactsCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.contact, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tvName = (TextView) view.findViewById(R.id.name);
        TextView tvPhone = (TextView) view.findViewById(R.id.phone);

        String name = cursor.getString(cursor.getColumnIndexOrThrow(Table.CONTACTS._NAME));
        String phone = cursor.getString(cursor.getColumnIndexOrThrow(Table.CONTACTS._PHONE));

        tvName.setText(name);
        tvPhone.setText(phone);
    }
}