package com.example.keepeverythingsafe;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class Adapter extends ArrayAdapter<Info> {

    public Adapter(Context context, ArrayList<Info> infoList) {
        super(context, 0,infoList);
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listitemView = convertView;
        if (listitemView == null) {
            listitemView = LayoutInflater.from(getContext()).inflate(R.layout.single_info, parent, false);
        }

        Info Info = getItem(position);

        TextView title = listitemView.findViewById(R.id.textViewtitle);
        TextView email= listitemView.findViewById(R.id.textViewemail);
        TextView password= listitemView.findViewById(R.id.textViewpassword);
        ImageView imageView = listitemView.findViewById(R.id.passeye);

        title.setText("Title: " + Info.getTitle());

        try {
            email.setText("Email: "+ AES.decrypt(Info.getEmail()));
            imageView.setOnClickListener(v -> {
                try {
                    password.setText("Password: " + AES.decrypt(Info.getPassword()));
                    imageView.setVisibility(View.GONE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        listitemView.setOnClickListener(v -> {

        });

        return listitemView;
    }
}
