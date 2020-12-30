package com.example.myapplication5;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;

//
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
//

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


class Contact_Adapter extends BaseAdapter {
    private TextView phoneNumber;
    private TextView name;
    private TextView id;
    private ArrayList<Contact> contact_list = new ArrayList<Contact>();

    public Contact_Adapter() {

    }

    @Override
    public int getCount() {
        return contact_list.size();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_contact, parent, false);
        }

        phoneNumber = (TextView) convertView.findViewById(R.id.contact_phonenumber);
        name = (TextView) convertView.findViewById(R.id.contact_name);
        id = (TextView) convertView.findViewById(R.id.contact_id);

        Contact item = contact_list.get(position);

        phoneNumber.setText(item.getPhoneNumber());
        name.setText(item.getName());
        id.setText(Long.toString(item.getId()));
        return convertView;

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return contact_list.get(position);
    }

    public void addItem(String phonenumber, String name, Long id) {
        Contact item = new Contact();

        item.setId(id);
        item.setName(name);
        item.setPhoneNumber(phonenumber);

        contact_list.add(item);
    }
}


public class MainActivity extends AppCompatActivity {

    //connect to firebase
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference contactsRef = mRootRef.child("Contacts");

    ContactUtil contactutil;
    Context context;
    private ListView listview;
    private Contact_Adapter adapter;
    static final String[] LIST_MENU = {"Name", "Phone Number", "Id"};

    GoogleSignInClient mGoogleSignInClient;

    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(MainActivity.this, "Signed out successfully", Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.toolbar, menu);

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.toolbar_next_button:{
                signOut();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        JsonObject data = new JsonObject();
        JsonArray contact_list = new JsonArray();
        String RESULT;

        Toolbar toolbar = (Toolbar) findViewById(R.id.bar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);


        adapter = new Contact_Adapter();

        listview = (ListView) findViewById(R.id.contact_list);
        listview.setAdapter(adapter);

        context = this;
        contactutil = new ContactUtil(this);
        Contact iter;

        listview.setAdapter(adapter);

        ArrayList<Contact> arraylist = contactutil.getContactList();

        for(int i=0;i < arraylist.size();i++) {
            iter = arraylist.get(i);
            JsonObject obj = new JsonObject();
            adapter.addItem(iter.getPhoneNumber(), iter.getName(), iter.getId());

            obj.addProperty("id", iter.getId());
            obj.addProperty("name", iter.getName());
            obj.addProperty("phonenumber", iter.getPhoneNumber());

            contact_list.add(obj);
        }

        data.addProperty("ContactList", String.valueOf(contact_list));

        adapter.notifyDataSetChanged();


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(Scopes.DRIVE_FULL))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);

        //Making Owner Info
        JsonObject owner = new JsonObject();
        owner.addProperty("ID", acct.getId());
        owner.addProperty("displayname", acct.getDisplayName());
        owner.addProperty("email", acct.getEmail());
        data.add("Owner", owner);
        RESULT = new Gson().toJson(data);

        //MAKE onclicklistener for upload btn
        Button upload_btn = (Button) findViewById(R.id.contactlist_upload);
        upload_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //contactsRef.setValue(RESULT);
                String Category = acct.getId();
                // "."이나 "@"가 들어가면 안된다.
                contactsRef.child(Category).setValue(RESULT);
            }
        });

        TabHost tabHost1 = (TabHost) findViewById(R.id.tabHost1) ;
        tabHost1.setup() ;

        // 첫 번째 Tab. (탭 표시 텍스트:"TAB 1"), (페이지 뷰:"content1")
        TabHost.TabSpec ts1 = tabHost1.newTabSpec("Tab Spec 1") ;
        ts1.setContent(R.id.content1) ;
        ts1.setIndicator("연락처") ;
        tabHost1.addTab(ts1)  ;

        // 두 번째 Tab. (탭 표시 텍스트:"TAB 2"), (페이지 뷰:"content2")
        TabHost.TabSpec ts2 = tabHost1.newTabSpec("Tab Spec 2") ;
        ts2.setContent(R.id.content2) ;
        ts2.setIndicator("갤러리") ;
        tabHost1.addTab(ts2) ;

        // 세 번째 Tab. (탭 표시 텍스트:"TAB 3"), (페이지 뷰:"content3")
        TabHost.TabSpec ts3 = tabHost1.newTabSpec("Tab Spec 3") ;
        ts3.setContent(R.id.content3) ;
        ts3.setIndicator("^?^") ;
        tabHost1.addTab(ts3) ;

    }

}