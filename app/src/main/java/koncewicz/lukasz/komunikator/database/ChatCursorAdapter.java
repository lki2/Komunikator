package koncewicz.lukasz.komunikator.database;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import koncewicz.lukasz.komunikator.R;

public class ChatCursorAdapter extends CursorAdapter {
    public ChatCursorAdapter(Context context, Cursor cursor) {
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

        LinearLayout root = (LinearLayout) view;
        int status = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseAdapter.COLUMN_STATUS));
        switch (MessagePOJO.Status.fromInt(status)){
            case FAILURE:
                root.setGravity(Gravity.END);
                root.setPadding(50, 5, 10, 5);
                view.findViewById(R.id.messageBox).setBackground(ContextCompat.getDrawable(context,
                        R.drawable.failure_msg));
                ((TextView)view.findViewById(R.id.msg_content)).setTextColor(context.getResources().getColor(R.color.failureMsgFg));
                break;
            case SENT:
                root.setGravity(Gravity.END);
                root.setPadding(50, 5, 10, 5);
                view.findViewById(R.id.messageBox).setBackground(ContextCompat.getDrawable(context,
                        R.drawable.my_msg_bg));
                break;
            case RECEIVED:
                root.setGravity(Gravity.START);
                root.setPadding(10, 5, 50, 5);
                view.findViewById(R.id.messageBox).setBackground(ContextCompat.getDrawable(context,
                        R.drawable.foreign_msg_bg));
                break;
        }

        TextView tvContent = (TextView) view.findViewById(R.id.msg_content);
        TextView tvTime = (TextView) view.findViewById(R.id.msg_time);

        String continent = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseAdapter.COLUMN_CONTENT));
        String region = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseAdapter.COLUMN_DATETIME));

        tvContent.setText(continent);
        tvTime.setText(region);
    }
}