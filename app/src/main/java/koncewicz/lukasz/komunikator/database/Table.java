package koncewicz.lukasz.komunikator.database;

abstract class Table {
    private static final String TEXT_TYPE = " TEXT";
    private static final String DATETIME_TYPE = " DATETIME";
    private static final String INTEGER_TYPE = " INTEGER";

    static abstract class CONTACTS {
        static final String TABLE_NAME = "contacts";

        static final String _ID = "_id";
        static final String _PHONE = "_phone";
        static final String _NAME = "_name";

        static final String[] COLUMNS = {_ID, _PHONE, _NAME};

        static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + INTEGER_TYPE + " PRIMARY KEY autoincrement," +
                        _PHONE + TEXT_TYPE + "," +
                        _NAME + TEXT_TYPE + " )";

        static final String SQL_DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    static abstract class KEYS {
        static final String TABLE_NAME = "keys";

        static final String _ID = "_id";
        static final String _KEY = "_key";
        static final String _CONTACT_ID = "_contact_id";

        static final String[] COLUMNS = {_ID, _KEY, _CONTACT_ID};

        static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + INTEGER_TYPE + " PRIMARY KEY autoincrement," +
                        _CONTACT_ID + INTEGER_TYPE + "," +
                        _KEY + TEXT_TYPE + "," +
                        " FOREIGN KEY(" + _CONTACT_ID + ") REFERENCES " + CONTACTS.TABLE_NAME + "(" + CONTACTS._ID + "))";

        static final String SQL_DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    static abstract class MESSAGES {
        static final String TABLE_NAME = "messages";

        static final String _ID = "_id";
        static final String _CONTACT_ID = "_contact_id";
        static final String _DATETIME = "_datetime";
        static final String _CONTENT = "_content";
        static final String _STATUS = "_status";

        static final String[] COLUMNS = {_ID, _CONTACT_ID, _DATETIME, _CONTENT, _STATUS};

        static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + INTEGER_TYPE + " PRIMARY KEY autoincrement," +
                        _DATETIME + DATETIME_TYPE + " DEFAULT CURRENT_TIMESTAMP," +
                        _CONTACT_ID + INTEGER_TYPE + "," +
                        _CONTENT + TEXT_TYPE + "," +
                        _STATUS + INTEGER_TYPE + "," +
                        " FOREIGN KEY(" + _CONTACT_ID + ") REFERENCES " + CONTACTS.TABLE_NAME + "(" + CONTACTS._ID + "))";

        static final String SQL_DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }
}
