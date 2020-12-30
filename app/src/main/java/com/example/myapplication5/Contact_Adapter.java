package com.example.myapplication5;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Contact_Adapter extends BaseAdapter {
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
            convertView = inflater.inflate(R.layout.contact_listview, parent, false);
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
