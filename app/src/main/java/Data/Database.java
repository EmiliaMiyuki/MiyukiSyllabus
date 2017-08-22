package Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.*;

/**
 * Created by Miyuki on 2017/8/21.
 */

public class Database {

    private final String DB_NAME = "data.db";
    private SQLiteDatabase db = null;

    public Database() {
        db = SQLiteDatabase.openOrCreateDatabase(DB_NAME, null);
    }

    public SQLiteDatabase getDb() {
        return db;
    }

    protected void finalize() {
        if (db != null)
            db.close();
    }

}
