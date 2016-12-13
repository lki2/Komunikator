package koncewicz.lukasz.komunikator.database;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import koncewicz.lukasz.komunikator.R;

public class MessageCursorAdapter extends CursorAdapter {
    public MessageCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    // The newView method is used to inflate a new view and return it,
    // you don't bind any data to the view at this point.
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.message, parent, false);
    }

    // The bindView method is used to bind all data to a given view
    // such as setting the text on a TextView.
    @Override
    public void bindView(View view, Context context, Cursor cursor) {


        // Find fields to populate in inflated template
        TextView tvContinent = (TextView) view.findViewById(R.id.continent);
        TextView tvRegion = (TextView) view.findViewById(R.id.region);
        TextView tvCode = (TextView) view.findViewById(R.id.code);
        TextView tvName = (TextView) view.findViewById(R.id.name);

        // Extract properties from cursor
        String continent = cursor.getString(cursor.getColumnIndexOrThrow(CountriesDbAdapter.KEY_CONTINENT));
        String region = cursor.getString(cursor.getColumnIndexOrThrow(CountriesDbAdapter.KEY_REGION));
        String code = cursor.getString(cursor.getColumnIndexOrThrow(CountriesDbAdapter.KEY_CODE));
        String name = cursor.getString(cursor.getColumnIndexOrThrow(CountriesDbAdapter.KEY_NAME));


        // Populate fields with extracted properties
        tvContinent.setText(continent);
        tvRegion.setText(region);
        tvCode.setText(code);
        tvName.setText(name);
    }
}