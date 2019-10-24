package com.js98012.vacplanner;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.js98012.vacplanner.DB.DBHelper;

import java.util.ArrayList;

public class GridFragment extends Fragment {
    public GridFragment() {

    }
    public static GridFragment newInstance() {
        return new GridFragment();
    }

    GridView gvSchedule;
    ArrayList<DayInPlan> dayInPlans = new ArrayList<>();
    int days, _id, total, k;

    SheduleAdapter sheduleAdapter;

    DBHelper dbHelper;
    SQLiteDatabase db;
    Cursor cursor;

    public GridFragment(int days, int _id, int total, int k) {
        this.days = days;
        this._id = _id;
        this.total = total;
        this.k = k;
    }

    int w_num;
    boolean isWishReceive = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grid, container, false);
        gvSchedule = view.findViewById(R.id.gvSchedule);

        if (getArguments() != null) {
            w_num = getArguments().getInt("w_num");
            isWishReceive = getArguments().getBoolean("isWishReceive");
            Log.d("testaaa",w_num+" "+isWishReceive);
        }
        try {
            dayInPlans.clear();
            dbHelper = new DBHelper(getActivity());
            db = dbHelper.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }

        sheduleAdapter = new SheduleAdapter(getActivity(), dayInPlans);
        gvSchedule.setAdapter(sheduleAdapter);


        DayInPlan dayInPlan;

        int t;
        if ((k + 1) < total) {
            t = 3 * (k + 1);
        } else {
            t = days;
        }

        //Log.d("test333", t + "");
        for (int i = 3 * k; i < t; i++) {
            dayInPlan = new DayInPlan();
            dayInPlan.day = (i + 1);

            dayInPlans.add(dayInPlan);
            //Log.d("test11", dayInPlan.day + "");
        }


        sheduleAdapter.notifyDataSetChanged();


        return view;
    }

    private class SheduleAdapter extends BaseAdapter {

        private final ArrayList<DayInPlan> list;
        private final LayoutInflater inflater;

        ArrayList<Wish> wishes = new ArrayList<>();

        public SheduleAdapter(Context context, ArrayList<DayInPlan> list) {
            this.list = list;
            this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_schedule, parent, false);
                holder = new ViewHolder();
                holder.tvDay = convertView.findViewById(R.id.tvDay);
                holder.lvSchedule = convertView.findViewById(R.id.lvSchedule);
                holder.llClick = convertView.findViewById(R.id.llClick);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final DayInPlan dayInPlan = getItem(position);
            //Log.d("test33", dayInPlan.day + "");

            holder.tvDay.setText(dayInPlan.day + "일차");
            //Log.d("test2",gvSchedule.getNumColumns()+" "+gvSchedule.getColumnWidth());
            cursor = db.rawQuery("select vs_num as '_id',* from VacSchedule  join WishList\n" +
                    "on VacSchedule.w_num=WishList.w_num where vs_day="+dayInPlan.day+" " +
                    "and vp_num= "+_id+
                    " order by w_importance desc", null);
            //convertView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,));
            wishes.clear();
            //cursor = db.rawQuery("select w_num as '_id',* from wishlist", null);
            Wish wish;
            while (cursor.moveToNext()) {
                wish = new Wish();
                wish.vs_num =cursor.getInt(cursor.getColumnIndex("_id"));
                wish._id = cursor.getInt(cursor.getColumnIndex("w_num"));
                wish._title = cursor.getString(cursor.getColumnIndex("w_title"));
                wish._importance = cursor.getInt(cursor.getColumnIndex("w_importance"));

                wishes.add(wish);
            }
            WishAdapter scheduleListAdapter = new WishAdapter(getActivity(), wishes);
            holder.lvSchedule.setAdapter(scheduleListAdapter);

            if (isWishReceive) {
                Log.d("testA", isWishReceive + " "+k);
                holder.tvDay.setBackgroundResource(R.drawable.shape_received);

                holder.llClick.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent it = new Intent();
                        it.putExtra("day", dayInPlan.day);
                        //wishlist의 넘버 붙여넣기
                        it.putExtra("w_num", w_num);
                        String query="insert into VacSchedule values(null," +
                                " "+w_num+","+
                                " "+_id+","+
                                " "+dayInPlan.day+","+
                                " 0,"+
                                "null);";
                        // tvtest.setText(query + "");
                        db.execSQL(query);
                        isWishReceive=false;
                        ((VacscheduleActivity)getActivity()).isDone(k);
                        notifyDataSetChanged();
                    }
                });
            }else{
                holder.tvDay.setBackgroundResource(R.color.clear);
                holder.llClick.setOnClickListener(null);
            }
            //setListViewHeightBasedOnChildren(holder.lvSchedule);


            return convertView;
        }


        @Override
        public DayInPlan getItem(int position) {
            return list.get(position);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        private class ViewHolder {
            TextView tvDay;
            ListView lvSchedule;
            LinearLayout llClick;
        }

        private class ViewHolder_wish {
            TextView tv;
            RatingBar rbImportance;
            RelativeLayout rbTemp;
        }

        private class WishAdapter extends BaseAdapter {
            private final ArrayList<Wish> list;
            private final LayoutInflater inflater;
            int w_num;
            public WishAdapter(Context context, ArrayList<Wish> list) {
                this.list = list;
                this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(int position, View view, ViewGroup parent) {
                ViewHolder_wish holder = null;
                if (view == null) {
                    view = inflater.inflate(R.layout.item_wish, parent, false);
                    holder = new ViewHolder_wish();
                    holder.tv = (TextView) view.findViewById(R.id.tvWishName);
                    holder.rbImportance = view.findViewById(R.id.rbImportance);
                    holder.rbTemp=view.findViewById(R.id.lltemp);
                } else {
                    holder = (ViewHolder_wish) view.getTag();
                }
                int height=gvSchedule.getHeight()/6;
                holder.rbTemp.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,height));
                final Wish wish = getItem(position);
                final int vs_num=wish.vs_num;
                try {
                    w_num = wish._id;
                    String _title = wish._title;
                    Log.d("test00", w_num + " " + _title);
                    int _importance = wish._importance;
                    holder.rbImportance.setRating(_importance);
                    holder.tv.setText(_title);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("error",wish._title+" "+wish._id+" "+wish._importance);
                }
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent it = new Intent(getActivity(), WishaddActivity.class);
                        it.putExtra("id", wish._id);
                        it.putExtra("vs_num", vs_num);

                        it.putExtra("update",true);
                        it.putExtra("schedule",true);
                        //Log.d("test_last",vs_num+" ");
                        startActivityForResult(it,1004);
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

        class Wish {
            int _id;
            String _title;
            int _importance;
            int vs_num;
        }


    }

    private class DayInPlan {
        int day;
        String date;
    }
}
