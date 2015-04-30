package com.test.frank.dynamicproject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Looper;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {

    Button btnSearchLocation;
    Button btnSearchType;
    String device_id;
    String device_sensor_id;
    private ProgressDialog pDialog;
    double longitude;
    double latitude;
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_DEVICE_SENSORID = "device_sensorID";
    private static final String TAG_TYPE = "type";
    private static final String TAG_LONGITUDE = "longitude";
    private static final String TAG_LATITUDE = "latitude";
    private static final String TAG_VALUE = "value";
    private static final String url_update_sensor = "http://www.fyjdesign.com/finalproject/update_sensor.php";
    JSONParser jsonParser=new JSONParser();
    protected LocationManager locationManager;
    LocationListener locationListener;
    Location location;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnSearchLocation=(Button)findViewById(R.id.btnlocationsearch);
        btnSearchType=(Button) findViewById(R.id.btntypesearch);

        device_id= Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        new updatesensors().execute();
        btnSearchLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), search_location.class);
                double[] latlng={(double)latitude,(double)longitude};
                intent.putExtra("LatLng",latlng);
                startActivity(intent);
            }
        });
        btnSearchType.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), Search_type.class);
                double[] latlng={(double)latitude,(double)longitude};
                intent.putExtra("LatLng",latlng);
                startActivity(intent);
            }
        });
    }

    class updatesensors extends AsyncTask<String,String,String> {
        @Override
        protected String doInBackground(String... params) {
            locationListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                }

                public void onStatusChanged(String provider, int status,
                                            Bundle extras) {
                }

                public void onProviderEnabled(String provider) {
                }

                public void onProviderDisabled(String provider) {
                }
            };
            try{
                locationManager=(LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0, locationListener, Looper.getMainLooper());
                if (locationManager!=null){
                    location=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if(location!=null){
                        latitude=(float)location.getLatitude();
                        longitude=(float)location.getLongitude();
                    }
                }
            }catch(Exception e){
                e.printStackTrace();
            }
            SensorManager mSensorManager;
            mSensorManager=(SensorManager)getSystemService(Context.SENSOR_SERVICE);
            if(mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)!=null){
                device_sensor_id=device_id+"_1";
                updatefunction("Accelerometer");
            }
            if (mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null){
                device_sensor_id=device_id+"_2";
                updatefunction("Magnetic");
            }
            if (mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE) != null){
                device_sensor_id=device_id+"_3";
                updatefunction("Thermometer");
            }
            if (mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null){
                device_sensor_id=device_id+"_4";
                updatefunction("Gyroscope");
            }
            if (mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT) != null){
                device_sensor_id=device_id+"_5";
                updatefunction("Light");
            }
            if (mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY) != null){
                device_sensor_id=device_id+"_6";
                updatefunction("Proximity");
            }
            if (mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE) != null){
                device_sensor_id=device_id+"_7";
                updatefunction("Pressure");
            }

            return null;
        }
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Detecting sensors. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }
        void updatefunction(String type) {
            float value = 50;
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair(TAG_DEVICE_SENSORID, device_sensor_id));
            params.add(new BasicNameValuePair(TAG_TYPE, type));
            params.add(new BasicNameValuePair(TAG_LONGITUDE, Double.toString(longitude)));
            params.add(new BasicNameValuePair(TAG_LATITUDE, Double.toString(latitude)));
            params.add(new BasicNameValuePair(TAG_VALUE, Float.toString(value)));
            System.out.println(params.toString());
            JSONObject json = jsonParser.makeHttpRequest(url_update_sensor, "POST", params);
            //   try {
            //int success = json.getInt(TAG_SUCCESS);
            //  }catch (JSONException e) {
            //    e.printStackTrace();
            // }
        }
        protected void onPostExecute(String file_url){
            pDialog.dismiss();
        }


    }
}
