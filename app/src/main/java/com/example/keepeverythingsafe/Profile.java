package com.example.keepeverythingsafe;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class Profile extends AppCompatActivity {

    private ListView listView;
    private FirebaseFirestore db;
    private String email;
    ArrayList<Info> infoList;
    private String encryptedEmail, encryptedPassword;

    public Profile() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();//hiding top bar
        setContentView(R.layout.activity_profile);

        ImageView imageView = (ImageView)findViewById(R.id.refresh);

        imageView.setOnClickListener(v -> recreate());
        email = getIntent().getStringExtra("Email");

        infoList = new ArrayList<>();


        listView = findViewById(R.id.list_of_pass);
        loadDatainListview();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.addpassword);
        fab.setOnClickListener(view -> showPopupDialog());

    }


    private void showPopupDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Profile.this);

        alertDialogBuilder.setTitle("Add User Name");
        alertDialogBuilder.setIcon(R.drawable.ic_baseline_add_circle_outline_24);
        alertDialogBuilder.setCancelable(false);

        LayoutInflater layoutInflater = LayoutInflater.from(Profile.this);
        View inputUserNameView = layoutInflater.inflate(R.layout.input_pass, null);


        alertDialogBuilder.setView(inputUserNameView);

        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        Button saveUserNameButton = inputUserNameView.findViewById(R.id.button_save_username);
        saveUserNameButton.setOnClickListener(v -> {

            EditText editText = inputUserNameView.findViewById(R.id.edit_text_username);
            EditText title = inputUserNameView.findViewById(R.id.alertdialogtitle);
            EditText password = inputUserNameView.findViewById(R.id.alertdialogpassword);

            String userName = editText.getText().toString();
            String strtitle = title.getText().toString();
            String strpassword = password.getText().toString();

            if(TextUtils.isEmpty(userName) || TextUtils.isEmpty(strtitle) || TextUtils.isEmpty(strpassword)) {
                Snackbar.make(v, "User name can not be empty.", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            } else {

                Log.d("MY TAG","EMAIL"+email);
                Log.d("MY TAG","TITLE"+strtitle);
                Log.d("MY TAG","USERNAME"+userName);
                Log.d("MY TAG","PASSWORD"+strpassword);
                try {
                    encryptedEmail = AES.encrypt(userName);
                    encryptedPassword = AES.encrypt(strpassword);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                db = FirebaseFirestore.getInstance();
                Info info = new Info(encryptedEmail,encryptedPassword,strtitle);
                db.collection("Users").document(email).collection(email).add(info);
                Toast.makeText(Profile.this,"Added Successfully",Toast.LENGTH_LONG).show();
            }
        });

        Button cancelUserNameButton = inputUserNameView.findViewById(R.id.button_cancel_username);
        cancelUserNameButton.setOnClickListener(v -> alertDialog.cancel());
    }

    private void loadDatainListview() {
        FirebaseFirestore database = FirebaseFirestore.getInstance();

        database.collection("Users").document(email).collection(email).get().
       addOnSuccessListener(queryDocumentSnapshots -> {

           if (!queryDocumentSnapshots.isEmpty()) {

               List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
               for (DocumentSnapshot d : list) {

                   Info info = d.toObject(Info.class);

                   infoList.add(info);
               }
               Adapter adapter = new Adapter(Profile.this, infoList);

               listView.setAdapter(adapter);
           } else {
                   Toast.makeText(Profile.this, "No data found in Database", Toast.LENGTH_SHORT).show();
           }
       }).addOnFailureListener(e -> {

           Toast.makeText(Profile.this, "Fail to load data..    "+e.getMessage(), Toast.LENGTH_SHORT).show();
       });
    }
}