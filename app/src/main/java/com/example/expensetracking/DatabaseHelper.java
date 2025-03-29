package com.example.expensetracking;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.HashMap;

public class DatabaseHelper extends SQLiteOpenHelper {

    private Context context;
    private static final String DB_Name = "ExpensesManager.db";
    private static final int DB_Version = 4;

    private static final String Expenses_Table_Name = "Expenses";
    private static final String Expense_Id_Column_Name = "ID";
    private static final String Expense_Category_Column_Name = "Category";
    private static final String Expense_Time_Column_Name = "Time";
    private static final String Expense_Hour_Column_Name = "Time_Hour";
    private static final String Expense_Minute_Column_Name = "Time_Minute";
    private static final String Expense_Day_Column_Name = "Day";
    private static final String Expense_Month_Column_Name = "Month";
    private static final String Expense_Year_Column_Name = "Year";
    private static final String Expense_Amount_Column_Name = "Amount";
    private static final String Expense_IsFD_Column_Name = "IsFD";

    private static final String FD_Table_Name = "FDs";
    private static final String FD_Id_Column_Name = "ID";
    private static final String FD_Category_Column_Name = "Category";
    private static final String FD_Time_Column_Name = "Time";
    private static final String FD_Hour_Column_Name = "Time_Hour";
    private static final String FD_Minute_Column_Name = "Time_Minute";
    private static final String FD_Day_Column_Name = "Day";
    private static final String FD_Month_Column_Name = "Month";
    private static final String FD_Year_Column_Name = "Year";
    private static final String FD_Amount_Column_Name = "Amount";

    private static final String Bill_Table_Name = "Bills";
    private static final String Bill_Name_Column_Name = "Name";
    private static final String Bill_Id_Column_Name = "ID";
    private static final String Bill_Time_Column_Name = "Time";
    private static final String Bill_Hour_Column_Name = "Time_Hour";
    private static final String Bill_Minute_Column_Name = "Time_Minute";
    private static final String Bill_Day_Column_Name = "Day";
    private static final String Bill_Month_Column_Name = "Month";
    private static final String Bill_Year_Column_Name = "Year";
    private static final String Bill_Amount_Column_Name = "Amount";
    private static final String Bill_Frequency_Column_Name = "Frequency";


    public DatabaseHelper(@Nullable Context context) {
        super(context, DB_Name, null, DB_Version);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String queryCreate =
                "CREATE TABLE "+Expenses_Table_Name +
                        " ("+Expense_Id_Column_Name+" TEXT PRIMARY KEY,"+
                        Expense_Category_Column_Name+" TEXT,"+
                        Expense_Time_Column_Name+" TEXT,"+
                        Expense_Hour_Column_Name+" INTEGER,"+
                        Expense_Minute_Column_Name+" INTEGER,"+
                        Expense_Day_Column_Name+" INTEGER,"+
                        Expense_Month_Column_Name+" TEXT,"+
                        Expense_Year_Column_Name+" INTEGER,"+
                        Expense_Amount_Column_Name+" INTEGER,"+
                        Expense_IsFD_Column_Name+" INTEGER);";

        db.execSQL(queryCreate);

        String queryCreateFD =
                "CREATE TABLE "+FD_Table_Name +
                        " ("+FD_Id_Column_Name+" TEXT PRIMARY KEY,"+
                        FD_Category_Column_Name+" TEXT,"+
                        FD_Time_Column_Name+" TEXT,"+
                        FD_Hour_Column_Name+" INTEGER,"+
                        FD_Minute_Column_Name+" INTEGER,"+
                        FD_Day_Column_Name+" INTEGER,"+
                        FD_Month_Column_Name+" TEXT,"+
                        FD_Year_Column_Name+" INTEGER,"+
                        FD_Amount_Column_Name+" INTEGER);";

        db.execSQL(queryCreateFD);

        String queryCreateBill =
                "CREATE TABLE "+Bill_Table_Name +
                        " ("+Bill_Id_Column_Name+" TEXT PRIMARY KEY,"+
                        Bill_Name_Column_Name+" TEXT,"+
                        Bill_Time_Column_Name+" TEXT,"+
                        Bill_Hour_Column_Name+" INTEGER,"+
                        Bill_Minute_Column_Name+" INTEGER,"+
                        Bill_Day_Column_Name+" INTEGER,"+
                        Bill_Month_Column_Name+" TEXT,"+
                        Bill_Year_Column_Name+" INTEGER,"+
                        Bill_Amount_Column_Name+" INTEGER,"+
                        Bill_Frequency_Column_Name+" TEXT);";

        db.execSQL(queryCreateBill);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+Expenses_Table_Name);
        db.execSQL("DROP TABLE IF EXISTS "+FD_Table_Name);
        db.execSQL("DROP TABLE IF EXISTS "+Bill_Table_Name);
        onCreate(db);
    }

    public void addExpense(String Category,String Time,int Hour,int Minute,int Day,String Month,int Year,int Amount,boolean IsFD){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        HashMap<String,Integer> MonthsHash= new HashMap<>();

        MonthsHash.put("January", 0);
        MonthsHash.put("February", 1);
        MonthsHash.put("March", 2);
        MonthsHash.put("April", 3);
        MonthsHash.put("May", 4);
        MonthsHash.put("June", 5);
        MonthsHash.put("July", 6);
        MonthsHash.put("August", 7);
        MonthsHash.put("September", 8);
        MonthsHash.put("October", 9);
        MonthsHash.put("November", 10);
        MonthsHash.put("December", 11);

        String IDString = System.currentTimeMillis() + ""+Day+""+MonthsHash.getOrDefault(Month,0)+""+Year+"";

        cv.put(Expense_Id_Column_Name,IDString);
        cv.put(Expense_Category_Column_Name,Category);
        cv.put(Expense_Time_Column_Name,Time);
        cv.put(Expense_Hour_Column_Name,Hour);
        cv.put(Expense_Minute_Column_Name,Minute);
        cv.put(Expense_Day_Column_Name,Day);
        cv.put(Expense_Month_Column_Name,Month);
        cv.put(Expense_Year_Column_Name,Year);
        cv.put(Expense_Amount_Column_Name,Amount);

        if (IsFD){
            cv.put(Expense_IsFD_Column_Name,1);
        }else{cv.put(Expense_IsFD_Column_Name,0);}

        long result = db.insert(Expenses_Table_Name,null,cv);
        if (result == -1){
            Toast.makeText(context, "Failed to Enter Expense", Toast.LENGTH_SHORT).show();
        }
    }

    public void addFD(String Category,String Time,int Hour,int Minute,int Day,String Month,int Year,int Amount){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        HashMap<String,Integer> MonthsHash= new HashMap<>();

        MonthsHash.put("January", 0);
        MonthsHash.put("February", 1);
        MonthsHash.put("March", 2);
        MonthsHash.put("April", 3);
        MonthsHash.put("May", 4);
        MonthsHash.put("June", 5);
        MonthsHash.put("July", 6);
        MonthsHash.put("August", 7);
        MonthsHash.put("September", 8);
        MonthsHash.put("October", 9);
        MonthsHash.put("November", 10);
        MonthsHash.put("December", 11);

        String IDString = System.currentTimeMillis() + ""+Day+""+MonthsHash.getOrDefault(Month,0)+""+Year+"FD";

        cv.put(FD_Id_Column_Name,IDString);
        cv.put(FD_Category_Column_Name,Category);
        cv.put(FD_Time_Column_Name,Time);
        cv.put(FD_Hour_Column_Name,Hour);
        cv.put(FD_Minute_Column_Name,Minute);
        cv.put(FD_Day_Column_Name,Day);
        cv.put(FD_Month_Column_Name,Month);
        cv.put(FD_Year_Column_Name,Year);
        cv.put(FD_Amount_Column_Name,Amount);

        long result = db.insert(FD_Table_Name,null,cv);
        if (result == -1){
            Toast.makeText(context, "Failed to Enter Expense with FD", Toast.LENGTH_SHORT).show();
        }
    }

    public void addBill(String Name,String Time,int Hour,int Minute,int Day,String Month,int Year,int Amount,String Freq){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        HashMap<String,Integer> MonthsHash= new HashMap<>();

        MonthsHash.put("January", 0);
        MonthsHash.put("February", 1);
        MonthsHash.put("March", 2);
        MonthsHash.put("April", 3);
        MonthsHash.put("May", 4);
        MonthsHash.put("June", 5);
        MonthsHash.put("July", 6);
        MonthsHash.put("August", 7);
        MonthsHash.put("September", 8);
        MonthsHash.put("October", 9);
        MonthsHash.put("November", 10);
        MonthsHash.put("December", 11);

        String IDString = System.currentTimeMillis() + ""+Day+""+MonthsHash.getOrDefault(Month,0)+""+Year+"Bill";

        cv.put(Bill_Id_Column_Name,IDString);
        cv.put(Bill_Name_Column_Name,Name);
        cv.put(Bill_Time_Column_Name,Time);
        cv.put(Bill_Hour_Column_Name,Hour);
        cv.put(Bill_Minute_Column_Name,Minute);
        cv.put(Bill_Day_Column_Name,Day);
        cv.put(Bill_Month_Column_Name,Month);
        cv.put(Bill_Year_Column_Name,Year);
        cv.put(Bill_Amount_Column_Name,Amount);
        cv.put(Bill_Frequency_Column_Name,Freq);

        long result = db.insert(Bill_Table_Name,null,cv);
        if (result == -1){
            Toast.makeText(context, "Failed to Enter Bill", Toast.LENGTH_SHORT).show();
        }
    }

    public Cursor GetMonthlyData(String Month, int Year) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + Expenses_Table_Name + " WHERE "+Expense_Month_Column_Name+" = '"+Month+"' AND "+Expense_Year_Column_Name+" = '"+Year+"';";

        Cursor cursor = null;

        if (db != null) {
            cursor = db.rawQuery(query,null);
        }

        return cursor;
    }

    public Cursor GetDayData(String Month, int Year,int Day) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + Expenses_Table_Name + " WHERE "+Expense_Month_Column_Name+" = '"+Month+"' AND "+Expense_Year_Column_Name+" = '"+Year+"' AND "+Expense_Day_Column_Name+" = '"+Day+"';";

        Cursor cursor = null;

        if (db != null) {
            cursor = db.rawQuery(query,null);
        }

        return cursor;
    }

    public Cursor GetCategoryData(String Month, int Year, String Category) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + Expenses_Table_Name + " WHERE "+Expense_Month_Column_Name+" = '"+Month+"' AND "+Expense_Year_Column_Name+" = '"+Year+"' AND "+Expense_Category_Column_Name+" = '"+Category+"';";

        Cursor cursor = null;

        if (db != null) {
            cursor = db.rawQuery(query,null);
        }

        return cursor;
    }

    public Cursor GetBillData(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + Bill_Table_Name +" ;";

        Cursor cursor = null;

        if (db != null) {
            cursor = db.rawQuery(query,null);
        }

        return cursor;
    }

    public Cursor GetFDData(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + FD_Table_Name +" ;";

        Cursor cursor = null;

        if (db != null) {
            cursor = db.rawQuery(query,null);
        }

        return cursor;
    }

    public void DeleteBill(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(Bill_Table_Name,Bill_Id_Column_Name+"=?",new String[]{id});

        if (result == -1) {
            Toast.makeText(context, "Failed to delete Bill", Toast.LENGTH_SHORT).show();
        }else if(result == 0){
                Toast.makeText(context, "No such Bill found", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(context, "Bill Deleted", Toast.LENGTH_SHORT).show();
        }
    }

    public void DeleteFD(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(FD_Table_Name,FD_Id_Column_Name+"=?",new String[]{id});

        if (result == -1){
            Toast.makeText(context, "Failed to delete Fixed Deposit", Toast.LENGTH_SHORT).show();
        }else if(result == 0){
            Toast.makeText(context, "No such FD found", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(context, "Fixed Deposit Deleted", Toast.LENGTH_SHORT).show();
        }
    }
}