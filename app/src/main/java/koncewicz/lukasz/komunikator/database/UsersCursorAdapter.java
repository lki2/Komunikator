package koncewicz.lukasz.komunikator.database;

import android.content.Context;
import android.database.Cursor;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import koncewicz.lukasz.komunikator.R;

public class UsersCursorAdapter extends CursorAdapter {
    public UsersCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    // The newView method is used to inflate a new view and return it,
    // you don't bind any data to the view at this point.
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.chat_list_item, parent, false);
    }

    // The bindView method is used to bind all data to a given view
    // such as setting the text on a TextView.
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        LinearLayout root = (LinearLayout) view;

        root.setGravity(Gravity.LEFT);
        root.setPadding(10, 5, 50, 5);
        view.findViewById(R.id.messageBox).setBackgroundDrawable(context.getResources().getDrawable( R.drawable.foreign_msg_bg));

        TextView tvContinent = (TextView) view.findViewById(R.id.msgText);
        TextView tvRegion = (TextView) view.findViewById(R.id.msgTime);

        String continent = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseAdapter.COLUMN_USERNAME));
        String region = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseAdapter.COLUMN_PHONE));

        tvContinent.setText(continent);
        tvRegion.setText(region);
    }
}