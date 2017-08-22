package Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.*;

import ProgramFeatures.ProgramConfig;

/**
 * Created by Miyuki on 2017/8/21.
 */

public class Database {
    private final String DB_PATH = "/data/data/" + ProgramConfig.package_name + "/";
    private final String DB_NAME = DB_PATH + "data.db";
    private SQLiteDatabase db = null;

    public Database() {
        System.out.println(DB_NAME);
        db = SQLiteDatabase.openOrCreateDatabase(DB_NAME, null);
    }

    public SQLiteDatabase getDb() {
        return db;
    }

    protected void finalize() {
        //if (db != null)
        //    db.close();
    }

}
