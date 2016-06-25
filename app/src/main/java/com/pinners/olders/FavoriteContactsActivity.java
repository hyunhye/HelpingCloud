package com.pinners.olders;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;

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
                Intent intent =  new Intent(FavoriteContactsActivity.this, RegistrationModifyActivity.class);
                intent.putExtra("itemId",position);
                intent.putExtra("itemName", item.getName().toString());
                intent.putExtra("itemPhoneNumber",item.getPhoneNumber().toString());
                intent.putExtra("itemFavorite",item.getFavorite().toString());
                startActivity(intent);
            }
        });
    }

    private void init(){
        contactGridView = (GridView) findViewById(R.id.contactGridView);
        list = new ArrayList();
        adapter = new GridViewAdapter();
        db = new DBHelper(this);
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
