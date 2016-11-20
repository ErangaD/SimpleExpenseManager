package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.DatabaseHelper;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

/**
 * Created by Eranga on 11/19/2016.
 */
public class PersistentTransactionDAO implements TransactionDAO,Serializable {
    private static final String TableTrans ="Transact";
    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        SQLiteDatabase db= DatabaseHelper.getDataBaseHelper().getWritableDatabase();
        int type=0;
        switch (expenseType) {
            case EXPENSE:
                type=0;
                break;
            case INCOME:
                type=1;
                break;
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put("Transaction_date",date.getTime());
        contentValues.put("Account_No", accountNo);
        contentValues.put("Expense_type",type);
        contentValues.put("Amount",amount);
        db.insert(TableTrans,null,contentValues);
    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        SQLiteDatabase db= DatabaseHelper.getDataBaseHelper().getWritableDatabase();
        Log.e("","come to tj");
        Cursor res=db.rawQuery("select * from "+TableTrans,null);
        ExpenseType type=null;
        List<Transaction> tr=new LinkedList<>();
        while(res.moveToNext()){
            switch (res.getInt(2)) {
                case 0:
                    type=ExpenseType.EXPENSE;
                    break;
                case 1:
                    type=ExpenseType.INCOME;
                    break;
            }
            Date date=new Date(res.getLong(0));
            String accountNo=res.getString(1);

            Transaction transaction = new Transaction(date, accountNo, type, res.getDouble(3));

            tr.add(transaction);
        }
        return tr;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        SQLiteDatabase db= DatabaseHelper.getDataBaseHelper().getWritableDatabase();
        Cursor res=db.rawQuery("SELECT * from Transact",null);

        int size = res.getCount();
        if (size <= limit) {
            ExpenseType type=null;
            List<Transaction> tr=new LinkedList<>();
            while(res.moveToNext()){
                switch (res.getInt(3)) {
                    case 0:
                        type=ExpenseType.EXPENSE;
                        break;
                    case 1:
                        type=ExpenseType.INCOME;
                        break;
                }
                Date date=new Date(res.getLong(1));
                String accountNo=res.getString(2);

                Transaction transaction = new Transaction(date, accountNo, type, res.getDouble(4));

                tr.add(transaction);
            }
            return tr;
        }
        Cursor ret=db.rawQuery("select * from "+ TableTrans +" where ID in (select ID from "+ TableTrans +" order by ID desc " +
                "limit " + limit + ")", null);
        ExpenseType type=null;
        List<Transaction> tr=new LinkedList<>();
        while(ret.moveToNext()){
            switch (ret.getInt(3)) {
                case 0:
                    type=ExpenseType.EXPENSE;
                    break;
                case 1:
                    type=ExpenseType.INCOME;
                    break;
            }
            Date date=new Date(ret.getLong(1));
            String accountNo=ret.getString(2);

            Transaction transaction = new Transaction(date, accountNo, type, ret.getDouble(4));

            tr.add(transaction);
        }
        return tr;

    }


}
