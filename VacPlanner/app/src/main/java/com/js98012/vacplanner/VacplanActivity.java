package com.js98012.vacplanner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.js98012.vacplanner.DB.DBHelper;
import com.travijuu.numberpicker.library.Enums.ActionEnum;
import com.travijuu.numberpicker.library.Interface.ValueChangedListener;
import com.travijuu.numberpicker.library.NumberPicker;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Year;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class VacplanActivity extends AppCompatActivity {


    DBHelper dbHelper;
    SQLiteDatabase db;
    Cursor cursor,cursor2;

    String year,month,day;
    TextView tvStart,tvBack,tvYear1,tvYear2,tvArrange;
    ListView lvVac;

    EditText etVacName;


    VacationAdapter vacationAdapter;

    LinearLayout btnVacAdd;
    Button btnConfirm;

    String[] from_till=new String[2];

    int id;

    ArrayAdapter<String> adapter;
    ArrayList<Vacation> vacationArrayList=new ArrayList<>();
    ArrayList<Integer> useDays=new ArrayList<>();

    ArrayList<Vacation> selectArrayList=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vacplan);
        setTitle("휴가 계획");

        tvStart=findViewById(R.id.tvStart);
        tvBack=findViewById(R.id.tvBack);
         tvYear1=findViewById(R.id.tvYear1);
         tvYear2=findViewById(R.id.tvYear2);
        tvArrange=findViewById(R.id.tvArrange);
         lvVac=findViewById(R.id.lvVac);

         btnConfirm=findViewById(R.id.btnConfirm);
         btnVacAdd=findViewById(R.id.btnVacAdd);

         etVacName=findViewById(R.id.etVacContent);

        try{
            dbHelper=new DBHelper(getApplicationContext());
            db=dbHelper.getWritableDatabase();
            Intent it=getIntent();
            if(it.getBooleanExtra("update",false)){
                id=it.getIntExtra("id",0);
                cursor2=db.rawQuery("select * from VacPlan where vp_num="+id,null);
                cursor2.moveToNext();
                from_till[0]=cursor2.getString(cursor2.getColumnIndex("vp_from"));
                from_till[0]=cursor2.getString(cursor2.getColumnIndex("vp_from"));
            }else {
                from_till[0] = it.getStringExtra("date");
            }
                String date[] = from_till[0].split("-");
                year = date[0];
                month = date[1];
                day = date[2];
                tvYear1.setText(year + "년");
                tvStart.setText(month + "월 " + day + "일");
            try{
                cursor=db.rawQuery("select Vacation.v_num as '_id' ,Vacation.*,v_days-SUM(uv_days) as 'left',sum(uv_days) as 'sum' from Vacation" +
                        " left JOIN UseVacation " +
                        "on Vacation.v_num=UseVacation.v_num " +
                        "group by Vacation.v_num;",null);

            }catch (Exception e){
                e.printStackTrace();
            }
            vacationAdapter=new VacationAdapter(getApplicationContext(),selectArrayList);

        }catch (Exception e){
            e.printStackTrace();
        }

        lvVac.setAdapter(vacationAdapter);

        adapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_singlechoice);
        Vacation v;
        String s;
        while(cursor.moveToNext()){
            v=new Vacation(cursor.getInt(cursor.getColumnIndex("_id")),cursor.getString(cursor.getColumnIndex("v_type")),
                    cursor.getInt(cursor.getColumnIndex("v_days")),cursor.getInt(cursor.getColumnIndex("left")));
            //v.info();
            vacationArrayList.add(v);
            s=v.type+"    "+v.day+"일";
            if(cursor.getInt(cursor.getColumnIndex("sum"))!=0)
                s+="("+v.left+"일)";
            adapter.add(s);
        }

        adapter.notifyDataSetChanged();

        btnVacAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createListDialog();
            }
        });


        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(etVacName.getText().toString().length()==0||etVacName.getText()==null){
                    Toast.makeText(getApplicationContext(),"휴가 이름을 입력해주세요.",Toast.LENGTH_SHORT).show();
                    return;
                }else if(selectArrayList.size()==0){
                    Toast.makeText(getApplicationContext(),"휴가를 등록해주세요.",Toast.LENGTH_SHORT).show();
                    return;
                }

                String query= "insert into VacPlan values(null,"+
                        ""+total+", "+
                        "'"+from_till[0]+"',"+
                        "'"+from_till[1]+"',"+
                        "'"+etVacName.getText().toString()+"', "+
                        " null);";

                exeQuery(query);

                Vacation vacation;
                try {
                    cursor2 = db.rawQuery("select vp_num from VacPlan order by vp_num desc", null);
                    cursor2.moveToNext();
                    int vp_num = cursor2.getInt(0);
                    for (int i = 0; i < selectArrayList.size(); i++) {
                        vacation = selectArrayList.get(i);

                        query = "insert into UseVacation values(null," +
                                "" + vacation._id + ", " +
                                "" + vp_num + ", " +
                                "null," +
                                "" + useDays.get(i) + ", " +
                                " null);";

                        exeQuery(query);

                        Toast.makeText(getApplicationContext(),"휴가 계획 생성 완료!",Toast.LENGTH_SHORT).show();

                        setResult(RESULT_OK);
                        finish();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }
    private void setBackDay(){
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        try {
            Date date = df.parse(year+"-"+month+"-"+day);

            // 날짜 더하기
            cal.setTime(date);
            cal.add(Calendar.DATE, total-1);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        tvArrange.setText((total-1)+"박 "+total+"일");
        tvYear2.setText(cal.get(Calendar.YEAR)+"년");
        tvBack.setText((cal.get(Calendar.MONTH)+1)+"월 "+(cal.get(Calendar.DAY_OF_MONTH)<10?"0"+cal.get(Calendar.DAY_OF_MONTH):cal.get(Calendar.DAY_OF_MONTH))+"일");
        from_till[1]=cal.get(Calendar.YEAR)+"-"+
                ((cal.get(Calendar.MONTH)+1)<10?"0"+(cal.get(Calendar.MONTH)+1):(cal.get(Calendar.MONTH)+1))+"-"+(cal.get(Calendar.DAY_OF_MONTH)<10?"0"+cal.get(Calendar.DAY_OF_MONTH):cal.get(Calendar.DAY_OF_MONTH));
    }

    public boolean exeQuery(String query) {
        try {
            db.execSQL(query);
            return true;
        } catch (Exception e) {
            //tvtest.append(e+"");
            e.printStackTrace();
            return false;
        }
    }

    int total=0;
    private class VacationAdapter extends BaseAdapter {
        private final ArrayList<Vacation> list;
        private final LayoutInflater inflater;
        private int temp;
            @SuppressWarnings("deprecation")
            public VacationAdapter(Context context,ArrayList<Vacation> list) {
                this.list = list;
                this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_vacation2, parent, false);
                holder = new ViewHolder();
                holder.tvType=convertView.findViewById(R.id.tvType);
                holder.npDays=convertView.findViewById(R.id.npDays);
                holder.ivDel=convertView.findViewById(R.id.ivDel);
                convertView.setTag(holder);
            }else {
                holder = (ViewHolder)convertView.getTag();
            }
            final Vacation v=getItem(position);
            holder.tvType.setText(v.type);
            if(v.left==0)
                holder.npDays.setMax(v.day);
            else
                holder.npDays.setMax(v.left);
            final int p=position;
            temp=holder.npDays.getValue();
            useDays.set(p,holder.npDays.getValue());
            //Log.d("test",useDays.get(p)+"");
            holder.ivDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    total-=temp;
                    setBackDay();
                    selectArrayList.remove(p);
                    notifyDataSetChanged();
                }
            });
            holder.npDays.setValueChangedListener(new ValueChangedListener() {
                @Override
                public void valueChanged(int value, ActionEnum action) {
                    if(temp>value)
                        total--;
                    else
                        total++;
                    temp=value;
                    useDays.set(p,value);
                    //Log.d("test",total+" "+value);
                    setBackDay();
                }
            });


            return convertView;
        }

        @Override
        public Vacation getItem(int position) {
            return list.get(position);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        class ViewHolder {
                TextView tvType;
                NumberPicker npDays;
                ImageView ivDel;
        }
    }
    public void createListDialog(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("휴가 목록");

        //어답터 , 클릭이벤트 설정
        alert.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Log.d("test",which+"");
                selectArrayList.add(vacationArrayList.get(which));
                useDays.add(1);
                //vacationArrayList.get(which).info();
                total++;
                vacationAdapter.notifyDataSetChanged();
                setBackDay();
            }
        });
        alert.show();
    }

    private class Vacation{
        Vacation(){}
        Vacation(int _id,String type,int day,int left){
            this._id=_id;
            this.type=type;
            this.day=day;
            this.left=left;
        }
        int _id;
        int left;
        String type;
        int day;
        int useday;
        void info(){
            Log.d("test",_id+" "+type+" "+day+" "+left);
        }
    }
}
