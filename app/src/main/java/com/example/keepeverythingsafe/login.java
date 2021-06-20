package com.example.keepeverythingsafe;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class login extends AppCompatActivity implements  View.OnClickListener{
    private EditText Textemaillogin, Textpasswordlogin;
    private Button btnsignin;
    private Toast toast;
    private TextView register;
    ProgressDialog progressDialog;
    private int totalAttempts= 3;
    private TextView failtime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();//hiding top bar
        setContentView(R.layout.activity_login);
        register = (TextView) findViewById(R.id.register);
        register.setOnClickListener(this);

        Textemaillogin = (EditText) findViewById(R.id.email);
        Textpasswordlogin = (EditText) findViewById(R.id.password);

        btnsignin = (Button) findViewById(R.id.signin);
        btnsignin.setOnClickListener(this);

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void  onClick(View view){
        switch (view.getId()){
            case R.id.signin:
                userlogin();
                break;
            case R.id.register:
                startActivity(new Intent(this, RegisterUser.class));
                break;

        }
    }

    private void userlogin() {
        progressDialog = ProgressDialog.show(this, "", "Signing in....", true);

        String emaillgn = Textemaillogin.getText().toString().trim();
        String passwordlgn = Textpasswordlogin.getText().toString().trim();


        if (!emaillgn.matches("[a-zA-Z0-9._-]+@[a-z]+\\.[a-z]+")) {//email validation with regex
            progressDialog.dismiss();
            Textemaillogin.setError("Please provide a valid email address");
            Textemaillogin.requestFocus();
            return;
        }

        if (emaillgn.isEmpty()) {//email validation with regex
            progressDialog.dismiss();
            Textemaillogin.setError("Email is required");
            Textemaillogin.requestFocus();
            return;
        }

        if (passwordlgn.isEmpty()) {
            progressDialog.dismiss();
            Textpasswordlogin.setError("Password is required");
            Textpasswordlogin.requestFocus();
            return;
        }

        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        rootRef.collection("Users").document(emaillgn)
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot doc = task.getResult();
                        if (doc.exists()) {
                            String email = (String) doc.get("email");
                            String hashpassword = (String) doc.get("password");
                            try {
                                if (totalAttempts != 0) {
                                    if (validatePassword(passwordlgn, hashpassword) && emaillgn.equals(email)) {
                                        Toast.makeText(login.this, "Success" , Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(getBaseContext(), Profile.class);
                                        intent.putExtra("Email",email);
                                        startActivity(intent);
                                        progressDialog.dismiss();
                                    } else {
                                        Toast.makeText(login.this, "Information is wrong" , Toast.LENGTH_LONG).show();
                                        totalAttempts--;
                                        progressDialog.dismiss();
                                    }
                                } else {
                                    new CountDownTimer(10000, 1000) {
                                        @SuppressLint("SetTextI18n")
                                        public void onTick(long millisUntilFinished) {
                                            failtime = findViewById(R.id.failtime);
                                            NumberFormat f = new DecimalFormat("00");
                                            long min = (millisUntilFinished / 60000) % 60;
                                            long sec = (millisUntilFinished / 1000) % 60;
                                            toast = Toast.makeText(login.this, "App blocked for 5 minutes" , Toast.LENGTH_SHORT);
                                            toast.show();
                                            failtime.setText(f.format(min) + ":" + f.format(sec));
                                            btnsignin.setEnabled(false);
                                            progressDialog.dismiss();
                                            toast.cancel();
                                        }
                                        @SuppressLint("SetTextI18n")
                                        public void onFinish() {
                                            failtime.setText("00:00");
                                            progressDialog.dismiss();
                                            btnsignin.setEnabled(true);
                                            totalAttempts = 3;
                                            toast.cancel();
                                        }
                                    }.start();
                                }
                            } catch (NoSuchAlgorithmException e) {
                                e.printStackTrace();
                            } catch (InvalidKeySpecException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }



    private static boolean validatePassword(String originalPassword, String storedPassword) throws NoSuchAlgorithmException, InvalidKeySpecException
    {
        String[] parts = storedPassword.split(":");
        int iterations = Integer.parseInt(parts[0]);
        byte[] salt = fromHex(parts[1]);
        byte[] hash = fromHex(parts[2]);

        PBEKeySpec spec = new PBEKeySpec(originalPassword.toCharArray(), salt, iterations, hash.length * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] testHash = skf.generateSecret(spec).getEncoded();

        int diff = hash.length ^ testHash.length;
        for(int i = 0; i < hash.length && i < testHash.length; i++) {
            diff |= hash[i] ^ testHash[i];
        }
        return diff == 0;
    }


    private static byte[] fromHex(String hex) {
        byte[] bytes = new byte[hex.length() / 2];
        for(int i = 0; i<bytes.length ;i++)
        {
            bytes[i] = (byte)Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }
        return bytes;
    }
}


