package hugomoran.com.journeytracker;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.util.ArrayList;


public class MainListAll extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    MyDBManager db;
    ListView list;
    int _id;
    String userName;
    int preference;
    String word;
    String resulttext;

    ArrayList<Double> Start_Lat;

    ArrayList<Double> Start_Lon;

    ArrayList<Double> Start_Event;

    ArrayList<Double> Stop_Distance;

    ArrayList<Double> Stop_Duration;

    ArrayList<Double> Stop_Lat;

    ArrayList<Double> Stop_Lon;

    ArrayList<String> Start_Date;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_all);

        Start_Lat = new ArrayList<>();
        Start_Lon = new ArrayList<>();
        Start_Event = new ArrayList<>();

        Stop_Lat = new ArrayList<>();
        Stop_Lon = new ArrayList<>();
        Stop_Distance = new ArrayList<>();
        Stop_Duration = new ArrayList<>();

        Start_Date = new ArrayList<>();

        Bundle bundle = getIntent().getExtras();
        _id = (bundle.getInt("id"));
        userName = (bundle.getString("userName"));
        preference = (bundle.getInt("pref"));

        db = new MyDBManager(this);

        db.open();
//------calls the getJourney methods in the db manager to get the coordinates of each journey---------------------
        getJourney(_id);

        db.close();


        list = (ListView)findViewById(R.id.journeylist);
//---------------calls the customlistadapter to creat list view-------------------------------
        list.setAdapter(new MyCustomListAdapter(MainListAll.this, R.layout.activity_all_row, Start_Event, Start_Lat, Start_Lon));

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                double lat = Start_Lat.get(position);
                double lon = Start_Lon.get(position);
//-----------------adding the lats and longs to map so they can be added to the map-----------------------
                Intent mapIntent = new Intent(MainListAll.this, MainMap.class);
                mapIntent.putExtra("id", _id);
                mapIntent.putExtra("start_lat", lat);
                mapIntent.putExtra("start_lon", lon);

                mapIntent.putExtra("stop_lat", Stop_Lat.get(position));
                mapIntent.putExtra("stop_lon", Stop_Lon.get(position));
                startActivity(mapIntent);


            }
        });

    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
//-------------------------This activity implements OnMenuItemClickListener-------------------
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.menu_main);
        popup.getMenu().findItem(R.id.action_list_all).setVisible(false);
        popup.getMenu().findItem(R.id.map_hybrid).setVisible(false);
        popup.getMenu().findItem(R.id.map_normal).setVisible(false);
        popup.show();
    }

    //------------------------Switch case for menu options---------------------------------
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
//------------Home Option---------------
            case R.id.action_home:
                Intent homeIntent = new Intent(MainListAll.this, MainHome.class);
                homeIntent.putExtra("id", _id);
                homeIntent.putExtra("userName", userName);
                homeIntent.putExtra("pref", preference);
                startActivity(homeIntent);
                return true;
//---------------BMI option---------------------------
            case R.id.my_bmi:
                Intent bmiIntent = new Intent(MainListAll.this, MainBMI.class);
                bmiIntent.putExtra("id", _id);
                bmiIntent.putExtra("userName", userName);
                bmiIntent.putExtra("pref", preference);
                startActivity(bmiIntent);
                return false;
//---------------------List Distance Option--------------------
            case R.id.action_list_dist:
                Intent list = new Intent(MainListAll.this, MainTopDist.class);
                list.putExtra("id", _id);
                list.putExtra("userName", userName);
                list.putExtra("pref", preference);
                startActivity(list);
                return true;
//-----------------------Top Time Option----------------
            case R.id.action_list_time:
                Intent listTime = new Intent(MainListAll.this, MainTopTime.class);
                listTime.putExtra("id", _id);
                listTime.putExtra("userName", userName);
                listTime.putExtra("pref", preference);
                startActivity(listTime);
                return true;
//-----------------Logout option------------------------
            case R.id.action_logout:
                Intent logIntent = new Intent(MainListAll.this, MainActivity.class);
                startActivity(logIntent);
                finish();
                return true;
//-------------------------Normal Map----------------------
            case R.id.map_normal:
                item.setVisible(false);
                //mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                return true;
//-------------------------Hybrid map-----------------------
            case R.id.map_hybrid:
                item.setEnabled(false);
                //mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                return true;
//------------------------List all option------------------
            case R.id.action_list_all:
                Intent listIntent = new Intent(MainListAll.this, MainListAll.class);
                listIntent.putExtra("id", _id);
                listIntent.putExtra("userName", userName);
                listIntent.putExtra("pref", preference);
                startActivity(listIntent);
                return true;
            default:
                return false;
        }
    }
    public class MyCustomListAdapter extends ArrayAdapter<Double>{

        ArrayList<Double> Adapter_Event;
        ArrayList<Double> Adapter_Lat;
        ArrayList<Double> Adapter_Lon;



        public MyCustomListAdapter(Context context, int textViewResourceId, ArrayList<Double> a, ArrayList<Double> b, ArrayList<Double> c) {
            super(context, textViewResourceId, a);
            // TODO Auto-generated constructor stub
            Adapter_Event = a;
            Adapter_Lat = b;
            Adapter_Lon = c;

        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
//---------------inflates the custom rows in the list view-----------------------------
            if (row == null) {
                LayoutInflater inflater = getLayoutInflater();
                row = inflater.inflate(R.layout.activity_all_row, parent, false);
            }

            TextView date = (TextView) row.findViewById(R.id.date);
            TextView mileage = (TextView) row.findViewById(R.id.mileage);
            TextView duration = (TextView) row.findViewById(R.id.duration);


            ImageView icon = (ImageView) row.findViewById(R.id.icon);
            icon.setImageResource(R.drawable.map);
//-------------reads the 1 or 2 from db and sets text to either mls or kms------------
            if (preference == 1){
                word = "Mls";
            } else {
                word = "Kms";
            }
//-------------journry date-----------------------
            date.setText("" + Start_Date.get(position));
//--------------format results to two place of decimal-----------------
            resulttext = String.format("%.2f", Stop_Distance.get(position));
            mileage.setText(resulttext + " " + word);
//---------------time in seconds------------------------
            duration.setText("" + Stop_Duration.get(position) + "sec");

            return row;
        }

    }
//------------------------read the database getstarts and getstops and add to the cursor----------
    public void getJourney(int user) {
        Cursor c = db.getStarts(user);
        Cursor c2 = db.getStops(user);

        if (c.moveToFirst() && c2.moveToFirst()) {
            do {

                ShowJourney(c, c2);

            }
            while (c.moveToNext() && c2.moveToNext());
        }
    }

    public void ShowJourney(Cursor c, Cursor c2) {

//--------------add the required information top the arraylist-----------
        Start_Date.add((c.getString(6)));
        Start_Lat.add((c.getDouble(3)));
        Start_Event.add((c.getDouble(2)));
        Start_Lon.add((c.getDouble(8)));

        Stop_Lat.add((c2.getDouble(3)));
        Stop_Lon.add((c2.getDouble(8)));
        Stop_Duration.add((c2.getDouble(5)));
        Stop_Distance.add((c2.getDouble(4)));


    }

}
