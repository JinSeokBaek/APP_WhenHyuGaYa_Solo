package com.js98012.vacplanner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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

public class WishActivity extends AppCompatActivity {
    @Override
    public boolean supportRequestWindowFeature(int featureId) {
        return super.supportRequestWindowFeature(featureId);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        return super.onTouchEvent(event);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        ActionBar actionBar = getSupportActionBar();

        // Custom Actionbar를 사용하기 위해 CustomEnabled을 true 시키고 필요 없는 것은 false 시킨다
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);            //액션바 아이콘을 업 네비게이션 형태로 표시합니다.
        actionBar.setDisplayShowTitleEnabled(false);        //액션바에 표시되는 제목의 표시유무를 설정합니다.
        actionBar.setDisplayShowHomeEnabled(false);            //홈 아이콘을 숨김처리합니다.


        //layout을 가지고 와서 actionbar에 포팅을 시킵니다.
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View actionbar = inflater.inflate(R.layout.actionbar, null);
        ImageView ivLeft=actionbar.findViewById(R.id.iv_left);
        ImageView ivRight=actionbar.findViewById(R.id.iv_right);
        ivRight.setImageResource(R.drawable.cal);
        ivLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it=new Intent(WishActivity.this,SettingActivity.class);
                startActivity(it);
            }
        });
        ivRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it=new Intent(WishActivity.this,MainActivity.class);
                startActivity(it);
                finish();
            }
        });
//        ImageView ivIcon = (ImageView) actionbar.findViewById(R.id.title_icon);
//        ImageView ivHelp = (ImageView) actionbar.findViewById(R.id.help);
//        TextView ivLetter = (TextView) actionbar.findViewById(R.id.title_letter);

        actionBar.setCustomView(actionbar);
        //액션바 양쪽 공백 없애기
        Toolbar parent = (Toolbar) actionbar.getParent();
        parent.setContentInsetsAbsolute(0, 0);

        return true;
    }

    ImageView btnAddWish;
    GridView gvWishList;
    DBHelper dbHelper;
    SQLiteDatabase db;
    Cursor cursor,cursor2;
    WishAdapter myCursorAdapter;
    TextView tvAlert;

    ArrayList<Wish> wishes=new ArrayList<>();

    BackPressCloseHandler b;

    @Override
    public void onBackPressed() {
        b.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wish);
        getSupportActionBar().setElevation(0);

        btnAddWish=findViewById(R.id.btnAdd_wish);
        gvWishList=findViewById(R.id.gvWishlist);
        tvAlert=findViewById(R.id.tvAlert);
        b = new BackPressCloseHandler(this, "종료");

        try {
            dbHelper = new DBHelper(this);
            db = dbHelper.getWritableDatabase();
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
        try{
            cursor2=db.rawQuery("select count(*) from WishList",null);
            cursor2.moveToNext();
            if(cursor2.getInt(0)>0)
                tvAlert.setVisibility(View.GONE);
            else
            tvAlert.setVisibility(View.VISIBLE);

        }catch (Exception e){

        }
        btnAddWish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it=new Intent(WishActivity.this,WishaddActivity.class);
                startActivityForResult(it,1002);
            }
        });

    }
//    class MyCursorAdapter extends CursorAdapter {
//        @SuppressWarnings("deprecation")
//        public MyCursorAdapter(Context context, Cursor c) {
//            super(context, c);
//        }
//
//        @Override
//        public View newView(Context context, Cursor cursor, ViewGroup parent) {
//            LayoutInflater inflater = LayoutInflater.from(context);
//            View v = inflater.inflate(R.layout.item_wish, parent, false);
//            return v;
//        }
//
//        @Override
//        public void bindView(View view, Context context, Cursor cursor) {
//            TextView tv=(TextView)view.findViewById(R.id.tvWishName);
//            RatingBar rbImportance=view.findViewById(R.id.rbImportance);
//            RelativeLayout lltemp=view.findViewById(R.id.lltemp);
//
//            final int _id=cursor.getInt(cursor.getColumnIndex("_id"));
//            String _title = cursor.getString(cursor.getColumnIndex("w_title"));
//            int _importance=cursor.getInt(cursor.getColumnIndex("w_importance"));
//            rbImportance.setRating(_importance);
//            tv.setText(_title);
//
//            DisplayMetrics dm = getApplicationContext().getResources().getDisplayMetrics();
//            int height = dm.heightPixels/6;
//
//            Log.d("test1",_id+" "+_title+" "+view.getHeight());
//
//            view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,height));
//
//            Log.d("test2",_id+" "+_title+" "+view.getHeight());
//
//            lltemp.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent it=new Intent(WishActivity.this,WishaddActivity.class);
//                    it.putExtra("id",_id);
//                    it.putExtra("update",true);
//                    startActivityForResult(it,1002);
//                }
//            });
////            view.setClickable(true);
////            view.setOnLongClickListener(new View.OnLongClickListener() {
////                @Override
////                public boolean onLongClick(View view) {
////                    String query="delete from WishList where w_num = "+_id;
////                    db.execSQL(query);
////                    getCursor().requery();
////                    notifyDataSetChanged();
////                    return false;
////                }
////            });
//
//
//        }
//    }
    private class WishAdapter extends BaseAdapter {
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
        public View getView(int position, View view, ViewGroup parent) {
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
            int height = dm.heightPixels/6;

            //Log.d("test1",_id+" "+_title+" "+view.getHeight());

            view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,height));

            //Log.d("test2",_id+" "+_title+" "+view.getHeight());

            lltemp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it=new Intent(WishActivity.this,WishaddActivity.class);
                    it.putExtra("id",_id);
                    it.putExtra("update",true);
                    startActivityForResult(it,1002);
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
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

            try{
                cursor2=db.rawQuery("select count(*) from WishList",null);
                cursor2.moveToNext();
                if(cursor2.getInt(0)>0)
                    tvAlert.setVisibility(View.GONE);
                else
                    tvAlert.setVisibility(View.VISIBLE);

            }catch (Exception e){
                tvAlert.setVisibility(View.VISIBLE);
            }
        }
    }
    class Wish{
        int _id;
        String _title;
        int _importance;
    }
}
