package com.pinners.olders;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.database.Cursor;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    ImageButton registrationBtn, favoriteContactBtn, settingBtn, profileBtn;
    Button sendMsgBtn;
    TextView nameTv;
    TextView phoneNumberTv;
    TextView groupTv;
    LinearLayout profileBackgroundL;

    private static final int PERMISSION_SEND_SMS = 101;
    private static final int PERMISSION_WRITE_EXTERNAL_STORAGE = 102;
    private static final int PERMISSION_READ_EXTERNAL_STORAGE = 103;
    private static final int PERMISSION_CALL_PHONE = 104;
    private static final String folderName = "HelpingCloud";
    private static final String backgroundName = "background";

    DBHelper db;
    GPSTracker gps;
    double lng=0, lat=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        startActivity(new Intent(this, SplashActivity.class));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Permission
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{ Manifest.permission.SEND_SMS}, PERMISSION_SEND_SMS);
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_WRITE_EXTERNAL_STORAGE);
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_READ_EXTERNAL_STORAGE);
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{ Manifest.permission.CALL_PHONE}, PERMISSION_CALL_PHONE);
        }

        checkDangerousPermissions();

        // Init
        init();

        // profile setting
        getPreferences();
        makeSDKDirectory();
        try{
            Bitmap bitMapImage = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+folderName+"/"+backgroundName+".jpg");
            Drawable drawableProfileBackgroundImage = new BitmapDrawable(getResources(), bitMapImage);
            profileBackgroundL.setBackground(drawableProfileBackgroundImage);
        }catch(Exception e){
            Log.e("Exception", e.getMessage());
            profileBackgroundL.setBackgroundColor(Color.BLACK);
        }

        // GPS
        gps = new GPSTracker(MainActivity.this);
        if(gps.canGetLocation()){
            lat = gps.getLatitude();
            lng = gps.getLongitude();
        }else{
            gps.showSettingsAlert();
        }

        // Button Click Listener
        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                intent.putExtra("myName",nameTv.getText().toString());
                intent.putExtra("myPhoneNumber",phoneNumberTv.getText().toString());
                intent.putExtra("myGroup",groupTv.getText().toString());
                startActivity(intent);
            }
        });
        registrationBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ContactsActivity.class);
                startActivity(intent);
            }
        });
        favoriteContactBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FavoriteContactsActivity.class);
                startActivity(intent);
            }
        });
        sendMsgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //sendSMS("01027573200", "http://maps.google.com/maps?q="+lat+","+lng);
                phoneCall();
            }
        });
        settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });
    }

    protected  void onResume(){
        super.onResume();
        getPreferences();
        try{
            Bitmap bitMapImage = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+folderName+"/"+backgroundName+".jpg");
            Drawable drawableProfileBackgroundImage = new BitmapDrawable(getResources(), bitMapImage);
            profileBackgroundL.setBackground(drawableProfileBackgroundImage);
        }catch(Exception e){
            Log.e("Exception", e.getMessage());
            profileBackgroundL.setBackgroundColor(Color.BLACK);
        }
    }

    private void init(){
        registrationBtn = (ImageButton) findViewById(R.id.registrationBtn);
        sendMsgBtn = (Button) findViewById(R.id.sendMsgBtn);
        favoriteContactBtn = (ImageButton) findViewById(R.id.favoriteContactBtn);
        settingBtn= (ImageButton) findViewById(R.id.settingBtn);
        profileBtn =(ImageButton) findViewById(R.id.profileBtn);
        nameTv = (TextView) findViewById(R.id.nameTv);
        phoneNumberTv = (TextView) findViewById(R.id.phoneNumberTv);
        groupTv = (TextView) findViewById(R.id.groupTv);
        profileBackgroundL = (LinearLayout) findViewById(R.id.profileBackgroundL);
        db = new DBHelper(this);
    }

    private void makeSDKDirectory(){
        String str = Environment.getExternalStorageState();
        if ( str.equals(Environment.MEDIA_MOUNTED)) {

            String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+folderName;
            File file = new File(dirPath);
            if( !file.exists() )  // 원하는 경로에 폴더가 있는지 확인
                file.mkdirs();
        }
        else
            Toast.makeText(getApplication(), "SD Card 인식 실패", Toast.LENGTH_SHORT).show();
    }

    private void sendSMS(String phoneNumber, String message) {
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";

        PendingIntent sentPI = PendingIntent.getBroadcast(getApplication(), 0, new Intent(SENT), 0);
        PendingIntent deliveredPI = PendingIntent.getBroadcast(getApplication(), 0, new Intent(DELIVERED), 0);

        registerReceiver(new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS sent", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(getBaseContext(), "Generic failure", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(getBaseContext(), "No service", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(getBaseContext(), "Null PDU", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(getBaseContext(), "Radio off", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(SENT));

        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS delivered", Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getBaseContext(), "SMS not delivered", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(DELIVERED));

        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
    }
    private void phoneCall(){
        Uri uri = Uri.parse("tel:01012345678"); //전화와 관련된 Data는 'Tel:'으로 시작. 이후는 전화번호
        Intent intent = new Intent(Intent.ACTION_CALL,uri); //시스템 액티비티인 Dial Activity의 action값
        //startActivity(intent);
    }
    // SharedPreferences
    private void getPreferences(){
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        nameTv.setText( pref.getString("myName", ""));
        phoneNumberTv.setText(pref.getString("myPhoneNumber", ""));
        groupTv.setText(pref.getString("myGroup", ""));
    }

    private void checkDangerousPermissions() {
        String[] permissions = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        int permissionCheck = PackageManager.PERMISSION_GRANTED;
        for (int i = 0; i < permissions.length; i++) {
            permissionCheck = ContextCompat.checkSelfPermission(this, permissions[i]);
            if (permissionCheck == PackageManager.PERMISSION_DENIED) {
                break;
            }
        }
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "권한 있음", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "권한 없음", Toast.LENGTH_LONG).show();
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
                Toast.makeText(this, "권한 설명 필요함.", Toast.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(this, permissions, 1);
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, permissions[i] + " 권한이 승인됨.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, permissions[i] + " 권한이 승인되지 않음.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}

