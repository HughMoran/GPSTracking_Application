package hugomoran.com.journeytracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Timer;

public class MyDBManager {

    //
    public static final String KEY_ROWID = "_id";
    public static final String KEY_USERID = "user_id";
    public static final String KEY_EVENT = "event_type";
    public static final String KEY_LAT = "latitude";
    public static final String KEY_LON = "longitude";
    public static final String KEY_DIST = "distance";
    public static final String KEY_DUR = "duration";
    public static final String KEY_TIME = "time";
    public static final String KEY_MODE = "mode";

    public static final String KEY_USERNAME = "username";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_PREFERENCE = "preference";


    private static final String DATABASE_NAME = "Journey";
    private static final String DATABASE_TABLE = "Journey_Details";

    private static final String DATABASE_TABLE2 = "User_Details";


    private static final int DATABASE_VERSION = 1;

    //
    private static final String DATABASE_CREATE2 = "create table User_Details " +
            "(_id integer primary key autoincrement, " +
            "username text not null, " + "email text not null, " + "password text not null, " + "preference int not null);";

    private static final String DATABASE_CREATE = "create table Journey_Details " +
            "(_id integer primary key autoincrement, " + "user_id int not null, " +
            "event_type double not null, " + "latitude double not null, " + "distance double not null, " +
            "duration double not null, " + "time date not null, " + "mode int not null, " + "longitude double not null);";


    private final Context context;

    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    //
    public MyDBManager(Context ctx) {
        //
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }


    //
    private static class DatabaseHelper extends SQLiteOpenHelper {

        //
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME,
                    null, DATABASE_VERSION);
        }


        @Override
        //
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(DATABASE_CREATE);
            db.execSQL(DATABASE_CREATE2);
        }

        @Override

        //
        public void onUpgrade(SQLiteDatabase db, int oldVersion,
                              int newVersion) {
            // whatever is to be changed on dB structure

        }
    }   //


    //
    public MyDBManager open() throws SQLException {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    //
    public void close() {
        DBHelper.close();
    }

    //
    public long insertUser(String user, String email, String pass, int preference) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_USERNAME, user);
        initialValues.put(KEY_EMAIL, email);
        initialValues.put(KEY_PASSWORD, pass);
        initialValues.put(KEY_PREFERENCE, preference);
        return db.insert(DATABASE_TABLE2, null, initialValues);
    }

    //------------check user email and password for a match with rawQuery-----------------
    public boolean Login(String email, String password) throws SQLException {
        Cursor mCursor = db.rawQuery("SELECT * FROM " + DATABASE_TABLE2 + " WHERE email=? AND password=?", new String[]{email, password});

        if (mCursor != null) {
            if (mCursor.getCount() > 0) {
                return true;
            }
        }
        return false;
    }

    //-------------get user using email and password---------------
    public Cursor getUsers(String email, String password) {
        return db.query(true, DATABASE_TABLE2, new String[]{
                        KEY_ROWID,
                        KEY_USERNAME,
                        KEY_EMAIL,
                        KEY_PASSWORD,
                        KEY_PREFERENCE
                },
                KEY_EMAIL + "=\"" + email + "\" AND " + KEY_PASSWORD + "=\"" + password + "\"",
                null,
                null,
                null,
                null,
                null);
    }

    //------------------insert coordinates into the database--------------
    public long insertTask(int user_id, int event, double lat, double lon, double duration, double distance, int mode, String date) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_EVENT, event);
        initialValues.put(KEY_USERID, user_id);
        initialValues.put(KEY_LAT, lat);
        initialValues.put(KEY_LON, lon);
        initialValues.put(KEY_DIST, distance);
        initialValues.put(KEY_DUR, duration);
        initialValues.put(KEY_MODE, mode);
        initialValues.put(KEY_TIME, date);

        return db.insert(DATABASE_TABLE, null, initialValues);
    }

    //
    public boolean deleteTask(long rowId) {
        //
        return db.delete(DATABASE_TABLE, KEY_ROWID +
                "=" + rowId, null) > 0;
    }

    //-------------not used in application here to test database-----------------
    public Cursor getAllPolled() {
        return db.query(DATABASE_TABLE, new String[]{
                        KEY_ROWID,
                        KEY_USERID,
                        KEY_EVENT,
                        KEY_LAT,
                        KEY_DIST,
                        KEY_DUR,
                        KEY_TIME,
                        KEY_MODE,
                        KEY_LON
                },
                null,
                null,
                null,
                null,
                null);
    }

    //--------get events 1 for the user-------------
    public Cursor getStarts(int user) {
        Cursor mCursor = db.query(true, DATABASE_TABLE, new String[]{
                        KEY_ROWID,
                        KEY_USERID,
                        KEY_EVENT,
                        KEY_LAT,
                        KEY_DIST,
                        KEY_DUR,
                        KEY_TIME,
                        KEY_MODE,
                        KEY_LON
                },
                KEY_EVENT + "=" + 1 + " AND " + KEY_USERID + "=" + user,
                null,
                null,
                null,
                null,
                null);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    //-------------get starts for user 1 limit 10 and decend with greatest distance--------------
    public Cursor getDistStarts(int user) {
        Cursor mCursor = db.query(true, DATABASE_TABLE, new String[]{
                        KEY_ROWID,
                        KEY_USERID,
                        KEY_EVENT,
                        KEY_LAT,
                        KEY_DIST,
                        KEY_DUR,
                        KEY_TIME,
                        KEY_MODE,
                        KEY_LON
                },
                KEY_EVENT + "=" + 1 + " AND " + KEY_USERID + "=" + user,
                null,
                null,
                null,
                KEY_DIST + " DESC",
                "10");

        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    //------------get starts for user 1 limit 10 and decend with greatest time---------------
    public Cursor getTimeStarts(int user) {
        Cursor mCursor = db.query(true, DATABASE_TABLE, new String[]{
                        KEY_ROWID,
                        KEY_USERID,
                        KEY_EVENT,
                        KEY_LAT,
                        KEY_DIST,
                        KEY_DUR,
                        KEY_TIME,
                        KEY_MODE,
                        KEY_LON
                },
                KEY_EVENT + "=" + 1 + " AND " + KEY_USERID + "=" + user,
                null,
                null,
                null,
                KEY_DUR + " DESC",
                "10");
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    //-----------------user event 3 finish journey-----------------
    public Cursor getStops(int user) {
        Cursor mCursor = db.query(true, DATABASE_TABLE, new String[]{
                        KEY_ROWID,
                        KEY_USERID,
                        KEY_EVENT,
                        KEY_LAT,
                        KEY_DIST,
                        KEY_DUR,
                        KEY_TIME,
                        KEY_MODE,
                        KEY_LON
                },
                KEY_EVENT + "=" + 3 + " AND " + KEY_USERID + "=" + user,
                null,
                null,
                null,
                null,
                null);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    //---------------get starts for user 1 limit 10 and decend with greatest distance--------------
    public Cursor getDistStops(int user) {
        Cursor mCursor = db.query(true, DATABASE_TABLE, new String[]{
                        KEY_ROWID,
                        KEY_USERID,
                        KEY_EVENT,
                        KEY_LAT,
                        KEY_DIST,
                        KEY_DUR,
                        KEY_TIME,
                        KEY_MODE,
                        KEY_LON
                },
                KEY_EVENT + "=" + 3 + " AND " + KEY_USERID + "=" + user,
                null,
                null,
                null,
                KEY_DIST + " DESC",
                "10");

        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    //---------------get stops for user  limit 10 and decend with greatest time--------------
    public Cursor getTimeStops(int user) {
        Cursor mCursor = db.query(true, DATABASE_TABLE, new String[]{
                        KEY_ROWID,
                        KEY_USERID,
                        KEY_EVENT,
                        KEY_LAT,
                        KEY_DIST,
                        KEY_DUR,
                        KEY_TIME,
                        KEY_MODE,
                        KEY_LON
                },
                KEY_EVENT + "=" + 3 + " AND " + KEY_USERID + "=" + user,
                null,
                null,
                null,
                KEY_DUR + " DESC",
                "10");

        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    //
    public Cursor getTask(long rowId) throws SQLException {
        Cursor mCursor =
                db.query(true, DATABASE_TABLE, new String[]{
                                KEY_ROWID,
                                KEY_EVENT,
                                KEY_LON,
                                KEY_LAT,
                                KEY_DIST,
                                KEY_DUR
                        },
                        KEY_ROWID + "=" + rowId,
                        null,
                        null,
                        null,
                        null,
                        null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
}
