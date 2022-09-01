package com.example.cattower;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import java.util.ArrayList;

public class EatActivity extends Activity {
    TextView kcal;
    LineChart linechart;
    Button btncal, btnfeed;
    SeekBar feednum;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eat);

        kcal = (TextView) findViewById(R.id.kcal);

        linechart = (LineChart) findViewById(R.id.linecharteat);

        btncal = (Button) findViewById(R.id.btncal);
        btnfeed = (Button) findViewById(R.id.btnfeed);
        feednum = (SeekBar) findViewById(R.id.feednum);

        btncal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btncal.getText().equals("저열량")) {
                    Intent intent = new Intent(getApplicationContext(), MyService.class);
                    intent.putExtra("step", 1);
                    intent.putExtra("mode","weight,high");
                    startService(intent);
                }
                else if(btncal.getText().equals("고열량")) {
                    Intent intent = new Intent(getApplicationContext(), MyService.class);
                    intent.putExtra("step", 1);
                    intent.putExtra("mode","weight,autocal");
                    startService(intent);
                }
                else {
                    Intent intent = new Intent(getApplicationContext(), MyService.class);
                    intent.putExtra("step", 1);
                    intent.putExtra("mode","weight,low");
                    startService(intent);
                }
            }
        });


        btnfeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btnfeed.getText().equals("수동")) {
                    Intent intent = new Intent(getApplicationContext(), MyService.class);
                    intent.putExtra("step", 1);
                    intent.putExtra("mode","weight,autofeed");
                    startService(intent);
                }
                else {
                    Intent intent = new Intent(getApplicationContext(), MyService.class);
                    intent.putExtra("step", 1);
                    intent.putExtra("mode","weight,nonautofeed");
                    startService(intent);
                }
            }
        });

        feednum.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Intent intent = new Intent(getApplicationContext(), MyService.class);
                intent.putExtra("step", 1);
                intent.putExtra("mode","weight,"+feednum.getProgress());
                startService(intent);
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

            for (int i = 1; i <= array.length; i++){
                if (array[i].equals("low")){
                    array[i] = "저열량";
                }
                else if (array[i].equals("high")){
                    array[i] = "고열량";
                }
                else if (array[i].equals("autocal")){
                    array[i] = "auto";
                }
                else if (array[i].equals("autofeed")){
                    array[i] = "자동";
                }
                else if (array[i].equals("nonautofeed")){
                    array[i] = "수동";
                }
            }

            if (array.length>=5) {
                Log.d("TAG","array 1 = "+array[1]);
                String[] weight = array[1].split("-");

                ArrayList<Entry> values = new ArrayList<>();

                for (int i = 0; i < weight.length; i++) {
                    float val = (float) Integer.parseInt(weight[i]);
                    values.add(new Entry(i, val));
                    Log.d("TAG","weight = "+weight[i]);
                }

                LineDataSet set1 = new LineDataSet(values, "eat count");
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

                if (array.length==5) {
                    btncal.setText(array[2]);
                    btnfeed.setText(array[3]);
                    kcal.setText(array[4] + " kcal");
                    feednum.setProgress(Integer.parseInt(array[4]));
                }
                else {
                    btncal.setText(array[2]+"/"+array[3]);
                    btnfeed.setText(array[4]);
                    kcal.setText(array[5] + " kcal");
                    feednum.setProgress(Integer.parseInt(array[5]));
                }
            }
            else if (array[1].equals("저열량")) {
                btncal.setText(array[1]);
            }
            else if (array[1].equals("고열량")) {
                btncal.setText(array[1]);
            }
            else if (array[1].equals("auto")) {
                btncal.setText(array[1]+"/"+array[2]);
            }
            else if (array[1].equals("수동")) {
                btnfeed.setText(array[1]);
                kcal.setText(array[2]+" kcal");
                feednum.setProgress(Integer.parseInt(array[2]));
            }
            else if (array[1].equals("자동")) {
                btnfeed.setText(array[1]);
                kcal.setText(array[2]+" kcal");
                feednum.setProgress(Integer.parseInt(array[2]));
            }
        }
    }
}
