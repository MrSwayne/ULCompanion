package ie.swayne.ulcompanion;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
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

import static ie.swayne.ulcompanion.loginActivity.MSG;

public class timetableActivity extends AppCompatActivity {


    private TTModule[][] timetable;
    protected static String URL = "http://timetable.ul.ie/tt1.asp";
    protected static String URL2 = "http://timetable.ul.ie/tt2.asp";
    private final String ID = loginActivity.ID;
    private String HTML;
    private TimetableTask ttt;
    private ConstraintLayout layout;
    private LinearLayout modulesList;
    private TextView[] textViews;
    private ArrayList<TTModule> modules;
    TextView console;

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);

        ttt = new TimetableTask(this);
        ttt.execute();

        mSectionsPagerAdapter=new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));



        BottomNavigationView bottomNavigationView=(BottomNavigationView) findViewById(R.id.navBar);

        bottomNavigationView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.nav_home:
                        Intent intent1 = new Intent(timetableActivity.this,loginActivity.class);
                        startActivity(intent1);
                        break;

                    case R.id.nav_timetable:
                        Intent intent2 = new Intent(timetableActivity.this,timetableActivity.class);
                        startActivity(intent2);
                        break;

                    case R.id.nav_sulis:
                        Intent intent3 = new Intent(timetableActivity.this,sulisActivity.class);
                        startActivity(intent3);
                        break;

                }

            }
        });
    }

    private void setupViewPager(ViewPager viewPager)
    {
        SectionsPagerAdapter adapter= new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new mondayFrag(),"Monday");
        adapter.addFragment(new tuesdayFrag(),"Tuesday");
        adapter.addFragment(new wednesdayFrag(),"Wednesday");
        adapter.addFragment(new thursdayFrag(),"Thursday");
        adapter.addFragment(new fridayFrag(),"Friday");
        viewPager.setAdapter(adapter);
    }

    public static class PlaceholderFragment extends Fragment {

        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

        public void displayModules(ArrayList<TTModule> classes) {
            final String[] times = {"09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00"};
            final String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};

             timetable = new TTModule[times.length][days.length];

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

            String line;

            /*
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
            */
            textViews = new TextView[days.length * 2];
            modulesList = findViewById(R.id.moduleList);


            for(int i = 0, j = 0;i < days.length;i++) {
                line = "";
                TextView tv = new TextView(this);
                tv.setText(days[i]);
                tv.setClickable(true);
                tv.setTextSize(40);

                TextView tv2 = new TextView(this);
                tv2.setVisibility(View.GONE);
                tv2.setTextSize(45);

                textViews[j++] = tv;
                textViews[j++] = tv2;


                tv2.setText(line);

                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean found = false;
                        TextView tv = (TextView) v;
                        for(int i = 0;i < days.length && !found;i++) {
                            if(days[i].equalsIgnoreCase(tv.getText().toString())) {
                                found = true;
                                TextView tv2 = textViews[i * 2 + 1];
                                tv2.setVisibility(tv2.isShown()? View.GONE: View.VISIBLE);
                            }
                        }
                    }
                });

                modulesList.addView(tv);
                modulesList.addView(tv2);


                for(int z = 0;z < timetable.length;z++) {
                    if(timetable[z][i] != null) {
                        line += timetable[z][i].getStart() + "-" + timetable[z][i].getEnd() + " " + timetable[z][i].getCode() + "\n";
                        if(timetable[z][i].getDuration() > 1) for(int a = 1;a < timetable[z][i].getDuration();a++) z++;
                    }

                    else
                        line += times[z] + " N/A" + "\n";
                }
                tv2.setText(line);

            }


            /*
            for(int i = 0;i < timetable.length;i++) {
                Log.i(MSG, "len: " + timetable.length + "\t");
                for(int j = 0;j < timetable[i].length;j++) {
                    Log.i(MSG, "len: " + timetable[i].length + "\t");
                    if(timetable[i][j] != null)
                    Log.i(MSG, timetable[i][j].toString());
                }
             */


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

                if(doc == null) {
                    Toast.makeText(c, "Error, invalid student ID detected, returning home", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(c, loginActivity.class);
                    startActivity(i);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return HTML;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            Document doc = Jsoup.parse(HTML);
            try {
                String[] timetable = new String[12];

                Element table = doc.select("table").first();

                Iterator<Element> iterator = table.select("td").iterator();
                int x = 0;
                while (iterator.hasNext())
                    timetable[x++] = iterator.next().text();


                modules = new ArrayList<>();

                for (int i = 6; i < timetable.length - 1; i++) {

                    String[] temp = timetable[i].split("([a-zA-Z]{3}:[1-9]{1,2}-[1-9]{1,2},[1-9]{1,2}-[1-9]{1,2})");
                    for (int j = 0; j < temp.length; j++) {
                        String day = timetable[i - 6];

                        temp[j] = temp[j].replace("-", "");
                        temp[j] = temp[j].replace("  ", " ");
                        temp[j] = temp[j].trim();

                        String[] module = temp[j].split(" ");

                        if (module.length > 5) {
                            module[3] = module[3] + " " + module[4];
                            module[4] = module[5];
                        }
                        modules.add(new TTModule("", module[2], module[0], module[1], module[4], module[3], day));
                    }
                }
                displayModules(modules);
            } catch (Exception e) {
                Toast.makeText(c, "Error, invalid student ID detected", Toast.LENGTH_SHORT).show();
                Log.e(loginActivity.MSG, e.getStackTrace().toString());
                Intent i = new Intent(c, loginActivity.class);
                startActivity(i);
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(c, "Loading timetable", Toast.LENGTH_SHORT).show();
        }
    }

}
