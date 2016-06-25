package com.pinners.olders;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

/**
 * Created by Administrator on 2016-06-23.
 */
public class SettingActivity extends AppCompatActivity{

    private RadioGroup settingFontRg;
    private RadioButton settingFontBigRb, settingFontSmallRb;
    TextView bluetoothTv1, settingBluetoothTv, batteryTv1, settingBatteryTv, fontSizeTv1;
    private String fontSize = "small";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.setting_actionbar));
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("설정");

        init();
        getPreferences();
        settingFontRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.settingFontSmallRb){
                    fontSize = "small";
                    settingFontSmallRb.setTextSize(15);
                    settingFontBigRb.setTextSize(15);
                    bluetoothTv1.setTextSize(18);
                    settingBluetoothTv.setTextSize(15);
                    batteryTv1.setTextSize(18);
                    settingBatteryTv.setTextSize(15);
                    fontSizeTv1.setTextSize(18);
                }else{
                    fontSize = "big";
                    settingFontSmallRb.setTextSize(18);
                    settingFontBigRb.setTextSize(18);
                    bluetoothTv1.setTextSize(26);
                    settingBluetoothTv.setTextSize(18);
                    batteryTv1.setTextSize(26);
                    settingBatteryTv.setTextSize(18);
                    fontSizeTv1.setTextSize(26);
                }
                savePreferences(fontSize);
            }
        });
    }

    void init(){
        settingFontRg = (RadioGroup) findViewById(R.id.settingFontRg);
        settingFontBigRb = (RadioButton) findViewById(R.id.settingFontBigRb);
        settingFontSmallRb = (RadioButton) findViewById(R.id.settingFontSmallRb);
        bluetoothTv1 = (TextView) findViewById(R.id.bluetoothTv1);
        settingBluetoothTv = (TextView) findViewById(R.id.settingBluetoothTv);
        batteryTv1 = (TextView) findViewById(R.id.batteryTv1);
        settingBatteryTv = (TextView) findViewById(R.id.settingBatteryTv);
        fontSizeTv1 = (TextView) findViewById(R.id.fontSizeTv1);
    }

    private void savePreferences(String fontSize){
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("fontSize", fontSize);
        editor.commit();
    }

    private void getPreferences(){
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        fontSize = pref.getString("fontSize","");
        if(fontSize.equals("small")){
            settingFontSmallRb.setChecked(true);
            settingFontBigRb.setChecked(false);

            settingFontSmallRb.setTextSize(15);
            settingFontBigRb.setTextSize(15);
            bluetoothTv1.setTextSize(18);
            settingBluetoothTv.setTextSize(15);
            batteryTv1.setTextSize(18);
            settingBatteryTv.setTextSize(15);
            fontSizeTv1.setTextSize(18);
        }
        else{
            settingFontSmallRb.setChecked(false);
            settingFontBigRb.setChecked(true);

            settingFontSmallRb.setTextSize(18);
            settingFontBigRb.setTextSize(18);
            bluetoothTv1.setTextSize(26);
            settingBluetoothTv.setTextSize(18);
            batteryTv1.setTextSize(26);
            settingBatteryTv.setTextSize(18);
            fontSizeTv1.setTextSize(26);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }
}
