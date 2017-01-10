package koncewicz.lukasz.komunikator.fragments;

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
import koncewicz.lukasz.komunikator.database.DatabaseAdapter;

public class ChatCursorAdapter extends CursorAdapter {
    public ChatCursorAdapter(Context context, Cursor cursor) {
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

        switch (cursor.getString(cursor.getColumnIndexOrThrow(DatabaseAdapter.COLUMN_STATUS))){
            case "0":
                root.setGravity(Gravity.RIGHT);
                root.setPadding(50, 5, 10, 5);
                view.findViewById(R.id.messageBox).setBackgroundDrawable(context.getResources().getDrawable( R.drawable.failure_msg));
                ((TextView)view.findViewById(R.id.msgText)).setTextColor(context.getResources().getColor(R.color.failureMsgFg));
                break;
            case "1":
                root.setGravity(Gravity.RIGHT);
                root.setPadding(50, 5, 10, 5);
                view.findViewById(R.id.messageBox).setBackgroundDrawable(context.getResources().getDrawable( R.drawable.my_msg_bg));

                break;
            case "2":
                root.setGravity(Gravity.LEFT);
                root.setPadding(10, 5, 50, 5);
                view.findViewById(R.id.messageBox).setBackgroundDrawable(context.getResources().getDrawable( R.drawable.foreign_msg_bg));
                break;
        }

        TextView tvContinent = (TextView) view.findViewById(R.id.msgText);
        TextView tvRegion = (TextView) view.findViewById(R.id.msgTime);

        String continent = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseAdapter.COLUMN_CONTENT));
        String region = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseAdapter.COLUMN_DATETIME));

        tvContinent.setText(continent);
        tvRegion.setText(region);
    }
}