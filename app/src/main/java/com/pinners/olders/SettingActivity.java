package com.pinners.olders;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.RadioButton;
import android.widget.RadioGroup;

/**
 * Created by Administrator on 2016-06-23.
 */
public class SettingActivity extends AppCompatActivity{

    private RadioGroup settingFontRg;
    private RadioButton settingFontBigRb, settingFontSmallRb;
    private String fontSize = "small";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        init();
        getPreferences();
        settingFontRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.settingFontSmallRb){
                    fontSize = "small";
                }else{
                    fontSize = "big";
                }
                savePreferences(fontSize);
            }
        });
    }

    void init(){
        settingFontRg = (RadioGroup) findViewById(R.id.settingFontRg);
        settingFontBigRb = (RadioButton) findViewById(R.id.settingFontBigRb);
        settingFontSmallRb = (RadioButton) findViewById(R.id.settingFontSmallRb);
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
        }
        else{
            settingFontSmallRb.setChecked(false);
            settingFontBigRb.setChecked(true);
        }

    }
}
