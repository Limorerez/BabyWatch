package com.example.sap.babywatch;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.getpebble.android.kit.PebbleKit;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    UUID PEBBLE_APP_UUID = UUID.fromString("4f1ba7a6-5836-48cf-986e-dc9f3f8916bb");
    public void startWatchApp(View view) {
        PebbleKit.startAppOnPebble(getApplicationContext(), PEBBLE_APP_UUID);
    }

    // Send a broadcast to close the specified application on the connected Pebble
    public void stopWatchApp(View view) {
        PebbleKit.closeAppOnPebble(getApplicationContext(), PEBBLE_APP_UUID);
    }

    private void handleStart(){
        showMessage("Start");
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

        if (isStart) {
               this.handleStart();
               this.sendAlertToPebble();
        } else {
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
}
