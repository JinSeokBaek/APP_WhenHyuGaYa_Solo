package com.js98012.vacplanner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RemoteViews;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.js98012.vacplanner.DB.DBHelper;
import com.travijuu.numberpicker.library.NumberPicker;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SettingActivity extends AppCompatActivity {
    SharedPreferences settings;
    SharedPreferences.Editor editor;

    TextView tvIn,tvOut,tvToday,tvName,tvPercent;
    Button btnSetting,btnAdd;
    View dialogView;
    DBHelper dbHelper;
    SQLiteDatabase db;
    Cursor cursor,cursor2;
    Switch sw;
    ListView lvVac;

    NotificationSet notificationSet;

    MyCursorAdapter myCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        setTitle("유저 설정");
        tvIn=findViewById(R.id.tvIn);
        tvOut=findViewById(R.id.tvOut);
        tvName=findViewById(R.id.tvName);
        tvToday=findViewById(R.id.tvToday);
        tvPercent=findViewById(R.id.tvPercent);
        btnSetting=findViewById(R.id.btnSetting);

        btnAdd=findViewById(R.id.btnAdd);
        sw=findViewById(R.id.swNoti);
        lvVac=findViewById(R.id.lvVac);

        try{
            dbHelper=new DBHelper(getApplicationContext());
            db=dbHelper.getWritableDatabase();
            cursor=db.rawQuery("select Vacation.v_num as '_id' ,Vacation.*,v_days-SUM(uv_days) as 'left',SUM(uv_days) as 'sum' from Vacation" +
                    " left JOIN UseVacation " +
                    "on Vacation.v_num=UseVacation.v_num " +
                    "group by Vacation.v_num;",null);

            myCursorAdapter=new MyCursorAdapter(getApplicationContext(),cursor);
            lvVac.setAdapter(myCursorAdapter);
        }catch (Exception e){
            e.printStackTrace();
        }



        settings=getSharedPreferences("settings",MODE_PRIVATE);
        tvName.setText(settings.getString("name","이름을 입력해주세요."));
        tvIn.setText("입대일 : "+settings.getString("in","입대일을 입력해주세요."));
        tvOut.setText("전역일 : "+settings.getString("out","전역일을 입력해주세요."));

        if(!settings.getBoolean("isSetted",false)){
            makeDig(false).show();
        }
        notificationSet=new NotificationSet(this);
        sw.setChecked(settings.getBoolean("isNoti",false));
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(sw.isChecked()){
                    notificationSet.notificationSomethings();
                    editor=settings.edit();
                    editor.putBoolean("isNoti",true);
                    editor.apply();
                }
                else{
                    notificationSet.cancel();
                    editor=settings.edit();
                    editor.putBoolean("isNoti",false);
                    editor.apply();
                }

            }
        });
        tvPercent.setText(settings.getBoolean("isSetted",false)?
                calcu(settings.getString("in","0000-00-00"),settings.getString("out","0000-00-00"))+"%":"null");

        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeDig(true).show();
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogView = View.inflate(SettingActivity.this, R.layout.layout_vacadd, null);
                AlertDialog.Builder alt = new AlertDialog.Builder(SettingActivity.this);
                alt.setView(dialogView);
                final EditText etContent=dialogView.findViewById(R.id.etContent);
                final Spinner spType=dialogView.findViewById(R.id.spType);
                final NumberPicker npDays=dialogView.findViewById(R.id.vacDays);

                alt.setPositiveButton("추가", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String type=(String)spType.getSelectedItem();
                        String content=etContent.getText().toString();
                        int day=npDays.getValue();

                        String query = "insert into Vacation values(null,"+
                                "'"+type+"',"+
                                ""+day+","+
                                "'"+content+"',"+
                                " null);";
                        exeQuery(query);
                        myCursorAdapter.getCursor().requery();
                        myCursorAdapter.notifyDataSetChanged();
                        Toast.makeText(getApplicationContext(),"휴가 추가완료!",Toast.LENGTH_SHORT).show();
                    }
                });
                alt.setNegativeButton("취소",null);

                alt.show();
            }
        });

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
    public AlertDialog.Builder makeDig(final boolean update){
        dialogView = View.inflate(SettingActivity.this, R.layout.layout_dialog, null);
        AlertDialog.Builder alt = new AlertDialog.Builder(SettingActivity.this);
        alt.setView(dialogView);
        final EditText etName=dialogView.findViewById(R.id.etName);
        final EditText etIn=dialogView.findViewById(R.id.etIn);
        final EditText etOut=dialogView.findViewById(R.id.etOut);

        final NumberPicker npNormal=dialogView.findViewById(R.id.vacNormal);
        if(update) {
            etName.setText(settings.getString("name",""));
            etIn.setText(settings.getString("in",""));
            etOut.setText(settings.getString("out",""));
            npNormal.setValue(settings.getInt("normal", 25));
        }
        alt.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                editor=settings.edit();
                editor.putBoolean("isSetted",true);
                editor.putString("name",etName.getText().toString());
                editor.putString("in",etIn.getText().toString());
                editor.putString("out",etOut.getText().toString());
                editor.putInt("normal",npNormal.getValue());
                tvPercent.setText(calcu(etIn.getText().toString(),etOut.getText().toString())+"%");

                editor.apply();
                tvName.setText(settings.getString("name","이름을 입력해주세요."));
                tvIn.setText("입대일 : "+settings.getString("in","입대일을 입력해주세요."));
                tvOut.setText("전역일 : "+settings.getString("out","전역일을 입력해주세요."));
                String query;
                if(update){
                    query="update from Vacation " +
                            "set v_days = "+npNormal.getValue()+" " +
                            "where v_type='연가'";
                }else{
                    query= "insert into Vacation values(null,"+
                            "'연가', "+
                            ""+npNormal.getValue()+", "+
                            "'연가',"+
                            " null);";
                }
                exeQuery(query);
                myCursorAdapter.getCursor().requery();
                myCursorAdapter.notifyDataSetChanged();


            }
        });
        alt.setNegativeButton("취소",null);
        return alt;
    }
    public String calcu(String begin,String end){
        try{
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");


            Date beginDate = formatter.parse(begin);
            // Date todayDate=formatter.parse(vs_num);
            Date todayDate=new Date();
            Date endDate = formatter.parse(end);
            long diff1 = endDate.getTime() - beginDate.getTime();

            long diff2 = endDate.getTime() - todayDate.getTime();

            long diff3=todayDate.getTime()-beginDate.getTime();

            float diffDays1 = diff1 / (24 * 60 * 60 * 1000);
            float diffDays2 = diff2 / (24 * 60 * 60 * 1000);
            float diffDays3 = diff3 / (24 * 60 * 60 * 1000);

            float percent=(diffDays3/diffDays1)*100;
            tvToday.setText("오늘 : "+formatter.format(todayDate));
            System.out.println("남은일수 : "+(int)diffDays2+"\n한일수 : "+		(int)diffDays3+"\n"+(int)percent+"% 했음");
            return String.valueOf((int)percent);
        }catch(Exception e){
            e.getStackTrace();
            System.out.println(e.toString());
            return null;
        }
    }
    class MyCursorAdapter extends CursorAdapter {
        @SuppressWarnings("deprecation")
        public MyCursorAdapter(Context context, Cursor c) {
            super(context, c);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View v = inflater.inflate(R.layout.item_vacation, parent, false);
            return v;
        }
        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            TextView vacType=(TextView)view.findViewById(R.id.vacType);
            TextView vacDays=(TextView)view.findViewById(R.id.vacDays);
            ImageView ivEdit=view.findViewById(R.id.ivEdit);
            ImageView ivDel=view.findViewById(R.id.ivDel);
            LinearLayout llEdits=view.findViewById(R.id.llEdits);

            final int _id=cursor.getInt(cursor.getColumnIndex("_id"));

            if((cursor.getString(cursor.getColumnIndex("v_type")).equals("연가")))
                llEdits.setVisibility(View.INVISIBLE);

            final int day=cursor.getInt(cursor.getColumnIndex("v_days"));
            final int day2=cursor.getInt(cursor.getColumnIndex("sum"));
            final String content=cursor.getString(cursor.getColumnIndex("v_content"));
            vacType.setText(cursor.getString(cursor.getColumnIndex("v_type")));
            vacDays.setText(day+"일");
            int left=cursor.getInt(cursor.getColumnIndex("left"));
            //Log.d("test",left+" "+day2);
            if(day2!=0)
                vacDays.append("("+left + "일)");

            ivEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogView = View.inflate(SettingActivity.this, R.layout.layout_vacadd, null);
                    AlertDialog.Builder alt = new AlertDialog.Builder(SettingActivity.this);
                    alt.setView(dialogView);
                    alt.setTitle("휴가 추가");
                    final EditText etContent=dialogView.findViewById(R.id.etContent);
                    final Spinner spType=dialogView.findViewById(R.id.spType);
                    final NumberPicker npDays=dialogView.findViewById(R.id.vacDays);

                    npDays.setValue(day);
                    etContent.setText(content);

                    alt.setPositiveButton("수정", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String type=(String)spType.getSelectedItem();
                            String content=etContent.getText().toString();
                            int day=npDays.getValue();

                            String query = "update Vacation " +
                                    "set " +
                                    "v_type="+"'"+type+"' ,"+
                                    "v_days="+day+", "+
                                    "v_content='"+content+"' "+
                                    "where v_num="+_id+
                                    ";";
                            exeQuery(query);
                            getCursor().requery();
                            notifyDataSetChanged();
                            Toast.makeText(getApplicationContext(),"휴가 수정완료!",Toast.LENGTH_SHORT).show();
                        }
                    });
                    alt.setNegativeButton("취소",null);

                    alt.show();
                }
            });
            ivDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alt=new AlertDialog.Builder(SettingActivity.this);
                    alt.setTitle("정말 삭제하시겠습니까?");
                    alt.setPositiveButton("예", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String query="delete from Vacation where v_num = "+_id;
                            db.execSQL(query);
                            getCursor().requery();
                            notifyDataSetChanged();
                        }
                    });
                    alt.setNegativeButton("아니오",null);
                    alt.show();
                }
            });

        }
    }

    public class NotificationSet {

        SharedPreferences setting;
        SharedPreferences.Editor editor;


        public RemoteViews contentView;
        NotificationManager nm;

        Context context;

        NotificationSet(Context context) {
            this.context = context;
            setting = context.getSharedPreferences("setting", MODE_PRIVATE);
            nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }

        public void notificationSomethings() {
            Intent notificationIntent = new Intent(context, MainActivity.class);
            //notificationIntent.putExtra("notificationId", 9999); //전달할 값
            PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            Intent notifiIntent=new Intent(context,WishaddActivity.class);
            //notifiIntent.putExtra("test","test");
            notifiIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notifiIntent,  PendingIntent.FLAG_UPDATE_CURRENT);

//            Intent notifiIntent2=new Intent(context,PopupActivity.class);
//            notifiIntent2.putExtra("img",takeScreenshot());
//            notifiIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            PendingIntent pendingIntent2 = PendingIntent.getActivity(context, 0, notifiIntent2,  PendingIntent.FLAG_UPDATE_CURRENT);


            NotificationCompat.Builder builder = getNotificationBuilder("wishlist","위시리스트");

            contentView = new RemoteViews(context.getPackageName(), R.layout.layout_notify);
            contentView.setOnClickPendingIntent(R.id.ivAdd,pendingIntent);

            //contentView.setOnClickPendingIntent(R.id.noti_btn2,pendingIntent2);

            builder.setSmallIcon(R.drawable.noti)
                    .setContent(contentView)
                    .setContentIntent(contentIntent)
                    .setOngoing(true);
            // 숫자 설정
            builder.setNumber(100);
            // 타이틀 설정
            builder.setContentTitle("위시리스트");

            // 메시지 객체를 생성
            Notification notification = builder.build();

            // 알림 메시지 관리 객체를 추출한다.
            NotificationManager manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            // 알림 메시지를 출력한다.
            manager.notify(40, notification);
        }
        public NotificationCompat.Builder getNotificationBuilder(String id, String name){
            NotificationCompat.Builder builder = null;

            // OS 버전별로 분기한다.
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){     // 안드로이드 8.0 오레오 버전 이상
                // 알림 메시지를 관리하는 객체를 추출한다.
                NotificationManager manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
                // 채널 객체를 생성한다.
                NotificationChannel channel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH);
                // 메시지 출력시 단말기 LED를 사용할 것인지..
                channel.enableLights(true);
                // LED 색상을 설정
                channel.setLightColor(Color.RED);
                // 진동 사용여부
                channel.enableVibration(true);
                // 알림 메시지를 관리하는 객체에 채널을 등록한다.
                manager.createNotificationChannel(channel);
                // 메시지 생성을 위한 객체를 생성한다.
                builder = new NotificationCompat.Builder(context, id);
            } else {
                builder = new NotificationCompat.Builder(context);
            }

            return builder;
        }
        public void cancel(){
            nm.cancel(40);
        }
    }
}
