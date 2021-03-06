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

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.message, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        LinearLayout root = (LinearLayout) view;
        int statusInt = cursor.getInt(cursor.getColumnIndexOrThrow(Table.MESSAGES._STATUS));
        Message.Status status = Message.Status.fromInt(statusInt);
        assert status != null;
        switch (status){
            case FAILURE:
                root.setGravity(Gravity.END);
                root.setPadding(50, 5, 10, 5);
                view.findViewById(R.id.messageBox).setBackground(ContextCompat.getDrawable(context,
                        R.drawable.failure_msg));
                ((TextView)view.findViewById(R.id.msg_content)).setTextColor(ContextCompat.getColor(context, R.color.failureMsgFg));
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
            default:
                break;
        }

        TextView tvContent = (TextView) view.findViewById(R.id.msg_content);
        TextView tvTime = (TextView) view.findViewById(R.id.msg_time);

        String continent = cursor.getString(cursor.getColumnIndexOrThrow(Table.MESSAGES._CONTENT));
        String region = cursor.getString(cursor.getColumnIndexOrThrow(Table.MESSAGES._DATETIME));

        tvContent.setText(continent);
        tvTime.setText(region);
    }
}