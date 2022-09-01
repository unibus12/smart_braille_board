package com.example.cattower;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class MenuActivity extends Activity {
    ImageButton btnstep, btnheat, btneat, btnplay, btnhealth, btninfo;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        ImageButton btnstep = (ImageButton) findViewById(R.id.btnstep);
        ImageButton btnheat = (ImageButton) findViewById(R.id.btnheat);
        ImageButton btneat = (ImageButton) findViewById(R.id.btneat);
        ImageButton btnplay = (ImageButton) findViewById(R.id.btnplay);
        ImageButton btnhealth = (ImageButton) findViewById(R.id.btnhealth);
        ImageButton btninfo = (ImageButton) findViewById(R.id.btninfo);

        btnstep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MyService.class);
                intent.putExtra("step", 1);
                intent.putExtra("mode","step");
                startService(intent);

                Intent step_intent = new Intent(getApplicationContext(), StepActivity.class);
                startActivity(step_intent);
            }
        });
        btnheat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MyService.class);
                intent.putExtra("step", 1);
                intent.putExtra("mode","temp");
                startService(intent);

                Intent heat_intent = new Intent(getApplicationContext(), HeatActivity.class);
                startActivity(heat_intent);
            }
        });
        btneat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MyService.class);
                intent.putExtra("step", 1);
                intent.putExtra("mode","weight");
                startService(intent);

                Intent eat_intent = new Intent(getApplicationContext(), EatActivity.class);
                startActivity(eat_intent);
            }
        });
        btnplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MyService.class);
                intent.putExtra("step", 1);
                intent.putExtra("mode","play");
                startService(intent);

                Intent play_intent = new Intent(getApplicationContext(), PlayActivity.class);
                startActivity(play_intent);
            }
        });
        btnhealth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MyService.class);
                intent.putExtra("step", 1);
                intent.putExtra("mode","health");
                startService(intent);

                Intent health_intent = new Intent(getApplicationContext(), HealthActivity.class);
                startActivity(health_intent);
            }
        });
        btninfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MyService.class);
                intent.putExtra("step", 1);
                intent.putExtra("mode","info");
                startService(intent);

                Intent info_intent = new Intent(getApplicationContext(), InfoActivity.class);
                startActivity(info_intent);
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
