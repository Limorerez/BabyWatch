package com.example.sap.babywatch;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.getpebble.android.kit.PebbleKit;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Legend;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class MainActivity extends ActionBarActivity {

    TextView statusTextView;
    Boolean pebbleConnected;
    ImageButton startStopBtn;
    LineChart chart;

    //Pebble EA76 device
    final UUID ORANGE_UUID = UUID.fromString("749ebe87-42a5-4cb9-8ad3-a9778c52661b");
    final UUID PEBBLE_APP_UUID = ORANGE_UUID;
    final int STOP_BTN_RES_ID = R.drawable.ic_stop_button;
    final int START_BTN_RES_ID = R.drawable.ic_start_button;
    boolean isStart = false;

    private SoundMeter mSensor;
    private Handler mHandler = new Handler();
    private static final int POLL_INTERVAL = 150;
    private double mThreshold = 0.5;
    private boolean msgSent = false;


    private Runnable mSleepTask = new Runnable() {
        public void run() {
            //Log.i("Noise", "runnable mSleepTask");

            start();
        }
    };

    private void start() {
        //Log.i("Noise", "==== start ===");

        try {
            mSensor.start();
        } catch (IOException e) {
            showMessage(e.getMessage());
        }

        //Noise monitoring start
        // Runnable(mPollTask) will execute after POLL_INTERVAL
        mHandler.postDelayed(mPollTask, POLL_INTERVAL);
    }
    // Create runnable thread to Monitor Voice
    private Runnable mPollTask = new Runnable() {
        public void run() {

            double amp = mSensor.getAmplitude();
            //Log.i("Noise", "runnable mPollTask");
            //updateDisplay("Monitoring Voice...", amp);

            if ((amp > mThreshold)) {
                sendAlertToPebble();
                showChart();
                //Log.i("Noise", "==== onCreate ===");

            }

            // Runnable(mPollTask) will again execute after POLL_INTERVAL
            mHandler.postDelayed(mPollTask, POLL_INTERVAL);

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        statusTextView = (TextView) findViewById(R.id.pebbelStatus);
        statusTextView.setText("Waiting for Pebble Status...");
        startStopBtn = (ImageButton) findViewById(R.id.startStopButton);
        createChart();
        mSensor = new SoundMeter();

        final Handler handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                handler.postDelayed(this, 1000);
                pebbleConnected = PebbleKit.isWatchConnected(getApplicationContext());
                startStopBtn.setEnabled(pebbleConnected);
                if (pebbleConnected) {
                    statusTextView.setText("Pebble device is connected");

                } else {
                    statusTextView.setText("Pebble device is disconnected");
                }
            }
        };
        handler.postDelayed(r, 0000);
    }

    private void createChart(){
        chart = (LineChart) findViewById(R.id.chart);
        chart.setBackgroundColor(Color.BLACK);
        chart.setValueTextColor(Color.WHITE);
        Paint whitePaint = new Paint();
        whitePaint.setColor(Color.WHITE);
        Paint blackPaint = new Paint();
        blackPaint.setColor(Color.BLACK);
        chart.setPaint(blackPaint, Chart.PAINT_GRID_BACKGROUND);
        chart.setPaint(blackPaint, Chart.PAINT_GRID);
        addEmptyData();
    }

    private LineData setData(int count, float range) {
        //chart.setUnit("dB Level");
        chart.setDrawYValues(false);
        chart.setDescription(" ");
        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < count; i++) {
            xVals.add((i) + "");
        }

        ArrayList<Entry> yVals = new ArrayList<Entry>();

        for (int i = 0; i < count; i++) {
            float mult = (range + 1);
            float val = (float) (Math.random() * mult) + 20;
            yVals.add(new Entry(val, i));
        }

        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(yVals, "dB");
        set1.setLineWidth(2f);
        set1.setColor(ColorTemplate.getHoloBlue());
        set1.setCircleColor(ColorTemplate.getHoloBlue());
        set1.setLineWidth(2f);
        set1.setCircleSize(4f);
        set1.setFillAlpha(65);
        set1.setFillColor(ColorTemplate.getHoloBlue());
        set1.setHighLightColor(Color.rgb(244, 117, 117));

        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        dataSets.add(set1); // add the datasets

        // create a data object with the datasets
        LineData data = new LineData(xVals, dataSets);

        // set data
        return data;
    }

    public void startWatchApp(View view) {
        PebbleKit.startAppOnPebble(getApplicationContext(), PEBBLE_APP_UUID);
    }

    // Send a broadcast to close the specified application on the connected Pebble
    public void stopWatchApp(View view) {
        PebbleKit.closeAppOnPebble(getApplicationContext(), PEBBLE_APP_UUID);
    }

    private void handleStart(){
        try {
            mSensor.start();
        } catch (IOException e) {
            showMessage(e.getMessage());
        }
        mHandler.postDelayed(mPollTask, POLL_INTERVAL);
    }

    private void handleStop(){
        addEmptyData();
        mHandler.removeCallbacks(mSleepTask);
        mHandler.removeCallbacks(mPollTask);
        mSensor.stop();
    }

    public void sendAlertToPebble() {
        final Intent i = new Intent("com.getpebble.action.SEND_NOTIFICATION");

        final Map data = new HashMap();
        data.put("title", "Baby is crying!");
        data.put("body", "");
        final JSONObject jsonData = new JSONObject(data);
        final String notificationData = new JSONArray().put(jsonData).toString();

        i.putExtra("messageType", "PEBBLE_ALERT");
        i.putExtra("sender", "MyAndroidApp");
        i.putExtra("notificationData", notificationData);

        //Log.d(TAG, "About to send a modal alert to Pebble: " + notificationData);
        sendBroadcast(i);
    }


    private void showMessage(String str){
        Context context = getApplicationContext();
        CharSequence text = str;
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    public void onClicked(View view){
        isStart = !isStart;
        if (isStart) {
            startStopBtn.setImageResource(STOP_BTN_RES_ID);
            this.handleStart();
        } else {
            startStopBtn.setImageResource(START_BTN_RES_ID);
            this.handleStop();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
//                Intent intent = new Intent(this, PickerPreferences.class);
//                startActivity(intent);
//                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private static final int SPEECH_REQUEST_CODE = 0;

    private void showChart(){
        chart.setData(this.setData(45,100));
        Legend l = chart.getLegend();
        l.setTextColor(Color.WHITE);
        chart.animateX(325);
        chart.refreshDrawableState();
    }

    private void addEmptyData() {

        // create 30 x-vals
        String[] xVals = new String[30];

        for (int i = 0; i < 30; i++)
            xVals[i] = "" + i;

        // create a chartdata object that contains only the x-axis labels (no entries or datasets)
        LineData data = new LineData(xVals);

        chart.setData(data);
        chart.invalidate();
    }
}
