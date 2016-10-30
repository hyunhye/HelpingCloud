package com.pinners.olders.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.pinners.olders.dto.Contact;
import com.pinners.olders.util.DBHelper;
import com.pinners.olders.adapter.ListViewAdapter;
import com.pinners.olders.adapter.ListViewAdapter2;
import com.pinners.olders.R;

import java.util.List;

/**
 * Created by Administrator on 2016-06-22.
 */
public class ContactsActivity  extends AppCompatActivity {
    private ImageButton firstContactsBtn;
    private ListView favoriteContactListView;
    private ListView contactsListView;
    private ListViewAdapter adapter;
    private ListViewAdapter2 favoriteAdapter;
    private LinearLayout firstLayout;
    private LinearLayout secondLayout;
    private TextView firstContactsTv1,firstContactsTv2;
    private String fontSize = "small";

    public static DBHelper db;
    private  List<Contact> contacts;
    public static DBHelper getDB(){
        return db;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.contacts_actionbar));
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("비상 연락망");

        init();
        getPreferences();

        contacts = db.getAllContacts();

        if(contacts.isEmpty()){
            firstLayout.setVisibility(View.VISIBLE);
            secondLayout.setVisibility(View.GONE);
        } else {
            firstLayout.setVisibility(View.GONE);
            secondLayout.setVisibility(View.VISIBLE);
        }

        firstContactsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setData(ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(intent, 0);
            }
        });

        for (Contact contact : contacts) {
            adapter.addItem(contact.getName(), contact.getPhoneNumber(), contact.getFavorite());

            if(contact.getFavorite().toString().equals("true")){
                favoriteAdapter.addItem(contact.getName(), contact.getPhoneNumber(), contact.getFavorite());
            }
        }
        contactsListView.setAdapter(adapter);
        favoriteContactListView.setAdapter(favoriteAdapter);

        contactsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Contact item = (Contact) parent.getItemAtPosition(position) ;
                Intent intent =  new Intent(ContactsActivity.this, RegistrationModifyActivity.class);
                intent.putExtra("itemId",position);
                intent.putExtra("itemName", item.getName().toString());
                intent.putExtra("itemPhoneNumber",item.getPhoneNumber().toString());
                intent.putExtra("itemFavorite",item.getFavorite().toString());
                startActivity(intent);
            }
        });

        favoriteContactListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Contact item = (Contact) parent.getItemAtPosition(position) ;
                /*
                Intent intent =  new Intent(ContactsActivity.this, RegistrationModifyActivity.class);
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

    void init(){
        firstContactsBtn = (ImageButton) findViewById(R.id.firstContactsBtn);
        favoriteContactListView = (ListView) findViewById(R.id.favoriteContactListView);
        contactsListView = (ListView) findViewById(R.id.contactsListView);
        db = new DBHelper(this);
        adapter = new ListViewAdapter();
        favoriteAdapter = new ListViewAdapter2();
        firstLayout = (LinearLayout) findViewById(R.id.firstLayout);
        secondLayout = (LinearLayout) findViewById(R.id.secondLayout);
        firstContactsTv1 = (TextView) findViewById(R.id.firstContactsTv1);
        firstContactsTv2 = (TextView) findViewById(R.id.firstContactsTv2);
    }

    private void getPreferences(){
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        fontSize = pref.getString("fontSize","");
        if(fontSize.equals("small")){
            firstContactsTv1.setTextSize(13);
            firstContactsTv2.setTextSize(13);
        }
        else{
            firstContactsTv1.setTextSize(26);
            firstContactsTv2.setTextSize(26);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK){
            Cursor cursor = getContentResolver().query(data.getData(), new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.NUMBER}, null, null, null);
            cursor.moveToFirst();
            String sName = cursor.getString(0);
            String sNumber = cursor.getString(1);
            cursor.close();

            Intent intent = new Intent(ContactsActivity.this, RegistrationActivity.class);
            intent.putExtra("name",sName);
            intent.putExtra("phoneNumber",sNumber);
            startActivity(intent);
        }
        super.onActivityResult(requestCode, resultCode, data);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.contacts_btn:
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setData(ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(intent, 0);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }
}
