package com.js98012.vacplanner.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(Context context) {
        super(context, "MyData.db",null,1);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //wishlist
        String query1=String.format("create table %s(" +
                "%s integer primary key autoincrement," +
                "%s text," +
                "%s text," +
                "%s integer," +
                "%s text," +
                "%s BLOB," +
                "%s text);","WishList","w_num","w_title","w_content","w_importance","w_date","w_img","w_note");
        db.execSQL(query1);
        //Vacation
        String query2=String.format("create table %s(" +
                "%s integer primary key autoincrement," +
                "%s text," +
                "%s integer," +
                "%s text," +
                "%s text);","Vacation","v_num","v_type","v_days","v_content","v_note");
        db.execSQL(query2);
        //VacPlan
        String query3=String.format("create table %s(" +
                "%s integer primary key autoincrement," +
                "%s integer," +
                "%s text," +
                "%s text," +
                "%s text," +
                "%s text);","VacPlan","vp_num","vp_days","vp_from","vp_till","vp_content","vp_note");
        db.execSQL(query3);
        //UseVacation
        String query4=String.format("create table %s(" +
                "%s integer primary key autoincrement," +
                "%s integer NOT NULL," +
                "%s integer NOT NULL," +
                "%s text," +
                "%s integer," +
                "%s text);","UseVacation","uv_num","v_num","vp_num","uv_date","uv_days","uv_note");
        db.execSQL(query4);
        //VacSchedule
        String query5=String.format("create table %s(" +
                "%s integer primary key autoincrement," +
                "%s integer NOT NULL," +
                "%s integer NOT NULL," +
                "%s integer," +
                "%s integer," +
                "%s text);","VacSchedule","vs_num","w_num","vp_num","vs_day","vs_check","vs_note");
        db.execSQL(query5);

    }
}
