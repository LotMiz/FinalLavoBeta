package main.finallavobeta;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    private ProgressDialog pDialog;

    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();

    ArrayList<HashMap<String, String>> productsList;

//Location of php file in wamp
    private static String url_all_products = "http://www/androidconnect/get_all_events.php";
        //"http://api.lavodb.info/androidconnect/get_all_events.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_EVENT = "event";
    private static final String TAG_PID = "pid";
    private static final String TAG_NAME = "name";
    private static final String TAG_DATE = "date";

//Testing testing 123

    // products JSONArray
    JSONArray products = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_events);
        //ListView lv = getListView();

        //lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
          //  @Override
            //public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //String pid = ((TextView) view.findViewById(R.id.pid)).getText().toString();
              //  Intent in = new Intent(getApplicationContext(), MainActivity.class);
                //in.putExtra(TAG_PID, pid);
                //startActivityForResult(in,100);
            //}
       // });
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        getSupportActionBar().setDisplayOptions(android.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0B8500")));
        //getSupportActionBar().setCustomView(R.layout.actionbartitle);
        //TextView title = (TextView)findViewById(R.id.title);
        //Typeface typeface = Typeface.createFromAsset(getAssets(), "Righteous-Regular.ttf");
        //title.setTypeface(typeface);


        // Hashmap for ListView
        productsList = new ArrayList<HashMap<String, String>>();




        //StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        //StrictMode.setThreadPolicy(policy);
        new LoadAllProducts().execute();


    }





    class LoadAllProducts extends AsyncTask<String, String, Void> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Loading events. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

        }

        /**
         * getting All products from url
         * */
        protected Void doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            JSONObject json = null;
            try {
                json = jParser.makeHttpRequest(url_all_products, "GET", params);
            } catch (JSONException e) {
                e.printStackTrace();
            }


            // Check your log cat for JSON reponse
            Log.d("All Events: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // products found
                    // Getting Array of Products
                    products = json.getJSONArray(TAG_EVENT);

                    // looping through All Products
                    for (int i = 0; i < products.length(); i++) {
                        JSONObject c = products.getJSONObject(i);

                        // Storing each json item in variable
                        String id = c.getString(TAG_PID);
                        String event = c.getString(TAG_EVENT);
                        String date = c.getString(TAG_DATE);
                        String name = c.getString(TAG_NAME);

                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        map.put(TAG_PID, id);
                        map.put(TAG_EVENT, event);
                        map.put(TAG_DATE, date);
                        map.put(TAG_NAME, name);

                        // adding HashList to ArrayList
                        productsList.add(map);
                    }
                } else {
                    Intent i = new Intent(getApplicationContext(),
                            MainActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(Void file_url) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Updating parsed JSON data into ListView
                     * */
                    ListAdapter adapter = new SimpleAdapter(
                            MainActivity.this, productsList,
                            R.layout.list_item, new String[] { TAG_PID,
                            TAG_EVENT, TAG_DATE, TAG_NAME},
                            new int[] { R.id.pid, R.id.eventtitle, R.id.date });
                    // updating listview
                    ListView lv = (ListView)findViewById(R.id.list);
                    lv.setAdapter(adapter);
                }
            });

        }

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
/*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }
    */
}
