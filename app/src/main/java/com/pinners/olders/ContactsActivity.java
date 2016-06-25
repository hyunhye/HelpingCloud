package com.pinners.olders;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import java.util.List;

/**
 * Created by Administrator on 2016-06-22.
 */
public class ContactsActivity  extends AppCompatActivity {
    private ImageButton contactsBtn, firstContactsBtn;
    private ListView favoriteContactListView;
    private ListView contactsListView;
    private ListViewAdapter adapter;
    private ListViewAdapter favoriteAdapter;
    private LinearLayout firstLayout;
    private LinearLayout secondLayout;

    public static DBHelper db;
    private  List<Contact> contacts;
    public static DBHelper getDB(){
        return db;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        init();

        contacts = db.getAllContacts();

        if(contacts.isEmpty()){
            firstLayout.setVisibility(View.VISIBLE);
            secondLayout.setVisibility(View.GONE);
        } else {
            firstLayout.setVisibility(View.GONE);
            secondLayout.setVisibility(View.VISIBLE);
        }

        contactsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setData(ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(intent, 0);
            }
        });

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
        contactsBtn = (ImageButton) findViewById(R.id.contactsBtn);
        firstContactsBtn = (ImageButton) findViewById(R.id.firstContactsBtn);
        favoriteContactListView = (ListView) findViewById(R.id.favoriteContactListView);
        contactsListView = (ListView) findViewById(R.id.contactsListView);
        db = new DBHelper(this);
        adapter = new ListViewAdapter();
        favoriteAdapter = new ListViewAdapter();
        firstLayout = (LinearLayout) findViewById(R.id.firstLayout);
        secondLayout = (LinearLayout) findViewById(R.id.secondLayout);
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
}
