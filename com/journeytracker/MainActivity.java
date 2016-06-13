package hugomoran.com.journeytracker;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    MyDBManager db;
    Button btn;
    Button btn1;
    EditText email;
    EditText password;
    TextView text;
    String db_email;
    String db_password;
    String userName;
    int _id;
    int preference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new MyDBManager(this);
        btn = (Button) findViewById(R.id.button);
        btn1 = (Button) findViewById(R.id.button1);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.passwd);
        text = (TextView) findViewById(R.id.error);
//---------------------------------Underline the text in the button------------
        btn1.setPaintFlags(btn1.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
//----------------------login button contains a getRows() method to get user details so they can be bundled--------------
//-------------------the Login method checks the database to see if the email and password match the user--------------
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db_password = password.getText().toString();
                db_email = email.getText().toString();

                if (db_email.length() > 0 && db_password.length() > 0) {
//----------------------database look up-----------------------------------------
                    db.open();
                    getRows();
                    if (db.Login(db_email, db_password)) {
//-------------toasts out if the user was successful or if the access was denied------------------------
                        Toast.makeText(MainActivity.this, "Successfully Logged In", Toast.LENGTH_LONG).show();
                        Intent loginIntent = new Intent(MainActivity.this, MainHome.class);
                        loginIntent.putExtra("id", _id);
                        loginIntent.putExtra("userName", userName);
                        loginIntent.putExtra("pref", preference);
                        startActivity(loginIntent);
                    } else {
                        Toast.makeText(MainActivity.this, "Invalid Username/Password", Toast.LENGTH_LONG).show();
                    }
                    db.close();
                }
            }
        });
//-----------------Button to register page-------------------------------
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent regIntent = new Intent(MainActivity.this, MainRegister.class);
                startActivity(regIntent);
            }
        });

    }
//-----------------getRows() method checks the getUsers method in the db to get the user information--------
//-----------------at the different columns in the database table--------------
    public void getRows() {
        Cursor c = db.getUsers(db_email, db_password);

        if (c.moveToFirst()) {
            _id = c.getInt(0);
            userName = c.getString(1);
            preference = c.getInt(4);
        }

    }

}
