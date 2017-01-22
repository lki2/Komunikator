package koncewicz.lukasz.komunikator.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;

import android.util.Log;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

import koncewicz.lukasz.komunikator.utils.PhoneNumberUtils;

public class DatabaseAdapter {

    // Logcat tag
    private static final String TAG = DatabaseAdapter.class.getName();

    private static DatabaseAdapter sInstance;
    private static DatabaseHelper mDbHelper;
    private static SQLiteDatabase mDb;
    private Context mCtx;

    // Database Version
    private static final int DATABASE_VERSION = 12;
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

    // KEYS Table - column names
    public static final String COLUMN_KEY = "key";


    private static final String TEXT_TYPE = " TEXT";
    private static final String DATETIME_TYPE = " DATETIME";
    private static final String INTEGER_TYPE = " INTEGER";

    private static final String SQL_CREATE_TABLE_USERS =
            "CREATE TABLE " + TABLE_USERS + " (" +
                    COLUMN_ID + INTEGER_TYPE + " PRIMARY KEY autoincrement," +
                    COLUMN_PHONE + TEXT_TYPE + "," +
                    COLUMN_USERNAME + TEXT_TYPE + " )";

    private static final String SQL_CREATE_TABLE_CHATS =
            "CREATE TABLE " + TABLE_CHATS + " (" +
                    COLUMN_ID + INTEGER_TYPE + " PRIMARY KEY autoincrement," +
                    COLUMN_DATETIME + DATETIME_TYPE + " DEFAULT CURRENT_TIMESTAMP," +
                    COLUMN_USER_ID + INTEGER_TYPE + "," +
                    COLUMN_CONTENT + TEXT_TYPE + "," +
                    COLUMN_STATUS + TEXT_TYPE + "," +
    " FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + "))";

    private static final String SQL_CREATE_TABLE_KEYS =
            "CREATE TABLE " + TABLE_KEYS + " (" +
                    COLUMN_ID + INTEGER_TYPE + " PRIMARY KEY autoincrement," +
                    COLUMN_USER_ID + INTEGER_TYPE + "," +
                    COLUMN_KEY + TEXT_TYPE + "," +
                    " FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + "))";

    private static final String SQL_DELETE_TABLE_USERS = "DROP TABLE IF EXISTS " + TABLE_USERS;
    private static final String SQL_DELETE_TABLE_CHATS = "DROP TABLE IF EXISTS " + TABLE_CHATS;
    private static final String SQL_DELETE_TABLE_KEYS = "DROP TABLE IF EXISTS " + TABLE_KEYS;

    private DatabaseAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    public static synchronized DatabaseAdapter getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DatabaseAdapter(context.getApplicationContext());
        }
        return sInstance;
    }

    public boolean isOpen(){
        return mDb != null && mDb.isOpen();
    }

    public void open(String pass) throws SQLException {
        if (mDbHelper == null) {
            mDbHelper = new DatabaseHelper(mCtx);
            Log.w(TAG, "open DbHelper");
        }
        if (mDb == null || !mDb.isOpen()){
            mDb = mDbHelper.getWritableDatabase(pass);
            Log.w(TAG, "open Db");
        }
    }

    public void close() {
        if (mDbHelper != null) {
            mDbHelper.close();
            Log.w(TAG, "close DbHelper");
        }
        if (mDb != null){
            mDb.close();
            Log.w(TAG, "close Db");
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
            Log.w(TAG, SQL_CREATE_TABLE_KEYS);
            db.execSQL(SQL_CREATE_TABLE_KEYS);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL(SQL_DELETE_TABLE_USERS);
            db.execSQL(SQL_DELETE_TABLE_CHATS);
            db.execSQL(SQL_DELETE_TABLE_KEYS);
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

    public long addContact(UserPOJO contact) {
        if(findContact(contact.getPhone()) > 0){
            // Contact exists
            return -1;
        }else{
            ContentValues initialValues = new ContentValues();
            initialValues.put(COLUMN_PHONE, contact.getPhone());
            initialValues.put(COLUMN_USERNAME, contact.getUsername());
            return mDb.insert(TABLE_USERS, null, initialValues);
        }
    }

    public long addKey(KeyPOJO key){
        ContentValues initialValues = new ContentValues();
        initialValues.put(COLUMN_KEY, key.getKey());
        initialValues.put(COLUMN_USER_ID, key.getUserId());
        return mDb.insert(TABLE_KEYS, null, initialValues);
    }

    public String getKey(Long userId){
        Cursor mCursor = mDb.query(TABLE_KEYS, new String[] {COLUMN_ID, COLUMN_USER_ID, COLUMN_KEY}, COLUMN_USER_ID + " = '" + userId + "'",
                null, null, null, null, null);
        if (mCursor != null && mCursor.getCount() >= 1) {
            mCursor.moveToFirst();
            return mCursor.getString(mCursor.getColumnIndexOrThrow(DatabaseAdapter.COLUMN_KEY));
        } else {
            return null;
        }
    }

    public long findContact(String phone){
        String normalizedNumber = PhoneNumberUtils.normalizeNumber(phone);
        Cursor mCursor = mDb.query(TABLE_USERS, new String[] {COLUMN_ID, COLUMN_PHONE, COLUMN_USERNAME}, COLUMN_PHONE + " = '" + normalizedNumber + "'",
                null, null, null, null, null);
        if (mCursor != null && mCursor.getCount() >= 1) {
            mCursor.moveToFirst();
            return mCursor.getLong(mCursor.getColumnIndexOrThrow(DatabaseAdapter.COLUMN_ID));
        } else {
            return -1;
        }
    }

    public Cursor fetchChat(long userId){
        Cursor mCursor = mDb.query(TABLE_CHATS, new String[] {COLUMN_ID, COLUMN_DATETIME,
                COLUMN_USER_ID, COLUMN_CONTENT, COLUMN_STATUS}, COLUMN_USER_ID + " = " + userId,
                null, null, null, null, null);

        if (mCursor != null) {
            mCursor.moveToFirst();
            Log.d(TAG, "pobrano wiadomosci");
        }
        return mCursor;
    }

    public Cursor fetchContacts() {
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

    public void addChats() {
        addMsg(new MessagePOJO(1, "debranaA", MessagePOJO.Status.RECEIVED));
        addMsg(new MessagePOJO(1, "wyslanaB", MessagePOJO.Status.SENT));
        addMsg(new MessagePOJO(1, "niewyslanaC", MessagePOJO.Status.FAILURE));
        addMsg(new MessagePOJO(2, "Chat2idebranaA", MessagePOJO.Status.RECEIVED));
        addMsg(new MessagePOJO(2, "Chat2wyslanaB", MessagePOJO.Status.SENT));
        addMsg(new MessagePOJO(2, "Chat2niewyslanaC", MessagePOJO.Status.FAILURE));
        Log.d(TAG, "dodano wiadomosci");
    }

    public void addUsers(){
        addContact(new UserPOJO("737473606", "Maria"));
        addContact(new UserPOJO("790561175", "Łukasz"));
        addContact(new UserPOJO("phone3", "username3"));

        addContact(new UserPOJO("phone4", "username4"));
        addContact(new UserPOJO("phone5", "username5"));
        addContact(new UserPOJO("phone6", "username6"));
        addContact(new UserPOJO("phone7", "username7"));
        addContact(new UserPOJO("phone8", "username8"));
        addContact(new UserPOJO("phone9", "username9"));
        addContact(new UserPOJO("phone10", "username10"));
        Log.d(TAG, "dodano uzytkownikow");
    }
}