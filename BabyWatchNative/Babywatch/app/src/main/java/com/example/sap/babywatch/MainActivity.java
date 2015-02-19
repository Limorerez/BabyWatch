package com.example.sap.babywatch;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.getpebble.android.kit.PebbleKit;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    final int STOP_BTN_RES_ID = R.drawable.stopbutton;
    final int START_BTN_RES_ID = R.drawable.startbutton;
    boolean isStart = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        statusTextView = (TextView) findViewById(R.id.pebbelStatus);
        statusTextView.setText("Waiting for Pebble Status...");
        startStopBtn = (ImageButton) findViewById(R.id.startStopButton);
        chart = (LineChart) findViewById(R.id.chart);

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
        for ( int i = 0 ; i < 15 ; i++) {
            this.addEntry(chart.getData(),i);
        }
        chart.animateXY(20, 20);
        chart.refreshDrawableState();

    }
    private LineData setData(int count, float range) {
        chart.setVisibility(View.VISIBLE);
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
            float val = (float) (Math.random() * mult) + 3;// + (float)
            // ((mult *
            // 0.1) / 10);
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
        displaySpeechRecognizer();
    }

    private void handleStop(){
        chart.setVisibility(View.VISIBLE);
        chart.clear();
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
