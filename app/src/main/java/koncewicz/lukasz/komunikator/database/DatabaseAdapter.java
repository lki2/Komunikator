package koncewicz.lukasz.komunikator.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.util.Log;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

public class DatabaseAdapter {

    // Logcat tag
    private static final String TAG = DatabaseHelper.class.getName();

    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    private final Context mCtx;

    // Database Version
    private static final int DATABASE_VERSION = 6;
    // Database Name
    private static final String DATABASE_NAME = "komunikator";

    // Table Names
    private static final String TABLE_USERS = "users";
    private static final String TABLE_CHATS = "chats";
    private static final String TABLE_KEYS = "keys";

    // Common column names
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_CREATED_AT = "created_at";

    // USERS Table - column nmaes
    public static final String COLUMN_PHONE = "phone";
    public static final String COLUMN_USERNAME = "username";

    // CHATS Table - column names
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_DATETIME = "datetime";
    public static final String COLUMN_CONTENT = "content";
    public static final String COLUMN_STATUS = "status";

    private static final String TEXT_TYPE = " TEXT";
    private static final String DATETIME_TYPE = " DATETIME";

    private static final String SQL_CREATE_TABLE_USERS =
            "CREATE TABLE " + TABLE_USERS + " (" +
                    COLUMN_ID + " integer PRIMARY KEY autoincrement," +
                    COLUMN_PHONE + TEXT_TYPE + "," +
                    COLUMN_USERNAME + TEXT_TYPE +
                    " )";

    private static final String SQL_CREATE_TABLE_CHATS =
            "CREATE TABLE " + TABLE_CHATS + " (" +
                    COLUMN_ID + " integer PRIMARY KEY autoincrement," +
                    COLUMN_DATETIME + DATETIME_TYPE + " DEFAULT CURRENT_TIMESTAMP," +
                    COLUMN_USER_ID + TEXT_TYPE + "," +
                    COLUMN_CONTENT + TEXT_TYPE + "," +
                    COLUMN_STATUS + TEXT_TYPE +
                    " )";

    private static final String SQL_DELETE_TABLE_USERS = "DROP TABLE IF EXISTS " + TABLE_USERS;
    private static final String SQL_DELETE_TABLE_CHATS = "DROP TABLE IF EXISTS " + TABLE_CHATS;

    public DatabaseAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    public DatabaseAdapter open(String pass) throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase(pass);
        return this;
    }

    public void close() {
        if (mDbHelper != null) {
            mDbHelper.close();
        }
    }



    private static class DatabaseHelper extends SQLiteOpenHelper {

        private DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            Log.w(TAG, SQL_CREATE_TABLE_USERS);
            db.execSQL(SQL_CREATE_TABLE_USERS);
            Log.w(TAG, SQL_CREATE_TABLE_CHATS);
            db.execSQL(SQL_CREATE_TABLE_CHATS);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL(SQL_DELETE_TABLE_USERS);
            db.execSQL(SQL_DELETE_TABLE_CHATS);
            onCreate(db);
        }
    }

    public long addMsg(MessagePOJO msg) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(COLUMN_USER_ID, msg.getUserId());
        initialValues.put(COLUMN_CONTENT, msg.getContent());
        initialValues.put(COLUMN_STATUS, msg.getStatus().getValue());
        return mDb.insert(TABLE_CHATS, null, initialValues);
    }

    private long addUser(UserPOJO user) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(COLUMN_PHONE, user.getPhone());
        initialValues.put(COLUMN_USERNAME, user.getUsername());
        return mDb.insert(TABLE_USERS, null, initialValues);
    }

    public void addtChats() {
        addMsg(new MessagePOJO(1, "debranaA", MessagePOJO.Status.RECEIVED));
        addMsg(new MessagePOJO(1, "wyslanaB", MessagePOJO.Status.SENT));
        addMsg(new MessagePOJO(1, "niewyslanaC", MessagePOJO.Status.FAILURE));
        addMsg(new MessagePOJO(2, "Chat2idebranaA", MessagePOJO.Status.RECEIVED));
        addMsg(new MessagePOJO(2, "Chat2wyslanaB", MessagePOJO.Status.SENT));
        addMsg(new MessagePOJO(2, "Chat2niewyslanaC", MessagePOJO.Status.FAILURE));
        Log.d(TAG, "dodano wiadomosci");
    }

    public void addUsers(){
        addUser(new UserPOJO("phone1", "username1"));
        addUser(new UserPOJO("phone2", "username2"));
        addUser(new UserPOJO("phone3", "username3"));
        Log.d(TAG, "dodano uzytkownikow");
    }



    public Cursor fetchChat(int userId){
        Cursor mCursor = mDb.query(TABLE_CHATS, new String[] {COLUMN_ID, COLUMN_DATETIME,
                COLUMN_USER_ID, COLUMN_CONTENT, COLUMN_STATUS}, COLUMN_USER_ID + " = " + userId,
                null, null, null, null, null);

        if (mCursor != null) {
            mCursor.moveToFirst();
            Log.d(TAG, "pobrano wiadomosci");
        }
        return mCursor;
    }

    public Cursor fetchUsers() {
        Cursor mCursor = mDb.query(TABLE_USERS, new String[] {COLUMN_ID, COLUMN_PHONE,
                COLUMN_USERNAME}, null, null, null, null, null);

        if (mCursor != null) {
            mCursor.moveToFirst();
            Log.d(TAG, "pobrano wiadomosci");
        }
        return mCursor;
    }

    public boolean deleteAllChats() {
        int doneDelete = 0;
        doneDelete = mDb.delete(TABLE_CHATS, null , null);
        Log.w(TAG, Integer.toString(doneDelete));
        return doneDelete > 0;

    }
}