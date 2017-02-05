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
    private static final int DATABASE_VERSION = 14;

    // Database Name
    private static final String DATABASE_NAME = "komunikator";

    private static final long publicKeyContactId = 200L;
    private static final long privateKeyContactId = 201L;

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
            Log.d(TAG, Table.CONTACTS.TABLE_NAME);
            db.execSQL(Table.CONTACTS.SQL_CREATE_TABLE);
            Log.d(TAG, Table.MESSAGES.SQL_CREATE_TABLE);
            db.execSQL(Table.MESSAGES.SQL_CREATE_TABLE);
            Log.d(TAG, Table.KEYS.SQL_CREATE_TABLE);
            db.execSQL(Table.KEYS.SQL_CREATE_TABLE);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL(Table.CONTACTS.SQL_DELETE_TABLE);
            db.execSQL(Table.MESSAGES.SQL_DELETE_TABLE);
            db.execSQL(Table.KEYS.SQL_DELETE_TABLE);
            onCreate(db);
        }
    }

    /**
     * Dodaje do bazy danych wiadomość powiązaną z kontaktem.
     * @param msg wiadomość.
     * @return ID wiadomości albo -1.
     */
    public long addMsg(MessagePOJO msg) {
        openGuard();
        long contactId = msg.getContactId();
        if (contactId == -1) {
            contactId = getContactId(msg.getSenderNumber());
            if (contactId == -1) {
                return -1L;
            }
        }else if (!checkIfContactExists(contactId)){
            return -1L;
        }

        ContentValues initialValues = new ContentValues();
        initialValues.put(Table.MESSAGES._CONTACT_ID, contactId);
        initialValues.put(Table.MESSAGES._CONTENT, msg.getContent());
        initialValues.put(Table.MESSAGES._STATUS, msg.getStatus().getValue());
        return mDb.insert(Table.MESSAGES.TABLE_NAME, null, initialValues);
    }

    /**
     * Dodaje do bazy danych kontakt. Kontakt musi posiadac unikatowy numer telefonu.
     * @param contact kontakt.
     * @return ID kontaktu albo -1.
     */
    public long addContact(ContactPOJO contact) {
        openGuard();
        if(getContactId(contact.getPhone()) < 0){
            ContentValues initialValues = new ContentValues();
            initialValues.put(Table.CONTACTS._PHONE, contact.getPhone());
            initialValues.put(Table.CONTACTS._NAME, contact.getName());
            return mDb.insert(Table.CONTACTS.TABLE_NAME, null, initialValues);
        }else {
            return -1L;
        }
    }

    /**
     * Pobiera ID kontaktu o podanym numerze telefonu.
     * @param phone numer telefonu.
     * @return ID kontaktu albo -1.
     */
    public long getContactId(String phone){
        openGuard();
        if (phone == null){
            return -1;
        }
        String normalizedNumber = PhoneNumberUtils.normalizeNumber(phone);
        Cursor mCursor = mDb.query(Table.CONTACTS.TABLE_NAME, Table.CONTACTS.COLUMNS,
                Table.CONTACTS._PHONE + " = '" + normalizedNumber + "'", null, null, null, null, null);
        if (mCursor.getCount() > 0){
            mCursor.moveToFirst();
            long contactId = mCursor.getLong(mCursor.getColumnIndexOrThrow(Table.CONTACTS._ID));
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
        mDb.delete(Table.MESSAGES.TABLE_NAME, Table.MESSAGES._CONTACT_ID + "=" + contactId, null);
        return mDb.delete(Table.CONTACTS.TABLE_NAME, Table.CONTACTS._ID + "=" + contactId, null) > 0;
    }

    /**
     * Dodaje do bazy danych klucz powiązany z kontaktem. Kontakt o podanym ID musi istnieć.
     * @param key klucz.
     * @return ID klucza albo -1.
     */
    public long addOrUpdateContactKey(KeyPOJO key){
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
    private long addOrUpdateKey(KeyPOJO key){ //todo update
        openGuard();
        if (getKey(key.getContactId()) == null){
            ContentValues contentValues = new ContentValues();
            contentValues.put(Table.KEYS._KEY, key.getKey());
            contentValues.put(Table.KEYS._CONTACT_ID, key.getContactId());
            return mDb.insert(Table.KEYS.TABLE_NAME, null, contentValues);
        }else {
            ContentValues contentValues = new ContentValues();
            contentValues.put(Table.KEYS._KEY, key.getKey());
            return mDb.update(Table.KEYS.TABLE_NAME, contentValues, Table.KEYS._CONTACT_ID + " = " + key.getContactId(), null);
        }
    }

    /**
     * Pobiera klucz o podanym ID kontaktu.
     * @param contactId ID kontaktu.
     * @return klucz albo {@code null} w przypadku braku klucza o podanym {@code contactId}.
     */
    @Nullable
    private String getKey(long contactId){
        openGuard();
        Cursor mCursor = mDb.query(Table.KEYS.TABLE_NAME, Table.KEYS.COLUMNS,
                Table.KEYS._CONTACT_ID + " = '" + contactId + "'", null, null, null, null, null);
        if (mCursor.getCount() > 0){
            mCursor.moveToFirst();
            String key = mCursor.getString(mCursor.getColumnIndexOrThrow(Table.KEYS._KEY));
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
        Cursor mCursor = mDb.query(Table.CONTACTS.TABLE_NAME, Table.CONTACTS.COLUMNS,
                Table.CONTACTS._ID + " = '" + contactId + "'", null, null, null, null, null);
        if (mCursor.getCount() > 0){
            mCursor.close();
            return true;
        }else {
            mCursor.close();
            return false;
        }
    }

    /**
     * Zwraca kursor z wiadomościami należącymi do chatu z kontaktem o podanym {@code contactId}.
     * @param contactId ID kontaktu.
     * @return kursor z wiadomościami.
     */
    public Cursor fetchChat(long contactId){
        openGuard();
        Cursor mCursor = mDb.query(Table.MESSAGES.TABLE_NAME, Table.MESSAGES.COLUMNS,
                Table.MESSAGES._CONTACT_ID + " = " + contactId, null, null, null, null, null);
        mCursor.moveToFirst();
        return mCursor;
    }

    /**
     * Zwraca kursor z kontaktami.
     * @return kursor z kontaktami.
     */
    public Cursor fetchContacts() {
        openGuard();
        Cursor mCursor = mDb.query(Table.CONTACTS.TABLE_NAME, Table.CONTACTS.COLUMNS, null, null, null, null, null);
        if (mCursor.getCount() > 0) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    @Nullable
    public static ContactPOJO getContact(Cursor cursor){
        if (cursor.isClosed()){
            return null;
        }
        try {
            long userId = cursor.getLong(cursor.getColumnIndexOrThrow(Table.CONTACTS._ID));
            String phone = cursor.getString(cursor.getColumnIndexOrThrow(Table.CONTACTS._PHONE));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(Table.CONTACTS._NAME));

            return new ContactPOJO(userId, phone, name);

        }catch (IllegalArgumentException e){
            return null;
        }
    }

    private void openGuard() throws SQLiteException {
        if(isOpen()) return;
        open();
        if(isOpen()) return;
        Log.d(TAG, "open guard failed");
        throw new  SQLiteException("Could not open database");
    }
}