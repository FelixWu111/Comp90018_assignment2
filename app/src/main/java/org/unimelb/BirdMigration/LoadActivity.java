package org.unimelb.BirdMigration;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
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

import java.time.LocalDateTime;
import java.util.Calendar;


public class LoadActivity extends AppCompatActivity {
    // Info shown
    private TextView infoLabel;
    private TextView loadLabel;
    ProgressBar progressBar;
    //Video View
    private VideoView loadingVideo;
    // Gps
    protected LocationManager locationManager;
    private double latitude = 0;
    private double longitude = 0;
    // Search resultmorn
    private String cityName = "";
    private String woied = "";
    private String weather = "";

    public class LoadingBar extends Animation {
        private Context context;
        private ProgressBar progressBar;
        private TextView textView;
        private float from;
        private float to;


        public LoadingBar(Context context, ProgressBar progressBar, TextView textView, float from, float to) {
            this.context = context;
            this.progressBar = progressBar;
            this.textView = textView;
            this.from = from;
            this.to = to;
        }

        @Override
        protected void applyTransformation(float interpolatedTim, Transformation t){
            super.applyTransformation(interpolatedTim, t);
            float fV = from + (to - from)* interpolatedTim;
            progressBar.setProgress((int) fV);
            loadLabel.setText((int) fV + "%");
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //// Require permission
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET};

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        //// XML part
        // Play video
        loadingVideo = (VideoView) findViewById(R.id.videoView);
        // Set Video content base o time
        Calendar rightNow = Calendar.getInstance();
        int hour = rightNow.get(Calendar.HOUR_OF_DAY);
        if(hour >= 6 && hour <= 18) {
            loadingVideo.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.morning));
        }else{
            loadingVideo.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.night));
        }
        // Start video
        loadingVideo.start();
        // Set progress bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        progressBar = findViewById(R.id.progress_bar);

        loadLabel = findViewById(R.id.load_view);
        infoLabel = findViewById(R.id.info_view);
        progressBar.setMax(100);
        progressBar.setScaleY(3f);
        progressAnimation();

        //// Require GPS data
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationUpdate();
    }
    // Permission check
    public static boolean hasPermissions(Context context, String... permissions){
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

            infoLabel.setText("Get Location: " + String.valueOf(latitude) + ", "+ longitude);
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
    // City location
    private void requestURL(){
        RequestQueue queue = Volley.newRequestQueue(LoadActivity.this);

        String urlLocation = "https://www.metaweather.com/api/location/search/?lattlong=" + latitude + "," + longitude;
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, urlLocation, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    cityName = response.getJSONObject(0).getString("title");
                    woied = response.getJSONObject(0).getString("woeid");
                    infoLabel.setText("Get City: " + cityName);
                    cityWeather();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, error -> Toast.makeText(LoadActivity.this, "Error occur in locate city", Toast.LENGTH_SHORT).show());
        queue.add(request);
    }
    // Weather check
    private void cityWeather(){
        RequestQueue queue = Volley.newRequestQueue(LoadActivity.this);

        String urlLocation = "https://www.metaweather.com/api/location/"+ woied + "/";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, urlLocation, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray weatherArray = (JSONArray) response.get("consolidated_weather");
                    weather = weatherArray.getJSONObject(0).getString("weather_state_name");
                    infoLabel.setText("Get Location: " + cityName + "Weather: " + weather);
                    // Jump to next activity, and pass weather to it
                    Intent intent = new Intent(getApplicationContext(), StartActivity.class);
                    intent.putExtra("weather", weather);
                    intent.putExtra("cityName", cityName);  //新增
                    startActivity(intent);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, error -> Toast.makeText(LoadActivity.this, "Error occur in weather check", Toast.LENGTH_SHORT).show());
        queue.add(request);
    }

    public void progressAnimation(){
        LoadingBar anim = new LoadingBar(this, progressBar, loadLabel, 0f, 100f);
        anim.setDuration(8000);
        progressBar.setAnimation(anim);
    }

}
