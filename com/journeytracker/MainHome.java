package hugomoran.com.journeytracker;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainHome extends FragmentActivity implements OnMapReadyCallback, LocationListener, PopupMenu.OnMenuItemClickListener {

    private GoogleMap mMap;
    Location location;
    ImageButton button;
    Button btn;
    Button btn1;
    MyDBManager db;
    Spinner spinner;
    String userName;
    TextView name;
    int mode;
    String formattedDate;
    int time;
    Timer timer;

    int _id;
    int preference;

    String[] options = {
            "Walking",
            "Running",
            "Cycling"
    };

    boolean first_reading = true;
    private LocationManager locationManager;
    double distance = 0;
    double previous_lat, previous_lon;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        button = (ImageButton) findViewById(R.id.button);
        btn = (Button) findViewById(R.id.button1);
        btn1 = (Button) findViewById(R.id.button2);
        spinner = (Spinner) findViewById(R.id.spinner);
        name = (TextView) findViewById(R.id.name);
        db = new MyDBManager(this);
//----------------bundle all required information around the application------------
        Bundle bundle = getIntent().getExtras();
        userName = (bundle.getString("userName"));
        _id = (bundle.getInt("id"));
        preference = (bundle.getInt("pref"));
//---------------construct new timer to time journeys----------------
        timer = new Timer();
//--------------set every user name here on login---------------
        name.setText(userName);
//-------------------get the latest date of the journey----------------
        formattedDate = new SimpleDateFormat("yyyy.MM.dd").format(Calendar
                .getInstance().getTime());

//----------------Builds map allows to be created----------------------
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

//-----------------------------Image Spinner calls a Custom adapter---------------------
        spinner.setAdapter(new MyCustomAdapter(MainHome.this, R.layout.activity_spinner, options));

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//---------------switch case saves mode to an int------------------------------
                switch (position) {
                    case 0:
                        mode = 1;
                        //mode = spinner.getSelectedItem().toString();
                        break;
                    case 1:
                        mode = 2;
                        //mode = spinner.getSelectedItem().toString();
                        break;
                    case 2:
                        mode = 3;
                        //mode = spinner.getSelectedItem().toString();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

//-----------------Start Button------------------------------------
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn1.setVisibility(View.VISIBLE);
                btn.setVisibility(View.GONE);
                onLocationChanged(location);

//-----------------start timer---------------------
                timer.scheduleAtFixedRate(new TimerTask() {
                    public void run() {

                        time++; //continually increase time
                    }
                }, 0, 1000); //increment by defined times
//--------------zoom in on map----------------
                mMap.moveCamera(CameraUpdateFactory.zoomTo(12));
//-----------------------call method to set current location--------------
                setUpLocation();

            }
        });

    }

//------------------Spinner Custom Adapter-----------------------------------------------
    public class MyCustomAdapter extends ArrayAdapter<String> {

        String[] names;

        public MyCustomAdapter(Context context, int textViewResourceId, String[] objects) {
            super(context, textViewResourceId, objects);
            // TODO Auto-generated constructor stub
            names = objects;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return getView(position, convertView, parent);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
//------------------inflate spinner---------------------------------
            if (row == null) {
                LayoutInflater inflater = getLayoutInflater();
                row = inflater.inflate(R.layout.activity_spinner, parent, false);
            }

            // LayoutInflater inflater = getLayoutInflater();
            // View row = inflater.inflate(R.layout.activity_main6, parent, false);
            TextView label = (TextView) row.findViewById(R.id.text);
            label.setText(names[position]);
            ImageView icon = (ImageView) row.findViewById(R.id.image);
//----------------------add icons to the to the options in the spinner--------------------------
            if (options[position].equals("Walking")) {
                icon.setImageResource(R.drawable.ic_directions_walk_black_24dp);
            } else if (options[position].equals("Running")) {
                icon.setImageResource(R.drawable.ic_directions_run_black_24dp);
            } else if (options[position].equals("Cycling")) {
                icon.setImageResource(R.drawable.ic_directions_bike_black_24dp);
            }

            return row;
        }
    }

//--------------------------ImageButton Popup Menu-----------------------------------
    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
//-------------------------This activity implements OnMenuItemClickListener-------------------
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.menu_main);
        popup.getMenu().findItem(R.id.action_home).setVisible(false);
        popup.show();
    }

//------------------------Switch case for menu options---------------------------------
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
//---------------BMI option------------------
            case R.id.my_bmi:
                Intent bmiIntent = new Intent(MainHome.this, MainBMI.class);
                bmiIntent.putExtra("id", _id);
                bmiIntent.putExtra("userName", userName);
                bmiIntent.putExtra("pref", preference);
                startActivity(bmiIntent);
                return true;
//-----------------------top distance list--------------
            case R.id.action_list_dist:
                Intent list = new Intent(MainHome.this, MainTopDist.class);
                list.putExtra("id", _id);
                list.putExtra("userName", userName);
                list.putExtra("pref", preference);
                startActivity(list);
                return true;
//-----------------top time list------------------
            case R.id.action_list_time:
                Intent listTime = new Intent(MainHome.this, MainTopTime.class);
                listTime.putExtra("id", _id);
                listTime.putExtra("userName", userName);
                listTime.putExtra("pref", preference);
                startActivity(listTime);
                return true;
//----------------------logout----------------------------------
            case R.id.action_logout:
                Intent logIntent = new Intent(MainHome.this, MainActivity.class);
                startActivity(logIntent);
                finish();
                return true;
//------------toggle normal map-----------------
            case R.id.map_normal:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                return true;
//----------------toggle hybrid map------------------------
            case R.id.map_hybrid:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                return true;
//----------------------list all journeys-----------------------------------
            case R.id.action_list_all:
                Intent listIntent = new Intent(MainHome.this, MainListAll.class);
                listIntent.putExtra("id", _id);
                listIntent.putExtra("userName", userName);
                listIntent.putExtra("pref", preference);
                startActivity(listIntent);
                return true;
            default:
                return false;
        }
    }

    //------------------location starts here-----------------------------------
    public void onLocationChanged(Location location) {

        if (location != null) {
            if (first_reading) {
                if (location.getLatitude() != 0 && location.getLongitude() != 0) {
                    previous_lat = location.getLatitude();

                    previous_lon = location.getLongitude();

                    first_reading = false;

                    distance = 0;
//-------------------------first db insert for starts-----------------------------
                    db.open();
                    db.insertTask(_id, 1, previous_lat, previous_lon, 0, distance, mode, formattedDate);
                    db.close();

                }
            } else {

                // Calculate distance
                double dlong = (location.getLongitude() - previous_lon);
                double dlat = (location.getLatitude() - previous_lat);
                double a =
                        Math.pow(Math.sin(toRad(dlat) / 2.0), 2)
                                + Math.cos(toRad(previous_lat))
                                * Math.cos(toRad(location.getLatitude()))
                                * Math.pow(Math.sin(toRad(dlong) / 2.0), 2);
                double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
                double d = 6367 * c;

                distance = distance + d;
//---------------------second insert calcualte middle points-----------------------
                db.open();
                db.insertTask(_id, 2, previous_lat, previous_lon, time, distance, mode, formattedDate);
                db.close();

                previous_lat = location.getLatitude();

                previous_lon = location.getLongitude();

//----------------------------finish Button---------------------------
                btn1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        btn1.setVisibility(View.GONE);
                        btn.setVisibility(View.VISIBLE);
//--------------------finish insert into the database ends journey-----------------
                        db.open();
                        db.insertTask(_id, 3, previous_lat, previous_lon, time, distance, preference, formattedDate);
                        db.close();
//------------------------stops and purges the timer makes sure its finished----------------
                        timer.cancel();
                        timer.purge();
//----------------------------calls the stopOnClick method---------------------
                        stopOnClick();

                    }
                });

            }
        }

        Toast.makeText(MainHome.this, previous_lat + " " + previous_lon,
                Toast.LENGTH_LONG).show();
        LatLng pos = new LatLng(previous_lat, previous_lon);


        mMap.addMarker(new MarkerOptions().position(pos).title("You Are Here"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(pos));

        //This method sets the zoom level of the view....
        mMap.moveCamera(CameraUpdateFactory.zoomTo(9));

        /// This allows the user to zoom in and out of the map
        mMap.getUiSettings().setZoomControlsEnabled(true);

        //Show My Location Button on the map
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        // locationText.setText(distance + "kms");

    }
//-----------------methods stops the GPS reading and wont add anymore points to the database after this------------------
    public void stopOnClick() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.removeUpdates(this);
    }

    private Double toRad(Double value) {
        return value * Math.PI / 180;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // Auto Generated
    }

    @Override
    public void onProviderEnabled(String provider) {
        // Auto Generated
    }

    @Override
    public void onProviderDisabled(String provider) {
        // Auto Generated
    }

    // custom method
    private void setUpLocation() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
//-------------begins readings after 5 seconds and 1 meter-------------------------
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                5000,
                1,
                this);
    }
//------------------------sets up google map with user extras----------------------
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.moveCamera(CameraUpdateFactory.zoomTo(1));


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);

        /// This allows the user to zoom in and out of the map
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().isMyLocationButtonEnabled();
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

    }
}