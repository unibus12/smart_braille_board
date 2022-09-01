package com.example.cattower;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class SignupActivity extends AppCompatActivity {

    Button btnok, btncansel;
    EditText signup_id, signup_pw, signup_name, signup_age, signup_breed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        signup_id = (EditText) findViewById(R.id.signup_id);
        signup_pw = (EditText) findViewById(R.id.signup_pw);
        signup_name = (EditText) findViewById(R.id.signup_name);
        signup_age = (EditText) findViewById(R.id.signup_age);
        signup_breed = (EditText) findViewById(R.id.signup_breed);

        btnok = (Button) findViewById(R.id.sign_ok);
        btncansel = (Button) findViewById(R.id.sign_cancel);

        btnok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mode;
                mode = signup_id.getText().toString()+",";
                mode += signup_pw.getText().toString()+",";
                mode += signup_name.getText().toString()+",";
                mode += signup_age.getText().toString()+",";
                mode += signup_breed.getText().toString();

                Intent intent = new Intent(getApplicationContext(), MyService.class);
                intent.putExtra("step", 1);
                intent.putExtra("mode", "signup,"+mode);
                startService(intent);

                finish();
            }
        });

        btncansel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MyService.class);
                intent.putExtra("step", 1);
                intent.putExtra("mode","return,l");
                startService(intent);

                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MyService.class);
        intent.putExtra("step", 1);
        intent.putExtra("mode","return,l");
        startService(intent);
        super.onBackPressed();
    }
}
