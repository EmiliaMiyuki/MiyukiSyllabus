package Data;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Miyuki on 2017/8/23.
 */

public class SettingsDAO {
    SQLiteDatabase db = new Database().getDb();

    // Constants
    public final String KEY_OPEN_SCHOOL_DATE = "OPEN_SCHOOL_DATE", KEY_FIRST_INIT = "FIRS_INIT", KEY_USER_NAME = "USER_NAME", KEY_AVATER_IMG = "AVATER_IMG",
            KEY_BACKGROUND_IMG = "BACKGROUND_IMG", KEY_THEME = "THEME", KEY_NOTIFY_COURSE = "NOTIFY_COURSE", KEY_JWC_USER_NAME = "JWC_USER_NAME",
            KEY_JWC_PASSWORD = "JWC_PASSWORD", KEY_SHOW_WELCOME = "SHOW_WELCOME";

    public final String TABLE_NAME = "settings";
    public final String COLUMN_KEY = "key";
    public final String COLUMN_VALUE = "value";

    void setSetting(String key, String value) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_VALUE, value);
        db.update(TABLE_NAME, cv, COLUMN_KEY + "=?", new String[]{ key });
    }

    String getSetting(String key) {
        Cursor cur = db.rawQuery("select * from "+TABLE_NAME+" where " + COLUMN_KEY + "=?", new String[]{ key });
        if (cur == null || cur.getCount() <= 0)
            return null;
        return cur.getString(cur.getColumnIndex(COLUMN_VALUE));
    }

    int getSettingInt(String key) throws Exception {
        try {
            return Integer.parseInt(getSetting(key));
        }
        catch (Exception e) {
            throw e;
        }
    }

    boolean getSettingBoolean(String key) throws Exception {
        try {
            return getSetting(key).equals("true");
        }
        catch (Exception e) {
            throw e;
        }
    }

    void insert(String key, String value) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_KEY, key);
        cv.put(COLUMN_VALUE, value);
        db.insert(TABLE_NAME, null, cv);
    }

    boolean tableExists(){
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM sqlite_master WHERE type = ? AND name = ?", new String[] {"table", TABLE_NAME});
        if (!cursor.moveToFirst()) {
            cursor.close();
            return false;
        }
        int count = cursor.getInt(0);
        cursor.close();
        return count > 0;
    }

    void createTable() {
        if (tableExists())
            return;
        db.execSQL("create table " + TABLE_NAME + "(" + COLUMN_KEY + " text primary key, " + COLUMN_VALUE +" text)");
        insert(KEY_OPEN_SCHOOL_DATE, new SimpleDateFormat("yyyy-MM-dd").format(new Date()).toString());
        insert(KEY_FIRST_INIT, "true");
        insert(KEY_USER_NAME, "燕羽若雪");
        insert(KEY_AVATER_IMG, "");
        insert(KEY_BACKGROUND_IMG, "");
        insert(KEY_THEME, "blue");
        insert(KEY_NOTIFY_COURSE, "false");
        insert(KEY_JWC_PASSWORD, "");
        insert(KEY_JWC_USER_NAME, "");
        insert(KEY_SHOW_WELCOME, "");
    }

    void load_settings() {

    }
}
