package com.pinners.olders.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pinners.olders.dto.Contact;
import com.pinners.olders.util.DBHelper;
import com.pinners.olders.util.GPSTracker;
import com.pinners.olders.R;
import com.pinners.olders.util.UartService;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;


public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private ImageButton registrationBtn, favoriteContactBtn, settingBtn, profileBtn, accidentPositionBtn;
    private Button sendMsgBtn;
    private TextView nameTv, phoneNumberTv, groupTv, phoneNumberStaticTv, groupStaticTv;
    private TextView favoriteTv1, favoriteTv2, contactsTv1, contactsTv2, settingTv1, settingTv2, positionTv1, positionTv2;
    private ImageView profileBackgroundIv;
    private LinearLayout l1, l2;

    private static final String folderName = "HelpingCloud";
    private static final String backgroundName = "background";

    DBHelper db;
    GPSTracker gps;
    double lng = 0, lat = 0;

    String DEFAULT_PHONE_NUMBER = "010-8907-7586";


    /* ohdoking add*/

    private static final int REQUEST_ENABLE_BT = 2;
    private static final int FINISH_SPLASH = 3;
    public static final String TAG = "nRFUART";

    int firstData = 0;

    int option = 0;
    int count = 0;
    private float x = 0, y = 0, z = 0;

    public Handler checkHandler;

    //    자이로 센서 사용
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mGyroscope;

    boolean gyro = false;

    private static final double EPSILON = 0.1f;
    private static final float NS2S = 1.0f / 1000000000.0f;
    private double gyroscopeRotationVelocity = 0;

    private long timestamp;

    String deviceAddress = null;
    private String fontSize = "small";

    TelephonyManager tMgr;
    String mPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        startActivityForResult(new Intent(this, SplashActivity.class), FINISH_SPLASH);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        //        WindowManager.LayoutParams.FLAG_FULLSCREEN);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        TelephonyManager tMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        mPhoneNumber = tMgr.getLine1Number();

        // Permission
        checkDangerousPermissions();

        // Init
        init();

        // profile setting
        getPreferences();
        makeSDKDirectory();
        try {
            Bitmap bitMapImage = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + folderName + "/" + backgroundName + ".jpg");
            Drawable drawableProfileBackgroundImage = new BitmapDrawable(getResources(), bitMapImage);
            profileBackgroundIv.setBackground(drawableProfileBackgroundImage);
        } catch (Exception e) {
            Log.e("Exception", e.getMessage());
            profileBackgroundIv.setBackgroundColor(Color.BLACK);
        }

        // GPS
        gps = new GPSTracker(MainActivity.this);
        if (gps.canGetLocation()) {
            lat = gps.getLatitude();
            lng = gps.getLongitude();
        } else {
            gps.showSettingsAlert();
        }


        // Button Click Listener
        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                intent.putExtra("myName", nameTv.getText().toString());
                if (nameTv.getText().toString().equals("이름")) {
                    intent.putExtra("myName", "");
                }
                intent.putExtra("myPhoneNumber", phoneNumberTv.getText().toString());
                intent.putExtra("myGroup", groupTv.getText().toString());
                startActivity(intent);
            }
        });
        registrationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ContactsActivity.class);
                startActivity(intent);
            }
        });
        favoriteContactBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FavoriteContactsActivity.class);
                startActivity(intent);
            }
        });
        accidentPositionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PositionActivity.class);
                startActivity(intent);
            }
        });

        sendMsgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Contact> contactLists = db.getAllContacts();

                if (contactLists.size() == 0) {
                    phoneCall(DEFAULT_PHONE_NUMBER);
                } else {

                    for (Contact contact : contactLists) {
                        sendSMS(contact.getPhoneNumber(), mPhoneNumber + "의 주인에게 낙상사고가 발생하였습니다\n" +
                                "\n" +
                                "낙상사고 위치 보기 : http://maps.google.com/maps?q=" + lat + "," + lng);
                        if (firstData == 0) {
                            phoneCall(contact.getPhoneNumber());
                            firstData = 1;
                        }
                    }
                }
//                sendSMS("01027573200", "http://maps.google.com/maps?q="+lat+","+lng);
//                phoneCall();
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


    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        getPreferences();
        try {
            Bitmap bitMapImage = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + folderName + "/" + backgroundName + ".jpg");
            Drawable drawableProfileBackgroundImage = new BitmapDrawable(getResources(), bitMapImage);
            profileBackgroundIv.setBackground(drawableProfileBackgroundImage);
        } catch (Exception e) {
            Log.e("Exception", e.getMessage());
            profileBackgroundIv.setBackgroundColor(Color.BLACK);
        }

        mSensorManager.registerListener(this, mGyroscope, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
        mSensorManager.unregisterListener(this);
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart");
    }

    private void init() {
        registrationBtn = (ImageButton) findViewById(R.id.registrationBtn);
        sendMsgBtn = (Button) findViewById(R.id.sendMsgBtn);
        favoriteContactBtn = (ImageButton) findViewById(R.id.favoriteContactBtn);
        settingBtn = (ImageButton) findViewById(R.id.settingBtn);
        profileBtn = (ImageButton) findViewById(R.id.profileBtn);
        accidentPositionBtn = (ImageButton) findViewById(R.id.accidentPositionBtn);
        nameTv = (TextView) findViewById(R.id.nameTv);
        phoneNumberTv = (TextView) findViewById(R.id.phoneNumberTv);
        groupTv = (TextView) findViewById(R.id.groupTv);
        phoneNumberStaticTv = (TextView) findViewById(R.id.phoneNumberStaticTv);
        groupStaticTv = (TextView) findViewById(R.id.groupStaticTv);
        profileBackgroundIv = (ImageView) findViewById(R.id.profileBackgroundIv);
        favoriteTv1 = (TextView) findViewById(R.id.favoriteTv1);
        favoriteTv2 = (TextView) findViewById(R.id.favoriteTv2);
        contactsTv1 = (TextView) findViewById(R.id.contactsTv1);
        contactsTv2 = (TextView) findViewById(R.id.contactsTv2);
        settingTv1 = (TextView) findViewById(R.id.settingTv1);
        settingTv2 = (TextView) findViewById(R.id.settingTv2);
        positionTv1 = (TextView) findViewById(R.id.positionTv1);
        positionTv2 = (TextView) findViewById(R.id.positionTv2);
        l1 = (LinearLayout) findViewById(R.id.l1);
        l2 = (LinearLayout) findViewById(R.id.l2);
        db = new DBHelper(this);
    }

    private void makeSDKDirectory() {
        String str = Environment.getExternalStorageState();
        if (str.equals(Environment.MEDIA_MOUNTED)) {

            String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + folderName;
            File file = new File(dirPath);
            if (!file.exists())  // 원하는 경로에 폴더가 있는지 확인
                file.mkdirs();
        } else
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

    private void phoneCall(String phoneNum) {
        Uri uri = Uri.parse("tel:" + phoneNum);
        Intent intent = new Intent(Intent.ACTION_CALL, uri);
        try {
            startActivity(intent);
        } catch (SecurityException e) {
            Log.e("SecurityException", e.getMessage());
        }
    }

    private void getPreferences() {
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        nameTv.setText(pref.getString("myName", ""));
        if (nameTv.getText().toString().equals("")) {
            nameTv.setText("이름");
        }
        phoneNumberTv.setText(pref.getString("myPhoneNumber", ""));
        groupTv.setText(pref.getString("myGroup", ""));
        fontSize = pref.getString("fontSize", "");
        if (fontSize.equals("small")) {
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

            profileBtn.setPadding(0, 0, 0, 0);
            l1.setPadding(0, 20, 0, 0);
            l2.setPadding(0, 20, 0, 0);
        } else {
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

            profileBtn.setPadding(0, 30, 0, 0);
            l1.setPadding(0, 0, 0, 0);
            l2.setPadding(0, 0, 0, 0);
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



    /*
    * ohdoking add
    * */


    private void initAcceratorAndBluetooth() {

        checkHandler = new Handler();

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mGyroscope, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UartService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(UartService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(UartService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(UartService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(UartService.DEVICE_DOES_NOT_SUPPORT_UART);
        return intentFilter;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {

            case FINISH_SPLASH:
                //        AcceratorAndBluetooth init
                initAcceratorAndBluetooth();
                break;
            default:
                Log.e(TAG, "wrong request code");
                break;
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(R.string.popup_title)
                .setMessage(R.string.popup_message)
                .setPositiveButton(R.string.popup_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton(R.string.popup_no, null)
                .show();
    }

    private void showMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


    public float toFloat(byte[] bytes) {
        return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getFloat();
    }

    public boolean checkFallingState(int flag, double x, double y, double z) {
        double total = expression1(1, x, y, z);
//        text.setText(total + "\nx" + x + "\ny"+ y + "\nz " + z);

        if (flag == 1) {
//            text3.setText(total + "\nx" + x + "\ny"+ y + "\nz " + z);
        }
        // sqrt(x^2 + y^2 + z^2)로 넘어짐을 판별한다.

        Log.i("ohdoking-gyro-result", gyro + " : " + total);
        // 9~11         : 가만히 있다.   (arduino : 10~11)
        if (total > 9.5d && total < 10.5d) {
            switch (flag) {
                case 0:     // 넘어지지 않았고 가만히 계시네여    (정상)
                    return false;
                case 1:     // 넘어지고 가만히 계시네여            (전화나 메세지 보내야되여!!!!!!)
                    Toast.makeText(this, "전화나 메세지 보내야 됩니다.", Toast.LENGTH_SHORT).show();

                    List<Contact> contactLists = db.getAllContacts();

//                    Log.i("ohdoking-db-data",contactLists.get(0).getPhoneNumber());
                    //@TODO 주석 지워야함
                    /*if (contactLists == null) {
                        phoneCall(DEFAULT_PHONE_NUMBER);
                    } else {

                        for (Contact contact : contactLists) {
                            sendSMS(contact.getPhoneNumber(), mPhoneNumber + "의 주인에게 낙상사고가 발생하였습니다\n" +
                                    "\n" +
                                    "낙상사고 위치 보기 : http://maps.google.com/maps?q=" + lat + "," + lng);
                            if (firstData == 0) {
                                phoneCall(contact.getPhoneNumber());
                                firstData = 1;
                            }
                        }
                    }*/
//                    sendSMS("01036687952", "http://maps.google.com/maps?q="+lat+","+lng);
//                    phoneCall();
//                        finish();
                    return false;
            }
        }
        // 7~9 , 10~11  : 걷는다.
        else if ((total > 7 && total < 9) || (total > 10.3d && total < 11.5d)) {
            switch (flag) {
                case 0:     // 넘어지지 않았고 걸어가시네여  (정상)
                    return false;
                case 1:     // 넘어지고 걸어가시고 계시네여 (정상)
                    return false;
            }
        }

        // 15 ~         : 넘어진다.
        else if (total > 19d) {
            if (gyro) {
                switch (flag) {
                    case 0:     // 넘어지 않은 상태였고 넘어지셨네여   (타이머 걸고, 5초뒤에 다시 체크)
                        Toast.makeText(this, "넘어졌다!!", Toast.LENGTH_LONG).show();
//                        text2.setText("넘어짐 감지");
                        return true;
                    case 1:     // 넘어진 상태에서 또 넘어지셨네여    (정상)
                        return false;
                }
            }
        }

        return false;
    }

    double expression1(double time, double x, double y, double z) {
        double result_current = Math.sqrt(Math.pow(x, 2.0) + Math.pow(y, 2.0) + Math.pow(z, 2.0));
        return Math.abs(result_current) * time;
    }

    /*
    *   Gyro Sensor 사용
    * */
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        synchronized (this) {
            //4. Sensor row data 수신
            float var0, var1, var2;

            switch (sensorEvent.sensor.getType()) {
                case Sensor.TYPE_ACCELEROMETER:
                    float x = sensorEvent.values[0];
                    float y = sensorEvent.values[1];
                    float z = sensorEvent.values[2];

                    Log.i("ohdoking-acc-value",x + "," +y+ "," +z);
                    checkHandler.postDelayed(new CheckRunnable(x, y, z), 0);

                    break;
                case Sensor.TYPE_GYROSCOPE:
                    var0 = sensorEvent.values[0];
                    var1 = sensorEvent.values[1];
                    var2 = sensorEvent.values[2];
                    Log.d(TAG, "Gyroscope: " + "x = " + var0 + ", y = " + var1 + " , z = " + var2);

                    if (timestamp != 0) {
                        final float dT = (sensorEvent.timestamp - timestamp) * NS2S;
                        // Axis of the rotation sample, not normalized yet.
                        float axisX = sensorEvent.values[0];
                        float axisY = sensorEvent.values[1];
                        float axisZ = sensorEvent.values[2];

                        // Calculate the angular speed of the sample
                        gyroscopeRotationVelocity = Math.sqrt(axisX * axisX + axisY * axisY + axisZ * axisZ);

                        // Normalize the rotation vector if it's big enough to get the axis
                        if (gyroscopeRotationVelocity > EPSILON) {
                            axisX /= gyroscopeRotationVelocity;
                            axisY /= gyroscopeRotationVelocity;
                            axisZ /= gyroscopeRotationVelocity;
                        }

                        // Integrate around this axis with the angular speed by the timestep
                        // in order to get a delta rotation from this sample over the timestep
                        // We will convert this axis-angle representation of the delta rotation
                        // into a quaternion before turning it into the rotation matrix.
                        double thetaOverTwo = gyroscopeRotationVelocity * dT / 2.0f;
                        double sinThetaOverTwo = Math.sin(thetaOverTwo);
                        double cosThetaOverTwo = Math.cos(thetaOverTwo);

                        double somethingx = (float) (sinThetaOverTwo * axisX);
                        double somethingy = (float) (sinThetaOverTwo * axisX);
                        double somethingz = (float) (sinThetaOverTwo * axisZ);
                        double somethingw = (-(float) cosThetaOverTwo);

                        if (0.2d < gyroscopeRotationVelocity && gyroscopeRotationVelocity < 1.5d) {
                            gyro = false;
                        } else {
                            gyro = true;
                        }
                        Log.i("ohdoking-gyro", gyroscopeRotationVelocity + "" + gyro);
                    }
                    timestamp = sensorEvent.timestamp;


                    if (var0 > 4.0f || var1 > 4.0f || var2 > 4.0f)
//                            xVal.setText("Accelerometer: " + "x = " + axisX + ", y = " + axisY +" , z = " + axisZ);

                        break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        Log.d(TAG, "onAccuracyChanged()");
    }

    class CheckRunnable implements Runnable {

        float x, y, z;

        public CheckRunnable(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        @Override
        public void run() {
            if (option == 0) {
                if (checkFallingState(count, x, y, z)) {
                    option = 1;
                    Handler h = new Handler();
                    h.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            option = 0;
                        }
                    }, 5000);
                    count = 1;
                } else {
                    count = 0;
                }
            }
        }
    }

}

