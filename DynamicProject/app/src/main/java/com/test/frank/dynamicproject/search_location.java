package com.test.frank.dynamicproject;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.identity.intents.AddressConstants;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.lang.Math;


public class search_location extends Activity implements AdapterView.OnItemSelectedListener{
    String device_id;
    private ProgressDialog pDialog;
    private LatLng thisLatLng;
    private float radius=10;
    private static final String url_location_search = "http://www.fyjdesign.com/finalproject/search_by_location.php";
    JSONParser jsonParser=new JSONParser();
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_DEVICE_SENSORID = "device_sensorID";
    private static final String TAG_TYPE = "type";
    private static final String TAG_LATITUDE = "latitude";
    private static final String TAG_LONGITUDE="longitude";
    private static final String TAG_VALUE="value";
    Spinner spinner;
    private GoogleMap map;
    ArrayList<HashMap<String, String>> sensorList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_location);
        Bundle extra=getIntent().getExtras();
        if (extra==null){
            thisLatLng=new LatLng(0,0);
        }else{
            double[] temp=extra.getDoubleArray("LatLng");
            thisLatLng=new LatLng(temp[0],temp[1]);
        }
        device_id= Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        spinner=(Spinner)findViewById(R.id.spinner);
        ArrayAdapter adapter=ArrayAdapter.createFromResource(this,R.array.radius,android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        sensorList=new ArrayList<HashMap<String,String>>();
        new LoadAllSensors().execute();
        map=((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        CameraUpdate update= CameraUpdateFactory.newLatLngZoom(thisLatLng, 17);
        map.animateCamera(update);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        TextView myText=(TextView) view;
        switch ((String)myText.getText()){
            case "200m" :
                radius=200;
                break;
            case "1km" :
                radius=1000;
                break;
            case "10km":
                radius=10000;
                break;
            case "1000km":
                radius=1000000;
                break;
            case "5000km":
                radius=5000000;
                break;
            case "+":
                radius=Float.POSITIVE_INFINITY;
            default:
                radius=Float.POSITIVE_INFINITY;
        }
        map.clear();
        map.addMarker(new MarkerOptions().position(thisLatLng).title("My Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        for (HashMap<String,String> item : sensorList){
            double lat=Double.parseDouble(item.get("latitude"));
            double lon=Double.parseDouble(item.get("longitude"));
            if ((Math.hypot((lat-thisLatLng.latitude)*111000,(lon-thisLatLng.longitude)*Math.cos(thisLatLng.latitude)*111000))<radius) {
                map.addMarker(new MarkerOptions().position(new LatLng(lat, lon)));
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
    class LoadAllSensors extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(search_location.this);
            pDialog.setMessage("Loading sensors. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting All products from url
         * */
        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("device_sensorID", device_id));
            // getting JSON string from URL
            JSONObject json = jsonParser.makeHttpRequest(url_location_search, "GET", params);
            //System.out.println(json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // products found
                    // Getting Array of Products
                    //System.out.println("ran");
                    JSONArray jsonArray=json.getJSONArray("sensors");
                    //System.out.println("ran2");
                    //System.out.println(jsonArray.toString());
                    //System.out.println(jsonArray.length());
                    for (int i=0;i<jsonArray.length();i++){
                        //System.out.println("ranfor1");
                        JSONObject c=jsonArray.getJSONObject(i);
                        String device_sensorID=c.getString(TAG_DEVICE_SENSORID);
                        String type=c.getString(TAG_TYPE);
                        String longitude=c.getString(TAG_LONGITUDE);
                        String latitude=c.getString(TAG_LATITUDE);
                        String value=c.getString(TAG_VALUE);
                        //System.out.println("ranfor2");

                        HashMap<String,String> map=new HashMap<String,String>();

                        map.put(TAG_DEVICE_SENSORID,device_sensorID);
                        map.put(TAG_TYPE,type);
                        map.put(TAG_LONGITUDE,longitude);
                        map.put(TAG_LATITUDE,latitude);
                        map.put(TAG_VALUE,value);
                        //System.out.println("successfully added to map");
                        //System.out.println(map.toString());
                        sensorList.add(map);
                        //System.out.println("successfully added");
                    }
                    //System.out.println(sensorList.toString());

                } else {
                    // no products found
                    // Launch Add New product Activity
                    //System.out.println("success==0");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        @Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            // updating UI from Background Thread
            map.addMarker(new MarkerOptions().position(thisLatLng).title("My Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            for (HashMap<String,String> item : sensorList){
                double lat=Double.parseDouble(item.get("latitude"));
                double lon=Double.parseDouble(item.get("longitude"));
                if ((Math.hypot((lat-thisLatLng.latitude)*111000,(lon-thisLatLng.longitude)*Math.cos(thisLatLng.latitude)*111000))<radius) {
                    map.addMarker(new MarkerOptions().position(new LatLng(lat, lon)));
                }
            }
        }

    }


}
