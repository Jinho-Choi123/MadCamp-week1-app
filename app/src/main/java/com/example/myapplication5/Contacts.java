package com.example.myapplication5;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;

class Contact {
    Long id;
    String phoneNumber;
    String name;
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}


class ContactUtil {

    private Context context;

    public ContactUtil(Context context) {
        this.context = context;
    }

    public ArrayList<Contact> getContactList() {

        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

        String[] projection = new String[]{
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID, // 연락처 ID -> 사진 정보 가져오는데 사용
                ContactsContract.CommonDataKinds.Phone.NUMBER,        // 연락처
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME}; // 연락처 이름.

        String[] selectionArgs = null;

        String sort = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " COLLATE LOCALIZED ASC";

        Cursor contactCursor = context.getContentResolver().query(uri, projection, null, selectionArgs, sort);

        ArrayList<Contact> contactlist = new ArrayList<Contact>();

        if (contactCursor.moveToFirst()) {
            do {
                String phonenumber = contactCursor.getString(1).replaceAll("-", "");
                if (phonenumber.length() == 10) {
                    phonenumber = phonenumber.substring(0, 3) + "-"
                            + phonenumber.substring(3, 6) + "-"
                            + phonenumber.substring(6);
                } else if (phonenumber.length() > 8) {
                    phonenumber = phonenumber.substring(0, 3) + "-"
                            + phonenumber.substring(3, 7) + "-"
                            + phonenumber.substring(7);
                }

                Contact contact = new Contact();
                contact.setId(contactCursor.getLong(0));
                contact.setPhoneNumber(phonenumber);
                contact.setName(contactCursor.getString(2));
                contactlist.add(contact);

            } while (contactCursor.moveToNext());
        }

        return contactlist;

    }


}

public class Contacts extends AppCompatActivity {

    ContactUtil contactutil;
    Context context;
    private ListView listview;
    private Contact_Adapter adapter;
    static final String[] LIST_MENU = {"Name", "Phone Number", "Id"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        adapter = new Contact_Adapter();

        listview = (ListView) findViewById(R.id.contact_list);
        listview.setAdapter(adapter);

        context = this;
        contactutil = new ContactUtil(this);
        ListView lv = (ListView) findViewById(R.id.contact_list);

        lv.setAdapter(adapter);

        ArrayList<Contact> arraylist = contactutil.getContactList();

        for(int i=0;i < arraylist.size();i++) {

            adapter.addItem(arraylist.get(i).getPhoneNumber(), arraylist.get(i).getName(), arraylist.get(i).getId());

//            result += arraylist.get(i).getName();
//            result += arraylist.get(i).getPhoneNumber();
//            result += arraylist.get(i).getId();
        }
        adapter.notifyDataSetChanged();
    }
}