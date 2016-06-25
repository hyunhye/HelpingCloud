package com.pinners.olders;

import android.app.ActionBar;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

/**
 * Created by Administrator on 2016-06-24.
 */
public class SplashActivity extends AppCompatActivity {
    public ImageView loadingIv;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        final int[] imageArray = { R.drawable.loading1,
                R.drawable.loading2,
                R.drawable.loading3,
                R.drawable.loading1,
                R.drawable.loading2,
        };

        loadingIv = (ImageView)findViewById(R.id.loadingIv);
        final Handler handler = new Handler();

        Runnable runnable = new Runnable() {
            int i = 0;
            public void run() {
                loadingIv.setImageResource(imageArray[i]);
                i++;
                if(i == (imageArray.length-1))
                {
                    finish();
                    i--;
                }
                handler.postDelayed(this, 500);
            }
        };
        handler.postDelayed(runnable, 500);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}
