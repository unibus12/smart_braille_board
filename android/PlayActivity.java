package com.example.cattower;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.graphics.Color;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;

public class PlayActivity extends Activity {
    TextView textim;
    LineChart linechart;
    Button btnnofeed;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        textim = (TextView) findViewById(R.id.textim);
        linechart = (LineChart) findViewById(R.id.linechartplay);
        btnnofeed = (Button) findViewById(R.id.btnnofeed);

        btnnofeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btnnofeed.getText().equals("주기")) {
                    Intent intent = new Intent(getApplicationContext(), MyService.class);
                    intent.putExtra("step", 1);
                    intent.putExtra("mode","play,off");
                    startService(intent);
                    //btnnofeed.setText("안주기");
                }
                else if (btnnofeed.getText().equals("안주기")) {
                    Intent intent = new Intent(getApplicationContext(), MyService.class);
                    intent.putExtra("step", 1);
                    intent.putExtra("mode","play,auto");
                    startService(intent);
                    //btnnofeed.setText("auto");
                }
                else {
                    Intent intent = new Intent(getApplicationContext(), MyService.class);
                    intent.putExtra("step", 1);
                    intent.putExtra("mode","play,on");
                    startService(intent);
                    //btnnofeed.setText("주기");
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
        if(step==3) {
            Log.d("TAG", "recv data 받음");
            String data = intent.getStringExtra("data");
            Log.d("data = ", data);

            String[] array = data.split(",");

            for (int i = 1; i <= array.length; i++){
                if (array[i].equals("on")){
                    array[i] = "주기";
                }
                else if (array[i].equals("off")){
                    array[i] = "안주기";
                }
                else if (array[i].equals("auto/on")){
                    array[i] = "auto/주기";
                }
                else if (array[i].equals("auto/off")){
                    array[i] = "auto/안주기";
                }
            }

            if (array.length == 3) {
                Log.d("TAG", "array 1 = " + array[1]);
                String[] playcount = array[1].split("-");

                ArrayList<Entry> values = new ArrayList<>();

                String imt = "";
                for (int i = 0; i < playcount.length; i++) {
                    int val = (int) Integer.parseInt(playcount[i]);
                    values.add(new Entry(i, val));
                    Log.d("TAG", "playcount = " + playcount[i]);
                    if (val <= 1) {
                        imt += "😭 ";
                    } else if (val == 2) {
                        imt += "😌 ";
                    } else if (val >= 3) {
                        imt += "😄 ";
                    }
                }

                textim.setText(imt);

                LineDataSet set1 = new LineDataSet(values, "play count");
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

                btnnofeed.setText(array[2]);
            }
            else {
                btnnofeed.setText(array[1]);
            }
        }
    }
}