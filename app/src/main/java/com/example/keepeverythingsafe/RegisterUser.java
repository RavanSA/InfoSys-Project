package com.example.keepeverythingsafe;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Random;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class RegisterUser extends AppCompatActivity implements View.OnClickListener {

    private EditText Textfullname, Textemail, Textpassword, Textconfirmpassword,Textphone;
    ProgressDialog progressDialog;
    private FirebaseFirestore db;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);
        getSupportActionBar().hide();//hiding top bar

        db = FirebaseFirestore.getInstance();

        Button registerUser = findViewById(R.id.register);
        registerUser.setOnClickListener(this);

        Textfullname = (EditText) findViewById(R.id.username);
        Textemail = (EditText) findViewById(R.id.email);
        Textphone = (EditText) findViewById(R.id.phoneno);
        Textpassword = (EditText) findViewById(R.id.password);
        Textconfirmpassword = (EditText) findViewById(R.id.confirmpassword);

        ImageView recommend =  findViewById(R.id.recommend);
        recommend.setOnClickListener(view -> {

            String pass = getRandomString(10);

            AlertDialog alertDialog = new AlertDialog.Builder(RegisterUser.this).create();
            alertDialog.setTitle("Recommend Password");
            alertDialog.setMessage("Recommend password is: \n" + getRandomString(16));

            alertDialog.setButton("Copy Recommend Password", (dialog, which) -> {

                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("EditText", pass);

                clipboardManager.setPrimaryClip(clip);

                Toast.makeText(RegisterUser.this, "Recommend Password Copied to Clipboard",
                        Toast.LENGTH_LONG).show();
            });
            alertDialog.show();
        });
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.register) {
            try {
                registerUser();
            } catch (InvalidKeySpecException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
    }

    private void registerUser() throws InvalidKeySpecException, NoSuchAlgorithmException {
        progressDialog = ProgressDialog.show(this, "","This may take some time....", true);

        String email = Textemail.getText().toString().trim();
        String phoneNo = Textphone.getText().toString().trim();
        String fullname = Textfullname.getText().toString().trim();
        String password = Textpassword.getText().toString().trim();
        String confirmpassword = Textconfirmpassword.getText().toString().trim();

        if (!email.matches("[a-zA-Z0-9._-]+@[a-z]+\\.[a-z]+")) {//email validation with regex
            Textemail.setError("Please provide a valid email address");
            Textemail.requestFocus();
            progressDialog.dismiss();
            return;
        }

        if (email.isEmpty()) {
            Textemail.setError("Email is required");
            Textemail.requestFocus();
            progressDialog.dismiss();
            return;
        }

        if (fullname.isEmpty()) {
            Textfullname.setError("Name is required");
            Textfullname.requestFocus();
            progressDialog.dismiss();
            return;
        }

        if (password.isEmpty()) {
            Textpassword.setError("Password is required");
            Textpassword.requestFocus();
            progressDialog.dismiss();
            return;
        }

        if(password.length() <= 10){
            Textpassword.setError("Password length must be greater than 7");
            Textpassword.requestFocus();
            progressDialog.dismiss();
            return;
        }

        if (!checkString(password)) {
            Textconfirmpassword.setError("Password must contain at least one UPPERCASE LETTER and lowercase letters and numbers");
            Textconfirmpassword.requestFocus();
            progressDialog.dismiss();
            return;
        }

        if (!password.equals(confirmpassword)) {
            Textconfirmpassword.setError("Confirm password and password must be equal");
            Textconfirmpassword.requestFocus();
            progressDialog.dismiss();
            return;
        }

        String profilepic = "https://firebasestorage.googleapis.com/v0/b/durakgame-6af91.appspot." +
                "com/o/images%2Fprofilepic%2FLKJHGF.png?alt=media&token=521d4493-fa9c-4ad1-869e-910d83276465";
        addDataToFirestore(fullname, email, phoneNo,hashPassword(password),profilepic);
        progressDialog.dismiss();
        Toast.makeText(RegisterUser.this,"User Registrated Successfully",Toast.LENGTH_LONG).show();

    }

    @SuppressLint("ShowToast")
    private void addDataToFirestore(String fullname, String email, String phoneNO, String password, String profilepic) {
        User user = new User(fullname,email,phoneNO,password,profilepic);
        Toast.makeText(RegisterUser.this,"Registrating succesfully",Toast.LENGTH_LONG);
        db.collection("Users").document(email).set(user);
    }


    private static String getRandomString(final int sizeOfPasswordString) {
        String ALLOWED_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
                "abcdefghijklmnopqrstuvwxyz0123456789"+"!@#$%^&*()_-+=<>?/{}~|";
        final Random random = new Random();
        final StringBuilder sb = new StringBuilder(sizeOfPasswordString);

        for (int i = 0; i < sizeOfPasswordString; i++) {
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        }
        Log.d("MY TAG", "PASSWORD" + sb.toString());

        return sb.toString();
    }

    private static String hashPassword(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        int iterations = 100000;
        char[] chars = password.toCharArray();
        byte[] salt = getSalt();

        PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, 64 * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] hash = skf.generateSecret(spec).getEncoded();
        return iterations + ":" + toHex(salt) + ":" + toHex(hash);
    }

    private static byte[] getSalt() throws NoSuchAlgorithmException {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[128];
        sr.nextBytes(salt);
        Log.d("MY ACTIVITTY","SALT:  "+ Arrays.toString(salt));
        return salt;
    }

    private static String toHex(byte[] array) {
        BigInteger bi = new BigInteger(1, array);
        String hex = bi.toString(16);
        int paddingLength = (array.length * 2) - hex.length();
        if(paddingLength > 0) {
            return String.format("%0"  +paddingLength + "d", 0) + hex;
        }else{
            return hex;
        }
    }

    private static boolean checkString(String str) {
        char ch;
        boolean capitalFlag = false;
        boolean lowerCaseFlag = false;
        boolean numberFlag = false;
        for(int i=0;i < str.length();i++) {
            ch = str.charAt(i);
            if( Character.isDigit(ch)) {
                numberFlag = true;
            }
            else if (Character.isUpperCase(ch)) {
                capitalFlag = true;
            } else if (Character.isLowerCase(ch)) {
                lowerCaseFlag = true;
            }
            if(numberFlag && capitalFlag && lowerCaseFlag)
                return true;
        }
        return false;
    }
}

