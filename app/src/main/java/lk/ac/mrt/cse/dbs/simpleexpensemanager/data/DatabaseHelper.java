package lk.ac.mrt.cse.dbs.simpleexpensemanager.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.Serializable;

/**
 * Created by Eranga on 11/19/2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper implements Serializable
{
    private static final String DATABASE_NAME="DB_DEMO";
    private static final String TableTrans ="Transact";
    private static DatabaseHelper databaseHelper;

    public static DatabaseHelper getDataBaseHelper(){

        return databaseHelper;

    }
    public static void createConnection(Context context){
        if(databaseHelper==null){
            databaseHelper=new DatabaseHelper(context);
        }
    }

    public DatabaseHelper(Context context) {

        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table Account (Account_No TEXT PRIMARY KEY,Bank TEXT,Account_holder TEXT,Balance REAL)");
        db.execSQL("create table Transact (ID INTEGER PRIMARY KEY AUTOINCREMENT,Transaction_date INTEGER,Account_no TEXT,Expense_type INTEGER,Amount REAL)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Account");
        db.execSQL("DROP TABLE IF EXISTS Transact");
        onCreate(db);
    }
}
