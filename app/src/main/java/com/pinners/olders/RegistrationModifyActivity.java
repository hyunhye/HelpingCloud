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
 * Created by Administrator on 2016-06-22.
 */
public class RegistrationModifyActivity  extends AppCompatActivity {
    EditText nameEditText;
    EditText phoneNumberEditText;
    Button okBtn, deleteBtn;
    ToggleButton favoriteTb;

    DBHelper db;
    String check;
    int id;
    String prevName;
    String prevPhoneNumber;
    String prevFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        nameEditText = (EditText) findViewById(R.id.nameEt);
        phoneNumberEditText = (EditText) findViewById(R.id.phoneNumberEt);
        okBtn = (Button) findViewById(R.id.okBtn);
        deleteBtn = (Button) findViewById(R.id.deleteBtn);
        favoriteTb = (ToggleButton) findViewById(R.id.favoriteTb);
        db = ContactsActivity.getDB();

        Intent intent = getIntent();
        id = intent.getIntExtra("itemId", 0);
        prevName = intent.getStringExtra("itemName");
        prevPhoneNumber = intent.getStringExtra("itemPhoneNumber");
        prevFavorite = intent.getStringExtra("itemFavorite");
        if(prevFavorite.equals("true")) favoriteTb.setChecked(true);
        else favoriteTb.setChecked(false);

        nameEditText.setText(prevName);
        phoneNumberEditText.setText(prevPhoneNumber);
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
                db.updateContact(prevName, nameEditText.getText().toString(), phoneNumberEditText.getText().toString(), check);
                Intent intent = new Intent(RegistrationModifyActivity.this, ContactsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.deleteContact(prevName);
                Intent intent = new Intent(RegistrationModifyActivity.this, ContactsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
    }
}
