package com.example.cattower;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.github.mikephil.charting.charts.LineChart;

public class InfoActivity extends Activity {
    Button btnok,btncansel;
    EditText name_info,age_info,kind_info;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        name_info = (EditText) findViewById(R.id.name_info);
        age_info = (EditText) findViewById(R.id.age_info);
        kind_info = (EditText) findViewById(R.id.kind_info);

        btnok = (Button) findViewById(R.id.info_ok);
        btncansel = (Button) findViewById(R.id.info_cancel);

        btnok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mode;
                mode = name_info.getText().toString()+",";
                mode += age_info.getText().toString()+",";
                mode += kind_info.getText().toString();

                Intent intent = new Intent(getApplicationContext(), MyService.class);
                intent.putExtra("step", 1);
                intent.putExtra("mode", "info,"+mode);
                startService(intent);
            }
        });

        btncansel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MyService.class);
                intent.putExtra("step", 1);
                intent.putExtra("mode", "info,cancel");
                startService(intent);

                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MyService.class);
        intent.putExtra("step", 1);
        intent.putExtra("mode","return,m");
        startService(intent);
        super.onBackPressed();
    }

    @Override
    // 서비스쪽에서 던져준 데이터를 받기위한 메서드이다. processCommand는 출력하기위한 메소드
    // 처음이면 oncreate에서 확인 하고 그렇지 않으면(처음이 아니라면) oncreate에서 호출되지 않고
    // onNewIntent() 를 호출 하게 된다. 서비스 -> 액티비티에서 확인하는경우.
    protected void onNewIntent(Intent intent) {
        processIntent(intent);
        super.onNewIntent(intent);
    }

    private void processIntent(Intent intent){
        int step = intent.getIntExtra("step",0);
        Log.d("TAG","recv step 확인");
        if(step==3){
            Log.d("TAG","recv data 받음");
            String data = intent.getStringExtra("data");
            Log.d("data = ", data);

            String[] array = data.split(",");

            name_info.setText(array[1]);
            age_info.setText(array[2]);
            kind_info.setText(array[3]);
        }
    }
}