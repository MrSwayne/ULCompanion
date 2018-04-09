package ie.swayne.ulcompanion;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.util.ArrayList;
import java.util.Iterator;

public class timetableActivity extends Activity {


    protected static String URL = "http://timetable.ul.ie/tt1.asp";
    protected static String URL2 = "http://timetable.ul.ie/tt2.asp";
    private final String ID = loginActivity.ID;
    private String HTML;
    private TimetableTask ttt;
    private TableLayout timetable;
    private ConstraintLayout layout;
    TextView console;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);

        layout = findViewById(R.id.linearLayout);

        console = (TextView) findViewById(R.id.tv);
        console.setMovementMethod(new ScrollingMovementMethod());
        ttt = new TimetableTask(this);
        ttt.execute();
    }


        public void displayModules(ArrayList<TTModule> classes) {
            final String[] times = {"09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00"};
            final String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};

            TTModule[][] timetable = new TTModule[times.length][days.length];

            int ind = 0;

            for(int i = 0;i < classes.size();i++) {
                int x = -1,y = -1;

                String day = classes.get(i).getDay();
                String time = classes.get(i).getStart();
                int dur = classes.get(i).getDuration();

                for(int j = 0;j < times.length;j++)
                    if(time.equalsIgnoreCase(times[j])) {
                        x = j;
                        break;
                    }

                for(int j = 0;j < days.length;j++)
                    if (day.equalsIgnoreCase(days[j])) {
                        y = j;
                        break;
                    }

                if(x != -1 && y != -1 )
                    for(int j = 0;j < dur;j++) {
                        TTModule mod = classes.get(i);
                        if(j > 0) {
                            mod = (TTModule) mod.clone();
                            mod.addHour();
                        }
                        timetable[x + j][y] = mod;
                    }
            }

            String line = "";

            for(int i = 0;i < timetable[i].length;i++) {
                    line += days[i] + "\n";
                for(int j = 0;j < timetable.length;j++) {
                    if(timetable[j][i] != null)
                        line += timetable[j][i].getStart() + " " + timetable[j][i].getCode() + "\n";
                    else
                        line += times[j] + " Nothing" + "\n";
                }
                line += "\n";
            }
            console.setText(line);
            console.setTextSize(30);
        }

    //Parses HTML, gets modules from selected student ID, then sends arrayList of all the modules to displayModules method.
    class TimetableTask extends AsyncTask<Void, String, String> {

        private Context c;

        public TimetableTask(Context c) {
            this.c = c;
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
                        .data("T1", ID)
                        .data("submit", "Submit")
                        .cookies(loginForm.cookies())
                        .post();
                HTML = doc.toString();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return HTML;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            Document doc = Jsoup.parse(HTML);
            String[] timetable = new String[12];

            Element table = doc.select("table").first();

            Iterator<Element> iterator = table.select("td").iterator();
            int x = 0;
            while(iterator.hasNext())
                timetable[x++] = iterator.next().text();


            ArrayList<TTModule> modules = new ArrayList<>();

            for(int i = 6;i < timetable.length - 1;i++) {

                String[] temp = timetable[i].split("([a-zA-Z]{3}:[1-9]{1,2}-[1-9]{1,2},[1-9]{1,2}-[1-9]{1,2})");
                for (int j = 0; j < temp.length; j++) {
                    String day = timetable[i - 6];

                    temp[j] = temp[j].replace("-", "");
                    temp[j] = temp[j].replace("  ", " ");
                    temp[j] = temp[j].trim();

                    String[] module = temp[j].split(" ");

                    if(module.length > 5) {
                        module[3] = module[3] + " " + module[4];
                        module[4] = module[5];
                    }
                    modules.add(new TTModule("", module[2], module[0], module[1], module[4], module[3], day));
                  }
            }
            displayModules(modules);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(c, "Loading timetable", Toast.LENGTH_SHORT).show();
        }
    }

}
