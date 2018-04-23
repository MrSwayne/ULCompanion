package ie.swayne.ulcompanion;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class loginActivity extends Activity {

    protected static String MSG = "ie.swayne.ulcompanion log";

    private String HTML;
    private LoginTask loginTask;
    protected static String ID;
    private TextView tv;
    private EditText iet;
    private EditText pet;
    private String pw;
    private CheckBox cb;

    protected static String URL = "https://sulis.ul.ie/portal/xlogin";
    protected static String URL2 = "https://sulis.ul.ie/portal/xlogin";

    public static final String SETTINGS_FILE = "ULCompanionSettings";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SharedPreferences prefs = getSharedPreferences(SETTINGS_FILE, MODE_PRIVATE);
        ID = prefs.getString("SETTINGS_ID", null);

        iet = (EditText) findViewById(R.id.ID);
        pet = (EditText) findViewById(R.id.password);
        //cb = findViewById(R.id.cb);

        cb = new CheckBox(this);
        cb.setChecked(false);

        if(prefs.getBoolean("SETTINGS_REMEMBER", false) == true)
            cb.setChecked(true);

        if(ID != null && cb.isChecked())
            iet.setText(ID);
    }



    protected void onPause() {
        super.onPause();
        if(cb.isChecked()) {
            Log.i(MSG, "SAVING DATA");
            getSharedPreferences(SETTINGS_FILE, MODE_PRIVATE)
                    .edit()
                    .putString("SETTINGS_ID", ID)
                    .putBoolean("SETTINGS_REMEMBER", true)
                    .apply();
        } else {
            getSharedPreferences(SETTINGS_FILE, MODE_PRIVATE)
                    .edit()
                    .putBoolean("SETTINGS_REMEMBER", false)
                    .apply();
        }
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    public void onClickSulis(View view) {

        ID = iet.getText().toString().trim();
        pw = pet.getText().toString();

        if(!ID.isEmpty() && !pw.isEmpty()) {
                loginTask = new LoginTask(this);
                loginTask.execute();
        }

        else Toast.makeText(this, "Please supply a username or password", Toast.LENGTH_SHORT).show();
    }

    public void onClickTT(View view) {

        Log.i(MSG, "timetable clicked");
        ID = iet.getText().toString();
        pw = pet.getText().toString();

        if(!ID.isEmpty()) {
            Intent i = new Intent(this, timetableActivity.class);
            i.putExtra("pw", pw);
            startActivity(i);
        }
        else    Toast.makeText(this, "Please input a Student ID to view your timetable", Toast.LENGTH_SHORT).show();

    }

     class LoginTask extends AsyncTask<Void, String, String> {

        private Context context;

        public LoginTask(Context c) {
            context = c;
        }

        @Override
        protected String doInBackground(Void... arg0) {
            Document doc;
            try {

                Connection.Response loginForm = Jsoup.connect(URL)
                        .method(Connection.Method.POST)
                        .execute();

                doc = Jsoup.connect(URL2)
                        .data("cookieexists", "false")
                        .data("eid", ID)
                        .data("pw", pw)
                        .data("submit", "Submit")
                        .cookies(loginForm.cookies())
                        .post();
                HTML = doc.toString();

            } catch (Exception e) {
                e.printStackTrace();
            }
            doc = null;
            return HTML;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Document doc = Jsoup.parse(HTML);

            if(doc.title().contains("Login Required"))
                Toast.makeText(context, "Incorrect ID or password supplied", Toast.LENGTH_SHORT).show();

            if(doc.title().contains("My Workspace")) {
                Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT).show();


                Elements elements = doc.select("div.fav-title");
                doc = null;
                Elements eLinks = elements.select("a[href]");

                ArrayList<SulisModule> modules = new ArrayList<SulisModule>();
                for(Element x : eLinks) {

                    String[] temp = x.text().split(" ");
                    if(temp.length == 3) {
                        SulisModule module = new SulisModule("", temp[0], temp[2] + " " + temp[1]);
                        module.setModuleLink(x.attr("abs:href"));
                        modules.add(module);
                    }
                }

                Intent i = new Intent(context, sulisActivity.class);
                i.putExtra("pw", pw);
                i.putExtra("modules", modules);
                startActivity(i);
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(context, "Logging into Sulis..", Toast.LENGTH_LONG).show();
        }
    }
}
