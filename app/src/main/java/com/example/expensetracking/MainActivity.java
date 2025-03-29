package com.example.expensetracking;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Guideline;
import androidx.core.content.ContextCompat;

import java.text.*;
import java.util.*;

public class MainActivity extends AppCompatActivity {
    DatabaseHelper dbh;

    ConstraintLayout BillCell;
    ConstraintLayout FDCell;

    String CurrentMonthAnal;
    int CurrentYearAnal;

    boolean IsComparing = false;

    int CompGroceriesBar = 0;
    int CompFoodBar = 0;
    int CompElectronicsBar = 0;
    int CompMaintenanceBar = 0;
    int CompMiscBar = 0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        dbh = new DatabaseHelper(MainActivity.this);

        ToHome(null);
    }

    public void go_to_second(View view){

    }

    boolean SetBillFD = false;
    int SetBillAmount = 0;

    public void Load_Monthly_Main_Page(){
        SimpleDateFormat SDFM = new SimpleDateFormat("MMMM");
        String Month = SDFM.format(new Date());

        SimpleDateFormat SDFY = new SimpleDateFormat("yyyy");
        String Year = SDFY.format(new Date());

        SimpleDateFormat SDFD = new SimpleDateFormat("dd");
        String Day = SDFD.format(new Date());

        int Amt = Load_Total_Month_Amount(Month,Integer.parseInt(Year));
        int AmtDay = Load_Total_Day_Amount(Month,Integer.parseInt(Year),Integer.parseInt(Day));

        DecimalFormat NF = new DecimalFormat("##,##,###");
        String AmtForm = NF.format(Amt);
        String AmtFormDay = NF.format(AmtDay);

        ((TextView) findViewById(R.id.MonthExpense)).setText("₹ "+AmtForm);
        ((TextView) findViewById(R.id.DailyExpense)).setText("₹ "+AmtFormDay);
    }


    // Set various Content views

    public void ToHome(View view){
        setContentView(R.layout.activity_main);

        SimpleDateFormat SDFM = new SimpleDateFormat("MMMM");
        String Month = SDFM.format(new Date());

        SimpleDateFormat SDFY = new SimpleDateFormat("yyyy");
        String Year = SDFY.format(new Date());

        SimpleDateFormat SDFD = new SimpleDateFormat("dd");
        String Day = SDFD.format(new Date());

        int Amt = Load_Total_Month_Amount(Month,Integer.parseInt(Year));
        int AmtDay = Load_Total_Day_Amount(Month,Integer.parseInt(Year),Integer.parseInt(Day));

        DecimalFormat NF = new DecimalFormat("##,##,###");
        String AmtForm = NF.format(Amt);
        String AmtFormDay = NF.format(AmtDay);

        ((TextView) findViewById(R.id.MonthExpense)).setText("₹ "+AmtForm);
        ((TextView) findViewById(R.id.DailyExpense)).setText("₹ "+AmtFormDay);
    }

    public void ToAnalytics(View view){
        SimpleDateFormat SDFM = new SimpleDateFormat("MMMM");
        String Month = SDFM.format(new Date());

        SimpleDateFormat SDFY = new SimpleDateFormat("yyyy");
        String Year = SDFY.format(new Date());

        CurrentMonthAnal = Month;
        CurrentYearAnal = Integer.parseInt(Year);

        IsComparing = false;

        setContentView(R.layout.analytics);
        Load_MonthlyCells(view);


        int Groceries_A = Analytics_Get_Bar("Groceries");
        int Food_A = Analytics_Get_Bar("Food");
        int Electronics_A = Analytics_Get_Bar("Electronics");
        int Maintenance_A = Analytics_Get_Bar("Maintenance");
        int Misc_A = Analytics_Get_Bar("Misc");
        
        Load_analytics_Bar(Groceries_A,Food_A,Electronics_A,Maintenance_A,Misc_A);
        
        SimpleDateFormat SDF = new SimpleDateFormat("MMMM yyyy");
        String Date = SDF.format(new Date());

        ((TextView) findViewById(R.id.CurrentMonthNYear)).setText(Date);
    }
    
    public int Analytics_Get_Bar(String Category){
        int Category_A = 0;
        
        Cursor cursor = dbh.GetCategoryData(CurrentMonthAnal,CurrentYearAnal,Category);

        if (cursor.getCount() == 0 || cursor == null){
            Toast.makeText(this, "No Data Found", Toast.LENGTH_SHORT).show();
        }else{
            while (cursor.moveToNext()){
                Category_A += Integer.parseInt(cursor.getString(8));
            }
        }
        return Category_A;
    }

    public void ToBills(View view){
        setContentView(R.layout.bills);
        Load_Bills();
    }

    public void ToFDs(View view){
        setContentView(R.layout.fixed_deposits);
        Load_FDs();
    }

    public void Load_Bills(){
        HashMap<String,Integer> Frequencies = new HashMap<>();

        Frequencies.put("Monthly",0);
        Frequencies.put("Quarterly",1);
        Frequencies.put("Yearly",2);

        Cursor cursor = dbh.GetBillData();

        if (cursor.getCount() == 0 || cursor == null){
            Toast.makeText(this, "No Data Found", Toast.LENGTH_SHORT).show();
        }else{
            while (cursor.moveToNext()){
                Add_BillCell(cursor.getString(1),Integer.parseInt(cursor.getString(8)),cursor.getString(5)+" "+cursor.getString(6)+" "+cursor.getString(7),Frequencies.getOrDefault(cursor.getString(9),0),cursor.getString(0));
            }
        }
    }

    public void Load_FDs(){
        HashMap<String,Integer> Categories = new HashMap<>();

        Categories.put("Groceries",0);
        Categories.put("Food",1);
        Categories.put("Electronics",2);
        Categories.put("Maintenance",3);
        Categories.put("Bill",4);
        Categories.put("Misc",5);

        Cursor cursor = dbh.GetFDData();

        if (cursor.getCount() == 0 || cursor == null){
            Toast.makeText(this, "No Data Found", Toast.LENGTH_SHORT).show();
        }else{
            while (cursor.moveToNext()){
                Add_FDCell(Categories.getOrDefault(cursor.getString(1),5),Integer.parseInt(cursor.getString(8)),cursor.getString(5)+" "+cursor.getString(6)+" "+cursor.getString(7),cursor.getString(0));
            }
        }
    }

    // Load Monthly Expenses for analysis
    public void Load_MonthlyCells(View view){
        if (!IsComparing){
            UnLoad_Cells(findViewById(R.id.linlay));
            Unload_BarTint();

            int MonthlyAmountTotal = Load_Total_Month_Amount(CurrentMonthAnal,CurrentYearAnal);
            DecimalFormat DF = new DecimalFormat("##,##,###");
            String FormattedAmount = DF.format(MonthlyAmountTotal);

            ((TextView) findViewById(R.id.AnalyTxt1Amt)).setText("₹ "+FormattedAmount);

            TextView AnalyText2 = findViewById(R.id.AnalyTxt2);
            AnalyText2.setText(R.string.Dailyavg);

            SimpleDateFormat SDFD = new SimpleDateFormat("dd");
            SimpleDateFormat SDFM = new SimpleDateFormat("MMMM");
            SimpleDateFormat SDFY = new SimpleDateFormat("yyyy");

            String CurrentDay = SDFD.format(new Date());
            String CurrentMonth = SDFM.format(new Date());
            String CurrentYear = SDFY.format(new Date());

            HashMap<String,Integer> CategoriesNum = new HashMap<>();
            CategoriesNum.put("Groceries",0);
            CategoriesNum.put("Food",1);
            CategoriesNum.put("Electronics",2);
            CategoriesNum.put("Maintenance",3);
            CategoriesNum.put("Bill",4);
            CategoriesNum.put("Misc",5);

            HashMap<String,Integer> MonthsDays = new HashMap<>();
            MonthsDays.put("January",31);
            if (Integer.parseInt(CurrentYear) % 4 == 0){
                MonthsDays.put("February",29);
            }else{
                MonthsDays.put("February",28);
            }
            MonthsDays.put("March",31);
            MonthsDays.put("April",30);
            MonthsDays.put("May",31);
            MonthsDays.put("June",30);
            MonthsDays.put("July",31);
            MonthsDays.put("August",31);
            MonthsDays.put("September",30);
            MonthsDays.put("October",31);
            MonthsDays.put("November",30);
            MonthsDays.put("December",31);

            int DailyAvg;

            if (CurrentMonth.matches(CurrentMonthAnal)){
                DailyAvg = MonthlyAmountTotal/Integer.parseInt(CurrentDay);
            }else{
                DailyAvg = MonthlyAmountTotal/MonthsDays.getOrDefault(CurrentMonthAnal,30);
            }

            ((TextView) findViewById(R.id.AnalyTxt2Amt)).setText("₹ "+DF.format(DailyAvg));

            Cursor cursor = dbh.GetMonthlyData(CurrentMonthAnal,CurrentYearAnal);

            if (cursor.getCount() == 0 || cursor == null){
                Toast.makeText(this, "No Data Found", Toast.LENGTH_SHORT).show();
            }else{
                while (cursor.moveToNext()){
                    Add_ExpCellDaily(CategoriesNum.getOrDefault(cursor.getString(1),5),Integer.parseInt(cursor.getString(8)),cursor.getString(2),cursor.getString(5),false);
                }
            }
        }
    }

    public int Load_Total_Month_Amount(String Month,int Year){
        Cursor cursor = dbh.GetMonthlyData(Month,Year);

        int Amount = 0;

        if (cursor.getCount() == 0 || cursor == null){
            //Toast.makeText(this, "No Data Found", Toast.LENGTH_SHORT).show();
        }else{
            while (cursor.moveToNext()){
                Amount += Integer.parseInt(cursor.getString(8));
            }
        }
        return Amount;
    }

    public int Load_Total_Day_Amount(String Month,int Year,int Day){
        Cursor cursor = dbh.GetDayData(Month,Year,Day);

        int Amount = 0;

        if (cursor.getCount() == 0 || cursor == null){
            //Toast.makeText(this, "No Data Found", Toast.LENGTH_SHORT).show();
        }else{
            while (cursor.moveToNext()){
                Amount += Integer.parseInt(cursor.getString(8));
            }
        }
        return Amount;
    }

    public int Load_Total_Category_Amount(String Month,int Year,String Category){
        Cursor cursor = dbh.GetCategoryData(Month,Year,Category);

        int Amount = 0;

        if (cursor.getCount() == 0 || cursor == null){
            //Toast.makeText(this, "No Data Found", Toast.LENGTH_SHORT).show();
        }else{
            while (cursor.moveToNext()){
                Amount += Integer.parseInt(cursor.getString(8));
            }
        }
        return Amount;
    }

    // Disable the background tint applied when selecting the bar in analytics
    public void Unload_BarTint(){
        ConstraintLayout GraphLay = findViewById(R.id.GraphLay);

        for (int i = 0; i < GraphLay.getChildCount(); i++) {
            View getChild = GraphLay.getChildAt(i);

            getChild.setBackgroundColor(Color.parseColor("#00000000"));
        }
    }

    public void Load_CategoryCells(View view){
        if (!IsComparing){
            UnLoad_Cells(findViewById(R.id.linlay));
            Unload_BarTint();

            TextView AnalyText2 = findViewById(R.id.AnalyTxt2);
            AnalyText2.setText(R.string.Cateexp);

            TextView AnalyText2Amt = findViewById(R.id.AnalyTxt2Amt);
            DecimalFormat DF = new DecimalFormat("##,##,###");

            view.setBackgroundColor(Color.parseColor("#7F000000"));

            HashMap<String, Integer> Cates = new HashMap<>();

            Cates.put("Groceries", 0);
            Cates.put("Food", 1);
            Cates.put("Electronics", 2);
            Cates.put("Maintenance", 3);
            Cates.put("Misc", 5);

            String ViewID = getResources().getResourceEntryName(view.getId());
            AnalyText2Amt.setText("₹ "+DF.format(Load_Total_Category_Amount(CurrentMonthAnal,CurrentYearAnal,ViewID)));

            Cursor cursor = dbh.GetCategoryData(CurrentMonthAnal,CurrentYearAnal,ViewID);

            if (cursor.getCount() == 0 || cursor == null){
                Toast.makeText(this, "No Data Found", Toast.LENGTH_SHORT).show();
            }else{
                while (cursor.moveToNext()){
                    Add_ExpCellDaily(Cates.getOrDefault(cursor.getString(1),5),Integer.parseInt(cursor.getString(8)),cursor.getString(2),cursor.getString(5),false);
                }
            }
        }
    }

    // Unload cells
    public void UnLoad_Cells(LinearLayout linlay){
        linlay.removeAllViews();
    }

    // Unfocus Menus
    public void unfocusAddExp(){
        EditText AmountText = findViewById(R.id.Amount);
        Spinner Category = findViewById(R.id.Category);
        CheckBox FDCheck = findViewById(R.id.FDCheckBox);

        AmountText.setText("");
        Category.setSelection(0);
        FDCheck.setChecked(false);
        AmountText.clearFocus();

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(AmountText.getWindowToken(), 0);
    }

    public void unfocusAddBill(){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(findViewById(R.id.EnterBillsName).getWindowToken(), 0);

        Spinner FreqSpin = findViewById(R.id.FrequencySpinner);
        FreqSpin.setSelection(0);

        TextView BillName = findViewById(R.id.EnterBillsName);
        BillName.setText("");
    }

    // Add in Expense
    public void AddExpense(View view){
        EditText AmountText = findViewById(R.id.Amount);
        Spinner Category = findViewById(R.id.Category);
        CheckBox FDCheck = findViewById(R.id.FDCheckBox);

        boolean CheckBox_Value = FDCheck.isChecked();
        String CheckBox_ValueString = (CheckBox_Value) ? "True":"False";

        int ExpAmount;
        String ExpCategory;

        String[] Expense = {AmountText.getText().toString(), Category.getSelectedItem().toString(),CheckBox_ValueString};

        if (Expense[0].isEmpty() && Category.getSelectedItem().toString().equals("Category")){
            unfocusAddExp();
        }
        else if (Category.getSelectedItem().toString().equals("Category")) {
            unfocusAddExp();
            Toast.makeText(this,"Select Category!",Toast.LENGTH_SHORT).show();
        }
        else if (Expense[0].isEmpty()) {
            unfocusAddExp();
            Toast.makeText(this,"Enter Amount!",Toast.LENGTH_SHORT).show();
        }
        else if (Category.getSelectedItem().toString().equals("Bill")) {
            View[] AddBillComponents = {findViewById(R.id.GoBackBill),
                    findViewById(R.id.AddBIll),
                    findViewById(R.id.FrequencySpinner),
                    findViewById(R.id.textView3),
                    findViewById(R.id.textView2),
                    findViewById(R.id.SecondaryMenuTitleText),
                    findViewById(R.id.EnterBillsName),
                    findViewById(R.id.SecondaryMenuTitle),
                    findViewById(R.id.lightBGBill2),
                    findViewById(R.id.LightBGBill1),
                    findViewById(R.id.SecondaryMenuBox),
                    findViewById(R.id.SecondaryMenuLayer)};

            SecondaryMenuActions SMA = new SecondaryMenuActions();
            SMA.EnableMenu(AddBillComponents);
            unfocusAddExp();

            SetBillAmount = Integer.parseInt(Expense[0]);
            SetBillFD = CheckBox_Value;

            Button AddButton = findViewById(R.id.Add_Expense);

            AmountText.setFocusable(false);
            Category.setEnabled(false);
            FDCheck.setEnabled(false);
            AddButton.setClickable(false);
        }
        else{
            ExpAmount = Integer.parseInt(Expense[0]);
            ExpCategory = Expense[1];
            unfocusAddExp();

            if (CheckBox_Value){
                Toast.makeText(this,"Expense Added with FD",Toast.LENGTH_SHORT).show();

                Date now = new Date();

                SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
                SimpleDateFormat hourFormat = new SimpleDateFormat("hh");
                SimpleDateFormat minuteFormat = new SimpleDateFormat("mm");
                SimpleDateFormat dayFormat = new SimpleDateFormat("dd");
                SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM");
                SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");

                // Format each component using the current date
                String time = timeFormat.format(now);
                String hour = hourFormat.format(now);
                String minute = minuteFormat.format(now);
                String day = dayFormat.format(now);
                String month = monthFormat.format(now);
                String year = yearFormat.format(now);

                DatabaseHelper db = new DatabaseHelper(this);
                db.addExpense(ExpCategory,time,Integer.parseInt(hour),Integer.parseInt(minute),Integer.parseInt(day),month,Integer.parseInt(year),ExpAmount,true);
                db.addFD(ExpCategory,time,Integer.parseInt(hour),Integer.parseInt(minute),Integer.parseInt(day),month,Integer.parseInt(year),ExpAmount);
            }
            else {
                Toast.makeText(this,"Expense Added",Toast.LENGTH_SHORT).show();

                Date now = new Date();

                SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
                SimpleDateFormat hourFormat = new SimpleDateFormat("hh");
                SimpleDateFormat minuteFormat = new SimpleDateFormat("mm");
                SimpleDateFormat dayFormat = new SimpleDateFormat("dd");
                SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM");
                SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");

                // Format each component using the current date
                String time = timeFormat.format(now);
                String hour = hourFormat.format(now);
                String minute = minuteFormat.format(now);
                String day = dayFormat.format(now);
                String month = monthFormat.format(now);
                String year = yearFormat.format(now);

                DatabaseHelper db = new DatabaseHelper(this);
                db.addExpense(ExpCategory,time,Integer.parseInt(hour),Integer.parseInt(minute),Integer.parseInt(day),month,Integer.parseInt(year),ExpAmount,false);
            }
        }
        Load_Monthly_Main_Page();
    }

    public void AddBill(View view){
        EditText Name = findViewById(R.id.EnterBillsName);
        Spinner Frequency = findViewById(R.id.FrequencySpinner);

        int BillAmount;
        String BillFrequency;
        Date now = new Date();

        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
        SimpleDateFormat hourFormat = new SimpleDateFormat("hh");
        SimpleDateFormat minuteFormat = new SimpleDateFormat("mm");
        SimpleDateFormat dayFormat = new SimpleDateFormat("dd");
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM");
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");

        // Format each component using the current date
        String time = timeFormat.format(now);
        String hour = hourFormat.format(now);
        String minute = minuteFormat.format(now);
        String day = dayFormat.format(now);
        String month = monthFormat.format(now);
        String year = yearFormat.format(now);

        if (Name.getText().toString().isEmpty()){
            Toast.makeText(this,"Enter Bill Name!",Toast.LENGTH_SHORT).show();

            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(findViewById(R.id.EnterBillsName).getWindowToken(), 0);
        }
        else {
            if (SetBillFD){
                Toast.makeText(this,"Bill Added with FD",Toast.LENGTH_SHORT).show();
                DatabaseHelper db = new DatabaseHelper(this);
                db.addFD("Bill",time,Integer.parseInt(hour),Integer.parseInt(minute),Integer.parseInt(day),month,Integer.parseInt(year),SetBillAmount);
            }
            else {
                Toast.makeText(this,"Bill Added",Toast.LENGTH_SHORT).show();
            }

            BillAmount = SetBillAmount;
            BillFrequency = Frequency.getSelectedItem().toString();

            DatabaseHelper db = new DatabaseHelper(this);
            db.addBill(Name.getText().toString(),time,Integer.parseInt(hour),Integer.parseInt(minute),Integer.parseInt(day),month,Integer.parseInt(year),BillAmount,BillFrequency);

            DisableAddBill(view);

            SetBillFD = false;
            SetBillAmount = 0;
        }
    }

    // Disable Secondary Menu

    public void DisableAddBill(View view){
        View[] AddBillComponents = {findViewById(R.id.GoBackBill),
                findViewById(R.id.AddBIll),
                findViewById(R.id.FrequencySpinner),
                findViewById(R.id.textView3),
                findViewById(R.id.textView2),
                findViewById(R.id.SecondaryMenuTitleText),
                findViewById(R.id.EnterBillsName),
                findViewById(R.id.SecondaryMenuTitle),
                findViewById(R.id.lightBGBill2),
                findViewById(R.id.LightBGBill1),
                findViewById(R.id.SecondaryMenuBox),
                findViewById(R.id.SecondaryMenuLayer)};

        unfocusAddBill();

        SecondaryMenuActions SMA = new SecondaryMenuActions();
        SMA.DisableMenu(AddBillComponents);

        SetBillFD = false;
        SetBillAmount = 0;

        EditText AmountText = findViewById(R.id.Amount);
        Spinner Category = findViewById(R.id.Category);
        CheckBox FDCheck = findViewById(R.id.FDCheckBox);
        Button AddButton = findViewById(R.id.Add_Expense);

        AmountText.setFocusable(true);
        AmountText.setFocusableInTouchMode(true);
        Category.setEnabled(true);
        FDCheck.setEnabled(true);
        AddButton.setClickable(true);
    }

    // Add Expense Entries

    public void Add_ExpCellDaily(int cate , int Amount , String Time , String Day , boolean IsFD){
        Object[][] CategoriesWColors = {{getString(R.string.Groceries),ContextCompat.getColor(this,R.color.Groceries)},
                {getString(R.string.Food),ContextCompat.getColor(this,R.color.Food)},
                {getString(R.string.Electronics),ContextCompat.getColor(this,R.color.Electronics)},
                {getString(R.string.Maintenance),ContextCompat.getColor(this,R.color.Maintenance)},
                {getString(R.string.Bill),ContextCompat.getColor(this,R.color.Bill)},
                {getString(R.string.Misc),ContextCompat.getColor(this,R.color.Misc)}};

        LinearLayout LinLay = findViewById(R.id.linlay);

        LayoutInflater infl = LayoutInflater.from(this);
        ConstraintLayout cloned = (ConstraintLayout) infl.inflate(R.layout.analytics_cell_template,LinLay,false);

        int CategoryColor = (int) CategoriesWColors[cate][1];
        String Category = (String) CategoriesWColors[cate][0];

        DecimalFormat format2 = new DecimalFormat("##,##,###");
        String AmountFormatted = format2.format(Amount);

        Button CC = cloned.findViewById(R.id.CategoryColor);
        TextView CCT = cloned.findViewById(R.id.CategoryText);
        TextView Amt = cloned.findViewById(R.id.EntryAmount);
        TextView Ti = cloned.findViewById(R.id.EntryTime);
        TextView Da = cloned.findViewById(R.id.ExpenseDay);

        CC.setBackgroundColor(CategoryColor);
        CCT.setText(Category);
        Amt.setText("₹ "+AmountFormatted);
        Ti.setText(Time);
        Da.setText(Day);

        if (IsFD){
            CC.setBackgroundColor((int) CategoriesWColors[4][1]);
            CCT.setText(Category+" "+getString(R.string.FD));
            CCT.setTextSize(14);
        }
        LinLay.addView(cloned,0);
    }

    public void Add_BillCell(String name , int Amount , String Time , int Frequency , String id){
        Object[] frequencies = {"Monthly","Quarterly","Yearly"};

        LinearLayout LinLay = findViewById(R.id.BillsList);

        LayoutInflater infl = LayoutInflater.from(this);
        ConstraintLayout cloned = (ConstraintLayout) infl.inflate(R.layout.bills_cells_template,LinLay,false);

        DecimalFormat format2 = new DecimalFormat("##,##,###");
        String AmountFormatted = format2.format(Amount);

        cloned.setTag(id);

        TextView BillName = cloned.findViewById(R.id.BillText);
        TextView Amt = cloned.findViewById(R.id.BillAmount);
        TextView Ti = cloned.findViewById(R.id.EntryDate);
        TextView Fre = cloned.findViewById(R.id.Frequency);

        BillName.setText(name);
        Amt.setText("₹ "+AmountFormatted);
        Ti.setText(Time);
        Fre.setText((String) frequencies[Frequency]);

        LinLay.addView(cloned,0);
    }

    public void Add_FDCell(int cate , int Amount , String Date , String id){
        Object[][] CategoriesWColors = {{getString(R.string.Groceries),ContextCompat.getColor(this,R.color.Groceries)},
                {getString(R.string.Food),ContextCompat.getColor(this,R.color.Food)},
                {getString(R.string.Electronics),ContextCompat.getColor(this,R.color.Electronics)},
                {getString(R.string.Maintenance),ContextCompat.getColor(this,R.color.Maintenance)},
                {getString(R.string.Bill),ContextCompat.getColor(this,R.color.Bill)},
                {getString(R.string.Misc),ContextCompat.getColor(this,R.color.Misc)}};

        LinearLayout LinLay = findViewById(R.id.FDList);

        LayoutInflater infl = LayoutInflater.from(this);
        ConstraintLayout cloned = (ConstraintLayout) infl.inflate(R.layout.fd_cells_template,LinLay,false);

        int CategoryColor = (int) CategoriesWColors[cate][1];
        String Category = (String) CategoriesWColors[cate][0];

        DecimalFormat format2 = new DecimalFormat("##,##,###");
        String AmountFormatted = format2.format(Amount);

        cloned.setTag(id);

        Button CC = cloned.findViewById(R.id.FDCategoryColor);
        TextView CCT = cloned.findViewById(R.id.FDCategoryText);
        TextView Amt = cloned.findViewById(R.id.FDAmount);
        TextView Ti = cloned.findViewById(R.id.FDDate);

        CC.setBackgroundColor(CategoryColor);
        Amt.setText("₹ "+AmountFormatted);
        Ti.setText(Date);

        CCT.setText(Category+" "+getString(R.string.FD));
        CCT.setTextSize(14);

        LinLay.addView(cloned,0);
    }

    public void Load_analytics_Bar(int Groceries,int Food,int Electronics,int Maintenance,int Misc){
        int Max_Amt = GetAbbreviatedAmount.NextLargeNumber(Math.max(Groceries,Math.max(Food,Math.max(Electronics,Math.max(Maintenance,Misc)))));

        TextView amt1 = findViewById(R.id.AnalAmt1);
        TextView amt2 = findViewById(R.id.AnalAmt2);
        TextView amt3 = findViewById(R.id.AnalAmt3);
        TextView amt4 = findViewById(R.id.AnalAmt4);
        TextView amt5 = findViewById(R.id.AnalAmt5);

        amt1.setText(GetAbbreviatedAmount.AbbAmount(Max_Amt)+" -");
        amt2.setText(GetAbbreviatedAmount.AbbAmount((Max_Amt/5)*4)+" -");
        amt3.setText(GetAbbreviatedAmount.AbbAmount((Max_Amt/5)*3)+" -");
        amt4.setText(GetAbbreviatedAmount.AbbAmount((Max_Amt/5)*2)+" -");
        amt5.setText(GetAbbreviatedAmount.AbbAmount((Max_Amt/5)*1)+" -");

        Guideline GuideGroceries = findViewById(R.id.GroceriesGuideline);
        Guideline GuideFood = findViewById(R.id.FoodGuideline);
        Guideline GuideElectronics = findViewById(R.id.ElectronicsGuideline);
        Guideline GuideMain = findViewById(R.id.MaintenanceGuideline);
        Guideline GuideMisc = findViewById(R.id.MiscGuideline);

        GuideGroceries.setGuidelinePercent(1-((((float) Groceries / Max_Amt)*97)/100));
        GuideFood.setGuidelinePercent(1-((((float) Food / Max_Amt)*97)/100));
        GuideElectronics.setGuidelinePercent(1-((((float) Electronics / Max_Amt)*97)/100));
        GuideMain.setGuidelinePercent(1-((((float) Maintenance / Max_Amt)*97)/100));
        GuideMisc.setGuidelinePercent(1-((((float) Misc / Max_Amt)*97)/100));
    }

    public void ActivateDeletionBill(View view){
        BillCell = (ConstraintLayout) view.getParent();
        ConstraintLayout BillRemovalMenu = findViewById(R.id.BillDeletionAssuranceMenu);
        TextView BillName = findViewById(R.id.Bill_Name_Del);

        TextView Bill_Name = BillCell.findViewById(R.id.BillText);

        BillName.setText(Bill_Name.getText());

        BillRemovalMenu.setVisibility(View.VISIBLE);
    }

    public void DeleteBill(View view){
        LinearLayout CellParent = (LinearLayout) BillCell.getParent();
        View viewtag = CellParent.findViewWithTag(BillCell.getTag());

        CellParent.removeAllViews();
        DisableBillDeletion(view);
        dbh.DeleteBill(viewtag.getTag().toString());

        Load_Bills();
    }

    public void DisableBillDeletion (View view){
        ConstraintLayout BillRemovalMenu = findViewById(R.id.BillDeletionAssuranceMenu);
        BillRemovalMenu.setVisibility(View.INVISIBLE);
    }

    public void ActivateDeletionFD(View view){
        FDCell = (ConstraintLayout) view.getParent();
        ConstraintLayout FDRemovalMenu = findViewById(R.id.FDDeletionAssuranceMenu);

        FDRemovalMenu.setVisibility(View.VISIBLE);
    }

    public void DeleteFD(View view){
        LinearLayout CellParent = (LinearLayout) FDCell.getParent();
        View viewtag = CellParent.findViewWithTag(FDCell.getTag());

        CellParent.removeAllViews();
        DisableFDDeletion(view);
        dbh.DeleteFD(viewtag.getTag().toString());

        Load_FDs();
    }

    public void DisableFDDeletion (View view){
        ConstraintLayout FDRemovalMenu = findViewById(R.id.FDDeletionAssuranceMenu);
        FDRemovalMenu.setVisibility(View.INVISIBLE);
    }

    public void GoToCompareM(View view){
        AnalMenuDisable(view);

        HashMap<String,Integer> Months = new HashMap<>();

        Months.put("January", 0);
        Months.put("February", 1);
        Months.put("March", 2);
        Months.put("April", 3);
        Months.put("May", 4);
        Months.put("June", 5);
        Months.put("July", 6);
        Months.put("August", 7);
        Months.put("September", 8);
        Months.put("October", 9);
        Months.put("November", 10);
        Months.put("December", 11);

        SimpleDateFormat SDF = new SimpleDateFormat("MMMM");
        String Month = SDF.format(new Date());

        SimpleDateFormat SDF2 = new SimpleDateFormat("yyyy");
        String Year = SDF2.format(new Date());

        int MntInt = ((Months.getOrDefault(Month,0)-1)>-1) ? (Months.getOrDefault(Month,0))-1 : 11;
        int PrevYear = ((Months.getOrDefault(Month,0)-1)>-1) ? Integer.parseInt(Year) : Integer.parseInt(Year)-1;


        Spinner MonthSpinner = findViewById(R.id.MonthSelectSpinnerCompare);
        MonthSpinner.setSelection(MntInt);

        TextView YearText = findViewById(R.id.YearSelectNumberCompare);
        YearText.setText(PrevYear+"");

        CmprMnthEnbl(view);
    }

    public void GoToMonthM(View view){
        AnalMenuDisable(view);

        HashMap<String,Integer> Months = new HashMap<>();

        Months.put("January", 0);
        Months.put("February", 1);
        Months.put("March", 2);
        Months.put("April", 3);
        Months.put("May", 4);
        Months.put("June", 5);
        Months.put("July", 6);
        Months.put("August", 7);
        Months.put("September", 8);
        Months.put("October", 9);
        Months.put("November", 10);
        Months.put("December", 11);

        SimpleDateFormat SDF = new SimpleDateFormat("MMMM");
        String Month = SDF.format(new Date());

        SimpleDateFormat SDF2 = new SimpleDateFormat("yyyy");
        String Year = SDF2.format(new Date());

        Spinner MonthSpinner = findViewById(R.id.MonthSelectSpinner);
        MonthSpinner.setSelection(Months.getOrDefault(Month,0));

        TextView YearText = findViewById(R.id.YearSelectNumber);
        YearText.setText(Year);

        SlctMnthEnbl(view);
    }

    public void AnalMenuDisable(View view){
        ConstraintLayout AnalMenu = findViewById(R.id.AnalyticsMenu);
        AnalMenu.setVisibility(View.INVISIBLE);
    }

    public void AnalMenuEnable(View view){
        ConstraintLayout AnalMenu = findViewById(R.id.AnalyticsMenu);
        AnalMenu.setVisibility(View.VISIBLE);
    }

    public void SlctMnthEnbl(View view){
        ConstraintLayout AnalMenu = findViewById(R.id.ChooseMonthMenu);
        AnalMenu.setVisibility(View.VISIBLE);
    }

    public void CmprMnthEnbl(View view){
        ConstraintLayout AnalMenu = findViewById(R.id.CompareMonthMenu);
        AnalMenu.setVisibility(View.VISIBLE);
    }

    public void MenuAnalDisable(View view){
        ConstraintLayout Menu = (ConstraintLayout) view.getParent();
        ConstraintLayout AnalMenu = findViewById(R.id.AnalyticsMenu);
        AnalMenu.setVisibility(View.VISIBLE);
        Menu.setVisibility(View.INVISIBLE);

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(findViewById(R.id.YearSelectNumber).getWindowToken(), 0);
        imm.hideSoftInputFromWindow(findViewById(R.id.YearSelectNumberCompare).getWindowToken(), 0);
    }

    public void MonthSelected(View view){
        findViewById(R.id.ChooseMonthMenu).setVisibility(View.INVISIBLE);

        Spinner MonthSpinner = findViewById(R.id.MonthSelectSpinner);
        String Month = MonthSpinner.getSelectedItem().toString();

        TextView YearText = findViewById(R.id.YearSelectNumber);
        int Year = Integer.parseInt(YearText.getText().toString());

        String Date = Month+" "+Year;

        ((TextView) findViewById(R.id.CurrentMonthNYear)).setText(Date);

        int TextColor = ((TextView) findViewById(R.id.AnalyTxt1)).getCurrentTextColor();

        ((TextView) findViewById(R.id.CurrentMonthNYear)).setTextColor(TextColor);

        ((TextView) findViewById(R.id.AnalyTxt1)).setText(getResources().getText(R.string.Monthsexp));
        ((TextView) findViewById(R.id.AnalyTxt2)).setText(getResources().getText(R.string.Dailyavg));

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(findViewById(R.id.YearSelectNumber).getWindowToken(), 0);
        imm.hideSoftInputFromWindow(findViewById(R.id.YearSelectNumberCompare).getWindowToken(), 0);

        CurrentMonthAnal = Month;
        CurrentYearAnal = Year;

        int Groceries_A = Analytics_Get_Bar("Groceries");
        int Food_A = Analytics_Get_Bar("Food");
        int Electronics_A = Analytics_Get_Bar("Electronics");
        int Maintenance_A = Analytics_Get_Bar("Maintenance");
        int Misc_A = Analytics_Get_Bar("Misc");

        Load_analytics_Bar(Groceries_A,Food_A,Electronics_A,Maintenance_A,Misc_A);

        IsComparing = false;

        Load_MonthlyCells(view);
    }

    public void CompareMonthSelected(View view){
        findViewById(R.id.CompareMonthMenu).setVisibility(View.INVISIBLE);

        Spinner MonthSpinner = findViewById(R.id.MonthSelectSpinnerCompare);
        String Month = MonthSpinner.getSelectedItem().toString();

        TextView YearText = findViewById(R.id.YearSelectNumberCompare);
        int Year = Integer.parseInt(YearText.getText().toString());

        int AmountMonthCurrent = Load_Total_Month_Amount(CurrentMonthAnal,CurrentYearAnal);
        int AmountMonthCompare = Load_Total_Month_Amount(Month,Year);

        int Comparison = AmountMonthCurrent - AmountMonthCompare;
        DecimalFormat format2 = new DecimalFormat("##,##,###");
        String FormattedComp = format2.format(Math.abs(Comparison));

        String AmtMntCCur = format2.format(AmountMonthCurrent);
        String AmtMntCCom = format2.format(AmountMonthCompare);

        if (AmountMonthCurrent > AmountMonthCompare){
            ((TextView) findViewById(R.id.CurrentMonthNYear)).setTextColor(getResources().getColor(R.color.Loss));
        } else if (AmountMonthCurrent == AmountMonthCompare) {
            ((TextView) findViewById(R.id.CurrentMonthNYear)).setTextColor(getResources().getColor(R.color.Zero));
        } else {
            ((TextView) findViewById(R.id.CurrentMonthNYear)).setTextColor(getResources().getColor(R.color.Profit));
        }

        String Date = CurrentMonthAnal+" "+CurrentYearAnal+" vs "+Month+" "+Year+" (₹ "+FormattedComp+")";

        ((TextView) findViewById(R.id.CurrentMonthNYear)).setText(Date);

        ((TextView) findViewById(R.id.AnalyTxt1)).setText(CurrentMonthAnal+" "+CurrentYearAnal+": ");
        ((TextView) findViewById(R.id.AnalyTxt1Amt)).setText("₹ "+AmtMntCCur);
        ((TextView) findViewById(R.id.AnalyTxt2)).setText(Month+" "+Year+": ");
        ((TextView) findViewById(R.id.AnalyTxt2Amt)).setText("₹ "+AmtMntCCom);

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(findViewById(R.id.YearSelectNumber).getWindowToken(), 0);
        imm.hideSoftInputFromWindow(findViewById(R.id.YearSelectNumberCompare).getWindowToken(), 0);

        IsComparing = true;

        UnLoad_Cells(findViewById(R.id.linlay));
        Unload_BarTint();

        AddinCompCells(Month,Year);
    }



    public void AddinCompCells(String Month2 , int Year2){
        Add_ComparisionCells(0,Load_Total_Category_Amount(CurrentMonthAnal,CurrentYearAnal,"Groceries"),Load_Total_Category_Amount(Month2,Year2,"Groceries"), Month2, Year2);
        Add_ComparisionCells(1,Load_Total_Category_Amount(CurrentMonthAnal,CurrentYearAnal,"Food"),Load_Total_Category_Amount(Month2,Year2,"Food"), Month2, Year2);
        Add_ComparisionCells(2,Load_Total_Category_Amount(CurrentMonthAnal,CurrentYearAnal,"Electronics"),Load_Total_Category_Amount(Month2,Year2,"Electronics"), Month2, Year2);
        Add_ComparisionCells(3,Load_Total_Category_Amount(CurrentMonthAnal,CurrentYearAnal,"Maintenance"),Load_Total_Category_Amount(Month2,Year2,"Maintenance"), Month2, Year2);
        Add_ComparisionCells(5,Load_Total_Category_Amount(CurrentMonthAnal,CurrentYearAnal,"Misc"),Load_Total_Category_Amount(Month2,Year2,"Misc"), Month2, Year2);
    }

    public void Add_ComparisionCells(int cate , int Amount1 , int Amount2 , String CompMonth , int CompYear){

        Object[][] CategoriesWColors = {{getString(R.string.Groceries),ContextCompat.getColor(this,R.color.Groceries)},
                {getString(R.string.Food),ContextCompat.getColor(this,R.color.Food)},
                {getString(R.string.Electronics),ContextCompat.getColor(this,R.color.Electronics)},
                {getString(R.string.Maintenance),ContextCompat.getColor(this,R.color.Maintenance)},
                {getString(R.string.Bill),ContextCompat.getColor(this,R.color.Bill)},
                {getString(R.string.Misc),ContextCompat.getColor(this,R.color.Misc)}};

        LinearLayout LinLay = findViewById(R.id.linlay);

        LayoutInflater infl = LayoutInflater.from(this);
        ConstraintLayout cloned = (ConstraintLayout) infl.inflate(R.layout.analytics_cell_template,LinLay,false);

        int CategoryColor = (int) CategoriesWColors[cate][1];
        String Category = (String) CategoriesWColors[cate][0];

        DecimalFormat format2 = new DecimalFormat("##,##,###");
        String AmountFormatted = format2.format(Amount1);
        String AmountFormatted2 = format2.format(Amount2);

        Button CC = cloned.findViewById(R.id.CategoryColor);
        TextView CCT = cloned.findViewById(R.id.CategoryText);
        TextView Amt = cloned.findViewById(R.id.EntryAmount);
        TextView Ti = cloned.findViewById(R.id.EntryTime);
        TextView Da = cloned.findViewById(R.id.ExpenseDay);

        TextView AmountText = cloned.findViewById(R.id.EntryAmountText);
        TextView TimeText = cloned.findViewById(R.id.EntryTimeText);
        TextView DayText = cloned.findViewById(R.id.DayText);

        Button ProfitLoss = cloned.findViewById(R.id.ProfitLoss);

        CCT.setTextSize(18);
        AmountText.setTextSize(15);
        TimeText.setTextSize(15);
        Amt.setTextSize(15);
        Ti.setTextSize(15);

        AmountText.setTypeface(Typeface.DEFAULT);
        TimeText.setTypeface(Typeface.DEFAULT);
        Amt.setTypeface(Typeface.DEFAULT);

        int diff = Amount1-Amount2;
        String FormattedDiff = format2.format(Math.abs(diff));

        CC.setBackgroundColor(CategoryColor);
        CCT.setText(Category+" (₹"+FormattedDiff+")");
        Amt.setText("₹ "+AmountFormatted);
        Ti.setText("₹ "+AmountFormatted2);
        Da.setText("");
        DayText.setText("");

        AmountText.setText(CurrentMonthAnal+" "+CurrentYearAnal+": ");
        TimeText.setText(CompMonth+" "+CompYear+": ");

        ProfitLoss.setVisibility(View.VISIBLE);

        if (diff < 0){
            ProfitLoss.setBackgroundColor(getResources().getColor(R.color.Profit));
            CCT.setTextColor(getResources().getColor(R.color.Profit));
        } else if (diff == 0) {
            ProfitLoss.setBackgroundColor(getResources().getColor(R.color.Zero));
            CCT.setTextColor(getResources().getColor(R.color.Zero));
        } else{
            ProfitLoss.setBackgroundColor(getResources().getColor(R.color.Loss));
            CCT.setTextColor(getResources().getColor(R.color.Loss));
        }

        switch (cate) {
            case 0:
                CompGroceriesBar = Math.abs(diff);
                if (diff < 0){findViewById(R.id.Groceries).setBackgroundColor(Color.parseColor("#A626FF00"));}
                else if(diff == 0){findViewById(R.id.Groceries).setBackgroundColor(Color.parseColor("#A6757575"));}
                else{findViewById(R.id.Groceries).setBackgroundColor(Color.parseColor("#A6FF0000"));}
                break;
            case 1:
                CompFoodBar = Math.abs(diff);
                if (diff < 0){findViewById(R.id.Food).setBackgroundColor(Color.parseColor("#A626FF00"));}
                else if(diff == 0){findViewById(R.id.Food).setBackgroundColor(Color.parseColor("#A6757575"));}
                else{findViewById(R.id.Food).setBackgroundColor(Color.parseColor("#A6FF0000"));}
                break;
            case 2:
                CompElectronicsBar = Math.abs(diff);
                if (diff < 0){findViewById(R.id.Electronics).setBackgroundColor(Color.parseColor("#A626FF00"));}
                else if(diff == 0){findViewById(R.id.Electronics).setBackgroundColor(Color.parseColor("#A6757575"));}
                else{findViewById(R.id.Electronics).setBackgroundColor(Color.parseColor("#A6FF0000"));}
                break;
            case 3:
                CompMaintenanceBar = Math.abs(diff);
                if (diff < 0){findViewById(R.id.Maintenance).setBackgroundColor(Color.parseColor("#A626FF00"));}
                else if(diff == 0){findViewById(R.id.Maintenance).setBackgroundColor(Color.parseColor("#A6757575"));}
                else{findViewById(R.id.Maintenance).setBackgroundColor(Color.parseColor("#A6FF0000"));}
                break;
            case 5:
                CompMiscBar = Math.abs(diff);
                if (diff < 0){findViewById(R.id.Misc).setBackgroundColor(Color.parseColor("#A626FF00"));}
                else if(diff == 0){findViewById(R.id.Misc).setBackgroundColor(Color.parseColor("#A6757575"));}
                else{findViewById(R.id.Misc).setBackgroundColor(Color.parseColor("#A6FF0000"));}
                Load_analytics_Bar(CompGroceriesBar,CompFoodBar,CompElectronicsBar,CompMaintenanceBar,CompMiscBar);
                break;
        }
        LinLay.addView(cloned);
    }
}