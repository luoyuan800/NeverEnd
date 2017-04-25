package cn.luo.yuan.maze.persistence.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import cn.luo.yuan.maze.utils.NormalRAMReader;

import java.util.HashMap;


/**
 * Created by gluo on 9/14/2015.
 */
public class Sqlite {
    public final static String DB_NAME = "NeverEnd";
    public static int DB_VERSION = 1;
    public static boolean isUpgrade = false;
    private static Sqlite sqlite;
    private Context context;
    private SQLiteDatabase database;

    private Sqlite(Context context) {
        this.context = context;
        getDB();
    }

    public synchronized static Sqlite getSqlite(Context context) {
        if (sqlite == null) {
            sqlite = new Sqlite(context);
        } else{
            if(sqlite.context != context){
                sqlite.close();
                sqlite = new Sqlite(context);
            }
        }
        return sqlite;
    }

    public static Sqlite getSqlite() throws Exception {
        if (sqlite == null) {
            throw new Exception("Please initlize Database first!");
        }
        return sqlite;
    }

    public Cursor excuseSOL(String sql) {
        Cursor cursor = getDB().rawQuery(sql, null);
        cursor.moveToFirst();
        return cursor;
    }

    public void excuseSQLWithoutResult(String sql) {
        try {
            getDB().execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void reCreateDB(Context context) {
        context.deleteDatabase(DB_NAME);
        database = context.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);
    }

    public void onCreate(SQLiteDatabase db) {
        try {
            db.beginTransaction();
            //Create Table
            createHeroTable(db);
            createAccessoryTable(db);
            createKeyTable(db);
            createMazeTable(db);
            createMonsterTable(db);
            createPetTable(db);
            db.setTransactionSuccessful();
            db.endTransaction();
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    private void createMazeTable(SQLiteDatabase db) {
        db.execSQL("create table maze(" +
                "id TEXT NOT NULL PRIMARY KEY," +
                "level INTEGER," +
                "max_level INTEGER," +
                "hero_index INTEGER NOT NULL" +
                ")");
    }
    private void createMonsterTable(SQLiteDatabase db) {
        db.execSQL("create table monster(" +
                "id TEXT NOT NULL PRIMARY KEY," +
                "type TEXT," +
                "min_level INTEGER," +
                "atk INTEGER," +
                "def INTEGER," +
                "hp INTEGER," +
                "hit FLOAT," +
                "silent FLOAT," +
                "meet_rate FLOAT," +
                "pet_rate FLOAT," +
                "image INTEGER," +
                "sex INTEGER DEFAULT -1," +
                "race INTEGER" +
                ")");
        db.execSQL("create table monster_book(" +
                "id TEXT NOT NULL PRIMARY KEY," +
                "meet_level INTEGER," +
                "catch_level INTEGER," +
                "win INTEGER," +
                "lost INTEGER" +
                ")");
    }

    private void createKeyTable(SQLiteDatabase db) {
        db.execSQL("create table key (hero_index INTEGER NOT NULL PRIMARY KEY, key INTEGER NOT NULL)");
    }

    private void createAccessoryTable(SQLiteDatabase db) {
        String table = "create table accessory (" +
                "hero_index TEXT NOT NULL," +
                "name TEXT NOT NULL," +
                "desc TEXT," +
                "mounted INTEGER," +
                "level INTEGER," +
                "id TEXT NOT NULL PRIMARY KEY" +
                ")";
        db.execSQL(table);
    }

    public void updateById(String table, ContentValues values, String ... ids){
        database.update(table, values, "id = ?", ids);
    }

    private void createHeroTable(SQLiteDatabase db) {
        String table = "create table hero (" +
                "last_update INTEGER," +
                "created INTEGER," +
                "hero_index TEXT NOT NULL ," +
                "name TEXT NOT NULL," +
                "gift TEXT ," +
                "reincarnate INTEGER ," +
                "id TEXT NOT NULL PRIMARY KEY," +
                "element INTEGER " +
                ")";
        db.execSQL(table);
    }


    private void createPetTable(SQLiteDatabase db) {
        String table = "create table pet (" +
                "last_update INTEGER," +
                "created INTEGER," +
                "hero_index TEXT NOT NULL ," +
                "name TEXT NOT NULL," +
                "tag TEXT ," +
                "level INTEGER ," +
                "sex INTEGER ," +
                "color TEXT ," +
                "mounted INTEGER ," +
                "id TEXT NOT NULL PRIMARY KEY," +
                "element INTEGER " +
                ")";
        db.execSQL(table);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion < oldVersion) {
            throw new RuntimeException("反向安装了低版本：" + oldVersion + "-->" + newVersion);
        }
    }

    public void beginTransaction() {
        getDB().beginTransaction();
    }

    public void markTransactionSuccess() {
        getDB().setTransactionSuccessful();
    }

    public void endTransaction() {
        markTransactionSuccess();
        getDB().endTransaction();
    }

    public void close() {
        if (database != null && database.isOpen()) {
            database.close();
        }
    }

    public SQLiteDatabase getDatabase() {
        return database;
    }

    private SQLiteDatabase openOrCreateInnerDB() {
        database = context.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);
        if (database.getVersion() == 0) {
            onCreate(database);
        } else if (database.getVersion() < DB_VERSION) {
            onUpgrade(database, database.getVersion(), DB_VERSION);
        }
        database.setVersion(DB_VERSION);
        return database;
    }

    private synchronized SQLiteDatabase getDB() {
        SQLiteDatabase db = database;
        if (db == null || !db.isOpen()) {
            db = openOrCreateInnerDB();
        }
        return db;
    }

    public int getKey(int index){
        Cursor cursor = excuseSOL("select key from key where hero_index = '" + index + "'");
        try {
            if (cursor.isAfterLast()) {
                int key = NormalRAMReader.generateKey();
                ContentValues values = new ContentValues(2);
                values.put("hero_index", index);
                values.put("key", key);
                insert("key",values);
                return key;
            } else {
                return cursor.getInt(cursor.getColumnIndex("key"));
            }
        }finally {
            cursor.close();
        }
    }

    public void insert(String table, ContentValues values){
        database.insert(table, null, values);
    }

}
