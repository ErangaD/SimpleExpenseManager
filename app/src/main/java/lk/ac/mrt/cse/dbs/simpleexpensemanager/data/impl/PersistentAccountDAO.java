package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcelable;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.DatabaseHelper;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

/**
 * Created by Eranga on 11/19/2016.
 */
public class PersistentAccountDAO implements AccountDAO,Serializable{

    @Override
    public List<String> getAccountNumbersList() {
        SQLiteDatabase db= DatabaseHelper.getDataBaseHelper().getWritableDatabase();
        ArrayList<String> accounts=new ArrayList<>();
        Cursor res=db.rawQuery("select Account_No from Account",null);
        while(res.moveToNext()){
            accounts.add((res.getString(0)));
        }
        return accounts;
    }

    @Override
    public List<Account> getAccountsList() {
        SQLiteDatabase db= DatabaseHelper.getDataBaseHelper().getWritableDatabase();
        ArrayList<Account> accounts=new ArrayList<>();
        Cursor res=db.rawQuery("select * from Account",null);
        while(res.moveToNext()){
            accounts.add(new Account(res.getString(0),res.getString(1),res.getString(2),res.getDouble(3)));
        }
        return accounts;
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase db= DatabaseHelper.getDataBaseHelper().getWritableDatabase();
        Cursor res=db.rawQuery("select Account_No,Bank,Account_holder,Balance from Account where Account_No = ?",new String[]{accountNo});
        if(res.getCount()==0){
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }
        else{
            return new Account(res.getString(0),res.getString(1),res.getString(2),res.getDouble(3));
        }


    }

    @Override
    public void addAccount(Account account) {
        SQLiteDatabase db= DatabaseHelper.getDataBaseHelper().getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put("Account_No",account.getAccountNo());
        contentValues.put("Bank",account.getBankName());
        contentValues.put("Account_holder",account.getAccountHolderName());
        contentValues.put("Balance",account.getBalance());
        db.insert("Account",null,contentValues);
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase db= DatabaseHelper.getDataBaseHelper().getWritableDatabase();
        if(!(db.delete("Account","Account_No="+accountNo,null)>0)){
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        SQLiteDatabase db= DatabaseHelper.getDataBaseHelper().getWritableDatabase();
        Cursor res=db.rawQuery("select * from Account where Account_No=?",new String[]{accountNo});
        if(res.getCount()>0){
            while(res.moveToNext()){
                ContentValues cv = new ContentValues();
                double bal=0;
                switch (expenseType) {
                    case EXPENSE:
                        bal=res.getDouble(3)- amount;
                        break;
                    case INCOME:
                        bal=res.getDouble(3)+ amount;
                        break;
                }
                cv.put("Balance",bal);
                Log.w("myApp", res.getString(0) + "");
                db.update("Account", cv, "Account_no ='" + accountNo+"'", null);
            }

        }
        else{
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }

    }
}
