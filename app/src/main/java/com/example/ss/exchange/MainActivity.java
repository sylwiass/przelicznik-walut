package com.example.ss.exchange;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends ActionBarActivity {

    private EditText text2;
    private Button count;
    private TextView text3;
    private Spinner first;
    public int to;


    private String url = "http://apilayer.net/api/live";
    private String access_key ="?access_key=88f60d06dbb48624a14e763a7d1e3b25&";
    private String restEndpoint = "";
    private String currency ="";
    Spinner spinner;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text2 = (EditText) findViewById(R.id.text2);
        count = (Button) findViewById(R.id.count);
        text3= (TextView) findViewById(R.id.text3);

        spinner = (Spinner) findViewById(R.id.first);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.first, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

            }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void PressButton (View view){

        String field = text2.getText().toString();
        if (field.isEmpty()){
            Toast.makeText(MainActivity.this, "Wpisz kwotę", Toast.LENGTH_SHORT).show();

        }
        else {
            currency = spinner.getSelectedItem().toString();
            restEndpoint = url + access_key + "currencies=" + currency + "&format=1";
            Log.wtf("url", restEndpoint);
            new CallApiAsync().execute(restEndpoint);
        }

    }
    public static String GET (String url){
        InputStream inputStream = null;
        String result = "";

        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse httpresponse = httpclient.execute(new HttpGet(url));
            inputStream = httpresponse.getEntity().getContent();
            if (inputStream != null) {
                result = convertInput(inputStream);
            }
            else{
                result = "Nie działa";
            }

        }catch (Exception e){

            Log.wtf("InputStream", e.getLocalizedMessage());
        }
        return result;

    }
    private static String convertInput (InputStream is) throws IOException {

        BufferedReader bf = new BufferedReader(new InputStreamReader(is));
        String line = "";
        String result = "";
        while ((line = bf.readLine()) != null){
            result += line;
        }
        is.close();
        return result;
    }

    private class CallApiAsync extends AsyncTask<String, Void, String> {

        ProgressDialog pd;
        @Override
        protected void onPreExecute(){
            pd = ProgressDialog.show(MainActivity.this, "Informacja", "Przeliczam");

        }

        @Override
        protected String doInBackground(String...urls){

            return GET(urls[0]);
        }
        @Override
        protected void onPostExecute(String result){

            pd.dismiss();

            Log.wtf("result", result);
            try {
                JSONObject json = new JSONObject(result);

                JSONObject quotes = json.getJSONObject("quotes");
                double z = quotes.getDouble("USD" + currency);

                Log.wtf("values",z+"");

                double amount = Double.parseDouble(text2.getText().toString());
                double sum = Math.round(((amount * z)*100.0)/100.0);
                text3.setText(Double.toString(sum));

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
    public void exchanger(View view){
        Intent intent1 = new Intent(this, MainActivity.class);
        startActivity(intent1);
        finish();
    }
    public void exit(View v){
        finish();
        System.exit(0);

    }
}
