package com.pinners.olders;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016-06-22.
 */
public class FavoriteContactsActivity extends AppCompatActivity {
    private ListView contactListView;
    ArrayList list;
    ListViewAdapter2 adapter;
    DBHelper db;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_contacts);

        init();

        List<Contact> contacts = db.getAllContacts();
        for (Contact contact : contacts) {
            if(contact.getFavorite().toString().equals("true")){
                adapter.addItem(contact.getName(), contact.getPhoneNumber(), contact.getFavorite());
            }
        }
        contactListView.setAdapter(adapter);
        contactListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
        contactListView = (ListView) findViewById(R.id.contactListView);
        list = new ArrayList();
        adapter = new ListViewAdapter2();
        db = new DBHelper(this);
    }
}
