package hugomoran.com.journeytracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

public class MainRegister extends AppCompatActivity {

    EditText name;
    EditText email;
    EditText password;
    EditText confirm;
    TextView error;
    RadioGroup group;
    Button button;
    int db_mode;
    String db_name;
    String db_password;
    String db_email;
    String db_confirm;
    MyDBManager db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        db = new MyDBManager(this);
        name = (EditText) findViewById(R.id.editTxt1);
        email = (EditText) findViewById(R.id.editTxt3);
        password = (EditText) findViewById(R.id.editTxt4);
        confirm = (EditText) findViewById(R.id.editTxt5);
        error = (TextView) findViewById(R.id.error);
        group = (RadioGroup) findViewById(R.id.radioGroup);
        button = (Button) findViewById(R.id.button);

        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
//---------------gets users preference km or miles------------------------
                switch (checkedId) {
                    case R.id.radio1:
                        db_mode = 1;
                        break;
                    case R.id.radio2:
                        db_mode = 2;
                        break;
                }
            }
        });
//----------button to add information to the database-------------
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                db_name = name.getText().toString();
                db_email = email.getText().toString();
                db_password = password.getText().toString();
                db_confirm = confirm.getText().toString();

                if (db_mode == 0) {
                    db_mode = 2;
                }
//----------------check passwords match and prints an error message---------------
                if (!db_password.matches(db_confirm)) {
                    error.setText("PASSWORDS DONT MATCH");
                    error.setError("");
                    return;
                }
                Intent intent = new Intent(MainRegister.this, MainActivity.class);
                db.open();
                db.insertUser(db_name, db_email, db_password, db_mode);
                db.close();
                startActivity(intent);
            }
        });

    }

}
