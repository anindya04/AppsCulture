package com.android.appsculture;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends Activity {

    private static final String URL = "http://appsculture.com/vocab/words.json";
    private static final String IMAGE_URL = "http://appsculture.com/vocab/images/";

    RecyclerView recyclerView;
    WordsAdapter adapter;

    ProgressDialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Creating a new non-ui thread task to download json data
        DownloadTask downloadTask = new DownloadTask();

        // Starting the download process
        downloadTask.execute(URL);

        // Getting a reference to ListView of activity_main
        recyclerView = (RecyclerView ) findViewById(R.id.word_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    protected void onPause() {

        super.onPause();
    }

    @Override
    protected void onResume() {

        super.onResume();
    }

    /** A method to download data from url */
    private void downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        try{
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb  = new StringBuffer();

            String line = "";
            while( ( line = br.readLine())  != null){
                sb.append(line);
            }

            data = sb.toString();
            br.close();

        }catch(Exception e){
            Log.d("URl Not found", e.toString());
        }finally{
            iStream.close();
        }

       parseData(data);

    }

    /** A method to download parse data */
    private void parseData(String data) {
        JSONObject jsonObject = null;
        ContentValues values;

        try {
            jsonObject = new JSONObject(data);
            JSONArray jsonArray = jsonObject.getJSONArray("words");

            for(int i = 0; i<jsonArray.length(); i++){
                JSONObject jObject = jsonArray.getJSONObject(i);
                values = new ContentValues();
                Float ratio = Float.parseFloat(jObject.getString("ratio"));

                if(ratio > 0) {
                    values.put(WordProvider.word, jObject.getString("word"));
                    values.put(WordProvider.meaning, jObject.getString("meaning"));
                    values.put(WordProvider.ratio, jObject.getString("ratio"));
                    values.put(WordProvider.image, IMAGE_URL + (i + 1) + ".png");
                    Uri uri = getContentResolver().insert(WordProvider.CONTENT_URI, values);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /** AsyncTask to download json data */
    private class DownloadTask extends AsyncTask<String, Integer, Integer> {
        Integer result = 1;
        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(MainActivity.this);
            dialog.setTitle("Loading...");
            dialog.setMessage("Please wait...");
            dialog.setIndeterminate(true);
            dialog.show();
        }
        @Override
        protected Integer doInBackground(String... url) {
            try{
                downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {
            Cursor cursor = getContentResolver().query(WordProvider.CONTENT_URI,null,null,null,null);
            if(cursor != null && cursor.moveToFirst()){
                adapter = new WordsAdapter(getApplicationContext(), cursor);
                recyclerView.setAdapter(adapter);
                dialog.dismiss();
            }

        }
    }
}
