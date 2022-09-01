package com.example.cattower;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;

public class HealthActivity extends Activity {
    TextView urgent_step, btnrecommend;
    LineChart linechart;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health);

        urgent_step = (TextView) findViewById(R.id.urgent_step);
        btnrecommend = (TextView) findViewById(R.id.btnrecommend);
        linechart = (LineChart) findViewById(R.id.linecharthealth);

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

            Log.d("TAG","array 1 = "+array[1]);
            String[] ammonia = array[1].split("-");

            ArrayList<Entry> values = new ArrayList<>();

            for (int i = 0; i < ammonia.length; i++) {
                float val = (float) Integer.parseInt(ammonia[i]);
                values.add(new Entry(i, val));
            }

            LineDataSet set1 = new LineDataSet(values, "ammonia value");
            //new LineDataSet(values, "DataSet");

            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1); // add the data sets

            // create a data object with the data sets
            LineData linedata = new LineData(dataSets);

            // black lines and points
            set1.setColor(Color.GRAY);
            set1.setCircleColor(Color.GRAY);

            // set data
            linechart.setData(linedata);

            Log.d("TAG","건강 상태 = "+array[2]+","+array[3]);
            urgent_step.setText(array[2]);
            btnrecommend.setText(array[3]);
        }
    }
}
