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
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private ImageButton registrationBtn, favoriteContactBtn, settingBtn, profileBtn;
    private Button sendMsgBtn;
    private TextView nameTv, phoneNumberTv, groupTv, phoneNumberStaticTv, groupStaticTv;
    private TextView favoriteTv1, favoriteTv2, contactsTv1, contactsTv2, settingTv1, settingTv2, positionTv1, positionTv2;
    private ImageView profileBackgroundIv;

    private static final String folderName = "HelpingCloud";
    private static final String backgroundName = "background";

    DBHelper db;
    GPSTracker gps;
    double lng=0, lat=0;

    private String fontSize = "small";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        startActivity(new Intent(this, SplashActivity.class));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        //        WindowManager.LayoutParams.FLAG_FULLSCREEN);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        // Permission
        checkDangerousPermissions();

        // Init
        init();

        // profile setting
        getPreferences();
        makeSDKDirectory();
        try{
            Bitmap bitMapImage = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+folderName+"/"+backgroundName+".jpg");
            Drawable drawableProfileBackgroundImage = new BitmapDrawable(getResources(), bitMapImage);
            profileBackgroundIv.setBackground(drawableProfileBackgroundImage);
        }catch(Exception e){
            Log.e("Exception", e.getMessage());
            profileBackgroundIv.setBackgroundColor(Color.BLACK);
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
                sendSMS("01027573200", "http://maps.google.com/maps?q="+lat+","+lng);
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
            profileBackgroundIv.setBackground(drawableProfileBackgroundImage);
        }catch(Exception e){
            Log.e("Exception", e.getMessage());
            profileBackgroundIv.setBackgroundColor(Color.BLACK);
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
        phoneNumberStaticTv = (TextView) findViewById(R.id.phoneNumberStaticTv);
        groupStaticTv = (TextView) findViewById(R.id.groupStaticTv);
        profileBackgroundIv = (ImageView) findViewById(R.id.profileBackgroundIv);
        favoriteTv1=(TextView) findViewById(R.id.favoriteTv1);
        favoriteTv2=(TextView) findViewById(R.id.favoriteTv2);
        contactsTv1=(TextView) findViewById(R.id.contactsTv1);
        contactsTv2=(TextView) findViewById(R.id.contactsTv2);
        settingTv1=(TextView) findViewById(R.id.settingTv1);
        settingTv2=(TextView) findViewById(R.id.settingTv2);
        positionTv1=(TextView) findViewById(R.id.positionTv1);
        positionTv2=(TextView) findViewById(R.id.positionTv2);
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
        Uri uri = Uri.parse("tel:010-2757-3200");
        Intent intent = new Intent(Intent.ACTION_CALL,uri);
        try{
            startActivity(intent);
        }catch(SecurityException e){
            Log.e("SecurityException", e.getMessage());
        }
    }

    private void getPreferences(){
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        nameTv.setText( pref.getString("myName", ""));
        phoneNumberTv.setText(pref.getString("myPhoneNumber", ""));
        groupTv.setText(pref.getString("myGroup", ""));
        fontSize = pref.getString("fontSize","");
        if(fontSize.equals("small")){
            nameTv.setTextSize(30);
            phoneNumberTv.setTextSize(18);
            groupTv.setTextSize(18);
            phoneNumberStaticTv.setTextSize(15);
            groupStaticTv.setTextSize(15);

            favoriteTv1.setTextSize(18);
            favoriteTv2.setTextSize(13);
            contactsTv1.setTextSize(18);
            contactsTv2.setTextSize(13);
            settingTv1.setTextSize(18);
            settingTv2.setTextSize(13);
            positionTv1.setTextSize(18);
            positionTv2.setTextSize(13);

            profileBtn.setPadding(0,0,0,0);
        }
        else{
            nameTv.setTextSize(32);
            phoneNumberTv.setTextSize(26);
            groupTv.setTextSize(26);
            phoneNumberStaticTv.setTextSize(17);
            groupStaticTv.setTextSize(17);

            favoriteTv1.setTextSize(26);
            favoriteTv2.setTextSize(15);
            contactsTv1.setTextSize(26);
            contactsTv2.setTextSize(15);
            settingTv1.setTextSize(26);
            settingTv2.setTextSize(15);
            positionTv1.setTextSize(26);
            positionTv2.setTextSize(15);

            profileBtn.setPadding(0,30,0,0);
        }

    }

    private void checkDangerousPermissions() {
        String[] permissions = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.SEND_SMS,
                Manifest.permission.READ_CONTACTS
        };

        int permissionCheck = PackageManager.PERMISSION_GRANTED;
        for (int i = 0; i < permissions.length; i++) {
            permissionCheck = ContextCompat.checkSelfPermission(this, permissions[i]);
            if (permissionCheck == PackageManager.PERMISSION_DENIED) {
                break;
            }
        }
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            //Toast.makeText(this, "권한 있음", Toast.LENGTH_LONG).show();
        } else {
            //Toast.makeText(this, "권한 없음", Toast.LENGTH_LONG).show();
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
                //Toast.makeText(this, "권한 설명 필요함.", Toast.LENGTH_LONG).show();
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
                    //Toast.makeText(this, permissions[i] + " 권한이 승인됨.", Toast.LENGTH_LONG).show();
                } else {
                    //Toast.makeText(this, permissions[i] + " 권한이 승인되지 않음.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}

