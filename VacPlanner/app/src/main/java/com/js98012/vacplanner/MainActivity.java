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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.js98012.vacplanner.DB.DBHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {


    @Override
    public boolean supportRequestWindowFeature(int featureId) {
        return super.supportRequestWindowFeature(featureId);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        return super.onTouchEvent(event);
    }

    //커스텀 액션바
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
        ImageView ivLeft = actionbar.findViewById(R.id.iv_left);
        ImageView ivRight = actionbar.findViewById(R.id.iv_right);

        //좌측 아이콘
        ivLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(it);
            }
        });
        //우측 아이콘
        ivRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(MainActivity.this, WishActivity.class);
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

    GridView gvCalendar;
    DBHelper dbHelper;
    SQLiteDatabase db;
    Cursor cursor, cursor2;
    Calendar mCalendar;
    GridViewAdapter gridViewAdapter;
    TextView btnPrev, btnNext, tvDate, tvInfo, tvInfo2, tvVacDate,tvVacDate2, tvVacDays;
    ArrayList<CalenderDay> calList = new ArrayList<>();
    int k = 0;
    RelativeLayout layout_noraml;
    LinearLayout layout_add, llAdd, llShow;

    BackPressCloseHandler b;

    String date;

    @Override
    public void onBackPressed() {
        b.onBackPressed();
    }

    boolean isTodayVac = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setElevation(0);

        b = new BackPressCloseHandler(this, "종료");

        btnPrev = findViewById(R.id.btnPrev);
        btnNext = findViewById(R.id.btnNext);
        tvDate = findViewById(R.id.tvDate);
        tvInfo = findViewById(R.id.tvInfo);
        tvInfo2 = findViewById(R.id.tvInfo2);

        layout_add = findViewById(R.id.layout_add);
        layout_noraml = findViewById(R.id.layout_normal);
        llAdd = findViewById(R.id.llAdd);
        llShow = findViewById(R.id.llShow);
        tvVacDate = findViewById(R.id.tvVacDate);
        tvVacDate2 = findViewById(R.id.tvVacDate2);
        tvVacDays = findViewById(R.id.tvVacDays);

        gvCalendar = findViewById(R.id.gvcalendar);
        try {
            dbHelper = new DBHelper(this);
            db = dbHelper.getWritableDatabase();
            cursor = db.rawQuery("select * from VacPlan ", null);
            gridViewAdapter = new GridViewAdapter(getApplicationContext(), calList);
            gvCalendar.setAdapter(gridViewAdapter);
            setCalendar(k);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "데이터베이스 로드 실패", Toast.LENGTH_SHORT).show();
        }

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isTodayVac = false;
                k++;
                setCalendar(k);
            }
        });
        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isTodayVac = false;
                k--;
                setCalendar(k);
            }
        });

        llAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(MainActivity.this, VacplanActivity.class);
                it.putExtra("date", date);
                startActivityForResult(it, 1001);
            }
        });
        //d-day/현재 진행중
        try {

            cursor2 = db.rawQuery("select count(*) from VacPlan", null);
            cursor2.moveToNext();
            if (cursor2.getInt(0) == 0)
                throw new Exception("NoVac");

            cursor2 = db.rawQuery("select vp_from,vp_num from VacPlan order by vp_from", null);

            int dday = 10000;
            long diff2;
            float diffDays2;
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date todayDate = new Date();
            while (cursor2.moveToNext()) {
                Date endDate = formatter.parse(cursor2.getString(cursor2.getColumnIndex("vp_from")));
                diff2 = endDate.getTime() - todayDate.getTime();
                diffDays2 = diff2 / (24 * 60 * 60 * 1000);
                if (diffDays2 > 0 && diffDays2 < dday)
                    dday = (int) diffDays2;
            }
            if(dday==10000)
                throw new Exception("NoVacation");

            tvInfo.setText("다음 휴가까지 D-" + dday);
        } catch (Exception e) {
            tvInfo.setText("예정된 휴가가 없습니다!\n날짜를 눌러 휴가를 계획해보세요!");
            e.printStackTrace();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            setCalendar(k);
            gridViewAdapter.notifyDataSetChanged();
        }else if(resultCode==RESULT_CANCELED){
            setCalendar(k);
            gridViewAdapter.notifyDataSetChanged();
        }
    }

    private void setCalendar(int j) {
        calList.clear();
        mCalendar = Calendar.getInstance();
        long now = System.currentTimeMillis();
        final Date date = new Date(now);
        //연,월,일을 따로 저장
        final SimpleDateFormat curYearFormat = new SimpleDateFormat("yyyy", Locale.KOREA);
        final SimpleDateFormat curMonthFormat = new SimpleDateFormat("MM", Locale.KOREA);
        final SimpleDateFormat curDayFormat = new SimpleDateFormat("dd", Locale.KOREA);

        mCalendar.set(Integer.parseInt(curYearFormat.format(date)), Integer.parseInt(curMonthFormat.format(date)) - 1, 1);

        mCalendar.add(mCalendar.MONTH, j);
        int dayNum = mCalendar.get(Calendar.DAY_OF_WEEK); //1일 - 요일 매칭 시키기 위해 공백 add
        for (int i = 1; i < dayNum; i++) {
            calList.add(new CalenderDay(true));
        }
        tvDate.setText(mCalendar.get(Calendar.YEAR) + "/" + (mCalendar.get(Calendar.MONTH) + 1) + "월");
        setCalendarDate(mCalendar.get(Calendar.MONTH) + 1);
        gridViewAdapter.notifyDataSetChanged();

    }

    private void setCalendarDate(int month) {
        mCalendar.set(Calendar.MONTH, month - 1);

        //이번달의 휴가 유무
        boolean isExist = isVacExist2(mCalendar);
        int vacDays=0;;
        MonthVac monthVac;
        for (int i = 0; i < mCalendar.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
            CalenderDay calenderDay = new CalenderDay((i + 1) + "", ((mCalendar.get(Calendar.MONTH) + 1) < 10 ? "0" + (mCalendar.get(Calendar.MONTH) + 1) : (mCalendar.get(Calendar.MONTH) + 1)) + "", mCalendar.get(Calendar.YEAR) + "");

            if (isExist) {
                for (int j = 0; j < monthVacs.size(); j++) {
                    monthVac = monthVacs.get(j);
                    if (monthVac.isTill) {
                        if (i <= monthVac.till) {
                            vacDays++;
                            calenderDay.isVac = true;
                            calenderDay.vacDay=String.valueOf(vacDays);
                            calenderDay.vacName = monthVac.vacName;
                            calenderDay.id=monthVac.id;
                        }
                    } else {
                        if (i >= monthVac.from - 1 && i <= monthVac.from + monthVac.days - 2) {
                            vacDays++;
                            calenderDay.isVac = true;
                            calenderDay.vacDay=String.valueOf(vacDays);
                            calenderDay.vacName = monthVac.vacName;
                            calenderDay.id=monthVac.id;
                        }
                    }
                }
            }else
                vacDays=0;


            calList.add(calenderDay);
        }
    }

    // int startMon,endMon;
    private class MonthVac {
        public MonthVac(int from, int till, boolean isTill, int days, String vacName,int id) {
            this.from = from;
            this.till = till;
            this.isTill = isTill;
            this.days = days;
            this.vacName = vacName;
            this.id=id;
        }
        int id;
        int from;
        int till;
        boolean isTill;
        int days;
        String vacName;
    }

    ArrayList<MonthVac> monthVacs = new ArrayList<>();

    private boolean isVacExist2(Calendar mCalendar) {
        monthVacs.clear();
        cursor2 = db.rawQuery("select vp_num,vp_from,vp_till,vp_days,vp_content from VacPlan", null);
        try {
            String from;
            String till;
            String content;
            int days,id;
            MonthVac monthVac;
            while (cursor2.moveToNext()) {
                id=cursor2.getInt(cursor2.getColumnIndex("vp_num"));
                from = cursor2.getString(cursor2.getColumnIndex("vp_from"));
                till = cursor2.getString(cursor2.getColumnIndex("vp_till"));
                content = cursor2.getString(cursor2.getColumnIndex("vp_content"));
                days = cursor2.getInt(cursor2.getColumnIndex("vp_days"));
                //Log.d("test", till + " " + from);
                String month = ((mCalendar.get(Calendar.MONTH) + 1) < 10 ? "0" + (mCalendar.get(Calendar.MONTH) + 1) : "" + (mCalendar.get(Calendar.MONTH) + 1));
                String today = mCalendar.get(Calendar.YEAR) + "-" + month;
                if (today.equals(from.substring(0, 7)) || today.equals(till.substring(0, 7))) {
                    if (!today.equals(from.substring(0, 7))) {//till이 이번달
                        monthVac = new MonthVac(0, Integer.parseInt(till.substring(8, 10)), true, 0, content,id);
                        monthVacs.add(monthVac);
                    } else {
                        monthVac = new MonthVac(Integer.parseInt(from.substring(8, 10)), Integer.parseInt(till.substring(8, 10)), false, days, content,id);
                        monthVacs.add(monthVac);
                    }
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    //대충 휴가 이번월 일 있는지 확인하는 메소드 만들기 return bool
//    private boolean isVacExist(Calendar mCalendar){
//        //2019-01 식으로 검색하는걸로 수정함
//        String month=((mCalendar.get(Calendar.MONTH)+1)<10?"0"+(mCalendar.get(Calendar.MONTH)+1):""+(mCalendar.get(Calendar.MONTH)+1));
//        cursor=db.rawQuery("select *,SUBSTR(vp_from,5,2) as 'startMon',SUBSTR(vp_till,5,2) as 'endMon' from VacPlan " +
//                "where SUBSTR(vp_from,1,7)='"+mCalendar.get(Calendar.YEAR)+"-"+month+"' " +
//                "OR SUBSTR(vp_till,1,7)='"+mCalendar.get(Calendar.YEAR)+"-"+month+"';",null);
//        try {
//            cursor.moveToNext();
//            startMon = Integer.parseInt(cursor.getString(cursor.getColumnIndex("startMon")));
//            endMon = Integer.parseInt(cursor.getString(cursor.getColumnIndex("endMon")));
//            return true;
//        }catch (Exception e){
//            e.printStackTrace();
//            return false;
//        }
//    }
    String dateInfo, vacnameInfo;

    class GridViewAdapter extends BaseAdapter {
        private final ArrayList<CalenderDay> list;
        private final LayoutInflater inflater;

        public GridViewAdapter(Context context, ArrayList<CalenderDay> list) {
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
                convertView = inflater.inflate(R.layout.item_calendar, parent, false);
                holder = new ViewHolder();
                holder.tvDay = (TextView) convertView.findViewById(R.id.tv_day);
                holder.tvVacDay = (TextView) convertView.findViewById(R.id.tv_VacDay);
                holder.tvVacName = (TextView) convertView.findViewById(R.id.tv_VacName);
                holder.llVac = convertView.findViewById(R.id.llVac);
                holder.llisDay = convertView.findViewById(R.id.llisDay);
                holder.llisNotDay = convertView.findViewById(R.id.llisNotDay);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            convertView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, gvCalendar.getHeight() / 5));
            final CalenderDay calenderDay = getItem(position);
            if (calenderDay.isNotADay) {
//                holder.llisDay.setVisibility(View.GONE);
//                holder.llisNotDay.setVisibility(View.VISIBLE);
                convertView.setVisibility(View.INVISIBLE);
            } else {
                holder.llisDay.setVisibility(View.VISIBLE);
                holder.llisNotDay.setVisibility(View.GONE);
                final String today = getItem(position).day;
                final String year = getItem(position).year;
                final String month = getItem(position).month;
                convertView.setFocusableInTouchMode(true);

                convertView.setVisibility(View.VISIBLE);
                holder.tvDay.setText(calenderDay.day);
                //해당 날짜 텍스트 컬러,배경 변경
                mCalendar = Calendar.getInstance();

                //오늘 day 가져와서 오늘 설정 및 휴가 확인
                String sToday = String.valueOf(mCalendar.get(Calendar.DAY_OF_MONTH));
                if (sToday.equals(getItem(position).day)) {
                    //오늘 day 텍스트 컬러 변경
                    holder.tvDay.setBackgroundResource(R.drawable.shape_highlight);
                    if (calenderDay.isVac) {
                        dateInfo = month + "월" + today + "일";
                        vacnameInfo = calenderDay.vacName;
                        isTodayVac = true;
                    }
                } else {
                    holder.tvDay.setBackgroundResource(R.color.clear);

                }

                //해당 일자가 휴가일 경우
                if (calenderDay.isVac) {
                    //오늘이 휴가면 info세팅
                    if(isTodayVac){
                        layout_noraml.setVisibility(View.VISIBLE);
                        llShow.setVisibility(View.GONE);
                        layout_add.setVisibility(View.GONE);
                        tvInfo.setText(calenderDay.vacName+" 휴가 중 "+calenderDay.vacDay+"일 째");
                    }
                    holder.llVac.setVisibility(View.VISIBLE);
                    holder.tvVacDay.setVisibility(View.VISIBLE);
                    holder.tvVacName.setText(calenderDay.vacName);
                    holder.tvVacDay.setText(calenderDay.vacDay+"일");
                    holder.llVac.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent it = new Intent(MainActivity.this, VacscheduleActivity.class);
                            it.putExtra("id", calenderDay.id);
                            startActivityForResult(it,1003);
                        }
                    });
                    //휴가인 일자를 눌렀을때 휴가info
                    convertView.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            tvVacDate.setText(month + "월" + today + "일");
                            tvVacDate2.setText(calenderDay.vacDay+"일차");
                            tvVacDays.setText(calenderDay.vacName + " 휴가");
                            layout_noraml.setVisibility(View.GONE);
                            llShow.setVisibility(View.VISIBLE);
                            layout_add.setVisibility(View.GONE);
                            return false;
                        }
                    });
                } else {
                    holder.llVac.setVisibility(View.INVISIBLE);
                    holder.tvVacDay.setVisibility(View.INVISIBLE);
                    convertView.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            tvInfo2.setText(month + "월" + today + "일" + " 예정된 휴가가 없습니다!");
                            layout_noraml.setVisibility(View.GONE);
                            layout_add.setVisibility(View.VISIBLE);
                            llShow.setVisibility(View.GONE);
                            return false;
                        }
                    });
                }

                convertView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (v.hasFocus()) {
                            v.setBackgroundResource(R.color.NotADay);
                            date = year + "-" + month + "-" + (Integer.parseInt(today) < 10 ? "0" + today : today);
                        } else
                            v.setBackgroundResource(R.drawable.shape_calendar);
                    }
                });
            }

            return convertView;
        }

        @Override
        public CalenderDay getItem(int position) {
            return list.get(position);
        }

        @Override
        public int getCount() {
            return list.size();
        }
    }

    private class ViewHolder {
        TextView tvDay;
        TextView tvVacName;
        TextView tvVacDay;
        LinearLayout llVac,llisDay,llisNotDay;
    }

    class CalenderDay {
        CalenderDay(String day, String month, String year) {
            this.day = day;
            this.month = month;
            this.year = year;
        }

        CalenderDay(boolean isNotADay) {
            this.isNotADay = isNotADay;
        }

        String day;
        String month;
        String year;
        int id;
        boolean isVac = false;
        boolean isNotADay = false;
        //휴가이름
        String vacName;
        //휴가 일차
        String vacDay;

        public String info() {
            return day + " " + isVac + " " + isNotADay + " " + vacName + " " + vacDay;
        }
    }
}
