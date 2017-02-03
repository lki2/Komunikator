package koncewicz.lukasz.komunikator.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;

import android.support.annotation.Nullable;
import android.util.Log;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteException;

import info.guardianproject.cacheword.CacheWordHandler;
import koncewicz.lukasz.komunikator.utils.PhoneNumberUtils;

public class DatabaseAdapter {

    // Logcat tag
    private static final String TAG = DatabaseAdapter.class.getName();

    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    private Context mCtx;
    private CacheWordHandler mCacheWord;

    // Database Version
    private static final int DATABASE_VERSION = 13;
    // Database Name
    private static final String DATABASE_NAME = "komunikator";

    private static final long publicKeyContactId = 200L;
    private static final long privateKeyContactId = 201L;

    // Table Names
    private static final String TABLE_CONTACTS = "contacts";
    private static final String TABLE_CHATS = "chats";
    private static final String TABLE_KEYS = "keys";

    // Common column names
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_CREATED_AT = "created_at";

    // CONTACTS Table - column names
    public static final String COLUMN_PHONE = "phone";
    public static final String COLUMN_NAME = "name";

    // CHATS Table - column names
    public static final String COLUMN_CONTACT_ID = "contact_id";
    public static final String COLUMN_DATETIME = "datetime";
    public static final String COLUMN_CONTENT = "content";
    public static final String COLUMN_STATUS = "status";

    // KEYS Table - column names
    public static final String COLUMN_KEY = "key";

    private static final String TEXT_TYPE = " TEXT";
    private static final String DATETIME_TYPE = " DATETIME";
    private static final String INTEGER_TYPE = " INTEGER";

    private static final String SQL_CREATE_TABLE_CONTACTS =
            "CREATE TABLE " + TABLE_CONTACTS + " (" +
                    COLUMN_ID + INTEGER_TYPE + " PRIMARY KEY autoincrement," +
                    COLUMN_PHONE + TEXT_TYPE + "," +
                    COLUMN_NAME + TEXT_TYPE + " )";

    private static final String SQL_CREATE_TABLE_CHATS =
            "CREATE TABLE " + TABLE_CHATS + " (" +
                    COLUMN_ID + INTEGER_TYPE + " PRIMARY KEY autoincrement," +
                    COLUMN_DATETIME + DATETIME_TYPE + " DEFAULT CURRENT_TIMESTAMP," +
                    COLUMN_CONTACT_ID + INTEGER_TYPE + "," +
                    COLUMN_CONTENT + TEXT_TYPE + "," +
                    COLUMN_STATUS + INTEGER_TYPE + "," +
    " FOREIGN KEY(" + COLUMN_CONTACT_ID + ") REFERENCES " + TABLE_CONTACTS + "(" + COLUMN_ID + "))";

    private static final String SQL_CREATE_TABLE_KEYS =
            "CREATE TABLE " + TABLE_KEYS + " (" +
                    COLUMN_ID + INTEGER_TYPE + " PRIMARY KEY autoincrement," +
                    COLUMN_CONTACT_ID + INTEGER_TYPE + "," +
                    COLUMN_KEY + TEXT_TYPE + "," +
                    " FOREIGN KEY(" + COLUMN_CONTACT_ID + ") REFERENCES " + TABLE_CONTACTS + "(" + COLUMN_ID + "))";

    private static final String SQL_DELETE_TABLE_CONTACTS = "DROP TABLE IF EXISTS " + TABLE_CONTACTS;
    private static final String SQL_DELETE_TABLE_CHATS = "DROP TABLE IF EXISTS " + TABLE_CHATS;
    private static final String SQL_DELETE_TABLE_KEYS = "DROP TABLE IF EXISTS " + TABLE_KEYS;

    public DatabaseAdapter(Context ctx, CacheWordHandler cacheWord) {
        this.mCtx = ctx;
        this.mCacheWord = cacheWord;
    }

    public boolean isOpen(){
        return mDb != null && mDb.isOpen();
    }

    public void open() throws SQLException {
        if (mDbHelper == null) {
            mDbHelper = new DatabaseHelper(mCtx, mCacheWord);
            Log.e(TAG, "open DbHelper");
        }
        if (mDb == null || !mDb.isOpen()){
            mDb = mDbHelper.getWritableDatabase();
            Log.e(TAG, "open Db");
        }
    }

    public void close() {
        if (mDbHelper != null) {
            mDbHelper.close();
            Log.e(TAG, "close DbHelper");
        }
        if (mDb != null && mDb.isOpen()){
            mDb.close();
            Log.e(TAG, "close Db");
        }
    }

    private static class DatabaseHelper extends SQLCipherOpenHelper {

        private DatabaseHelper(Context context, CacheWordHandler cacheWord) {
            super(cacheWord, context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            Log.d(TAG, SQL_CREATE_TABLE_CONTACTS);
            db.execSQL(SQL_CREATE_TABLE_CONTACTS);
            Log.d(TAG, SQL_CREATE_TABLE_CHATS);
            db.execSQL(SQL_CREATE_TABLE_CHATS);
            Log.d(TAG, SQL_CREATE_TABLE_KEYS);
            db.execSQL(SQL_CREATE_TABLE_KEYS);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL(SQL_DELETE_TABLE_CONTACTS);
            db.execSQL(SQL_DELETE_TABLE_CHATS);
            db.execSQL(SQL_DELETE_TABLE_KEYS);
            onCreate(db);
        }
    }

    /**
     * Dodaje do bazy danych wiadomość powiązaną z kontaktem. Kontakt o danym ID musi istnieć.
     * @param msg wiadomość.
     * @return ID wiadomości albo -1.
     */
    public long addMsg(MessagePOJO msg) {
        openGuard();
        if (checkIfContactExists(msg.getContactId())){
            ContentValues initialValues = new ContentValues();
            initialValues.put(COLUMN_CONTACT_ID, msg.getContactId());
            initialValues.put(COLUMN_CONTENT, msg.getContent());
            initialValues.put(COLUMN_STATUS, msg.getStatus().getValue());
            return mDb.insert(TABLE_CHATS, null, initialValues);
        }else {
            return -1L;
        }
    }

    /**
     * Dodaje do bazy danych kontakt. Kontakt musi posiadac unikatowy numer telefonu.
     * @param contact kontakt.
     * @return ID kontaktu albo -1.
     */
    public long addContact(ContactPOJO contact) {
        openGuard();
        if(getContact(contact.getPhone()) < 0){
            ContentValues initialValues = new ContentValues();
            initialValues.put(COLUMN_PHONE, contact.getPhone());
            initialValues.put(COLUMN_NAME, contact.getName());
            return mDb.insert(TABLE_CONTACTS, null, initialValues);
        }else {
            return -1L;
        }
    }

    /**
     * Pobiera kontakt o podanym numerze telefonu.
     * @param phone numer telefonu.
     * @return ID kontaktu albo -1.
     */
    public long getContact(String phone){
        openGuard();
        String normalizedNumber = PhoneNumberUtils.normalizeNumber(phone);
        Cursor mCursor = mDb.query(TABLE_CONTACTS, new String[] {COLUMN_ID, COLUMN_PHONE, COLUMN_NAME},
                COLUMN_PHONE + " = '" + normalizedNumber + "'", null, null, null, null, null);
        if (mCursor.getCount() > 0){
            mCursor.moveToFirst();
            long contactId = mCursor.getLong(mCursor.getColumnIndexOrThrow(DatabaseAdapter.COLUMN_ID));
            mCursor.close();
            return contactId;
        }else {
            mCursor.close();
            return -1L;
        }
    }

    /**
     * Usuwa kontakt i powiązane z nim wiadomości.
     * @param contactId kontakt.
     * @return {@code true} w przypadku powodzenia albo {@code false}.
     */
    public boolean deleteContact(Long contactId) {
        openGuard();
        mDb.delete(TABLE_CHATS, COLUMN_CONTACT_ID + "=" + contactId, null);
        return mDb.delete(TABLE_CONTACTS, COLUMN_ID + "=" + contactId, null) > 0;
    }

    /**
     * Dodaje do bazy danych klucz powiązany z kontaktem. Kontakt o podanym ID musi istnieć.
     * @param key klucz.
     * @return ID klucza albo -1.
     */
    public long addContactKey(KeyPOJO key){
        if (checkIfContactExists(key.getContactId())){
            return addOrUpdateKey(key);
        }else {
            return -1L;
        }
    }

    /**
     * Pobiera klucz o podanym ID kontaktu.
     * @param contactId ID kontaktu.
     * @return klucz albo {@code null} w przypadku braku klucza o podanym {@code contactId}.
     */
    public String getContactKey(Long contactId){
        if (contactId >= 0) {
            return getKey(contactId);
        }else {
            return null;
        }
    }

    /**
     * Pobiera klucz prywatny jeśli istnieje.
     * @return klucz prywatny albo {@code null} w przypadku braku klucza.
     */
    public String getPrivateKey(){
        return getKey(privateKeyContactId);
    }

    /**
     * Pobiera klucz publiczny jeśli istnieje.
     * @return klucz publiczny albo {@code null} w przypadku braku klucza.
     */
    public String getPublicKey(){
        return getKey(publicKeyContactId);
    }

    /**
     * Dodaje albo aktualizuje parę kluczy używaną w aplikacji.
     * @param publicKey klucz publicznby.
     * @param privateKey klucz prywatny.
     * @return {@code true} w przypadku powodzenia albo {@code false}.
     */
    public boolean addOrUpdateOwnKeyPair(String publicKey, String privateKey){
        return addOrUpdateKey(new KeyPOJO(publicKey, publicKeyContactId)) > 0
                && addOrUpdateKey(new KeyPOJO(privateKey, privateKeyContactId)) > 0;
    }

    /**
     * Dodaję albo aktualizuje klucz.
     * @param key klucz.
     * @return ID klucza.
     */
    private long addOrUpdateKey(KeyPOJO key){
        openGuard();
        ContentValues initialValues = new ContentValues();
        initialValues.put(COLUMN_KEY, key.getKey());
        initialValues.put(COLUMN_CONTACT_ID, key.getContactId());
        return mDb.insertWithOnConflict(TABLE_KEYS, null, initialValues, SQLiteDatabase.CONFLICT_REPLACE);
    }

    /**
     * Pobiera klucz o podanym ID kontaktu.
     * @param contactId ID kontaktu.
     * @return klucz albo {@code null} w przypadku braku klucza o podanym {@code contactId}.
     */
    @Nullable
    private String getKey(long contactId){
        openGuard();
        Cursor mCursor = mDb.query(TABLE_KEYS, new String[] {COLUMN_ID, COLUMN_CONTACT_ID, COLUMN_KEY},
                COLUMN_CONTACT_ID + " = '" + contactId + "'", null, null, null, null, null);
        if (mCursor.getCount() > 0){
            mCursor.moveToFirst();
            String key = mCursor.getString(mCursor.getColumnIndexOrThrow(DatabaseAdapter.COLUMN_KEY));
            mCursor.close();
            return key;
        }else {
            mCursor.close();
            return null;
        }
    }

    /**
     * Sprawdza czy istnieje w bazie kontakt o podanym ID.
     * @param contactId ID kontaktu.
     * @return {@code true} gdy kontakt istnieje albo {@code false}.
     */
    private boolean checkIfContactExists(Long contactId){
        openGuard();
        Cursor mCursor = mDb.query(TABLE_CONTACTS, new String[] {COLUMN_ID, COLUMN_PHONE, COLUMN_NAME},
                COLUMN_ID + " = '" + contactId + "'", null, null, null, null, null);
        if (mCursor.getCount() > 0){
            mCursor.close();
            return true;
        }else {
            mCursor.close();
            return false;
        }
    }

    //todo
    public Cursor fetchChat(long contactId){
        openGuard();
        Cursor mCursor = mDb.query(TABLE_CHATS, new String[] {COLUMN_ID, COLUMN_DATETIME,
                        COLUMN_CONTACT_ID, COLUMN_CONTENT, COLUMN_STATUS}, COLUMN_CONTACT_ID + " = " + contactId,
                null, null, null, null, null);
        mCursor.moveToFirst();
        return mCursor;
    }

    //todo
    public Cursor fetchContacts() {
        openGuard();
        Cursor mCursor = mDb.query(TABLE_CONTACTS, new String[] {COLUMN_ID, COLUMN_PHONE,
                COLUMN_NAME}, null, null, null, null, null);
        if (mCursor.getCount() > 0) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    private void openGuard() throws SQLiteException {
        if(isOpen()) return;
        open();
        if(isOpen()) return;
        Log.d(TAG, "open guard failed");
        throw new  SQLiteException("Could not open database");
    }
}