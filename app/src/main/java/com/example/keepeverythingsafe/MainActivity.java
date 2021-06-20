package com.example.keepeverythingsafe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import java.util.concurrent.Executor;


public class MainActivity extends AppCompatActivity {
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button loginButton = (Button) findViewById(R.id.login);
        TextView errtext = (TextView) findViewById(R.id.errtext);

        BiometricManager fingerprint = BiometricManager.from(this);

        switch (fingerprint.canAuthenticate()){
            case BiometricManager.BIOMETRIC_SUCCESS:
                errtext.setText("");
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                    errtext.setText("We can't access device sensor");
                    loginButton.setVisibility(View.GONE);
                    break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                errtext.setText("Your device have not fingerprint sensor");
                loginButton.setVisibility(View.GONE);
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                errtext.setText("You didn't save any fingerprint, go to settings and upload fingerprint");
                loginButton.setVisibility(View.GONE);
                break;
        }

        Executor executor = ContextCompat.getMainExecutor(this);

        BiometricPrompt bioprompt = new BiometricPrompt(MainActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);

            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                startActivity(new Intent(getApplicationContext(), login.class));

            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
            }
        });

        final BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Login").setDescription("Use fingerprint to login")
                .setNegativeButtonText("Cancel").build();

        loginButton.setOnClickListener(v -> bioprompt.authenticate(promptInfo));
    }
}