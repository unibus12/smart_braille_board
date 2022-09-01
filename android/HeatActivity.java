package com.example.cattower;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class HeatActivity extends Activity {
    Button btnheatch;
    TextView temp_data, temp_data1;
    ImageView tempImage;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heat);

        btnheatch = (Button) findViewById(R.id.btnheatch);
        temp_data = (TextView) findViewById(R.id.temp_data);
        temp_data1 = (TextView) findViewById(R.id.temp_data1);
        tempImage = (ImageView) findViewById(R.id.tempImage);

        btnheatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btnheatch.getText().equals("ON")) {
                    Intent intent = new Intent(getApplicationContext(), MyService.class);
                    intent.putExtra("step", 1);
                    intent.putExtra("mode","temp,off");
                    startService(intent);
                }
                else if (btnheatch.getText().equals("OFF")) {
                    Intent intent = new Intent(getApplicationContext(), MyService.class);
                    intent.putExtra("step", 1);
                    intent.putExtra("mode","temp,auto");
                    startService(intent);
                }
                else {
                    Intent intent = new Intent(getApplicationContext(), MyService.class);
                    intent.putExtra("step", 1);
                    intent.putExtra("mode","temp,on");
                    startService(intent);
                }
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
            Log.d("mytag","data = "+ data);

            String[] array = data.split(",");

            temp_data.setText("현재 기온 : "+array[1]+"도씨");


            if (array[2].equals("low")){
                array[2] = "낮은온도";
                tempImage.setImageResource(R.drawable.cold);
            } else if (array[2].equals("good")){
                array[2] = "적정온도";
                tempImage.setImageResource(R.drawable.good);
            } else if (array[2].equals("high")){
                array[2] = "높은온도";
                tempImage.setImageResource(R.drawable.hot);
            }

            temp_data1.setText(array[2]+"입니다.");
            btnheatch.setText(array[3]);
        }
    }
}
