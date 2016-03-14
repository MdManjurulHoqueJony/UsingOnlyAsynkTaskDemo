package com.wordpress.jonyonandroidcraftsmanship.usingonlyasynktaskdemo;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private EditText etDownloadURL = null;
    private ListView lvURLLinks = null;
    private ProgressBar pbDownload = null;
    private String[] listOfImageURLs = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialize();
    }

    private void initialize() {
        etDownloadURL = (EditText) findViewById(R.id.etDownloadURL);
        lvURLLinks = (ListView) findViewById(R.id.lvURLLinks);
        pbDownload = (ProgressBar) findViewById(R.id.pbDownload);
        listOfImageURLs = getResources().getStringArray(R.array.image_urls);
        lvURLLinks.setOnItemClickListener(this);
    }

    public void downloadImage(View view) {
        if (etDownloadURL.getText().toString() != null && etDownloadURL.getText().toString().length() > 0) {
            MyTask myTask = new MyTask();
            myTask.execute(etDownloadURL.getText().toString());
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        etDownloadURL.setText(listOfImageURLs[position]);
    }

    private class MyTask extends AsyncTask<String, Integer, Boolean> {

        private int contentLength = -1;
        private int counter = 0;
        private int calculatedProgress = 0;

        @Override
        protected void onPreExecute() {
            pbDownload.setVisibility(View.VISIBLE);
            if (MainActivity.this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                MainActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            } else {
                MainActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        }

        @Override
        protected Boolean doInBackground(String... params) {
            boolean successsful = false;
            URL downloadUrl = null;
            HttpURLConnection connection = null;
            InputStream inputStream = null;
            File file = null;
            FileOutputStream fileOutputStream = null;
            try {
                downloadUrl = new URL(params[0]);
                connection = (HttpURLConnection) downloadUrl.openConnection();
                contentLength = connection.getContentLength();
                inputStream = connection.getInputStream();

                file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath()
                        + "/" + Uri.parse(params[0]).getLastPathSegment());
                fileOutputStream = new FileOutputStream(file);
                int read = -1;
                byte[] buffer = new byte[1024];
                while ((read = inputStream.read(buffer)) != -1) {
                    Logger.log("" + read);
                    fileOutputStream.write(buffer, 0, read);
                    counter += read;
                    Logger.log("Counter: " + counter + "ContentLength: " + contentLength);
                    publishProgress(counter);
                }
                successsful = true;
            } catch (MalformedURLException e) {
                Logger.log(e.toString());
            } catch (IOException e) {
                Logger.log(e.toString());
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        Logger.log(e.toString());
                    }
                }
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close();
                    } catch (IOException e) {
                        Logger.log(e.toString());
                    }
                }
            }
            return successsful;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            calculatedProgress = (int) (((double) values[0] / contentLength) * 100);
            setProgress(calculatedProgress);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            pbDownload.setVisibility(View.GONE);
            MainActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        }
    }
}



