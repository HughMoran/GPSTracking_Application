package hugomoran.com.journeytracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainMap extends FragmentActivity implements OnMapReadyCallback, PopupMenu.OnMenuItemClickListener {

    GoogleMap mMap;
    double start_lat;
    double start_lon;
    double stop_lat;
    double stop_lon;
    int _id;
    int preference;
    String userName;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Bundle bundle = getIntent().getExtras();

//------------bundled lat and long along with the other information required around the app-------------
        if (bundle != null) {
            preference = (bundle.getInt("pref"));
            userName = (bundle.getString("userName"));
            _id = (bundle.getInt("id"));
            start_lat = bundle.getDouble("start_lat");
            start_lon = bundle.getDouble("start_lon");
            stop_lat = bundle.getDouble("stop_lat");
            stop_lon = bundle.getDouble("stop_lon");
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
//-------------------------This activity implements OnMenuItemClickListener-------------------
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.menu_main);
        popup.show();
    }

//------------------------Switch case for menu options---------------------------------
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
//--------------------Home Option-------------------------------
            case R.id.action_home:
                Intent homeIntent = new Intent(MainMap.this, MainHome.class);
                homeIntent.putExtra("id", _id);
                homeIntent.putExtra("userName", userName);
                homeIntent.putExtra("pref", preference);
                startActivity(homeIntent);
                return true;
//---------------BMI option---------------------------
            case R.id.my_bmi:
                Intent bmiIntent = new Intent(MainMap.this, MainBMI.class);
                bmiIntent.putExtra("id", _id);
                bmiIntent.putExtra("userName", userName);
                bmiIntent.putExtra("pref", preference);
                startActivity(bmiIntent);
                return false;
//---------------------List Distance Option--------------------
            case R.id.action_list_dist:
                Intent list = new Intent(MainMap.this, MainTopDist.class);
                list.putExtra("id", _id);
                list.putExtra("userName", userName);
                list.putExtra("pref", preference);
                startActivity(list);
                return true;
//-----------------------Top Time Option----------------
            case R.id.action_list_time:
                Intent listTime = new Intent(MainMap.this, MainTopTime.class);
                listTime.putExtra("id", _id);
                listTime.putExtra("userName", userName);
                listTime.putExtra("pref", preference);
                startActivity(listTime);
                return true;
//-----------------Logout option------------------------
            case R.id.action_logout:
                Intent logIntent = new Intent(MainMap.this, MainActivity.class);
                startActivity(logIntent);
                finish();
                return true;
//-------------------------Normal Map----------------------
            case R.id.map_normal:
                item.setVisible(false);
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                return true;
//-------------------------Hybrid map-----------------------
            case R.id.map_hybrid:
                item.setEnabled(false);
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                return true;
//------------------------List all option------------------
            case R.id.action_list_all:
                Intent listIntent = new Intent(MainMap.this, MainListAll.class);
                listIntent.putExtra("id", _id);
                listIntent.putExtra("userName", userName);
                listIntent.putExtra("pref", preference);
                startActivity(listIntent);
                return true;
            default:
                return false;
        }
    }
//---------------creates map with the lat and long of the journey----------------------
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng start = new LatLng(start_lat, start_lon);
        LatLng stop = new LatLng(stop_lat, stop_lon);


        //This method allow a marker to be placed in a position
        mMap.addMarker(new MarkerOptions().position(start).title("Start"));
        mMap.addMarker(new MarkerOptions().position(stop).title("Stop"));

        //This method positions the centre of the map at the position
        mMap.moveCamera(CameraUpdateFactory.newLatLng(start));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(stop));


        //This method sets the zoom level of the view....
       mMap.moveCamera(CameraUpdateFactory.zoomTo(8));

        /// This allows the user to zoom in and out of the map
        mMap.getUiSettings().setZoomControlsEnabled(true);

    }
}
