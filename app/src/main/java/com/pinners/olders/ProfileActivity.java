package com.pinners.olders;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Administrator on 2016-06-22.
 */
public class ProfileActivity extends AppCompatActivity {
    private EditText myNameEt, myPhoneNumberEt, myGroupEt;
    private Button profileOkBtn, profileImageBtn;
    String myName;
    String myPhoneNumber;
    String myGroup;
    private Bitmap profilePhoto;
    private static final String folderName = "HelpingCloud";
    private static final String backgroundName = "background";
    private static final int REQ_CODE_IMAGE = 1;

    private static final int PERMISSION_WRITE_EXTERNAL_STORAGE = 102;
    private static final int PERMISSION_READ_EXTERNAL_STORAGE = 103;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        if (ContextCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ProfileActivity.this,new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_WRITE_EXTERNAL_STORAGE);
            ActivityCompat.requestPermissions(ProfileActivity.this,new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_READ_EXTERNAL_STORAGE);
        }

        init();

        Intent intent = getIntent();
        myName = intent.getStringExtra("myName");
        myPhoneNumber = intent.getStringExtra("myPhoneNumber");
        myGroup = intent.getStringExtra("myGroup");
        myNameEt.setText(myName);
        myPhoneNumberEt.setText(myPhoneNumber);
        myGroupEt.setText(myGroup);

        profileImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( Intent.ACTION_PICK ) ;
                intent.setType( android.provider.MediaStore.Images.Media.CONTENT_TYPE ) ;
                startActivityForResult( intent, REQ_CODE_IMAGE ) ;
            }
        });

        profileOkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                savePreferences(myNameEt.getText().toString(), myPhoneNumberEt.getText().toString(),  myGroupEt.getText().toString(), profilePhoto);

                //intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                //startActivity(intent);
                finish();
            }
        });

    }
    void init(){
        myNameEt = (EditText) findViewById(R.id.myNameEt);
        myPhoneNumberEt = (EditText) findViewById(R.id.myPhoneNumberEt);
        myGroupEt = (EditText) findViewById(R.id.myGroupEt);
        profileOkBtn = (Button) findViewById(R.id.profileOkBtn);
        profileImageBtn = (Button) findViewById(R.id.profileImageBtn);
    }
    private void savePreferences(String myName, String myPhoneNumber, String myGroup, Bitmap profilePhoto){
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("myName", myName);
        editor.putString("myPhoneNumber", myPhoneNumber);
        editor.putString("myGroup", myGroup);
        editor.commit();
    }

    protected void onActivityResult( int requestCode, int resultCode, Intent data )
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQ_CODE_IMAGE && resultCode == RESULT_OK && null != data){
            final Uri selectImageUri = data.getData();
            final String[] filePathColumn = {MediaStore.Images.Media.DATA};

            final Cursor imageCursor = this.getContentResolver().query(selectImageUri, filePathColumn, null,null,null);
            imageCursor.moveToFirst();

            final int columnIndex = imageCursor.getColumnIndex(filePathColumn[0]);
            final String imagePath = imageCursor.getString(columnIndex);
            imageCursor.close();

            profilePhoto = BitmapFactory.decodeFile(imagePath);
            saveBitmaptoJpeg(profilePhoto, folderName, backgroundName);
        }
    }

    public static void saveBitmaptoJpeg(Bitmap bitmap,String folder, String name){
        String ex_storage = Environment.getExternalStorageDirectory().getAbsolutePath();
        String folder_name = "/"+folder+"/";
        String file_name = name+".jpg";
        String string_path = ex_storage+folder_name;
        File file_path;
        try{
            file_path = new File(string_path);
            if(!file_path.isDirectory()){
                file_path.mkdirs();
            }
            FileOutputStream out = new FileOutputStream(string_path+file_name);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close();

        }catch(FileNotFoundException exception){
            Log.e("FileNotFoundException", exception.getMessage());
        }catch(IOException exception){
            Log.e("IOException", exception.getMessage());
        }
    }
}
