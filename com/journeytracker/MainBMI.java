package hugomoran.com.journeytracker;

import android.content.Intent;
import android.opengl.Visibility;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;


public class MainBMI extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    private double valueheight = 0;
    private double valueweight = 0;
    private double bmi = 0;
    private String resulttext;

    Button btn;
    EditText height;
    EditText weight;
    TextView result;
//-----bundled items------------
    int _id;
    String userName;
    int preference;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmi);
//-----------bundle requried information through the app----------
        Bundle bundle = getIntent().getExtras();
        _id = (bundle.getInt("id"));
        userName = (bundle.getString("userName"));
        preference = (bundle.getInt("pref"));

        height = (EditText) findViewById(R.id.height);
        weight = (EditText) findViewById(R.id.weight);
        result = (TextView) findViewById(R.id.result);

        btn = (Button) findViewById(R.id.button);
//--------------button to call the calculate method calc the BMI-----------------
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculate();
            }
        });

    }

    private void calculate() {
//--------------parse user height to a Double-------------
        valueheight = Double.parseDouble(height.getText().toString());
//--------------parse the users weight to a Double------------
        valueweight = Double.parseDouble(weight.getText().toString());
        Double valueheightmeters;
//-----------convert the height to meters and centimeters--------------
        valueheightmeters = valueheight / 100;
        bmi = (valueweight / (valueheightmeters * valueheightmeters));

        if (bmi >= 30) {
//----------------------if the BMI reading is greater than 30 output the result plus------------
            resulttext = String.format("%.2f", bmi) + " is OBESE.";
            result.setText(resulttext);

        } else if (bmi >= 25) {
//--------------------if BMI reading is greater than 25 user is over weight--------------
            resulttext = String.format("%.2f", bmi) + " is OVERWEIGHT.";
            result.setText(resulttext);

        } else if (bmi >= 18.5) {
//----------------if BMI reading is 18.5 and up the user output the user weight plus----------------
            resulttext = String.format("%.2f", bmi) + " is IDEAL.";
            result.setText(resulttext);

        } else {
//----------------else the user is underweight-----------------------------
            resulttext = String.format("%.2f", bmi) + " is UNDERWEIGHT.";
            result.setText(resulttext);
        }
    }
//-----------------showPopup from onClick method in XML allows the menu to be created-------------
    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
//-------------------------This activity implements OnMenuItemClickListener-------------------
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.menu_main);
//------------hide certain menu items that are not needed------------------------------
        popup.getMenu().findItem(R.id.my_bmi).setVisible(false);
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
                Intent homeIntent = new Intent(MainBMI.this, MainHome.class);
                homeIntent.putExtra("id", _id);
                homeIntent.putExtra("userName", userName);
                homeIntent.putExtra("pref", preference);
                startActivity(homeIntent);
                return true;
//---------------BMI option---------------------------
            case R.id.my_bmi:
                Intent bmiIntent = new Intent(MainBMI.this, MainBMI.class);
                bmiIntent.putExtra("id", _id);
                bmiIntent.putExtra("userName", userName);
                bmiIntent.putExtra("pref", preference);
                startActivity(bmiIntent);
                return false;
//---------------------List Distance Option--------------------
            case R.id.action_list_dist:
                Intent list = new Intent(MainBMI.this, MainTopDist.class);
                list.putExtra("id", _id);
                list.putExtra("userName", userName);
                list.putExtra("pref", preference);
                startActivity(list);
                return true;
//-----------------------Top Time Option----------------
            case R.id.action_list_time:
                Intent listTime = new Intent(MainBMI.this, MainTopTime.class);
                listTime.putExtra("id", _id);
                listTime.putExtra("userName", userName);
                listTime.putExtra("pref", preference);
                startActivity(listTime);
                return true;
//-----------------Logout option------------------------
            case R.id.action_logout:
                Intent logIntent = new Intent(MainBMI.this, MainActivity.class);
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
                Intent listIntent = new Intent(MainBMI.this, MainListAll.class);
                listIntent.putExtra("id", _id);
                listIntent.putExtra("userName", userName);
                listIntent.putExtra("pref", preference);
                startActivity(listIntent);
                return true;
            default:
                return false;
        }
    }
}
