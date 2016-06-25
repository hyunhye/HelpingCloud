package com.pinners.olders;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ToggleButton;

/**
 * Created by Administrator on 2016-06-21.
 */
public class RegistrationActivity extends AppCompatActivity {
    private EditText nameEditText;
    private EditText phoneNumberEditText;
    private Button okBtn, deleteBtn;
    private ToggleButton favoriteTb;

    private DBHelper db;
    private String check;
    private String name;
    private String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        init();

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        phoneNumber = intent.getStringExtra("phoneNumber");

        nameEditText.setText(name);
        phoneNumberEditText.setText(phoneNumber);
        favoriteTb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(favoriteTb.isChecked()){
                    check = "true";
                } else {
                    check = "false";
                }
            }
        });
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.addContact(new Contact(nameEditText.getText().toString(), phoneNumberEditText.getText().toString(), check));

                Intent intent = new Intent(RegistrationActivity.this, ContactsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    void init(){

        nameEditText = (EditText) findViewById(R.id.nameEt);
        phoneNumberEditText = (EditText) findViewById(R.id.phoneNumberEt);
        okBtn = (Button) findViewById(R.id.okBtn);
        deleteBtn = (Button) findViewById(R.id.deleteBtn);
        favoriteTb = (ToggleButton) findViewById(R.id.favoriteTb);
        check = "false";
        db = ContactsActivity.getDB();
    }
}
