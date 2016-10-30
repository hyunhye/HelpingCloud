package com.pinners.olders.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.pinners.olders.dto.Contact;
import com.pinners.olders.util.DBHelper;
import com.pinners.olders.adapter.GridViewAdapter;
import com.pinners.olders.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016-06-22.
 */
public class FavoriteContactsActivity extends AppCompatActivity {
    private GridView contactGridView;
    ArrayList list;
    GridViewAdapter adapter;
    DBHelper db;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_contacts);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.favorite_actionbar));
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("즐겨찾기");

        init();

        List<Contact> contacts = db.getAllContacts();
        for (Contact contact : contacts) {
            if(contact.getFavorite().toString().equals("true")){
                adapter.addItem(contact.getName(), contact.getPhoneNumber(), contact.getFavorite());
            }
        }
        contactGridView.setAdapter(adapter);
        contactGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Contact item = (Contact) parent.getItemAtPosition(position) ;
                /*
                Intent intent =  new Intent(FavoriteContactsActivity.this, RegistrationModifyActivity.class);
                intent.putExtra("itemId",position);
                intent.putExtra("itemName", item.getName().toString());
                intent.putExtra("itemPhoneNumber",item.getPhoneNumber().toString());
                intent.putExtra("itemFavorite",item.getFavorite().toString());
                startActivity(intent);
                */
                phoneCall(item.getPhoneNumber());
            }
        });
    }

    private void init(){
        contactGridView = (GridView) findViewById(R.id.contactGridView);
        list = new ArrayList();
        adapter = new GridViewAdapter();
        db = new DBHelper(this);
    }

    private void phoneCall(String phoneNumber){
        Uri uri = Uri.parse("tel:"+phoneNumber);
        Intent intent = new Intent(Intent.ACTION_CALL,uri);
        try{
            startActivity(intent);
        }catch(SecurityException e){
            Log.e("SecurityException", e.getMessage());
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
