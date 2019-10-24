package com.js98012.vacplanner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Toast;

import com.js98012.vacplanner.DB.DBHelper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WishaddActivity extends AppCompatActivity {
    EditText etContent, etTitle;
    ImageView ivSubmit, ivDel, ivThumb;
    Button btnUpload, btnConfirm;
    RatingBar rbImportance;
    LinearLayout llEdits;

    int _id,vs_num;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_ALBUM = 3;


    boolean isUpdate = false;
    boolean isShcehdule=false;

    DBHelper dbHelper;
    SQLiteDatabase db;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishadd);
        getSupportActionBar().setElevation(0);

        setTitle("뭔 위시야?");

        etContent = findViewById(R.id.etContent);
        etTitle = findViewById(R.id.etTitle);

        ivThumb = findViewById(R.id.ivThumb);
        llEdits = findViewById(R.id.llEdits);
        ivDel = findViewById(R.id.ivDel);

        rbImportance = findViewById(R.id.rbImportance);
        btnConfirm = findViewById(R.id.btnConfirm);
        btnUpload = findViewById(R.id.btnUpload);

        try {
            Intent intent = getIntent();
            String action = intent.getAction();
            String type = intent.getType();
            if (Intent.ACTION_SEND.equals(action) && type != null) {
                if (type.equals("text/plain"))
                    etContent.setText(intent.getStringExtra(Intent.EXTRA_TEXT));
                else if (type.startsWith("image/")) {
                    Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
                    ivThumb.setImageURI(imageUri);
                }
            } else {
                Bitmap bitmap = BitmapFactory.decodeByteArray(intent.getByteArrayExtra("img"), 0, intent.getByteArrayExtra("img").length);
                ivThumb.setImageBitmap(bitmap);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        dbHelper = new DBHelper(getApplicationContext());
        db = dbHelper.getWritableDatabase();

        try {
            Intent intent = getIntent();
            isUpdate = intent.getBooleanExtra("update", false);
            isShcehdule = intent.getBooleanExtra("schedule", false);
            _id = intent.getIntExtra("id", 0);
            vs_num = intent.getIntExtra("vs_num", 0);


            cursor = db.rawQuery("select * from WishList where w_num=" + _id, null);
            cursor.moveToNext();
            String _title = cursor.getString(cursor.getColumnIndex("w_title"));
            String _content = cursor.getString(cursor.getColumnIndex("w_content"));
            int _rating = cursor.getInt(cursor.getColumnIndex("w_importance"));
            try {
                Bitmap bitmap = getAppIcon(cursor.getBlob(cursor.getColumnIndex("w_img")));
                ivThumb.setImageBitmap(bitmap);
            } catch (Exception e) {
            }
            etTitle.setText(_title);
            etContent.setText(_content);
            rbImportance.setRating(_rating);

            //불러오기의 경우
            llEdits.setVisibility(View.VISIBLE);
            ivDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alt = new AlertDialog.Builder(WishaddActivity.this);
                    if(isShcehdule){
                        alt.setTitle("일정에서 삭제하시겠습니까?");
                        alt.setPositiveButton("예", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String query = "delete from VacSchedule where vs_num = " + vs_num;
                                db.execSQL(query);
                                Toast.makeText(getApplicationContext(), "삭제완료", Toast.LENGTH_SHORT).show();
                                setResult(RESULT_OK);
                                finish();
                            }
                        });
                    }else{
                        alt.setTitle("정말 삭제하시겠습니까?\n이 위시가 모두 삭제됩니다.");
                        alt.setPositiveButton("예", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String query = "delete from WishList where w_num = " + _id;
                                db.execSQL(query);
                                try {
                                    query = "delete from VacSchedule where w_num = " + _id;
                                    db.execSQL(query);
                                } catch (Exception e) {
                                }
                                Toast.makeText(getApplicationContext(), "삭제완료", Toast.LENGTH_SHORT).show();
                                setResult(RESULT_OK);
                                finish();
                            }
                        });
                    }
                    alt.setNegativeButton("아니오", null);
                    alt.show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImageFromAlbum();
            }
        });
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etTitle.getText().toString().length()==0){
                    Toast.makeText(getApplicationContext(),"제목을 입력해주세요.",Toast.LENGTH_SHORT).show();
                    return;
                }else if(rbImportance.getRating()==0){
                    Toast.makeText(getApplicationContext(),"선호도를 입력해주세요..",Toast.LENGTH_SHORT).show();
                    return;
                }

                if (isUpdate) {
                    String query = "";

                    query = "update WishList " +
                            "set " +
                            "w_title=?," +
                            "w_content=?," +
                            "w_importance=?," +
                            "w_date=?," +
                            "w_img=? " +
                            "where w_num=" + _id + ";";
                    // tvtest.setText(query + "");
                    SQLiteStatement p = db.compileStatement(query);

                    Date todayDate = new Date();
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

                    p.bindString(1, etTitle.getText().toString());
                    p.bindString(2, etContent.getText().toString());
                    p.bindDouble(3, rbImportance.getRating());
                    p.bindString(4, formatter.format(todayDate));
                    try{
                        p.bindBlob(5, getByteArrayFromDrawable(ivThumb.getDrawable()));
                    }catch (Exception e){
                        p.bindNull(5);
                    }
                    p.execute();
                    Toast.makeText(getApplicationContext(), "위시 수정 완료", Toast.LENGTH_SHORT).show();
                } else {
                    String query = "";

                    query = "insert into WishList values(null, ?,?,?,?,?,null);";
                    // tvtest.setText(query + "");
                    SQLiteStatement p = db.compileStatement(query);

                    Date todayDate = new Date();
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

                    p.bindString(1, etTitle.getText().toString());
                    p.bindString(2, etContent.getText().toString());
                    p.bindDouble(3, rbImportance.getRating());
                    p.bindString(4, formatter.format(todayDate));
                    try{
                        p.bindBlob(5, getByteArrayFromDrawable(ivThumb.getDrawable()));
                    }catch (Exception e){
                        p.bindNull(5);
                    }
                    p.execute();

                    Toast.makeText(getApplicationContext(), "위시 등록 완료", Toast.LENGTH_SHORT).show();
                }
                setResult(RESULT_OK);
                finish();
                //refreshDB();
            }
        });
    }

    public Bitmap getAppIcon(byte[] b) {
        return BitmapFactory.decodeByteArray(b, 0, b.length);
    }

    public byte[] getByteArrayFromDrawable(Drawable d) {
        try {
            Bitmap bitmap = ((BitmapDrawable) d).getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] data = stream.toByteArray();
            return data;
        } catch (Exception e) {
            return null;
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

    private void getImageFromAlbum() {
        Intent it = new Intent(Intent.ACTION_PICK);
        it.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(it, PICK_FROM_ALBUM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FROM_ALBUM && data != null) {
            Uri dataUri = data.getData();
            if (dataUri != null)
                cropImageFromAlbum(dataUri);
        } else if (requestCode == CROP_FROM_ALBUM && data != null) {
            if (resultCode != RESULT_OK)
                return;
            final Bundle extras = data.getExtras();
            if (extras != null) {
                Bitmap photo = extras.getParcelable("data");
                ivThumb.setImageBitmap(photo);
            }
        }
    }

    private void cropImageFromAlbum(Uri inputUri) {
        Intent it = getCropIntent(inputUri);
        startActivityForResult(it, CROP_FROM_ALBUM);
    }

    private Intent getCropIntent(Uri inputUri) {
        Intent it = new Intent("com.android.camera.action.CROP");
        it.setDataAndType(inputUri, "image/*");
        it.putExtra("aspectX", 1);
        it.putExtra("aspectY", 1);
        it.putExtra("outputX", 200);
        it.putExtra("outputY", 200);
        it.putExtra("scale", true);
        it.putExtra("return-data", true);

        return it;
    }
}
