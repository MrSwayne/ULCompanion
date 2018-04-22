package ie.swayne.ulcompanion;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Text;
import static ie.swayne.ulcompanion.loginActivity.MSG;

import java.net.URLEncoder;
import java.util.ArrayList;

public class sulisActivity extends Activity {

    private TextView tv;
    private String ID = loginActivity.ID;
    private String HTML;
    private String pw;
    private ArrayList<SulisModule> modules;
    private TextView[] textViews;
    private LinearLayout moduleList;
    private String resourcesURL;
    private SulisTask task;
    private String[] moduleHTML;
    private TextView console;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sulis);

        Intent in = getIntent();
        pw = in.getStringExtra("pw");


        modules = (ArrayList<SulisModule>) in.getSerializableExtra("modules");
        moduleHTML = new String[modules.size()];

        moduleList = findViewById(R.id.moduleList);


        Log.i(loginActivity.MSG, Integer.toString(modules.size()));
        TextView textView = new TextView(this);
        textView.setText("Modules list:");
        textView.setTextSize(30);
        textView.setAllCaps(true);
        moduleList.addView(textView);
        textViews = new TextView[modules.size() * 2];
        for(int i = 0, j = 0;i < modules.size();i++) {
            TextView tv = new TextView(this);
            tv.setText(modules.get(i).getCode());
            tv.setClickable(true);
            tv.setTextSize(40);

            TextView tv2 = new TextView(this);
            tv2.setVisibility(View.GONE);
            tv2.setTextSize(45);

            textViews[j++] = tv;
            textViews[j++] = tv2;

            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView tv = (TextView) v;
                    boolean found = false;
                    for(int i = 0, j = 0;i < modules.size() && !found;i++)
                         if(modules.get(i).getCode().equalsIgnoreCase(tv.getText().toString())) {
                             found = true;
                             //textViews[i].setVisibility(View.GONE);
                             TextView resources = textViews[i * 2 + 1];
                             resources.setVisibility(resources.isShown()? View.GONE: View.VISIBLE);


                             getModuleData(modules.get(i), i);

                             /*
                                TODO
                                Go to each resource URL
                                Take all PDFS and links
                                display to user
                             */

                        }
                }
            });

            moduleList.addView(tv);
            moduleList.addView(tv2);
        }
    }

    public void getModuleData(SulisModule module, int index) {
        if(moduleHTML[index] == null && pw != null && ID != null) {
            task = new SulisTask(this, module);
            task.execute();


            WebView wv = findViewById(R.id.wv);
            String url = "https://sulis.ul.ie/access/content/group/"+module.getModuleID();

            /*
            wv.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return true;
                }
            });

            wv.loadUrl(url);
            wv.getSettings().setUserAgentString("Chrome/65.0.3325.181");

            try {
                String postData = "eid=" + URLEncoder.encode(ID, "UTF-8") + "&pw=" + URLEncoder.encode(pw, "UTF-8");
                wv.postUrl(url, postData.getBytes());
            } catch(Exception e) {
                Log.e(MSG, e.getStackTrace().toString());
            }*/

        } else {
            //TODO
            //-> Show saved string instead of searching internet again
        }
    }



    class SulisTask extends AsyncTask<Void, String, String> {

        private Context context;
        private SulisModule module;

        public SulisTask(Context c, SulisModule m) {
            context = c;
            module = m;
        }

        @Override
        protected String doInBackground(Void... arg0) {
            Document doc;
            try {

                String url = "https://sulis.ul.ie/access/content/group/"+module.getModuleID();



                Connection.Response loginForm = Jsoup.connect("https://sulis.ul.ie/access/login")
                        .method(Connection.Method.POST)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36")
                        .referrer(url)
                        .data("eid", ID)
                        .data("pw", pw)
                        .data("submit", "Log in")
                        .execute();

                Log.i(MSG, loginForm.body());


                doc = Jsoup.connect(url)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36")
                        .referrer(url)
                        .ignoreHttpErrors(true)
                        .post();



                HTML = doc.toString();



                Log.i(MSG, HTML);

                console = findViewById(R.id.tv);
                console.setText(HTML);
                console.setVerticalScrollBarEnabled(true);



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

            if(doc.title().contains("Login Required")) {
                Toast.makeText(context, "Error, Session Timed out", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(context, loginActivity.class);
                startActivity(i);
            }

            if(doc.title().contains("My Workspace")) {
                
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
    }
}
