package com.js98012.vacplanner;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.CursorAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.js98012.vacplanner.DB.DBHelper;

import java.util.ArrayList;

public class WishPopupActivity extends Activity {
    ImageView ivWishAdd;
    GridView gvWishList;

    DBHelper dbHelper;
    SQLiteDatabase db;
    Cursor cursor,cursor2;
    WishAdapter myCursorAdapter;

    ArrayList<Wish> wishes=new ArrayList<>();

    int id,position;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_wish_popup);

        ivWishAdd=findViewById(R.id.ivWishAdd);
        gvWishList=findViewById(R.id.gvWishlist);

        Intent it=getIntent();
        id=it.getIntExtra("id",0);
        position=it.getIntExtra("position",0);

        try{
            dbHelper=new DBHelper(getApplicationContext());
            db=dbHelper.getWritableDatabase();
            cursor=db.rawQuery("select w_num as '_id',* from WishList order by w_importance desc",null);

            Wish wish;

            while(cursor.moveToNext()){
                wish=new Wish();
                wish._id=cursor.getInt(cursor.getColumnIndex("_id"));
                wish._title= cursor.getString(cursor.getColumnIndex("w_title"));
                wish._importance=cursor.getInt(cursor.getColumnIndex("w_importance"));
                wishes.add(wish);
            }

            myCursorAdapter=new WishAdapter(getApplicationContext(),wishes);
            gvWishList.setAdapter(myCursorAdapter);
        }catch (Exception e){
            Toast.makeText(getApplicationContext(),"데이터베이스 로드 실패",Toast.LENGTH_SHORT).show();
        }
        ivWishAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it=new Intent(WishPopupActivity.this,WishaddActivity.class);
                startActivityForResult(it,1004);
            }
        });
    }

    private class WishAdapter extends BaseAdapter{
        private final ArrayList<Wish> list;
        private final LayoutInflater inflater;

        public WishAdapter(Context context,ArrayList<Wish> list) {
            this.list = list;
            this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View view, ViewGroup parent) {
            view = inflater.inflate(R.layout.item_wish, parent, false);
            TextView tv=(TextView)view.findViewById(R.id.tvWishName);
            RatingBar rbImportance=view.findViewById(R.id.rbImportance);
            RelativeLayout lltemp=view.findViewById(R.id.lltemp);

            Wish wish=getItem(position);

            final int _id=wish._id;
            String _title = wish._title;
            int _importance=wish._importance;
            rbImportance.setRating(_importance);
            tv.setText(_title);

            DisplayMetrics dm = getApplicationContext().getResources().getDisplayMetrics();
            int height = dm.heightPixels/5;

            view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,height));

            lltemp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it=new Intent();
                    it.putExtra("id",_id);
                    it.putExtra("position",position);

                    setResult(RESULT_CANCELED,it);
                    finish();
                }
            });


            return view;
        }

        @Override
        public Wish getItem(int position) {
            return list.get(position);
        }

        @Override
        public int getCount() {
            return list.size();
        }
    }
    class Wish{
        int _id;
        String _title;
        int _importance;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            wishes.clear();
            cursor=db.rawQuery("select w_num as '_id',* from WishList order by w_importance desc",null);

            Wish wish;

            while(cursor.moveToNext()){
                wish=new Wish();
                wish._id=cursor.getInt(cursor.getColumnIndex("_id"));
                wish._title= cursor.getString(cursor.getColumnIndex("w_title"));
                wish._importance=cursor.getInt(cursor.getColumnIndex("w_importance"));
                wishes.add(wish);
            }

            myCursorAdapter=new WishAdapter(getApplicationContext(),wishes);
            gvWishList.setAdapter(myCursorAdapter);
        }
    }
}
