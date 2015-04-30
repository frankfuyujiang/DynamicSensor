package com.test.frank.dynamicproject;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

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


public class Search_type extends Activity implements AdapterView.OnItemSelectedListener{
    private ProgressDialog pDialog;
    private String device_id;
    private String type;
    private LatLng thisLatLng;
    private static final String url_location_search = "http://www.fyjdesign.com/finalproject/search_by_type.php";
    private JSONParser jsonParser=new JSONParser();
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_DEVICE_SENSORID = "device_sensorID";
    private static final String TAG_TYPE = "type";
    private static final String TAG_LATITUDE = "latitude";
    private static final String TAG_LONGITUDE="longitude";
    private static final String TAG_VALUE="value";
    private Spinner typespinner;
    private GoogleMap map;
    private ArrayList<HashMap<String, String>> sensorList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_type);
        Bundle extra=getIntent().getExtras();
        if (extra==null){
            thisLatLng=new LatLng(0,0);
        }else{
            double[] temp=extra.getDoubleArray("LatLng");
            thisLatLng=new LatLng(temp[0],temp[1]);
        }
        device_id= Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        type="Accelerometer";
        typespinner=(Spinner)findViewById(R.id.types);
        ArrayAdapter typeadapter=ArrayAdapter.createFromResource(this,R.array.sensortype,android.R.layout.simple_spinner_dropdown_item);
        typespinner.setAdapter(typeadapter);
        typespinner.setOnItemSelectedListener(this);
        sensorList=new ArrayList<HashMap<String,String>>();
        new LoadTypeSensors().execute();
        map=((MapFragment) getFragmentManager().findFragmentById(R.id.fragment)).getMap();
        CameraUpdate update= CameraUpdateFactory.newLatLngZoom(thisLatLng, 17);
        map.animateCamera(update);
        //System.out.println("ran_Oncreate");

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        TextView myText=(TextView) view;
        //System.out.println("ran_ItemSelected");
        switch ((String)myText.getText()){
            case "Accelerometer" :
                type="Accelerometer";
                break;
            case "Magnetic" :
                type="Magnetic";
                break;
            case "Thermometer":
                type="Thermometer";
                break;
            case "Gyroscope":
                type="Gyroscope";
                break;
            case "Light":
                type="Light";
                break;
            case "Proximity":
                type="Proximity";
                break;
            case "Pressure":
                type="Pressure";
                break;
            default:
                type="Accelerometer";
        }
        //new LoadTypeSensors().execute();

        map.clear();
        map.addMarker(new MarkerOptions().position(thisLatLng).title("My Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        for (HashMap<String,String> item : sensorList){
            double lat=Double.parseDouble(item.get("latitude"));
            double lon=Double.parseDouble(item.get("longitude"));
            //System.out.println(item.get(TAG_TYPE).toString());
            if (item.get(TAG_TYPE).toString().equals(type)) {
                //System.out.println(item.get(TAG_TYPE).toString());
                map.addMarker(new MarkerOptions().position(new LatLng(lat, lon)).title(type));
            }

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    class LoadTypeSensors extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Search_type.this);
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
            sensorList.clear();
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("device_sensorID", device_id));
            //params.add(new BasicNameValuePair("type",type));
            // getting JSON string from URL
            JSONObject json = jsonParser.makeHttpRequest(url_location_search, "GET", params);
            //System.out.println(json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // products found
                    // Getting Array of Products
                    JSONArray jsonArray=json.getJSONArray("sensors");
                    System.out.println(jsonArray.toString());
                    System.out.println(jsonArray.length());
                    for (int i=0;i<jsonArray.length();i++){
                        JSONObject c=jsonArray.getJSONObject(i);
                        String device_sensorID=c.getString(TAG_DEVICE_SENSORID);
                        String type=c.getString(TAG_TYPE);
                        String longitude=c.getString(TAG_LONGITUDE);
                        String latitude=c.getString(TAG_LATITUDE);
                        String value=c.getString(TAG_VALUE);

                        HashMap<String,String> hashmap=new HashMap<String,String>();

                        hashmap.put(TAG_DEVICE_SENSORID,device_sensorID);
                        hashmap.put(TAG_TYPE,type);
                        hashmap.put(TAG_LONGITUDE,longitude);
                        hashmap.put(TAG_LATITUDE,latitude);
                        hashmap.put(TAG_VALUE,value);
                        //System.out.println("successfully added to map");
                       // System.out.println(hashmap.toString());
                        sensorList.add(hashmap);
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
            //System.out.println("done background");

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        @Override
         protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            map.addMarker(new MarkerOptions().position(thisLatLng).title("My Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

            for (HashMap<String,String> item : sensorList){
                double lat=Double.parseDouble(item.get("latitude"));
                double lon=Double.parseDouble(item.get("longitude"));
                if (item.get(TAG_TYPE).toString().equals(type)) {
                    map.addMarker(new MarkerOptions().position(new LatLng(lat, lon)).title(type));
                }
            }
        }

    }
}
