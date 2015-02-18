package com.example.sap.babywatch;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.provider.SyncStateContract;
import android.support.v7.app.ActionBarActivity;

import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.*;
import com.getpebble.android.kit.Constants;
import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;


import com.getpebble.android.kit.PebbleKit;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class MainActivity extends ActionBarActivity {

    TextView statusTextView;
    Boolean pebbleConnected;
    ToggleButton startStopBtn;
    LineChart chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        statusTextView = (TextView) findViewById(R.id.pebbelStatus);
        statusTextView.setText("Waiting for Pebble Status...");
        startStopBtn = (ToggleButton) findViewById(R.id.startStop);

        final Handler handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                handler.postDelayed(this, 1000);
                pebbleConnected = PebbleKit.isWatchConnected(getApplicationContext());
                startStopBtn.setEnabled(pebbleConnected);
                if (pebbleConnected) {
                    statusTextView.setText("Pebble is now connected");

                } else {
                    statusTextView.setText("Pebble is disconnected");
                }
            }
        };
        handler.postDelayed(r, 0000);
    }


    UUID PEBBLE_APP_UUID = UUID.fromString("4f1ba7a6-5836-48cf-986e-dc9f3f8916bb");

    private void addEntry(LineData lineData, int count){

        /*
        int mult = 16;
       // lineData.removeDataSet(count);
        float val = (float) (Math.random() * mult) + 3;// + (float)
      //  int valInt = (int) (Math.random() * 3) + 3;// + (float)
        Entry entry = new Entry(val, count);
        lineData.addEntry(entry,count);
        */

    }

    public void addData(View view){
        LineChart chart = (LineChart) findViewById(R.id.chart);
        for ( int i = 0 ; i < 15 ; i++) {
            this.addEntry(chart.getData(),i);
        }
        chart.animateXY(20,20);
        chart.refreshDrawableState();
    }
    private LineData setData(int count, float range) {

        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < count; i++) {
            xVals.add((i) + "");
        }

        ArrayList<Entry> yVals = new ArrayList<Entry>();

        for (int i = 0; i < count; i++) {
            float mult = (range + 1);
            float val = (float) (Math.random() * mult) + 3;// + (float)
            // ((mult *
            // 0.1) / 10);
            yVals.add(new Entry(val, i));
        }

        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(yVals, "DataSet 1");

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

        showMessage("Start Listening...");
        displaySpeechRecognizer();

    }

    private void handleStop(){
        showMessage("Stop");
    }

    public void sendAlertToPebble() {
        final Intent i = new Intent("com.getpebble.action.SEND_NOTIFICATION");

        final Map data = new HashMap();
        data.put("title", "Baby's Status");
        data.put("body", "Hurry! your baby is crying.");
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

    public void onToggleClicked(View view){
        // Is the toggle on?
        boolean isStart = ((ToggleButton) view).isChecked();
        LineChart chart = (LineChart) findViewById(R.id.chart);


        if (isStart) {
            this.handleStart();
        } else {
            this.handleStop();
            chart.clear();
         //   chart.setData(this.setData(55,55));
         //   chart.animateY(4500);
        //    chart.refreshDrawableState();


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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private static final int SPEECH_REQUEST_CODE = 0;

    // Create an intent that can start the Speech Recognizer activity
    private void displaySpeechRecognizer() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        // Start the activity, the intent will be populated with the speech text
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }

    private void showChart(){
        chart.setData(this.setData(15,15));
        chart.animateX(300);
        chart.refreshDrawableState();
    }
    // This callback is invoked when the Speech Recognizer returns.
    // This is where you process the intent and extract the speech text from the intent.
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);
            // Do something with spokenText
            this.sendAlertToPebble();
            this.showChart();

        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
