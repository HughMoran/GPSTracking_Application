package hugomoran.com.journeytracker;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.sql.SQLException;

/**
 * Created by Owner on 17-Apr-16.
 */
public class MainDB extends AppCompatActivity {
    TextView results;
    MyDBManager db;
    LinearLayout container;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_db);

        container = (LinearLayout) findViewById(R.id.database);
        results = (TextView) findViewById(R.id.results);
        db = new MyDBManager(this);


        db.open();

//----------Calls the method getRows();----------------
        getRows();

        db.close();
    }

    //-----------Method to return all the information from the Database---------------
    public void getRows() {
//-----------db.getAllPolled(); to call the method in MyDBManager--------------
        Cursor c = db.getAllPolled();

        if (c.moveToFirst()) {
            do {
                ShowTask(c);

            }
            while (c.moveToNext());
        }
    }

    //------------Prints out the information to a Text view container---------
    public void ShowTask(Cursor c) {

        TextView text = new TextView(this);

        results.append("event: " + c.getString(0) + "\n" +
                "rowid: " + c.getString(1) + "\n" +
                "lat: " + c.getString(2) + "\n" +
                "long: " + c.getString(3) + "\n"+
                "event: " + c.getString(4) + "\n"+
                "distance: " + c.getString(5) + "\n"+
                "duration: " + c.getString(6) + "\n"+
                "time: " + c.getString(7) + "\n"+
                "mode: " + c.getString(8) + "\n" + "\n");

        container.addView(text);

    }

}

