package org.unimelb.BirdMigration;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.*;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class StartActivity extends AppCompatActivity {

    private TextView locationLabel;
    private Switch locationSwitch;

    // Gps
    protected LocationManager locationManager;
    private double latitude = 0;
    private double longitude = 0;
    // Search result
    private String cityName = "";
    private String woied = "";
    private String weather = "";
    // Permission check
    public static boolean hasPermissions(Context context, String... permissions)
    {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null)
        {
            for (String permission : permissions)
            {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED)
                {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Require permission
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET};

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        // The entry of GPS visibility
        locationLabel = findViewById(R.id.locationLabel);
        locationSwitch = (Switch) findViewById(R.id.locationSwitch);

        // The entry of GPS visibility
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationUpdate();

        locationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!weather.equals("")){
                    String helpMessage = "Hello, Bird from " +  cityName+ "\nTake care, today's weather is "+ weather;
                    Toast.makeText(StartActivity.this, helpMessage, Toast.LENGTH_SHORT).show();
                } else{
                    Toast.makeText(StartActivity.this, "Wait for coming data.....", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    // Location udate
    private void LocationUpdate(){
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);
        criteria.setCostAllowed(true);
        criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
        criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);

        final LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestSingleUpdate(criteria, locationListener, null);

    }
    // Create location listener
    final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();

            locationLabel.setText("Location: " + String.valueOf(latitude) + ", "+ longitude);
            // Request for city woeid
            if(latitude != 0 && longitude != 0 && woied.equals("")){
                requestURL();
            }

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.d("Status Changed", String.valueOf(status));
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.d("Provider Enabled", provider);
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.d("Provider Disabled", provider);
        }
    };

    // city location
    private void requestURL(){
        RequestQueue queue = Volley.newRequestQueue(StartActivity.this);

        String urlLocation = "https://www.metaweather.com/api/location/search/?lattlong=" + latitude + "," + longitude;
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, urlLocation, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    cityName = response.getJSONObject(0).getString("title");
                    woied = response.getJSONObject(0).getString("woeid");
                    locationLabel.setText("Location: " + cityName);
                    cityWeather();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, error -> Toast.makeText(StartActivity.this, "Error occur in locate city", Toast.LENGTH_SHORT).show());
        queue.add(request);
    }

    // weather check
    private void cityWeather(){
        RequestQueue queue = Volley.newRequestQueue(StartActivity.this);

        String urlLocation = "https://www.metaweather.com/api/location/"+ woied + "/";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, urlLocation, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray weatherArray = (JSONArray) response.get("consolidated_weather");
                    weather = weatherArray.getJSONObject(0).getString("weather_state_name");
                    locationLabel.setText("Location: " + cityName + "\nWeather: " + weather);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, error -> Toast.makeText(StartActivity.this, "Error occur in weather check", Toast.LENGTH_SHORT).show());
        queue.add(request);
    }


    // Jump to the MainActivity interface
    public void startGame(View view) {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }

}
