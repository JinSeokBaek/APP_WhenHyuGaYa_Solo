package com.js98012.vacplanner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.js98012.vacplanner.DB.DBHelper;

import java.util.ArrayList;

public class VacscheduleActivity extends AppCompatActivity {
    GridView gvSchedule;
    EditText etTitle;
    ImageView ivEdit, ivDel, ivWishAdd;
    DBHelper dbHelper;
    SQLiteDatabase db;
    Cursor cursor, cursor2;

    ViewPager vpView;

    int id;
    int days;

    FragmentAdapter fragmentAdapter = new FragmentAdapter(getSupportFragmentManager());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_vacschedule);
        getSupportActionBar().setElevation(0);

        setTitle("뭔 휴가야?");

        vpView = findViewById(R.id.vpView);

        //gvSchedule=findViewById(R.id.gvSchedule);
        etTitle = findViewById(R.id.etTitle);
        ivEdit = findViewById(R.id.ivSubmit);
        ivDel = findViewById(R.id.ivDel);
        ivWishAdd = findViewById(R.id.ivWishAdd);

        Intent it = getIntent();
        id = it.getIntExtra("id", 0);

        try {
            dbHelper = new DBHelper(this);
            db = dbHelper.getWritableDatabase();
            cursor = db.rawQuery("select * from VacPlan where vp_num=" + id, null);
            cursor.moveToNext();
            days = cursor.getInt(cursor.getColumnIndex("vp_days"));
            String content = cursor.getString(cursor.getColumnIndex("vp_content"));
            etTitle.setText(content);

            int total = days % 3 != 0 ? days / 3 + 1 : days / 3;
            for (int i = 0; i < total; i++) {
                GridFragment fragment = new GridFragment(days, id, total, i);
                fragmentAdapter.addItem(fragment);
            }
            vpView.setAdapter(fragmentAdapter);

            //fragmentAdapter.notifyDataSetChanged();

        } catch (Exception e) {
            e.printStackTrace();
        }

        ivEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = "update VacPlan " +
                        "set " +
                        "vp_content='" + etTitle.getText().toString() + "' " +
                        "where vp_num=" + id +
                        ";";
                exeQuery(query);
                Toast.makeText(getApplicationContext(), "수정 완료", Toast.LENGTH_SHORT).show();
                //수정은 일단 보류
//                Intent it=new Intent(VacscheduleActivity.this,VacplanActivity.class);
//                it.putExtra("update",true);
//                it.putExtra("id",id);
//                startActivityForResult(it,1003);
            }
        });
        ivDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alt = new AlertDialog.Builder(VacscheduleActivity.this);
                alt.setTitle("정말 삭제하시겠습니까?");
                alt.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String query = "delete from VacPlan where vp_num = " + id;
                        db.execSQL(query);
                        query = "delete from UseVacation where vp_num = " + id;
                        db.execSQL(query);
                        Toast.makeText(getApplicationContext(), "삭제 완료", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_CANCELED);
                        finish();

                    }
                });
                alt.setNegativeButton("아니오", null);
                alt.show();
            }
        });

        ivWishAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(VacscheduleActivity.this, WishPopupActivity.class);
                it.putExtra("id", id);
                it.putExtra("position", vpView.getCurrentItem());
                startActivityForResult(it, 1004);
            }
        });
    }

    int p;

    public void isDone(int position) {
        this.p = position;
        Log.d("testB", p + " ");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED && data != null) {
            int position = vpView.getCurrentItem();
            FragmentAdapter fragmentAdapter = new FragmentAdapter(getSupportFragmentManager());
            int total = days % 3 != 0 ? days / 3 + 1 : days / 3;
            for (int i = 0; i < total; i++) {
                GridFragment fragment = new GridFragment(days, id, total, i);
                if (position == i) {
                    int w_num = data.getIntExtra("id", 0);
                    Bundle bundle = new Bundle();
                    bundle.putInt("w_num", w_num);
                    bundle.putBoolean("isWishReceive", true);
                    fragment.setArguments(bundle);

                    Log.d("test", position + " ");

                }
                fragmentAdapter.addItem(fragment);
            }
            vpView.setAdapter(fragmentAdapter);
//                GridFragment fragment = (GridFragment) fragmentAdapter.getItem(position);
//                int w_num = data.getIntExtra("id", 0);
//                Bundle bundle = new Bundle();
//                bundle.putInt("w_num", w_num);
//                bundle.putBoolean("isWishReceive", true);
//                fragment.setArguments(bundle);
//
//                Log.d("test", position + " ");
//
//                fragmentAdapter.editItem(fragment, position);
//
//                fragmentAdapter.notifyDataSetChanged();
//                vpView.setAdapter(fragmentAdapter);
            vpView.setCurrentItem(position);

//            int total = days % 3 != 0 ? days / 3 + 1 : days / 3;
//            for (int i = 0; i < total; i++) {
//
//                GridFragment fragment = new GridFragment(days, id, total, i);
//
//                Bundle bundle = new Bundle();
//                bundle.putInt("w_num", w_num);
//                bundle.putBoolean("isWishReceive", true);
//                fragment.setArguments(bundle);
//                fragmentAdapter.addItem(fragment);
            // }
        } else if (resultCode == RESULT_OK && data != null) {
            int position=data.getIntExtra("position",0);
            FragmentAdapter fragmentAdapter = new FragmentAdapter(getSupportFragmentManager());
            int total = days % 3 != 0 ? days / 3 + 1 : days / 3;
            for (int i = 0; i < total; i++) {
                GridFragment fragment = new GridFragment(days, id, total, i);
                fragmentAdapter.addItem(fragment);
            }
            vpView.setCurrentItem(position);
            vpView.setAdapter(fragmentAdapter);
        }


    }

    public boolean exeQuery(String query) {
        try {
            db.execSQL(query);
            return true;
        } catch (Exception e) {
            //tvtest.append(e+"");
            return false;
        }
    }

    class FragmentAdapter extends FragmentStatePagerAdapter {

        // ViewPager에 들어갈 Fragment들을 담을 리스트
        private ArrayList<GridFragment> fragments = new ArrayList<>();

        // 필수 생성자
        FragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        // List에 Fragment를 담을 함수
        void addItem(GridFragment fragment) {
            fragments.add(fragment);
        }

        void editItem(GridFragment fragment, int postion) {
            fragments.set(postion, fragment);
        }
    }
}
